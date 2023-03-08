package edu.alexey.toyslotto.domain.db.implementations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.stream.Stream;

import edu.alexey.toyslotto.AppSettings;
import edu.alexey.toyslotto.domain.db.FileUtils;
import edu.alexey.toyslotto.domain.db.FileUtils.ReadResult;
import edu.alexey.toyslotto.domain.db.interfaces.Queryable;

public class CsvQueryableBase<T> implements Queryable<T> {
	// aux inner types
	private static record SeqPair(int prev, int next) {
	}

	private static record AlignmentResult<E>(E entry, int lengthInBytes) {
	}

	// const
	public static final Charset CHARSET = AppSettings.CHARSET;
	public static final String COMMENT_LINE = "#";
	private static final String TMP_FILENAME = "toyslotto";

	// fields
	private final Function<T, String> toCsv;
	private final Function<String, Optional<T>> toObj;
	private final Function<T, Integer> getId;
	private final ObjIntConsumer<T> setId;
	private final File file;

	public CsvQueryableBase(
			String pathToCsv,
			Function<T, String> toCsv,
			Function<String, Optional<T>> toObj,
			Function<T, Integer> getId,
			ObjIntConsumer<T> setId) throws IOException {

		this.file = prepareFile(pathToCsv);
		this.toCsv = toCsv;
		this.toObj = toObj;
		this.getId = getId;
		this.setId = setId;

		if (!checkConsistency()) {
			throw new RuntimeException("Corrupted data found in the '"
					+ pathToCsv + "'. Unable to proceed.");
		}
	}

	@Override
	public T add(T entry) {
		int id = getNextId();
		setId.accept(entry, id);
		String csv = toCsv.apply(entry);
		String csvLine = System.lineSeparator() + csv;
		try (var fw = new FileWriter(file, CHARSET, true)) {
			fw.append(csvLine);

		} catch (Exception e) {
			// todo: log
			return null;
		}
		return entry;
	}

	@Override
	public T get(int id) {
		try (var linesStream = Files.lines(file.toPath(), CHARSET)) {

			var item = getEntriesStream(linesStream)
					.filter(t -> getId.apply(t) == id).findAny().orElseGet(() -> null);

			return item;

		} catch (IOException e) {
			// todo: log
			return null;
		}
	}

	@Override
	public Stream<T> queryAll() {
		try (var linesStream = Files.lines(file.toPath(), CHARSET)) {

			var items = getEntriesStream(linesStream).toList(); // accumulate to list due to short life of supplier
																// lifeStream
			return items.stream();

		} catch (IOException e) {
			// todo: log
			return Stream.<T>empty();
		}
	}

	private Stream<T> getEntriesStream(Stream<String> linesStream) {
		return linesStream.filter(s -> !(s.isBlank() || s.startsWith(COMMENT_LINE)))
				.map(this.toObj)
				.filter(Optional<T>::isPresent).map(Optional<T>::get);
	}

	@Override
	public boolean update(T entry) {
		if (entry == null) {
			return false;
		}
		Integer id = getId.apply(entry);
		if (id == null || id <= 0) {
			return false;
		}

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

			var alignmentResult = alignToRecord(raf, id);
			if (alignmentResult == null) {
				return false; // no such record
			}

			String csv = toCsv.apply(entry);
			String csvLine = csv + System.lineSeparator();
			var bytesArr = csvLine.getBytes(CHARSET);

			replaceNextBytes(raf, alignmentResult.lengthInBytes, bytesArr);
			return true;

		} catch (IOException e) {
			// todo: log
			System.err.println(e.getMessage());
		}

		return false;
	}

	@Override
	public T delete(int id) {
		if (id <= 0) {
			return null;
		}

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {

			var alignmentResult = alignToRecord(raf, id);
			if (alignmentResult == null) {
				return null;
			}

			deleteNextBytes(raf, alignmentResult.lengthInBytes);
			return alignmentResult.entry;

		} catch (IOException e) {
			// todo: log
			System.err.println(e.getMessage());
		}

		return null;
	}

	// aux & low-level

	private static void deleteNextBytes(RandomAccessFile raf, int length) throws IOException {
		Path tempFilePath = Files.createTempFile(TMP_FILENAME, ".tmp");

		try (RandomAccessFile tempRaf = new RandomAccessFile(tempFilePath.toFile(), "rw");
				FileChannel sourceChannel = raf.getChannel();
				FileChannel targetChannel = tempRaf.getChannel()) {

			long posDeleteFrom = raf.getFilePointer();
			long posTransferFrom = posDeleteFrom + length;
			long fileSize = raf.length();
			long transferCount = fileSize - posTransferFrom;

			sourceChannel.transferTo(posTransferFrom, transferCount, targetChannel);
			sourceChannel.truncate(posDeleteFrom);
			targetChannel.position(0L);
			sourceChannel.transferFrom(targetChannel, posDeleteFrom, transferCount);
		} finally {
			Files.delete(tempFilePath);
		}
	}

	private static void replaceNextBytes(RandomAccessFile raf, int length, byte[] replacement) throws IOException {
		assert length > 0 && replacement.length > 0;
		if (length == replacement.length) {
			raf.write(replacement);
			return;
		}

		Path tempFilePath = Files.createTempFile(TMP_FILENAME, ".tmp");
		try (RandomAccessFile tempRaf = new RandomAccessFile(tempFilePath.toFile(), "rw");
				FileChannel sourceChannel = raf.getChannel();
				FileChannel targetChannel = tempRaf.getChannel()) {

			long posDeleteFrom = raf.getFilePointer();
			long posTransferFrom = posDeleteFrom + length;
			long fileSize = raf.length();
			long transferCount = fileSize - posTransferFrom;

			sourceChannel.transferTo(posTransferFrom, transferCount, targetChannel);
			sourceChannel.truncate(posDeleteFrom);
			sourceChannel.write(ByteBuffer.wrap(replacement));
			targetChannel.position(0L);
			sourceChannel.transferFrom(targetChannel, posDeleteFrom + replacement.length, transferCount);
		} finally {
			Files.delete(tempFilePath);
		}
	}

	/**
	 * Выставляет позицию в файле на начало записи с соответствующим id.
	 * 
	 * @param raf экземпляр открытого random access file.
	 * @return Результат поиска записи, содержащий экземпляр найденной сущности
	 *         и длину соответствующей ей записи в байтах.
	 *         Если записи с соответствующим id не найдено, то возвращается null.
	 * @throws IOException
	 */
	private AlignmentResult<T> alignToRecord(RandomAccessFile raf, int id) throws IOException {
		// поиск с конца, чтобы сразу проверить валидность переданного id
		raf.seek(Math.max(raf.length() - 1, 0));
		ReadResult readResult;
		do {
			readResult = FileUtils.readLineBackward(raf, CHARSET);
			var csv = readResult.line().strip();
			if (csv.isEmpty() || csv.startsWith(COMMENT_LINE)) {
				continue;
			}

			var entryOpt = toObj.apply(csv);
			if (entryOpt.isEmpty()) {
				continue;
			}

			var entry = entryOpt.get();
			int entryId = getId.apply(entry);
			if (entryId < id) {
				return null;
			}

			if (entryId == id) {
				raf.seek(raf.getFilePointer() + 1);
				return new AlignmentResult<T>(entry, readResult.length());
			}

		} while (!readResult.endReached());

		return null;
	}

	private int getNextId() {
		try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {

			if (raf.length() == 0) {
				return 1;
			}

			raf.seek(Math.max(raf.length() - 1, 0));
			ReadResult readResult;
			Optional<T> lastItemOpt = null;
			while (!((readResult = FileUtils.readLineBackward(raf, CHARSET)).endReached())
					&&
					(readResult.line().isBlank()
							|| readResult.line().startsWith(COMMENT_LINE)
							|| (lastItemOpt = toObj.apply(readResult.line())).isEmpty())) {
			}

			if (lastItemOpt != null && lastItemOpt.isPresent()) {
				Integer lastId = getId.apply(lastItemOpt.get());
				if (lastId != null) {
					return lastId + 1;
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return 1;
	}

	private boolean checkConsistency() {
		try (var linesStream = Files.lines(file.toPath(), CHARSET)) {
			var idsSequence = linesStream.filter(s -> !(s.isBlank() || s.startsWith(COMMENT_LINE)))
					.map(this.toObj).filter(Optional::isPresent).map(Optional<T>::get)
					.map(this.getId);

			final int[] tmp = new int[] { 0 };
			boolean wrongSequence = idsSequence.map(i -> {
				var pair = new SeqPair(tmp[0], i);
				tmp[0] = i;
				return pair;
			}).anyMatch(p -> p.next <= p.prev);

			return !wrongSequence;

		} catch (IOException e) {
			return false;
		}
	}

	private static File prepareFile(String pathToCsv) throws IOException {
		Path path = Path.of(pathToCsv);
		if (Files.notExists(path)) {
			Files.createFile(path);
		}

		if (!Files.isRegularFile(path)) {
			throw new InvalidPathException(path.toString(), "Wrong file type");
		}

		if (!Files.isReadable(path)) {
			throw new IOException("File read access denied");
		}

		if (!Files.isWritable(path)) {
			throw new IOException("File write access denied");
		}

		return path.toFile();
	}

}

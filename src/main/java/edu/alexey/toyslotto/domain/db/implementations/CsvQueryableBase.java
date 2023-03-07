package edu.alexey.toyslotto.domain.db.implementations;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.stream.Stream;

import edu.alexey.toyslotto.AppSettings;
import edu.alexey.toyslotto.domain.db.FileUtils;
import edu.alexey.toyslotto.domain.db.FileUtils.ReadResult;
import edu.alexey.toyslotto.domain.db.interfaces.Queryable;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class CsvQueryableBase<T> implements Queryable<T> {
	private static record SeqPair(int prev, int next) {
	}

	public static final Charset CHARSET = AppSettings.CHARSET;
	public static final String COMMENT_LINE = "#";
	public static final String ESC_SEQ_PREFIX = "\\";

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
			return null;
		}
		return entry;
	}

	@Override
	public T delete(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public T get(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<T> queryAll() {
		try (var linesStream = Files.lines(file.toPath(), CHARSET)) {

			var items = linesStream.filter(s -> !(s.isBlank() || s.startsWith(COMMENT_LINE)))
					.map(this.toObj)
					.filter(Optional<T>::isPresent).map(Optional<T>::get)
					.toList();

			return items.stream();

		} catch (IOException e) {
			return Stream.<T>empty();
		}
	}

	@Override
	public boolean update(T entry) {
		// TODO Auto-generated method stub
		return false;
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

package edu.alexey.toyslotto.domain.db;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.ObjIntConsumer;
import java.util.stream.Stream;

public class CsvQueryableBase<T> implements Queryable<T> {
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String COMMENT_LINE = "#";
	public static final String ESC_SEQ_PREFIX = "\\";

	private final Function<T, String> toCsv;
	private final Function<String, Optional<T>> toObj;
	private final Function<T, Integer> getId;
	private final ObjIntConsumer<T> setId;
	private final Path path;
	private final File file;

	private int nextId;

	public CsvQueryableBase(
			String pathToCsv,
			Function<T, String> toCsv,
			Function<String, Optional<T>> toObj,
			Function<T, Integer> getId,
			ObjIntConsumer<T> setId) throws IOException {

		this.path = prepareFile(pathToCsv);
		this.file = this.path.toFile();
		this.toCsv = toCsv;
		this.toObj = toObj;
		this.getId = getId;
		this.setId = setId;
	}

	// @Override
	// public void close() throws Exception {
	// // TODO Auto-generated method stub

	// }

	

	private int getNextId() {
		return 0;
	}



	@Override
	public T add(T entry) {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean update(T entry) {
		// TODO Auto-generated method stub
		return false;
	}




	private static Path prepareFile(String pathToCsv) throws IOException {
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

		return path;
	}

	

}

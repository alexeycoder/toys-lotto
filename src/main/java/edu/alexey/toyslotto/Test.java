package edu.alexey.toyslotto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import edu.alexey.toyslotto.domain.db.FileUtils;

public class Test {
	public static record Pair(Integer a, Integer b) {
	}

	public static void main(String[] args) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		Path path = Path.of(".data/testfile3.csv");
		File file = path.toFile();

		System.out.println("Hello World!");

		System.out.println("=".repeat(80));

		var content = Files.lines(path, charset);
		content.forEach(System.out::println);

		System.out.println("=".repeat(80) + "READ_RAF");

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			boolean endReached = false;
			do {
				var readResult = FileUtils.readLine(raf, charset);
				endReached = readResult.endReached();
				System.out.print(readResult.line());
			} while (!endReached);
			System.out.println();
		}

		System.out.println("=".repeat(80));

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			if (raf.length() > 0) {
				raf.seek(Math.max(raf.length() - 1, 0));
				System.out.println("Pos before read last byte = " + raf.getFilePointer());
				System.out.println("File Length = " + raf.length());
				byte readByte = raf.readByte();
				System.out.println(String.format("Last Byte = %s", (char) readByte));
				System.out.println("Pos after read last byte = " + raf.getFilePointer());
			}
		}

		System.out.println("=".repeat(80) + "READ_RAF_BACKWARD");

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(Math.max(raf.length() - 1, 0));
			boolean endReached = false;
			do {
				var readResult = FileUtils.readLineBackward(raf, charset);
				endReached = readResult.endReached();
				System.out.print(readResult.line());
			} while (!endReached);
			System.out.println();
		}

		System.out.println("=".repeat(80));

		var lst = List.of(1, 2, 3, 4, 5, 6, 7, 8);
		var res = lst.stream().reduce((first, second) -> (second < first ? second : -1));// .filter(i -> i
		// <0);//.orElse(null);
		// var res =
		// lst.stream().mapMulti(null
		System.out.println(res);

		Stream.of("hello", "world")
				.<Character>mapMulti((str, sink) -> {
					for (char c : str.toCharArray()) {
						sink.accept(c);
					}
				}).forEach(System.out::println);

		System.out.println("=".repeat(80));
		
		checkConsistency(file, charset);
		
		System.out.println("=".repeat(80));

		testMap();
	}

	public static record Dummy(int id, String str) {
	}

	private static boolean checkConsistency(File file, Charset charset) throws IOException {
		final Function<String, Optional<Dummy>> toObj = s -> {
			var arr = s.strip().split(";");
			return arr.length == 2 ? Optional.of(new Dummy(Integer.parseInt(arr[0]), arr[1])) : Optional.empty();
		};

		final Function<Dummy, Integer> getId = d -> d.id;

		var linesStream = Files.lines(file.toPath(), charset);
		var idsSequence = linesStream.filter(s -> !s.startsWith("#"))
				.map(toObj).filter(Optional::isPresent).map(Optional<Dummy>::get)
				.map(getId);

		final Integer[] tmp = new Integer[] { 0 };
		idsSequence.forEach(i -> {
			// System.out.print(Test.prev + " ");
			System.out.print(tmp[0] + " ");
			// Test.prev = i;
			tmp[0] = i;
			// System.out.print(Test.prev + " ;");
			System.out.print(tmp[0] + " ;");
		});

		return false;
	}

	private static void testMap() {
		var myMap = Map.of(
				1, "Stroka1",
				2, "Stroka2",
				5, "Stroka5",
				4, "Stroka4",
				3, "Stroka",
				7, "Stroka7",
				8, "Stroka8",
				9, "Stroka9");

		for (var kv : myMap.entrySet()) {
			System.out.println(kv.getKey() + " -- " + kv.getValue());
		}

		var map2 = new LinkedHashMap<>(myMap);

		for (var kv : map2.entrySet()) {
			System.out.println(kv.getKey() + " -- " + kv.getValue());
		}

	}

	// public static String readUtf8Line(RandomAccessFile raf, Charset charset)
	// throws IOException {
	// final byte[] lineSepSequence = System.lineSeparator().getBytes(charset);
	// final byte[] readSequence = new byte[lineSepSequence.length];
	// ByteArrayOutputStream readBytes = new ByteArrayOutputStream();
	// while (raf.getFilePointer() < raf.length()) {
	// var readByte = raf.readByte();
	// readBytes.write(readByte);
	// shiftLeft(readSequence, readByte);
	// if (Arrays.equals(readSequence, lineSepSequence)) {
	// break;
	// }
	// }

	// if (readBytes.size() > 0) {
	// var res = new String(readBytes.toByteArray(), charset);
	// return res;
	// }

	// return null;
	// }

	// private static void shiftLeft(byte[] arr, byte right) {
	// int lastIndex = arr.length - 1;
	// for (int i = 0; i + 1 < lastIndex; ++i) {
	// arr[i] = arr[i + 1];
	// }
	// arr[lastIndex] = right;
	// }

	public static void insert(String filename, long offset, byte[] content)
			throws IOException {
		RandomAccessFile r = new RandomAccessFile(new File(filename), "rw");
		RandomAccessFile rtemp = new RandomAccessFile(new File(filename + "~"),
				"rw");
		long fileSize = r.length();
		FileChannel sourceChannel = r.getChannel();
		FileChannel targetChannel = rtemp.getChannel();
		sourceChannel.transferTo(offset, (fileSize - offset), targetChannel);
		sourceChannel.truncate(offset);
		r.seek(offset);
		r.write(content);
		long newOffset = r.getFilePointer();
		targetChannel.position(0L);
		sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset));
		sourceChannel.close();
		targetChannel.close();
	}

	public static void insert2(String filename, long offset, byte[] content) throws IOException {
		File temp = Files.createTempFile("insertTempFile", ".temp").toFile(); // Create a temporary file to save content
																				// to
		try (RandomAccessFile r = new RandomAccessFile(new File(filename), "rw"); // Open file for read & write
				RandomAccessFile rtemp = new RandomAccessFile(temp, "rw"); // Open temporary file for read & write
				FileChannel sourceChannel = r.getChannel(); // Channel of file
				FileChannel targetChannel = rtemp.getChannel()) { // Channel of temporary file
			long fileSize = r.length();
			sourceChannel.transferTo(offset, (fileSize - offset), targetChannel); // Copy content after insert index to
																					// temporary file
			sourceChannel.truncate(offset); // Remove content past insert index from file
			r.seek(offset); // Goto back of file (now insert index)
			r.write(content); // Write new content
			long newOffset = r.getFilePointer(); // The current offset
			targetChannel.position(0L); // Goto start of temporary file
			sourceChannel.transferFrom(targetChannel, newOffset, (fileSize - offset)); // Copy all content of temporary
																						// to end of file
		}
		Files.delete(temp.toPath()); // Delete the temporary file as not needed anymore
	}
}

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.IntStream;

import edu.alexey.toyslotto.domain.db.FileUtils;

public class Test {
	public static void main(String[] args) throws IOException {
		Charset charset = Charset.forName("UTF-8");
		Path path = Path.of(".data/testfile.csv");
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
			raf.seek(raf.length() - 1);
			System.out.println("Pos before read last byte = " + raf.getFilePointer());
			System.out.println("File Length = " + raf.length());

			byte readByte = raf.readByte();
			System.out.println(String.format("Last Byte = %s", (char) readByte));
			System.out.println("Pos after read last byte = " + raf.getFilePointer());
		}

		System.out.println("=".repeat(80) + "READ_RAF_BACKWARD");

		try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
			raf.seek(raf.length() - 1);
			boolean endReached = false;
			do {
				var readResult = FileUtils.readLineBackward(raf, charset);
				endReached = readResult.endReached();
				System.out.print(readResult.line());
			} while (!endReached);
			System.out.println();
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

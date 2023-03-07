package edu.alexey.toyslotto.domain.db;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;

public class FileUtils {
	public static final byte LINE_SEP = 0xA;

	public static record ReadResult(String line, boolean endReached) {
	}

	public static ReadResult readLine(RandomAccessFile raf, Charset charset) throws IOException {
		ByteArrayOutputStream readBytes = new ByteArrayOutputStream();
		while (raf.getFilePointer() < raf.length()) {
			var readByte = raf.readByte();
			readBytes.write(readByte);
			if (readByte == LINE_SEP) {
				break;
			}
		}

		boolean endReached = raf.getFilePointer() == raf.length();
		String line = new String(readBytes.toByteArray(), charset);
		return new ReadResult(line, endReached);

	}

	public static ReadResult readLineBackward(RandomAccessFile raf, Charset charset) throws IOException {
		if (raf.length() == 0) {
			return new ReadResult("", true);
		}

		long endPos = raf.getFilePointer();
		if (!(endPos == raf.length() - 1 || raf.readByte() == LINE_SEP)) {
			throw new IllegalStateException("File pointer must point at"
					+ " either the end of file"
					+ " or line separator before reading lines backward!");
		}

		// if (endPos == 0L) {
		// return "\n";
		// }

		long pos = endPos - 1;
		while (pos >= 0) {
			raf.seek(pos);
			if (raf.readByte() == LINE_SEP) {
				break;
			}
			--pos;
		}
		boolean endReached = false;
		if (pos < 0) {
			pos = 0;
			raf.seek(0);
			endReached = true;
		}

		int len = (int) (endPos - raf.getFilePointer() + 1);
		byte[] bytes = new byte[len];
		raf.readFully(bytes);
		raf.seek(pos);
		String line = new String(bytes, charset);
		return new ReadResult(line, endReached);
	}
}

package edu.alexey.utils;

public class StringUtils {
	/**
	 * Символ заполнитель окажется справа, содержимое - слева.
	 */
	public static String padRight(String str, String filler, int totalLength) {
		return padSide(false, str, filler, totalLength);
	}

	/**
	 * Символ заполнитель окажется слева, содержимое - справа.
	 */
	public static String padLeft(String str, String filler, int totalLength) {
		return padSide(true, str, filler, totalLength);
	}

	public static String padCenter(String str, String filler, int totalLength) {
		str = padLeft(str, filler, (totalLength + str.length()) / 2);
		return padRight(str, filler, totalLength);
	}

	private static String padSide(boolean left, String str, String filler, int totalLength) {
		int diff = totalLength - str.length();
		if (diff <= 0)
			return str;

		int fsLen = filler.length();
		if (fsLen == 0) {
			filler = " ";
			fsLen = 1;
		}
		String fillStr = filler.repeat(diff / fsLen);
		int remains = diff - fillStr.length();
		if (remains > 0) {
			fillStr = left ? filler.substring(0, remains) + fillStr : fillStr + filler.substring(0, remains);
		}
		return left ? fillStr + str : str + fillStr;
	}
}

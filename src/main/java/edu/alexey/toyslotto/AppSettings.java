package edu.alexey.toyslotto;

import java.nio.charset.Charset;
import java.util.Locale;

public class AppSettings {
	public static final Locale LOCALE = Locale.of("ru", "RU");
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String PATH_TO_CSV = ".data/prize_pool.csv";
	public static final int MAX_PARTICIPANTS = 10;
}

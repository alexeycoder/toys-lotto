package edu.alexey.toyslotto.client.viewmodels;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import edu.alexey.toyslotto.client.uielements.Menu;
import edu.alexey.toyslotto.client.uielements.MenuItem;
import edu.alexey.utils.StringUtils;

public class MenuViewModel extends ViewModelBase {

	private static final String FRAME_H = "\u2501";
	private static final String FRAME_V = "\u2502";
	private static final String FRAME_TOP_L = "\u250d";
	private static final String FRAME_TOP_R = "\u2511";
	private static final String FRAME_BTM_L = "\u2515";
	private static final String FRAME_BTM_R = "\u2519";
	private static final String FRAME_MID_L = "\u251c";
	private static final String FRAME_MID_R = "\u2524";
	private static final String FRAME_MID_H = "\u2500";
	private static final String SEP = "\u25b8";
	private static final String PADDING = " ";

	private final String strRepr;

	protected MenuViewModel(Menu menu) {
		var menuMap = menu.items();
		var menuKeysSet = menuMap.keySet();
		int keysMaxLen = menuKeysSet.stream().map(MenuViewModel::keyToStr).mapToInt(String::length).max().getAsInt();
		int namesMaxLen = menuMap.values().stream().filter(m -> m != null && m.name() != null)
				.map(MenuItem::name).mapToInt(String::length).max().getAsInt();

		int hFrameWidth = PADDING.length() * 4 + SEP.length() + keysMaxLen + namesMaxLen;

		var lines = new ArrayList<String>();
		lines.add(FRAME_TOP_L + FRAME_H.repeat(hFrameWidth) + FRAME_TOP_R);

		if (menu.header() != null) {
			lines.add(FRAME_V + StringUtils.padCenter(menu.header(), " ", hFrameWidth) + FRAME_V);
			lines.add(FRAME_MID_L + FRAME_MID_H.repeat(hFrameWidth) + FRAME_MID_R);
		}

		menuMap.entrySet().stream().filter(e -> e.getValue() != null)
				.sorted(Comparator.comparingInt(e -> e.getValue().order()))
				.forEachOrdered(e -> {
					var mi = e.getValue();
					var miName = mi.name() != null ? mi.name() : "";
					lines.add(FRAME_V + PADDING
							+ StringUtils.padCenter(keyToStr(e.getKey()), " ", keysMaxLen)
							+ PADDING + SEP + PADDING
							+ StringUtils.padRight(miName, " ", namesMaxLen)
							+ PADDING + FRAME_V);
				});
		lines.add(FRAME_BTM_L + FRAME_H.repeat(hFrameWidth) + FRAME_BTM_R);
		lines.add("");

		strRepr = String.join(System.lineSeparator(), lines);
	}

	private static String keyToStr(Set<String> key) {
		if (key.isEmpty()) {
			return "";
		}
		if (key.size() == 1) {
			return key.iterator().next();
		}
		var lst = key.stream().sorted().toList();
		var str = String.format("%s (%s)", lst.get(0),
				lst.stream().skip(1).collect(Collectors.joining(", ")));
		return str;
	}

	@Override
	public String toString() {
		return strRepr;
	}

}

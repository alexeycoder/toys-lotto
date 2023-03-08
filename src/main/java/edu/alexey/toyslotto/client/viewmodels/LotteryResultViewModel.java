package edu.alexey.toyslotto.client.viewmodels;

import java.util.List;

import edu.alexey.toyslotto.domain.entities.ToyItem;
import edu.alexey.utils.StringUtils;

public class LotteryResultViewModel extends ViewModelBase {

	private static final String HEAD_NUM = "№ участника";
	private static final String HEAD_NAME = "Приз";
	private static final String PADDING = " ";
	private static final String FRAME_V = "\u2502";

	private final int nMaxLen;
	private final int nameMaxLen;

	private final String strRepr;

	protected LotteryResultViewModel(List<ToyItem> toyItems) {
		if (toyItems.isEmpty()) {
			nMaxLen = 0;
			nameMaxLen = 0;
			strRepr = "Список пуст.\n";
			return;
		}
		nMaxLen = Integer.max(2, HEAD_NUM.length());
		int namesMaxLen = toyItems.stream().map(ToyItem::getName).mapToInt(String::length).max().getAsInt();
		nameMaxLen = Integer.max(namesMaxLen, HEAD_NAME.length());
		StringBuilder sb = new StringBuilder();
		appendLine(sb, HEAD_NUM, HEAD_NAME);
		appendLine(sb, PADDING, PADDING);
		int[] i = new int[] { 1 };
		toyItems.forEach(t -> {
			appendLine(sb,
					Integer.toString(i[0]++),
					t.getName());
		});

		strRepr = sb.toString();
	}

	private void appendLine(StringBuilder sb, String id, String name) {
		sb.append(PADDING).append(StringUtils.padLeft(id, " ", nMaxLen)).append(PADDING)
				.append(FRAME_V)
				.append(PADDING).append(StringUtils.padRight(name, " ", nameMaxLen)).append(PADDING)
				.append(System.lineSeparator());
	}

	@Override
	public String toString() {
		return strRepr;
	}

}

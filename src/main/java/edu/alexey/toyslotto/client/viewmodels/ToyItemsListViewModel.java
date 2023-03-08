package edu.alexey.toyslotto.client.viewmodels;

import java.util.List;

import edu.alexey.toyslotto.domain.entities.ToyItem;
import edu.alexey.utils.StringUtils;

public class ToyItemsListViewModel extends ViewModelBase {

	private static final String HEAD_ID = "ID";
	private static final String HEAD_NAME = "Наименование";
	private static final String HEAD_WT = "Усл. частота";
	private static final String HEAD_QTY = "Количество";
	private static final String PADDING = " ";
	private static final String FRAME_V = "\u2502";

	private final int idMaxLen;
	private final int weightMaxLen;
	private final int qtyMaxLen;
	private final int nameMaxLen;

	private final String strRepr;

	protected ToyItemsListViewModel(List<ToyItem> toyItems) {
		if (toyItems.isEmpty()) {
			idMaxLen = 0;
			weightMaxLen = 0;
			qtyMaxLen = 0;
			nameMaxLen = 0;
			strRepr = "Список пока пуст.\n";
			return;
		}
		idMaxLen = Integer.max(3, HEAD_ID.length());
		weightMaxLen = Integer.max(3, HEAD_WT.length());
		qtyMaxLen = Integer.max(4, HEAD_QTY.length());
		int namesMaxLen = toyItems.stream().map(ToyItem::getName).mapToInt(String::length).max().getAsInt();
		nameMaxLen = Integer.max(namesMaxLen, HEAD_NAME.length());
		StringBuilder sb = new StringBuilder();
		appendLine(sb, HEAD_ID, HEAD_NAME, HEAD_WT, HEAD_QTY);
		appendLine(sb, PADDING, PADDING, PADDING, PADDING);
		toyItems.forEach(t -> {
			appendLine(sb,
					t.getToyItemId().toString(),
					t.getName(),
					Integer.toString(t.getWeight()),
					Integer.toString(t.getQuantity()));
		});

		strRepr = sb.toString();
	}

	private void appendLine(StringBuilder sb, String id, String name, String weight, String qty) {
		sb.append(PADDING).append(StringUtils.padLeft(id, " ", idMaxLen)).append(PADDING)
				.append(FRAME_V)
				.append(PADDING).append(StringUtils.padRight(name, " ", nameMaxLen)).append(PADDING)
				.append(FRAME_V)
				.append(PADDING).append(StringUtils.padRight(weight, " ", weightMaxLen)).append(PADDING)
				.append(FRAME_V)
				.append(PADDING).append(StringUtils.padRight(qty, " ", qtyMaxLen)).append(PADDING)
				.append(System.lineSeparator());
	}

	@Override
	public String toString() {
		return strRepr;
	}

}

package edu.alexey.toyslotto.client.viewmodels;

import edu.alexey.toyslotto.domain.entities.ToyItem;

public class ToyItemViewModel extends ViewModelBase {

	private final String strRepr;

	protected ToyItemViewModel(ToyItem toyItem) {
		strRepr = String.format(
				"ID %2d:  %-20s  Усл. частота выпадения: %-3d  Количество: %d\n",
				toyItem.getToyItemId(),
				"'" + toyItem.getName() + "'",
				toyItem.getWeight(),
				toyItem.getQuantity());
	}

	@Override
	public String toString() {
		return strRepr;
	}

}

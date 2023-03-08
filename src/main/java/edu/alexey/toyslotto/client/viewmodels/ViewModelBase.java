package edu.alexey.toyslotto.client.viewmodels;

import java.util.List;

import edu.alexey.toyslotto.client.uielements.Menu;
import edu.alexey.toyslotto.domain.entities.ToyItem;

/**
 * Принудим реализовывать toString() у всех ViewModel,
 * поскольку это основной метод для представления в консоли.
 */
public abstract class ViewModelBase {
	public abstract String toString();

	// fab

	public static ViewModelBase of(Menu menu) {
		return new MenuViewModel(menu);
	}

	public static ViewModelBase of(ToyItem toyItem) {
		return new ToyItemViewModel(toyItem);
	}

	public static ViewModelBase of(List<ToyItem> toyItems) {
		return new ToyItemsListViewModel(toyItems);
	}

	public static ViewModelBase lotteryResultModelView(List<ToyItem> toyItems) {
		return new LotteryResultViewModel(toyItems);
	}

	public static ViewModelBase emptySpace(Integer nLines) {
		return new ViewModelBase() {
			@Override
			public String toString() {
				return nLines == null || nLines <= 1
						? System.lineSeparator()
						: System.lineSeparator().repeat(nLines);
			}
		};
	}
}

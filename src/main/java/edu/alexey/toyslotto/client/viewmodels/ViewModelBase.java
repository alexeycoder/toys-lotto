package edu.alexey.toyslotto.client.viewmodels;

/** Принудим реализовывать toString() у всех ViewModel,
 * поскольку это основной метод для представления в консоли.
 */
public abstract class ViewModelBase {
	public abstract String toString();
}

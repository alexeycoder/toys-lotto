package edu.alexey.toyslotto.domain.business.interfaces;

import java.util.List;

import edu.alexey.toyslotto.domain.entities.ToyItem;

public interface PrizePoolRepository {

	// create

	ToyItem addToyItem(ToyItem toyItem);

	// read

	ToyItem getToyItemById(int toyItemId);

	List<ToyItem> getToyItemsByName(String toyName);

	List<ToyItem> getAllToyItems();

	// update

	boolean updateToyItem(ToyItem toyItem);

	// delete

	boolean deleteToyItem(int toyItemId);

	// lotto logic

	/**
	 * Проведение розыгрыша игрушек среди заданного количества участников,
	 * по одному призу на участника.
	 * Вероятность выпадения игрушки определяется соотношением её условного веса
	 * к суммарному весу всех игрушек, имеющихся в наличии.
	 * Таким образом, как только какое-либо из наименований полностью выбывает
	 * из запаса в результате очередного круга розыгрыша, данное наименование
	 * перестаёт участвовать в формировании суммарного веса всех игрушек на
	 * последующих циклах розыгрышей.
	 * То есть игрушки участвуют в розыгрыше пока имеются в наличии.
	 * 
	 * @param numOfParticipants Начальное число участников лотереи.
	 *                          Если число участников больше количества игрушек в
	 *                          наличии, то участники
	 *                          со старшими номерами останутся без призов.
	 * @return Список призов. По одной игрушке на участника.
	 */
	List<ToyItem> conductLottery(int numOfParticipants);

	/**
	 * Подсчёт количества игрушек всех наименований в наличии.
	 * @return Количество всех игрушек в наличии.
	 */
	int countToysAvailable();
}

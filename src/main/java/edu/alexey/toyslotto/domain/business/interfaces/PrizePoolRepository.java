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
	 * @param numOfParticipants
	 * @return
	 */
	List<ToyItem> conductLottery(int numOfParticipants);
}

package edu.alexey.toyslotto.domain.business.implementations;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import edu.alexey.toyslotto.domain.business.interfaces.PrizePoolRepository;
import edu.alexey.toyslotto.domain.db.interfaces.Queryable;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class PrizePoolRepositoryImpl implements PrizePoolRepository {

	private final Queryable<ToyItem> prizePool;

	public PrizePoolRepositoryImpl(Queryable<ToyItem> prizePool) {
		this.prizePool = prizePool;
	}

	@Override
	public ToyItem addToyItem(ToyItem toyItem) {
		basicCheck(toyItem);
		return prizePool.add(toyItem);
	}

	@Override
	public ToyItem getToyItemById(int toyItemId) {
		return prizePool.get(toyItemId);
	}

	@Override
	public List<ToyItem> getToyItemsByName(String toyName) {
		if (toyName == null) {
			throw new NullPointerException("toyName");
		}
		if (toyName.isBlank()) {
			throw new InvalidParameterException("toyName");
		}
		final String name = toyName.toLowerCase();
		var dbEntities = prizePool.queryAll().filter(t -> t.getName().toLowerCase().contains(name)).toList();
		return dbEntities; // no need to clone
	}

	@Override
	public List<ToyItem> getAllToyItems() {
		return prizePool.queryAll().toList();
	}

	@Override
	public boolean updateToyItem(ToyItem toyItem) {
		basicCheck(toyItem);
		return prizePool.update(toyItem);
	}

	@Override
	public boolean deleteToyItem(int toyItemId) {
		return prizePool.delete(toyItemId) != null;
	}

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
	public List<ToyItem> conductLottery(int numOfParticipants) {

		List<ToyItem> toyItemsAvailable = prizePool.queryAll()
				.filter(t -> t.getWeight() > 0)
				.filter(t -> t.getQuantity() > 0)
				.toList();

		if (toyItemsAvailable.isEmpty()) {
			return toyItemsAvailable;
		}

		ArrayList<ToyItem> mutableToyItemsAvailable = new ArrayList<>(toyItemsAvailable);

		List<ToyItem> pickedToys = new ArrayList<>();
		int summaryWeight = calcSummaryWeight(mutableToyItemsAvailable);
		int numOfItems = mutableToyItemsAvailable.size();

		for (; numOfParticipants > 0; --numOfParticipants) {
			var pickedToy = pickOutToy(mutableToyItemsAvailable, summaryWeight);
			if (pickedToy == null) {
				break;
			}

			pickedToys.add(pickedToy);

			if (mutableToyItemsAvailable.size() != numOfItems) {
				// если в списке игрушек "в наличии" убавилось в результате
				// розыгрыша, то необходимо пересчитать суммарный вес
				numOfItems = mutableToyItemsAvailable.size();
				summaryWeight = calcSummaryWeight(mutableToyItemsAvailable);
			}
		}

		return pickedToys;
	}

	private static int calcSummaryWeight(List<ToyItem> toyItemsAvailable) {
		int sum = toyItemsAvailable.stream().collect(Collectors.summingInt(ToyItem::getWeight));
		return sum;
	}

	private static ToyItem pickOutToy(List<ToyItem> toyItemsAvailable, int summaryWeight) {
		int virtualPosition = ThreadLocalRandom.current().nextInt(1, summaryWeight);
		int acc = 0;
		ToyItem picked = null;
		for (ToyItem toyItem : toyItemsAvailable) {
			acc += toyItem.getWeight();
			if (acc >= virtualPosition) {
				picked = toyItem;
				break;
			}
		}
		if (picked != null) {
			int newQty = picked.getQuantity() - 1;
			picked.setQuantity(newQty);
			if (newQty == 0) {
				toyItemsAvailable.remove(picked);
			}
			return picked;
		}
		return null;
	}

	// aux:

	private void basicCheck(ToyItem toyItem) {
		if (toyItem == null) {
			throw new NullPointerException("toyItem");
		}
		if (toyItem.getName() == null) {
			throw new NullPointerException("toyItem.name");
		}
		if (toyItem.getName().isBlank()) {
			throw new InvalidParameterException("toyItem.name");
		}
	}

}

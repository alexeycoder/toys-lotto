package edu.alexey.toyslotto.domain.business.implementations;

import java.security.InvalidParameterException;
import java.util.List;

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

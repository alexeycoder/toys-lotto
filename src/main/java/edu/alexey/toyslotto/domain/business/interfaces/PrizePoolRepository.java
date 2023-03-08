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
}

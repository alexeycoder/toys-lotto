package edu.alexey.toyslotto.domain.db.interfaces;

import edu.alexey.toyslotto.domain.entities.ToyItem;

public interface DbContext {
	
	Queryable<ToyItem> prizePool();
}

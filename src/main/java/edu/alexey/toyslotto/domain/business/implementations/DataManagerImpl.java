package edu.alexey.toyslotto.domain.business.implementations;

import edu.alexey.toyslotto.domain.business.interfaces.DataManager;
import edu.alexey.toyslotto.domain.business.interfaces.PrizePoolRepository;
import edu.alexey.toyslotto.domain.db.interfaces.DbContext;

public class DataManagerImpl implements DataManager {

	private final PrizePoolRepository prizePoolRepository;

	public DataManagerImpl(DbContext dbContext) {
		this.prizePoolRepository = new PrizePoolRepositoryImpl(dbContext.prizePool());
	}

	@Override
	public PrizePoolRepository prizePoolRepository() {
		return prizePoolRepository;
	}
}

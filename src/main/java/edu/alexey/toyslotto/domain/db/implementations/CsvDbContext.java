package edu.alexey.toyslotto.domain.db.implementations;

import java.io.IOException;
import java.util.Optional;

import edu.alexey.toyslotto.domain.db.interfaces.DbContext;
import edu.alexey.toyslotto.domain.db.interfaces.Queryable;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class CsvDbContext implements DbContext {
	public static final String CSV_SEP = ";";
	public static final String CSV_SEP_REPLACEMENT = "```";
	private static final int TOY_ITEM_N_FIELDS = 4;

	private final Queryable<ToyItem> prizePool;

	public CsvDbContext(String pathToPrizePoolCsv) throws IOException {

		this.prizePool = new CsvQueryableBase<ToyItem>(
				pathToPrizePoolCsv,
				CsvDbContext::toyItemToCsv,
				CsvDbContext::csvToToyItem,
				ToyItem::getToyItemId,
				ToyItem::setToyItemId);
	}

	@Override
	public Queryable<ToyItem> prizePool() {
		return prizePool;
	}

	private static String toyItemToCsv(ToyItem toyItem) {
		StringBuilder sb = new StringBuilder()
				.append(toyItem.getToyItemId())
				.append(CSV_SEP).append(toyItem.getName().replace(CSV_SEP_REPLACEMENT, "").replace(CSV_SEP, CSV_SEP_REPLACEMENT))
				.append(CSV_SEP).append(toyItem.getWeight())
				.append(CSV_SEP).append(toyItem.getQuantity());
		return sb.toString();
	}

	private static Optional<ToyItem> csvToToyItem(String csv) {
		var fields = csv.strip().split(CSV_SEP);
		if (fields.length != TOY_ITEM_N_FIELDS) {
			return Optional.empty();
		}

		try {
			// 0 id;1 name;2 weight;3 qty
			int id = Integer.parseInt(fields[0]);
			String name = fields[1].replace(CSV_SEP_REPLACEMENT, CSV_SEP);
			int weight = Integer.parseInt(fields[2]);
			int qty = Integer.parseInt(fields[3]);
			ToyItem toyItem = new ToyItem(id, name, weight, qty);
			return Optional.of(toyItem);

		} catch (Exception e) {
			// throw new IllegalArgumentException(csv, e);
		}
		return Optional.empty();
	}

}

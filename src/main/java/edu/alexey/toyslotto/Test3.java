package edu.alexey.toyslotto;

import java.io.IOException;

import edu.alexey.toyslotto.domain.db.implementations.CsvDbContext;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class Test3 {
	public static void main(String[] args) throws IOException {

		var csvDb = new CsvDbContext(".data/test_prize_pool.csv");
		var prizePool = csvDb.prizePool();

		prizePool.add(new ToyItem(null, "Футбольный мяч", 70, 100));
		prizePool.add(new ToyItem(0, "Модель автомобиля\"Москвич\"", 20, 16));
		prizePool.add(new ToyItem(10, "Модель пассажирского самолёта", 20, 25));

		var all = prizePool.queryAll();

		all.forEachOrdered(System.out::println);
	}
}

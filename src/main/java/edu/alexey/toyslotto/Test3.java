package edu.alexey.toyslotto;

import java.io.IOException;

import edu.alexey.toyslotto.domain.db.implementations.CsvDbContext;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class Test3 {
	public static void main(String[] args) throws IOException {

		var csvDb = new CsvDbContext(".data/test_prize_pool.csv");
		var prizePool = csvDb.prizePool();

		// prizePool.add(new ToyItem(null, "Футбольный мяч", 70, 100));
		// prizePool.add(new ToyItem(0, "Модель автомобиля \"Москвич\"", 20, 16));
		// prizePool.add(new ToyItem(10, "Модель пассажирского самолёта", 20, 25));

		var all = prizePool.queryAll();

		all.forEachOrdered(System.out::println);

		System.out.println("-".repeat(60));

		var sec = prizePool.get(2);
		System.out.println(sec);

		System.out.println("-".repeat(60));

		//prizePool.delete(5);
		prizePool.update(new ToyItem(10, "ФУТБОЛЬНЫЙ МЯЧ", 42, 456));
	}
}
// 1;Кукла Маша;5;50
// 2;Кукла Даша;8;50
// 3;Плюшевый мишка Миша;10;60
// 4;Футбольный мяч;70;100
// 6;Модель пассажирского самолёта;20;25
// 7;Футбольный мяч;70;100
// 8;Модель автомобиля "Москвич";20;16
// 9;Модель пассажирского самолёта;20;25
// 10;Футбольный мяч;70;100
// 11;Модель автомобиля "Москвич";20;16
// 12;Модель пассажирского самолёта;20;25
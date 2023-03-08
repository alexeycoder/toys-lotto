package edu.alexey.toyslotto;

import java.util.Locale;

import edu.alexey.toyslotto.client.controllers.Controller;
import edu.alexey.toyslotto.client.views.ConsoleView;
import edu.alexey.toyslotto.client.views.View;
import edu.alexey.toyslotto.domain.business.implementations.DataManagerImpl;
import edu.alexey.toyslotto.domain.business.interfaces.DataManager;
import edu.alexey.toyslotto.domain.db.implementations.CsvDbContext;
import edu.alexey.toyslotto.domain.db.interfaces.DbContext;

public class App {
	public static void main(String[] args) {

		Locale.setDefault(AppSettings.LOCALE);

		// interfaces: DbContext -> DataManager, View
		View view = new ConsoleView();
		DataManager dataManager;

		try {
			DbContext dbContext = new CsvDbContext(AppSettings.PATH_TO_CSV);
			dataManager = new DataManagerImpl(dbContext);

		} catch (Exception e) {
			view.show(
					"Произошла непредвиденная ошибка во время инициализации CSV-базы данных. Приложение будет закрыто.\n");
			view.show("Подробности об ошибке:\n");
			view.show(e.getLocalizedMessage());
			view.show(System.lineSeparator());
			return;
		}

		try {
			Controller.createAndRun(dataManager, view);

		} catch (Exception e) {
			view.show("Произошла непредвиденная ошибка во время работы приложения. Приложение будет закрыто.\n");
			view.show("Подробности об ошибке:\n");
			view.show(e.getLocalizedMessage());
			view.show(System.lineSeparator());
		}
	}
}

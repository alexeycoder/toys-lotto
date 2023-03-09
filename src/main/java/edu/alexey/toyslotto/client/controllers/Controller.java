package edu.alexey.toyslotto.client.controllers;

import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;

import edu.alexey.toyslotto.client.uielements.Menu;
import edu.alexey.toyslotto.client.uielements.MenuItem;
import edu.alexey.toyslotto.client.viewmodels.ViewModelBase;
import edu.alexey.toyslotto.client.views.View;
import edu.alexey.toyslotto.domain.business.interfaces.DataManager;
import edu.alexey.toyslotto.domain.entities.ToyItem;

public class Controller {
	// inner types
	public static record ReturnStatus(boolean exit) {
	}

	// fabric
	public static void createAndRun(DataManager dataManager, View view) {
		var controller = new Controller(dataManager, view);
		controller.runLifecycle();
	}

	// const
	public static final Set<String> CMD_GO_BACK = Set.of("0");
	public static final Set<String> CMD_EXIT = Set.of("й", "q");
	public static final String MENU_MAKE_YOUR_CHOICE = "Выберите пункт меню: ";
	public static final String GOODBYE = "Вы завершили программу.\nСпасибо что пользуетесь Лотереей Игрушек!\n";
	public static final String SHORT_HR = "\u2014\n";
	public static final String LOGO = "\u2684 \u001b[1;3;97mЛотерея Игрушек\u001b[0m \u2684\n";
	public static final String RULES = "Правила:"
			+ "\nРозыгрыш игрушек проводится среди заданного количества участников,"
			+ "\nпо одному призу на участника."
			+ "\nВероятность выпадения той или иной игрушки определяется отношением"
			+ "\nеё условного веса суммарному весу всех игрушек в наличии."
			+ "\nВсякое наименование игрушек участвует в розыгрыше пока имеется"
			+ "\nв наличии хотя бы один экземпляр данного наименования.\n";

	public final Menu MAIN_MENU = new Menu(
			"Главное меню",
			Map.of(
					Set.of("1"), new MenuItem(1, "Призовой фонд ...", this::prizePoolLifecycle),
					Set.of("2"), new MenuItem(2, "Провести лотерею", this::conductLottery),
					Set.of(" "), new MenuItem(90, null, null),
					CMD_EXIT, new MenuItem(99, "Завершить работу", null)));

	public final Menu PRIZE_POOL_MENU = new Menu(
			"Призовой фонд",
			Map.of(
					Set.of("1"), new MenuItem(1, "Вывести список игрушек", this::showPrizePool),
					Set.of("2"), new MenuItem(2, "Найти по наименованию", this::findToyItemsByName),
					Set.of("3"), new MenuItem(3, "Найти по идентификатору", this::findToyItemById),
					Set.of("4"), new MenuItem(4, "Добавить", this::addToyItem),
					Set.of("5"), new MenuItem(5, "Редактировать", this::editToyItem),
					Set.of("6"), new MenuItem(6, "Удалить", this::deleteToyItem),
					Set.of(" "), new MenuItem(90, null, null),
					CMD_GO_BACK, new MenuItem(91, "Вернуться в предыдущее меню", null),
					CMD_EXIT, new MenuItem(99, "Завершить работу", null)));

	// fields
	private final View view;
	private final DataManager data;

	// ctor
	protected Controller(DataManager dataManager, View view) {
		this.data = dataManager;
		this.view = view;
	}

	private void runLifecycle() {
		menuLifecycle(MAIN_MENU);
	}

	// base lifecycle runners

	private ReturnStatus menuLifecycle(Menu menu, boolean onetime, boolean clear) {

		final var menuViewModel = ViewModelBase.of(menu);
		final var menuMap = menu.items();

		while (true) {
			if (clear) {
				view.clear();
			}
			view.show(LOGO);
			view.show(menuViewModel);
			var userChoice = view.askUserChoice(MENU_MAKE_YOUR_CHOICE, menuMap.keySet());
			view.show(SHORT_HR);

			if (userChoice.equals(CMD_EXIT)) {
				view.show(GOODBYE);
				return new ReturnStatus(true);
			} else if (userChoice.equals(CMD_GO_BACK)) {
				return new ReturnStatus(false);
			}

			var menuItem = menuMap.get(userChoice);
			var handler = menuItem.handler();
			if (handler != null) {
				if (handler.apply(null) instanceof ReturnStatus rs) {
					if (rs.exit()) {
						return rs;
					}
				}
			}

			if (onetime) {
				return new ReturnStatus(false);
			}
		}
	}

	private ReturnStatus menuLifecycle(Menu menu) {
		return menuLifecycle(menu, false, true);
	}

	// particular lifecycle runners

	private ReturnStatus prizePoolLifecycle(Object nothing) {
		return menuLifecycle(PRIZE_POOL_MENU);
	}

	// handlers

	// private ReturnStatus dummyHandler(Object nothing) {
	// view.show("Скоро, но не сейчас...\n"
	// + "Данная функция будет доступна в следующей версии.");
	// view.waitToProceed();
	// return new ReturnStatus(false);
	// }

	private ReturnStatus showPrizePool(Object nothing) {
		var prizePoolRepository = data.prizePoolRepository();
		var toyItems = prizePoolRepository.getAllToyItems();

		view.clear();
		view.show("Призовой фонд \u2014 Список игрушек\n");
		view.show(ViewModelBase.emptySpace(1));
		view.show(ViewModelBase.of(toyItems));
		view.show(SHORT_HR);
		view.waitToProceed();
		return new ReturnStatus(false);
	}

	private ReturnStatus findToyItemById(Object nothing) {
		var rs = new ReturnStatus(false);

		view.clear();
		view.show("Призовой фонд \u2014 Поиск позиции по ID\n");

		do {
			view.show(ViewModelBase.emptySpace(1));
			ToyItem toyItem = askToyItemById("Введите ID позиции (или пустой Ввод чтобы отменить): ");
			if (toyItem == null) {
				break;
			}

			view.show("Найдена запись:\n");
			view.show(ViewModelBase.of(toyItem));
			view.show(SHORT_HR);

		} while (view.askYesNo("Повторить поиск (Д/н) (Y/n)? ", true));

		view.waitToProceed();
		return rs;
	}

	private ToyItem askToyItemById(String prompt) {
		ToyItem toyItem = null;
		int id;
		do {
			OptionalInt answer = view.askInteger(prompt, 1, null);
			if (answer.isEmpty()) {
				return null;
			}
			id = answer.getAsInt();
			var prizePoolRepository = data.prizePoolRepository();
			toyItem = prizePoolRepository.getToyItemById(id);
		} while (toyItem == null
				&& view.askYesNo(String.format("Записи с ID %d не найдено.\nПовторить поиск (Д/н) (Y/n)? ", id), true));

		return toyItem;
	}

	private ReturnStatus findToyItemsByName(Object nothing) {
		var rs = new ReturnStatus(false);

		view.clear();
		view.show("Призовой фонд \u2014 Поиск позиций по наименованию\n");
		do {
			view.show(ViewModelBase.emptySpace(1));
			var answer = view.askString(
					"Введите наименование игрушки, частично или полностью"
							+ " (пустой Ввод для отмены):\n",
					null, null);
			if (answer.isEmpty()) {
				break;
			}

			String nameSample = answer.get();
			var prizePoolRepository = data.prizePoolRepository();
			var toyItems = prizePoolRepository.getToyItemsByName(nameSample);

			view.show(ViewModelBase.emptySpace(1));

			if (toyItems.isEmpty()) {
				view.show(String.format("Записей по образцу '%s' не найдено.\n", nameSample));
			} else {
				view.show(String.format("Найдено записей: %d\n", toyItems.size()));
				view.show(ViewModelBase.emptySpace(1));
				view.show(ViewModelBase.of(toyItems));
			}

			view.show(SHORT_HR);

		} while (view.askYesNo("Повторить поиск (Д/н) (Y/n)? ", true));

		view.waitToProceed();
		return rs;
	}

	private ReturnStatus addToyItem(Object nothing) {
		var rs = new ReturnStatus(false);

		view.clear();
		view.show("Призовой фонд \u2014 Добавление позиции\n");

		do {
			view.show(ViewModelBase.emptySpace(1));
			var nameOpt = view.askString(
					"Введите наименование игрушки (пустой Ввод для отмены):\n",
					null, null);
			if (nameOpt.isEmpty()) {
				view.show("Добавление отменено.\n");
				break;
			}

			var weightOpt = view.askInteger(
					"Введите вес \u2014 условную частоту выпадения 0..100 (пустой Ввод для отмены):\n",
					ToyItem.MIN_WEIGHT, ToyItem.MAX_WEIGHT);
			if (weightOpt.isEmpty()) {
				view.show("Добавление отменено.\n");
				break;
			}

			var qtyOpt = view.askInteger("Введите количество в наличии (пустой Ввод для отмены):\n",
					0, null);
			if (qtyOpt.isEmpty()) {
				view.show("Добавление отменено.\n");
				break;
			}

			ToyItem toyItemToAdd = new ToyItem(null, nameOpt.get(), weightOpt.getAsInt(), qtyOpt.getAsInt());
			var prizePoolRepository = data.prizePoolRepository();
			ToyItem addedToyItem = prizePoolRepository.addToyItem(toyItemToAdd);

			view.show(ViewModelBase.emptySpace(1));
			if (addedToyItem == null) {
				view.show("Что-то пошло не так. Не удалось добавить позицию.");
			} else {
				view.show("Успешно добавлена позиция:\n");
				view.show(ViewModelBase.emptySpace(1));
				view.show(ViewModelBase.of(addedToyItem));
			}

			view.show(SHORT_HR);

		} while (view.askYesNo("Добавить ещё позицию (Д/н) (Y/n)? ", true));

		view.waitToProceed();
		return rs;
	}

	private ReturnStatus editToyItem(Object nothing) {
		var rs = new ReturnStatus(false);

		view.clear();
		view.show("Призовой фонд \u2014 Редактирование позиции\n");

		do {
			view.show(ViewModelBase.emptySpace(1));

			ToyItem toyItem = askToyItemById(
					"Введите ID позиции, которую необходимо редактировать (или пустой Ввод чтобы отменить): ");
			if (toyItem == null) {
				view.show("Редактирование отменено.\n");
				break;
			}

			view.show("Найдена запись:\n");
			view.show(ViewModelBase.of(toyItem));

			boolean anyChanges = false;

			var nameOpt = view.askString(
					"Введите новое наименование (или пустой Ввод чтобы оставить прежнее):\n",
					null, null);
			if (nameOpt.isPresent()) {
				toyItem.setName(nameOpt.get());
				anyChanges = true;
			}

			var weightOpt = view.askInteger(
					"Введите новую условную частоту выпадения 0..100 (или пустой Ввод чтобы не менять):\n",
					ToyItem.MIN_WEIGHT, ToyItem.MAX_WEIGHT);
			if (weightOpt.isPresent()) {
				toyItem.setWeight(weightOpt.getAsInt());
				anyChanges = true;
			}

			var qtyOpt = view.askInteger("Введите количество в наличии (или пустой Ввод чтобы не менять):\n",
					0, null);
			if (qtyOpt.isPresent()) {
				toyItem.setQuantity(qtyOpt.getAsInt());
				anyChanges = true;
			}

			if (!anyChanges) {
				view.show("Вы не внесли никаких изменений.\n");
				continue;
			}

			if (view.askYesNo("Сохранить указанные изменения (Д/н) (Y/n)? ", true)) {

				var prizePoolRepository = data.prizePoolRepository();
				view.show(ViewModelBase.emptySpace(1));

				if (prizePoolRepository.updateToyItem(toyItem)) {

					view.show("Позиция успешно изменена:\n");
					view.show(ViewModelBase.emptySpace(1));
					view.show(ViewModelBase.of(toyItem));

				} else {
					view.show("Что-то пошло не так. Не удалось сохранить изменения.");
				}
			} else {
				view.show("Изменения отменены.\n");
				continue;
			}

			view.show(SHORT_HR);

		} while (view.askYesNo("Редактировать другую позицию (Д/н) (Y/n)? ", true));

		view.waitToProceed();
		return rs;
	}

	private ReturnStatus deleteToyItem(Object nothing) {
		var rs = new ReturnStatus(false);

		view.clear();
		view.show("Призовой фонд \u2014 Удаление позиции\n");

		do {
			view.show(ViewModelBase.emptySpace(1));

			ToyItem toyItem = askToyItemById(
					"Введите ID позиции, которую необходимо удалить (или пустой Ввод чтобы отменить): ");
			if (toyItem == null) {
				view.show("Удаление отменено.\n");
				break;
			}

			view.show("Найдена запись:\n");
			view.show(ViewModelBase.of(toyItem));

			if (view.askYesNo("Удалить (Д/н) (Y/n)? ", true)) {

				var prizePoolRepository = data.prizePoolRepository();
				view.show(ViewModelBase.emptySpace(1));

				if (prizePoolRepository.deleteToyItem(toyItem.getToyItemId())) {

					view.show("Позиция успешно удалена:\n");
					view.show(ViewModelBase.emptySpace(1));

				} else {
					view.show("Что-то пошло не так. Не удалось произвести удаление.");
				}
			} else {
				view.show("Удаление отменено.\n");
				continue;
			}

			view.show(SHORT_HR);

		} while (view.askYesNo("Удалить другую позицию (Д/н) (Y/n)? ", true));

		view.waitToProceed();
		return rs;
	}

	private ReturnStatus conductLottery(Object nothing) {
		var rs = new ReturnStatus(false);

		var prizePoolRepository = data.prizePoolRepository();

		view.clear();
		view.show(LOGO);
		view.show(ViewModelBase.emptySpace(1));
		view.show("Проведение Розыгрыша\n");
		view.show(ViewModelBase.emptySpace(1));
		view.show(RULES);
		view.show(ViewModelBase.emptySpace(1));

		int nAvailableToys = prizePoolRepository.countToysAvailable();
		if (nAvailableToys == 0) {
			view.show("Нет ни одной игрушки в наличии. Проведение лотереи невозможно!\n");
			view.show(ViewModelBase.emptySpace(1));
			view.waitToProceed();
			return rs;
		}

		view.show(String.format("Всего игрушек всех наименований в наличии: %d\n", nAvailableToys));
		view.show(ViewModelBase.emptySpace(1));

		var numOfParticipants = view.askInteger(
				String.format("Введите количество участников, не более %d (пустой Ввод для отмены):\n", nAvailableToys),
				1, nAvailableToys);
		if (numOfParticipants.isEmpty()) {
			view.show("Лотерея отменена.\n");
			view.show(ViewModelBase.emptySpace(1));
			view.waitToProceed();
			return rs;
		}

		view.show("Внимание! Результат розыгрыша:\n");
		view.show(ViewModelBase.emptySpace(1));

		var lottoResult = prizePoolRepository.conductLottery(numOfParticipants.getAsInt());
		view.show(ViewModelBase.lotteryResultModelView(lottoResult));

		view.show(SHORT_HR);

		var answer = view.askYesNo("Выберите:\n"
				+ "\tД (Y) \u2014 если желаете выдать призы (позиции будут изъяты из базы), либо\n"
				+ "\tн (n) \u2014 если желаете отменить результаты лотереи:\n", true);

		view.show(ViewModelBase.emptySpace(1));
		if (answer) {
			for (ToyItem toyItem : lottoResult) {
				prizePoolRepository.updateToyItem(toyItem);
			}
			view.show("Спасибо за ваш выбор. Призы успешно выданы.");
		} else {
			view.show("Спасибо за ваш выбор. Результаты лотереи аннулированы.");
		}

		view.show(ViewModelBase.emptySpace(1));
		view.waitToProceed();

		return rs;
	}
}

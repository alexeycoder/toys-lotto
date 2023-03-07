package edu.alexey.toyslotto.client.controllers;

import java.util.Map;
import java.util.Set;

import edu.alexey.toyslotto.client.uielements.MenuItem;
import edu.alexey.toyslotto.client.viewmodels.ViewModelBase;
import edu.alexey.toyslotto.client.views.ConsoleView;
import edu.alexey.toyslotto.client.views.View;
import edu.alexey.toyslotto.client.uielements.Menu;

public class Controller {
	// inner types
	public static record ReturnStatus(boolean exit) {
	}

	// fabric
	public static void createAndRun() {
		var controller = new Controller(new ConsoleView());
		controller.runLifecycle();
	}

	// const
	public static final Set<String> CMD_GO_BACK = Set.of("0");
	public static final Set<String> CMD_EXIT = Set.of("й", "q");
	public static final String MENU_MAKE_YOUR_CHOICE = "Выберите пункт меню: ";
	public static final String GOODBYE = "Вы завершили программу.\nСпасибо что пользуетесь Лотереей Игрушек!\n";
	public static final String SHORT_HR = "\u2014\n";
	public static final String LOGO = "\u2684 \u001b[1;3;97mЛотерея Игрушек\u001b[0m \u2684\n";

	public final Menu MAIN_MENU = new Menu(
			"Главное меню",
			Map.of(
					Set.of("1"), new MenuItem(1, "Призовой фонд ...", this::prizePoolLifecycle),
					Set.of("2"), new MenuItem(2, "Устроить розыгрыш", null),
					Set.of(" "), new MenuItem(90, null, null),
					CMD_EXIT, new MenuItem(99, "Завершить работу", null)));

	public final Menu PRIZE_POOL_MENU = new Menu(
			"Призовой фонд",
			Map.of(
					Set.of("1"), new MenuItem(1, "Вывести список игрушек", this::dummyHandler),
					Set.of("2"), new MenuItem(2, "Найти по наименованию", null),
					Set.of("3"), new MenuItem(3, "Найти по идентификатору", null),
					Set.of("4"), new MenuItem(4, "Добавить", null),
					Set.of("5"), new MenuItem(5, "Редактировать", null),
					Set.of("6"), new MenuItem(6, "Удалить", null),
					Set.of(" "), new MenuItem(90, null, null),
					CMD_GO_BACK, new MenuItem(91, "Вернуться в предыдущее меню", null),
					CMD_EXIT, new MenuItem(99, "Завершить работу", null)));

	// fields
	private final View view;

	// ctor
	protected Controller(View viewManager) {
		this.view = viewManager;
	}

	private void runLifecycle() {
		menuLifecycle(MAIN_MENU);
	}

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

	private ReturnStatus dummyHandler(Object nothing) {
		view.show("Скоро, но не сейчас...\n"
				+ "Данная функция будет доступна в следующей версии.");
		view.waitToProceed();
		return new ReturnStatus(false);
	}

	private ReturnStatus prizePoolLifecycle(Object nothing) {
		return menuLifecycle(PRIZE_POOL_MENU);
	}

	private ReturnStatus menuLifecycle(Menu menu) {
		return menuLifecycle(menu, false, true);
	}

	// TODO: Remove after tests
	public static void main(String[] args) {
		Controller.createAndRun();
	}

}

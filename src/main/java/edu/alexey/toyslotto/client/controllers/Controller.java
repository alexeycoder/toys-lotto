package edu.alexey.toyslotto.client.controllers;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.alexey.toyslotto.client.uielements.MenuItem;
import edu.alexey.toyslotto.client.viewmodels.MenuViewModel;
import edu.alexey.toyslotto.client.uielements.Menu;

public class Controller {

	public static final Set<String> CMD_GO_BACK = Set.of("0");
	public static final Set<String> CMD_EXIT = Set.of("й", "q");
	public static final String MENU_MAKE_YOUR_CHOICE = "Выберите пункт меню: ";
	public static final String GOODBYE = "Вы завершили программу.\nСпасибо что пользуетесь Notesimply!";
	public static final String SHORT_HR = "\u2014";

	public static final Menu MAIN_MENU = new Menu(
			"ЛОТЕРЕЯ ИГРУШЕК: Главное меню",
			Map.of(
					Set.of("1"), new MenuItem(1, "Призовой фонд игрушек", null),
					Set.of("2"), new MenuItem(2, "Устроить розыгрыш", null),
					Set.of(" "), new MenuItem(3, null, null),
					CMD_EXIT, new MenuItem(99, "Завершить работу", null)));

	public void runLifecycle() {

	}
	
	private void menuLifecycle(Menu menu) {
		var menuViewModel = new MenuViewModel(menu);
	}

	public static void main(String[] args) {
		System.out.println(new MenuViewModel(MAIN_MENU));
	}
}

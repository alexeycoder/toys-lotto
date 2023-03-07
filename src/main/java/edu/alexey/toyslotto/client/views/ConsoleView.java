package edu.alexey.toyslotto.client.views;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

import edu.alexey.toyslotto.AppSettings;
import edu.alexey.toyslotto.client.viewmodels.ViewModelBase;

public class ConsoleView implements View {

	protected static final Scanner SCANNER = new Scanner(System.in, AppSettings.CHARSET);

	public static final String PLEASE_REPEAT = "Пожалуйста попробуйте снова.";
	private static final String PROMPT_ENTER = "Нажмите Ввод чтобы продолжить...";
	private static final String WARN_WRONG_MENU_ITEM = "Некорректный ввод: требуется выбрать пункт меню. "
			+ PLEASE_REPEAT;

	@Override
	public void clear() {
		System.out.printf("\u001b[1;1H\u001b[2J");
	}

	@Override
	public void waitToProceed() {
		System.out.println(PROMPT_ENTER);
		SCANNER.nextLine();
	}

	@Override
	public void show(String text) {
		System.out.print(text);
	}

	@Override
	public void show(ViewModelBase viewModel) {
		System.out.print(viewModel);
	}

	@Override
	public void showList(List<? extends ViewModelBase> viewModelsList, String title) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'showList'");
	}

	@Override
	public boolean askYesNo(String prompt, boolean isYesDefault) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'askYesNo'");
	}

	@Override
	public Set<String> askUserChoice(String prompt, Set<Set<String>> options) {
		assert options != null && !options.isEmpty();

		boolean outOfRange = false;

		while (true) {
			if (outOfRange) {
				outOfRange = false;
				printError(WARN_WRONG_MENU_ITEM);
			}

			System.out.print(prompt);
			String rawInp = SCANNER.nextLine();
			final String inp = rawInp.strip();

			var resOpt = options.stream().filter(s -> s.contains(inp)).findAny();
			if (resOpt.isPresent()) {
				return resOpt.get();
			}
			outOfRange = true;
		}
	}

	@Override
	public OptionalInt askInteger(String prompt, Integer min, Integer max) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'askInteger'");
	}

	@Override
	public OptionalInt askInteger(String prompt, Function<Integer, Boolean> checkValidity, String wrongWarn) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'askInteger'");
	}

	@Override
	public Optional<String> askString(String prompt, Function<String, Boolean> checkValidity, String wrongWarn) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException("Unimplemented method 'askString'");
	}

	// aux

	private static void printError(String errorMessage) {
		System.err.println(errorMessage);
	}

	private static Integer tryParseInt(String str) {
		try {
			return Integer.parseInt(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}

}

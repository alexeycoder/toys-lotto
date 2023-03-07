package edu.alexey.toyslotto.client.views;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Function;

import edu.alexey.toyslotto.client.uielements.Menu;
import edu.alexey.toyslotto.client.viewmodels.ViewModelBase;

public interface View {

	void clear();

	void waitToProceed();

	void show(String text);

	void show(ViewModelBase viewModel);

	void showList(List<? extends ViewModelBase> viewModelsList, String title);

	boolean askYesNo(String prompt, boolean isYesDefault);

	/**
	 * Запрашивает выбор пункта меню от пользователя.
	 * 
	 * @param prompt    Приглашение ввода ответа, типа "Выберите пункт меню: "
	 * @param menuModel Модель представления меню. Используется здесь для проверки
	 *                  соответствия ввода пользователя пунктам, определённым в
	 *                  модели меню. Если введённый пользователем ответ не
	 *                  предусмотрен меню, то необходимо показать пользователю
	 *                  предупреждение и запросить повторный ввод.
	 * @return Выбор пользователя: ключ из модели представления меню.
	 */
	Set<String> askUserChoice(String prompt, Set<Set<String>> options);

	OptionalInt askInteger(String prompt, Integer min, Integer max);

	OptionalInt askInteger(String prompt, Function<Integer, Boolean> checkValidity, String wrongWarn);

	Optional<String> askString(String prompt, Function<String, Boolean> checkValidity, String wrongWarn);
}

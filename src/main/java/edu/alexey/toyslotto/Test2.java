package edu.alexey.toyslotto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Test2 {
	public static void main(String[] args) {
		var s = Set.of("a", "W", "я");
		var lst = s.stream().sorted().toList();
		var str = String.format("%s (%s)", lst.get(0),
				lst.stream().skip(1).collect(Collectors.joining(", ")));

		System.out.println(str);

		List<String> list = List.of("abc", "123456", "12", "123456789");
		var res = list.stream().mapToInt(String::length).max().getAsInt();
		System.out.println(res);
	}
}

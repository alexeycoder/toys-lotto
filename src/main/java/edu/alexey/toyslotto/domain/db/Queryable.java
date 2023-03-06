package edu.alexey.toyslotto.domain.db;

import java.util.stream.Stream;

public interface Queryable<T> {
	T add(T entry);

	Stream<T> queryAll();

	T get(int id);

	boolean update(T entry);

	T delete(int id);
}

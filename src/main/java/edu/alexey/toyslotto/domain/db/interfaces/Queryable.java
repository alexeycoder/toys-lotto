package edu.alexey.toyslotto.domain.db.interfaces;

import java.util.List;

public interface Queryable<T> {
	T add(T entry);

	List<T> queryAll();

	T get(int id);

	boolean update(T entry);

	T delete(int id);
}

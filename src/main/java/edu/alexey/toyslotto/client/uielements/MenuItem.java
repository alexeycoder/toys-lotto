package edu.alexey.toyslotto.client.uielements;

import java.util.function.Consumer;
import java.util.function.Function;

public record MenuItem(int order, String name, Function<?,?> handler) {
}
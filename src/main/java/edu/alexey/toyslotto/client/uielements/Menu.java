package edu.alexey.toyslotto.client.uielements;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public record Menu(String header, Map<Set<String>, MenuItem> items) {
}
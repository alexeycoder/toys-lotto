package edu.alexey.toyslotto.domain.entities;

public class ToyItem {
	private Integer toyItemId;
	private String name;
	private int quantity;
	private int weight;

	public Integer getToyItemId() {
		return toyItemId;
	}

	public void setToyItemId(Integer toyItemId) {
		this.toyItemId = toyItemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets quantity of toys of this kind.
	 * @param quantity Must be greater than 0.
	 */
	public void setQuantity(int quantity) {
		if (quantity < 0) {
			throw new IllegalArgumentException();
		}
		this.quantity = quantity;
	}

	public int getWeight() {
		return weight;
	}

	/**
	 * Sets toy drop relative rate.
	 * @param weight Must be in range 0-100.
	 */
	public void setWeight(int weight) {
		if (weight < 0 || weight > 100) {
			throw new IllegalArgumentException();
		}
		this.weight = weight;
	}
}

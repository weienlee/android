package com.weienlee.set;

public class Card {
	public enum Color {RED, PURPLE, GREEN};
	public enum Shape {DIAMOND, ROUND, SQUIGGLE};
	public enum Shading {EMPTY, STRIPE, FILLED};

	private final Integer number;
	private final Color color;
	private final Shape shape;
	private final Shading shading;
	
	/**
	 * Initializes Card object with the given input attributes
	 * 
	 * @param inputNumber
	 * @param inputColor
	 * @param inputShape
	 * @param inputShading
	 */
	public Card(Integer inputNumber, Color inputColor, Shape inputShape, Shading inputShading) {
		this.number = inputNumber;
		this.color = inputColor;
		this.shape = inputShape;
		this.shading = inputShading;
	}
	
	/**
	 * Gets the number of the card
	 * 
	 * @return this.number
	 */
	public Integer getNumber() {
		return this.number;
	}
	
	/**
	 * Gets the color of the card
	 * 
	 * @return this.color
	 */
	public Color getColor() {
		return this.color;
	}
	
	/**
	 * Gets the shape of the card
	 * 
	 * @return this.shape
	 */
	public Shape getShape() {
		return this.shape;
	}
	
	/** 
	 * Gets the shading of the card
	 * 
	 * @return this.shading
	 */
	public Shading getShading() {
		return this.shading;
	}
	
	@Override
	public String toString() {
		return this.number.toString() + " " +
				this.color + " " +
				this.shape + " " +
				this.shading;
	}
}

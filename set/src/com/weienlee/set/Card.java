package com.weienlee.set;

public class Card {
	public enum Color {RED, PURPLE, GREEN};
	public enum Shape {DIAMOND, ROUND, SQUIGGLE};
	public enum Shading {EMPTY, STRIPE, FILLED};

	private final int number;
	private final int color;
	private final int shape;
	private final int shading;
	private final int bits;
	
	/**
	 * Initializes Card object with the given input attributes
	 * 
	 * @param inputNumber
	 * @param inputColor
	 * @param inputShape
	 * @param inputShading
	 */
	public Card(int inputNumber, int inputColor, int inputShape, int inputShading) {
		this.number = inputNumber;
		this.color = inputColor;
		this.shape = inputShape;
		this.shading = inputShading;
		this.bits = (inputNumber << 6) + 
				(inputColor << 4) + 
				(inputShape << 2) + 
				(inputShading);
	}
	
	/**
	 * Gets the number of the card
	 * 
	 * @return this.number
	 */
	public int getNumber() {
		return this.number;
	}
	
	/**
	 * Gets the color of the card
	 * 
	 * @return this.color
	 */
	public int getColor() {
		return this.color;
	}
	
	/**
	 * Gets the shape of the card
	 * 
	 * @return this.shape
	 */
	public int getShape() {
		return this.shape;
	}
	
	/** 
	 * Gets the shading of the card
	 * 
	 * @return this.shading
	 */
	public int getShading() {
		return this.shading;
	}
	
	/**
	 * Gets the binary representation of the card
	 * 
	 * @return
	 */
	public int getBits() {
		return this.bits;
	}
	
	@Override
	public String toString() {
		return this.number + " " +
				this.color + " " +
				this.shape + " " +
				this.shading;
	}
}

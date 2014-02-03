package com.weienlee.set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.weienlee.set.Card.Color;
import com.weienlee.set.Card.Shape;
import com.weienlee.set.Card.Shading;


public class Deck {
	private List<Card> cards = new ArrayList<Card>();
	
	public Deck() {
		// populate Deck
		for (Integer number=1;number<=3;number++) {
			for (Color color : Color.values()) {
				for (Shape shape : Shape.values()){
					for (Shading shading : Shading.values()){
						cards.add(new Card(number, color, shape, shading));
					}
				}
			}
		}
		
		// shuffle Deck
		Collections.shuffle(cards);
	}
	
	/**
	 * Gets a list of cards in the deck
	 * 
	 * @return this.cards
	 */
	public List<Card> getCards() {
		return this.cards;
	}
	
	public static void main(String[] args) {
		Deck deck = new Deck();
		for (Card card : deck.getCards()){
			System.out.println(card);
		}
	}
}

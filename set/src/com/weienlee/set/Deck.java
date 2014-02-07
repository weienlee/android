package com.weienlee.set;


import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class Deck {
	private List<Card> cards = new LinkedList<Card>();
	
	public Deck() {
		// populate Deck
		
		for (int number=0; number<=2; number++) {
			for (int color=0; color<=2; color++) {
				for (int shape=0; shape<=2; shape++) {
					for (int shading=0; shading<=2; shading++) {
						cards.add(new Card(number, color, shape, shading));
					}
				}
			}
		}
		

		// 20 cards that don't make up a set, with one in the middle that does
		/*
		cards.add(new Card(0, 0, 1, 1));	// 1 green round filled
		cards.add(new Card(1, 0, 1, 1));	// 2 green round filled
		cards.add(new Card(2, 0, 2, 1));	// 3 green squiggle filled
		cards.add(new Card(2, 1, 2, 2));	// 3 purple squiggle stripe
		cards.add(new Card(2, 1, 2, 0));	// 3 purple squiggle empty
		cards.add(new Card(0, 1, 1, 2));	// 1 purple round stripe
		cards.add(new Card(1, 1, 1, 2));	// 2 purple round stripe
		cards.add(new Card(2, 2, 2, 1));	// 3 red squiggle filled
		cards.add(new Card(1, 1, 2, 1));	// 2 purple squiggle filled
		cards.add(new Card(0, 1, 2, 1));	// 1 purple squiggle filled
		cards.add(new Card(0, 1, 1, 1));	// 1 purple round filled
		cards.add(new Card(1, 1, 1, 1));	// 2 purple round filled
		
		// gives a set
		cards.add(new Card(2, 0, 1, 1));	// 3 green round filled
		
		
		cards.add(new Card(0, 0, 1, 2));	// 1 green round striped
		cards.add(new Card(1, 0, 1, 2));	// 2 green round striped
		cards.add(new Card(2, 2, 0, 2));	// 3 red diamond striped
		cards.add(new Card(2, 1, 0, 2));	// 3 purple diamond striped
		cards.add(new Card(2, 0, 0, 1));	// 3 green diamond filled
		cards.add(new Card(2, 0, 0, 0));	// 3 green diamond empty
		cards.add(new Card(0, 0, 0, 2));	// 1 green diamond striped
		cards.add(new Card(2, 0, 0, 2));	// 2 green diamond striped
		*/
		
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

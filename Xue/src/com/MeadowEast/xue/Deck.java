package com.MeadowEast.xue;

import java.util.LinkedList;

public class Deck {
	private LinkedList<CardStatus> cardStatusQueue = new LinkedList<CardStatus>();
	// Get a random deck of the specified size
	public Deck() {}
	public CardStatus get(){
		return cardStatusQueue.poll();
	}
	public void put(CardStatus cs){
		cardStatusQueue.add(cs);
	}
	//Push function to push card back to the front of the list (stacking)
	public void push(CardStatus cs){
		cardStatusQueue.push(cs);
	}
	public boolean isEmpty(){
		return cardStatusQueue.isEmpty();
	}
	public int size(){
		return cardStatusQueue.size();
	}
}

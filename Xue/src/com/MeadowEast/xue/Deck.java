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
	public boolean isEmpty(){
		return cardStatusQueue.isEmpty();
	}
	public int size(){
		return cardStatusQueue.size();
	}
	
	public boolean contains(CardStatus cs){
		return cardStatusQueue.contains(cs);
	}
	
	public void removeDuplicate(CardStatus cs){
		cardStatusQueue.remove(cardStatusQueue.indexOf(cs)); //Remove the cardstatus at index of desired duplicate parameter
	}
	
	public void putFront(CardStatus cs){
		cardStatusQueue.addFirst(cs);
	}
}

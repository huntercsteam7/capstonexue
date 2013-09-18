package com.MeadowEast.xue;

import java.util.LinkedList;

public class Deck {
	private LinkedList<CardStatus> cardStatusQueue = new LinkedList<CardStatus>();
	// Get a random deck of the specified size
	public Deck() {}
	public CardStatus get(){
		return cardStatusQueue.poll();
	}
	//Get last card in the list.
	public CardStatus getLast(){
		return cardStatusQueue.pollLast();
	}
	public void put(CardStatus cs){
		cardStatusQueue.add(cs);
	}
	//Push function to push card back to the front of the list (stacking)
	public void putFront(CardStatus cs){
		cardStatusQueue.addFirst(cs);
	}
	public boolean isEmpty(){
		return cardStatusQueue.isEmpty();
	}
	public int size(){
		return cardStatusQueue.size();
	}
}

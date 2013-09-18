package com.MeadowEast.xue;

public class CardStatus {
	private int index;
	private int level;
	private boolean isInDeck;
	public CardStatus(int index, int level){ //cTor? Set default isInDeck to true (all cards start off in a deck)
		this.level = level;
		this.index = index;
		this.isInDeck = true;
		
	}
	public int getIndex(){
		return index;
	}
	public int getLevel(){
		return level;
	}
	public void wrong(){
		if (level > 0){
			level -= 1;
		}
	}
	public void right(){
		if (level < 4){
			level += 1;
		}
	}
	//To change if the card is or isn't in the deck boolean flag.
	public void setIsInDeck(boolean value){
		this.isInDeck = value;
	}
	//Accessor function to check if card is in deck.
	public boolean isInDeck(){
		return this.isInDeck;
	}
	public String toString(){
		return "CardStatus: index="+index+" level="+level;
	}
}

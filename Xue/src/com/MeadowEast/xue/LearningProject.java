package com.MeadowEast.xue;

import java.io.*;
//import java.text.DateFormat;
//import java.text.ParseException;
import java.util.*;

import android.util.Log;

abstract public class LearningProject {
	
	private String name;
	private int n, seen, removed;
	protected List<IndexSet> indexSets;
	protected Map<Integer, Date> timestamps;
	protected Deck deck;
	protected CardStatus cardStatus = null;
	protected Card card = null;	
	final static String TAG = "CC LearningProject";
	
	public LearningProject(String name, int n) {
		this.n = n;
		this.name = name;
		this.seen = 0;
		this.removed = 0;
		Log.d(TAG, "Creating index sets");
		indexSets = new ArrayList<IndexSet>();
		for (int i=0; i<5; ++i){
			indexSets.add(new IndexSet());
		}
		timestamps = new HashMap<Integer, Date>();
		Log.d(TAG, "Reading status");
		readStatus();
		Log.d(TAG, "Making deck");
		deck = makeDeck(n, 700);
		Log.d(TAG, "Exiting LearningProject constructor");
	}	
	

	// n is the size of the deck
	// target is used to limit the number at Levels 1 and 2, the ones
	//   mainly being learned
 	private Deck makeDeck(int n, int target){
 		Date now = new Date();
		Random r = new Random();
		Deck d = new Deck();
		float[] cutoffs = new float[4];
		// Set cutoffs so that new cards are only introduced so as to maintain a limit
		// of target at Level 1 and 2*target at (Level 1 + Level 2).  We always spend
		// 50 percent of our time on Level 1 (and 7 percent and 3 percent each on Levels
		// 3 and 4).  The remaining 40 percent gets divided between Levels 0 and 2, with
		// Level 0 time approaching zero as the in-play number gets to 2*target
		
		// Note: When Level 1 and Level 2 are both half full, this cuts the time on Level 0
		// to 12.5 percent, which is kind of low.  Easy to fix by setting target higher.
		float factor1 = Math.max(0,  1-indexSets.get(1).size()/(float) target);
		float factor1n2 = Math.max(0, 1-(indexSets.get(1).size()+indexSets.get(2).size())/((float) 2*target));
		cutoffs[0] = .40f * factor1 * factor1n2;
		if (cutoffs[0] < 0) cutoffs[0] = 0f;
		cutoffs[1] = cutoffs[0] + .5f;
		cutoffs[2] = .90f;
		cutoffs[3] = .97f;
		if (indexSets.get(0).size() < n)
			addNewItems(n);
		while (d.size()<n){
			double x = r.nextFloat();
			int level;
			if (x < cutoffs[0]){
				level = 0;
			} else if (x < cutoffs[1]) {
				level = 1;
			} else if (x < cutoffs[2]) {
				level = 2;
			} else if (x < cutoffs[3]) {
				level = 3;
			} else {
				level = 4;
			}
			IndexSet targetSet = indexSets.get(level);
			if (targetSet.size() > 0){
				int index = targetSet.pickOne();
				// add it if it hasn't recently been seen
				long hoursSinceSeen = (now.getTime() - timestamps.get(index).getTime()) / (60 * 60 * 1000);
				if (hoursSinceSeen > 24){
					timestamps.put(index, now);
					d.put(new CardStatus(index, level));
				} else { // if it's too recent, put it back
					indexSets.get(level).add(index);
				}
			}
		}
		return d;
	}
	
	public boolean next() {
		if (deck.isEmpty()) return false;
		cardStatus = deck.get();
		seen++;
		card = AllCards.getCard(cardStatus.getIndex());
		return true;
	}
	
	//Do undo here
	public boolean undo(){
		if(deck.isEmpty()) return false;
		deck.putFront(cardStatus); 		//Puts current card into the front of the deck.
		cardStatus = deck.getLast(); 	//Gets the last card in the list, basically the last card that was put back to the back of the deck.
		seen--;							//Subtract the seen. (Check will be done when calling undo from LearnActivity to make sure seen != 1
		if(!cardStatus.isInDeck()){		//If card was removed from deck, set remove count -1, set isInDeck to true.
			removed--;
			cardStatus.setIsInDeck(true);
		}
		card = AllCards.getCard(cardStatus.getIndex()); //Update the card with what is now the previous card.
		return true;
	}
	//Do remove here, similiar to next but set boolean isInDeck flag to false.
	//THIS IS LAZY DELETION
	public boolean remove(){
		if(deck.isEmpty())return false;
		cardStatus.setIsInDeck(false);		//Lazy delete card from deck
		deck.put(cardStatus);				//Put back to the deck with false isInDeck flag.
		
		cardStatus = deck.get();			//Rest is the same as next() but add removed count to update statusText
		seen++;
		removed++;
		card = AllCards.getCard(cardStatus.getIndex());
		return true;
	}
	
	//Returns how many cards have been seen. (Used as safety check for undo at initial 1st card)
	public int seenTimes(){
		return seen;
	}
	//Check to see if current card is valid in deck
	public boolean isCardInDeck(){
		return cardStatus.isInDeck();
	}
	//Function to cycle cycle to next card. Used with above function to find first valid card in deck
	public boolean cycleCards(){
		if(deck.isEmpty() || cardStatus == null) return false; //If deck is empty or if there is no card to look at.
		deck.put(cardStatus); //Put current card into back.
		cardStatus = deck.get(); //Get new card status
		seen++;					 //Increase seen
		card = AllCards.getCard(cardStatus.getIndex());	//Set new card
		return true;
	}
	//Get total removed card count. If this equal to deck size, end.
	public boolean removedAllCards(){
		return removed == deck.size()+1;
	}
	
	public int currentIndex(){
		if (cardStatus==null)
			return -1;
		return cardStatus.getIndex();
	}
	
	abstract protected String prompt();
	abstract protected String answer();
	abstract protected String other();
	abstract public void addNewItems();
	abstract public void addNewItems(int n);
	
	public void right(){
		cardStatus.right();
		// put it in the appropriate index set
		indexSets.get(cardStatus.getLevel()).add(cardStatus.getIndex());
	}
	
	public void wrong(){
		cardStatus.wrong();
		// return to the deck
		deck.put(cardStatus);		
	}
	
	String deckStatus(){
		String left = ((deck.size()+1)-removed) + " left"; //Total deck size - removed cards left
		return seen > n ? left : seen + " of " + n + " seen, " + left; 
	}
	
	String queueStatus(){
		int [] n = new int[5];
		for (int i=0; i<5; ++i) n[i] = indexSets.get(i).size();
//		return String.format("  %7d  %4d  %4d  %4d  %4d  %7d  %5d  ", n[0], n[1], n[2], n[3], n[4],
//				n[2]+n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
		return String.format("    %d   %d + %d = %d    %d + %d = %d    %d",
				n[0], n[1], n[2], n[1]+n[2], n[3], n[4], n[3]+n[4], n[0]+n[1]+n[2]+n[3]+n[4]);
	}
	
	public void log(String s) throws IOException {
		Log.d(TAG, "Entering log okay");
		boolean append = true;
		File logfilehandle = new File(MainActivity.filesDir, name + ".log.txt");
		Log.d(TAG, "logfilehandle is: " +logfilehandle);
		FileWriter logfile = new FileWriter(logfilehandle, append);
		PrintWriter out = new PrintWriter(logfile);
		Date now = new Date();
		out.printf("%tD %tR %s\n", now, now, s);
		logfile.close();
		Log.d(TAG, "Exiting log okay");
	}
	
	public void writeStatus() throws IOException {
		File statusobjectfile = new File(MainActivity.filesDir, name + ".status.ser");
		FileOutputStream statusobjectFOS = new FileOutputStream(statusobjectfile);
		ObjectOutputStream statusobjectOOS = new ObjectOutputStream(statusobjectFOS);
		
		Log.d(TAG, "writing objects");
		statusobjectOOS.writeObject(timestamps);
		statusobjectOOS.writeObject(indexSets);
		statusobjectFOS.close();
	}
	
	@SuppressWarnings("unchecked")
	private void readStatus() {
		FileInputStream statusobjectFIS;
		ObjectInputStream statusobjectOIS;
		try {
			File statusobjectfile = new File(MainActivity.filesDir, name + ".status.ser");
			statusobjectFIS = new FileInputStream(statusobjectfile);
			statusobjectOIS = new ObjectInputStream(statusobjectFIS);
		} catch (Exception e) {
			Log.d(TAG, "No status file, adding 50 first items from AllCards");
			addNewItems(50);
			return;
		} 
		try {
			timestamps = (Map<Integer, Date>) statusobjectOIS.readObject();
			indexSets = (List<IndexSet>) statusobjectOIS.readObject();
			statusobjectFIS.close();
			Log.d(TAG, "OBJECT status file read without problems");
		} catch (Exception e) { Log.d(TAG, "Error in readStatus"); }
	}

}

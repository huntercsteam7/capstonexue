package com.MeadowEast.xue;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends Activity implements OnGestureListener, Callback {
	static final String TAG = "LearnActivity";
	static final int ECDECKSIZE = 4;
	static final int CEDECKSIZE = 60;

	
	public Chronometer cm;
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status;
	Button advance, okay;

	private SoundManager _soundManager;
	private GestureDetector gestureScanner;
	private static final int SWIPE_MIN_DISTANCE = 120;
	private static final int SWIPE_THRESHOLD_VELOCITY = 150;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        
        Log.d(TAG, "Entering onCreate");

        //Create sound manager and gesture detector below.
        _soundManager = SoundManager.getInstance();
        gestureScanner = new GestureDetector(this);
        
        itemsShown = 0;
        prompt  = (TextView) findViewById(R.id.promptTextView);
        status  = (TextView) findViewById(R.id.statusTextView);
        other   = (TextView) findViewById(R.id.otherTextView);
        answer  = (TextView) findViewById(R.id.answerTextView);
        //advance  = (Button) findViewById(R.id.advanceButton);
        //okay     = (Button) findViewById(R.id.okayButton);
        
        cm = (Chronometer) findViewById(R.id.chronometer1);
        
        
        
    	//findViewById(R.id.advanceButton).setOnClickListener(this);
    	//findViewById(R.id.okayButton).setOnClickListener(this);
    	
        //Setting long click instance for the status text view so user will be able to long click them and send email and get index

    	findViewById(R.id.statusTextView).setOnLongClickListener(new OnLongClickListener(){
    		public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(),"Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
				sendCardNoteEmail();
				return false;
			}
    	});
<<<<<<< HEAD
    	
=======
>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be
    	
    	if (MainActivity.mode.equals("ec")){
    		lp = new EnglishChineseProject(ECDECKSIZE);	
    		other.setTextIsSelectable(true);					//If e-c mode, set other chinese txtview to selectable and add callback.
<<<<<<< HEAD
        	other.setCustomSelectionActionModeCallback(this);   //Set callback to this activity on the event that a selection is detected
=======
        	other.setCustomSelectionActionModeCallback(this);
>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be
    	}
    	else{
    		lp = new ChineseEnglishProject(CEDECKSIZE);
    		prompt.setTextIsSelectable(true);					//If c-e mode chinese text view is in prompt.
<<<<<<< HEAD
        	prompt.setCustomSelectionActionModeCallback(this);  //Same as above
=======
        	prompt.setCustomSelectionActionModeCallback(this);
>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be
    	}
		
    	clearContent();
    	doAdvance();
    }
<<<<<<< HEAD
    
=======
>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    private void doAdvance(){
		if (status.getText().equals("DONE!"))
			try {
				lp.log(lp.queueStatus());
				lp.writeStatus();
				finish();
				return;
				//System.exit(0);
			} catch (IOException e) {
				Log.d(TAG, "couldn't write Status");
				return;
			}
		switch (itemsShown){
		case 0:
			if (lp.next()){
				prompt.setText(lp.prompt());
				status.setText(lp.deckStatus());
				itemsShown++;
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
			break;
		case 1:
			answer.setText(lp.answer());
			itemsShown++;
			break;
		case 2:
			other.setText(lp.other());
			//advance.setText("next");
			itemsShown++;
			break;
		case 3:
			// Got it wrong
			//advance.setText("show");
			lp.wrong();
			lp.next();
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());
			break;
		default:
			//Should not get here.
			Log.d(TAG, "Error: Default switch case reached!");
			break;
		}
	}
    
    /////////////////
    //UNDO FUNCTION//
	/////////////////
    private void doUndo(){
    	lp.undo();
    	clearContent();
    	prompt.setText(lp.prompt());
    	itemsShown = 1;
    	status.setText(lp.deckStatus());
    }
	
	private void clearContent(){
		prompt.setText("");
		answer.setText("");
		other.setText("");
	}
	
	private void doOkay(){
		if (status.getText().equals("DONE!"))
			try {
				lp.log(lp.queueStatus());
				lp.writeStatus();
				finish();
				return;
				//System.exit(0);
			} catch (IOException e) {
				Log.d(TAG, "couldn't write Status");
				return;
			}
		// Do nothing unless answer has been seen
		if (itemsShown < 2) return;
		// Got it right
		lp.right();
		if (lp.next()){
			//advance.setText("show");
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());
			Animation swipeAnimation;
			swipeAnimation = AnimationUtils.loadAnimation(this, R.anim.bottom_to_top_slide);
    		prompt.startAnimation(swipeAnimation);
    		status.startAnimation(swipeAnimation);
		} else {
			//((ViewManager) advance.getParent()).removeView(advance);
			status.setText("DONE!");
			//okay.setText("done");
			clearContent();
		}
	}
/*
 * No need for clicks with swipe gestures.
   
    public void onClick(View v){
    	switch (v.getId()){
    	case R.id.advanceButton:
    		doAdvance();
			break;
    	case R.id.okayButton:
    		doOkay();
			break;
//    	case R.id.promptTextView:
//    	case R.id.answerTextView:
//    	case R.id.otherTextView:
//    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
//    		break;
    	}
    }

    public boolean onLongClick(View v){
    	switch (v.getId()){
    	case R.id.promptTextView:
    	case R.id.answerTextView:
    	case R.id.otherTextView:
    		//Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
    		// Send an email to da Boss
    		sendCardNoteEmail();
    		break;
    	}
    	return true;
    }
*/
    public void sendCardNoteEmail() {
    	
    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
    	emailIntent.setType("plain/text");  
 
    	// Get the current card information and supply it in an email with a note attached
    	String cardEnglish = AllCards.getCard( lp.currentIndex() ).getEnglish();
    	String cardHanzi = AllCards.getCard( lp.currentIndex() ).getHanzi();
    	String getPinyin = AllCards.getCard( lp.currentIndex() ).getPinyin();
    	String strMsgBody = "English: " + cardEnglish + "\n" + "Hanzi: " + cardHanzi + "\n" + "Pinyin: " + getPinyin +
    			"\n\nInsert card comments here...\n";//+ cardEnglish + "\n" + cardHanzi + "\n" + getPinyin + "\n"; 
    	
        String aEmailList[] = { "anthony.olivence@gmail.com" /*"cullen.schaffer@gmail.com"*/ };   
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Card Index: " + lp.currentIndex() );  
        emailIntent.setType("plain/text");  
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, strMsgBody );  
        startActivity(Intent.createChooser(emailIntent, "Send your email in:"));   
    }
   
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "llkj");
            new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.quit)
            .setMessage(R.string.reallyQuit)
            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    LearnActivity.this.finish();    
                }
            })
            .setNegativeButton(R.string.no, null)
            .show();
            return true;
        } 
        else {
        	return super.onKeyDown(keyCode, event);
        }
    }

	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private long elapsedMillis=0;
	
	@Override
	public void onPause(){
		super.onPause();
		elapsedMillis = SystemClock.elapsedRealtime() - cm.getBase();
		cm.stop();
	}
	
	
	@Override
	public void onResume(){
		super.onResume();
		cm.setBase(SystemClock.elapsedRealtime()-elapsedMillis);
		cm.start();
	}

/////////////////////////
// SWIPE GESTURES START//
/////////////////////////
	@Override
	public boolean onTouchEvent(MotionEvent e){
		return gestureScanner.onTouchEvent(e);
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		try {
			Animation swipeAnimation;
			//right to left. ->next Card
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {

				//Toast.makeText(getApplicationContext(), "Next Card!!", Toast.LENGTH_SHORT).show();
				if(itemsShown>1){
					itemsShown=3;
					doAdvance();
					swipeAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_slide);
					prompt.startAnimation(swipeAnimation);
					status.startAnimation(swipeAnimation);
				}
			}
			//left to right -> undo
			else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY){
				if(lp.isUndoEmpty())
					Toast.makeText(getApplicationContext(), "Nothing to undo!", Toast.LENGTH_SHORT).show();
				else{
					doUndo();
					swipeAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_righ_slide);
					prompt.startAnimation(swipeAnimation);
					status.startAnimation(swipeAnimation);
				}
			}
			// bottom to top ->get rid of card only if itemsShown is at least 1
			else if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
				//Toast.makeText(getApplicationContext(), "Card Removed!!", Toast.LENGTH_SHORT).show();
				doOkay();

			}
			//top to bottom ->show next
			else if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY){
				//Toast.makeText(getApplicationContext(), "Show next!!", Toast.LENGTH_SHORT).show();
				if(itemsShown<3)
					doAdvance();
			}

		} catch (Exception e) {

		}
		return false;
	}
	
////////////////////////
// SWIPE GESTURES END //
////////////////////////
<<<<<<< HEAD
	

////////////////////////////////////////////////////////
//CUSTOM ACTION MODE FOR COPY/PASTE, ADDING DICTIONARY//
////////////////////////////////////////////////////////
	
	public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
		// TODO Auto-generated method stub
		int start = other.getSelectionStart();
		int end = other.getSelectionEnd();
		String selected = other.getText().toString().substring(start, end);
		if(arg1.getItemId()==0){ //If the thing clicked is the Lookup button
			//Toast.makeText(this, "HELLO MDBG.net! - " + arg1.getItemId(), Toast.LENGTH_SHORT).show();
			String url = "http://www.mdbg.net/chindict/chindict.php?page=worddict&wdrst=0&wdqb=" + selected;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i); 
		}
		return false;
	}

	public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
		// TODO Auto-generated method stub
		menu.add("MDBG.net Lookup");
		return true; //Set to true for custom CAB menu items
	}

/////////////////////////////////////////////
//UNUSED EXTRA IMPLEMENTATION METHODS BELOW//
/////////////////////////////////////////////
=======

>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be

	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

<<<<<<< HEAD
=======
	public boolean onActionItemClicked(ActionMode arg0, MenuItem arg1) {
		// TODO Auto-generated method stub
		int start = other.getSelectionStart();
		int end = other.getSelectionEnd();
		String selected = other.getText().toString().substring(start, end);
		if(arg1.getItemId()==0){ //If the thing clicked is the Lookup button
			//Toast.makeText(this, "HELLO MDBG.net! - " + arg1.getItemId(), Toast.LENGTH_SHORT).show();
			String url = "http://www.mdbg.net/chindict/chindict.php?page=worddict&wdrst=0&wdqb=" + selected;
			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(url));
			startActivity(i); 
		}
		return false;
	}

	public boolean onCreateActionMode(ActionMode arg0, Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0,0,0, "MDBG.net Lookup");
		return true; //Set to true for custom CAB menu items
	}
>>>>>>> b5e18c75faeb3cdb6a4cd9ea8f3ce41fb033b1be

	public void onDestroyActionMode(ActionMode arg0) {
		// TODO Auto-generated method stub
		
	}

	public boolean onPrepareActionMode(ActionMode arg0, Menu arg1) {
		// TODO Auto-generated method stub
		return false;
	}
    
}

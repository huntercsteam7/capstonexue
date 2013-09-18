package com.MeadowEast.xue;

import java.io.IOException;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LearnActivity extends Activity implements OnGestureListener {
	static final String TAG = "LearnActivity";
	static final int ECDECKSIZE = 40;
	static final int CEDECKSIZE = 60;
	
	private static final int SWIPE_MIN_DISTANCE = 120;		 //Min distance to register as swipe.
	private static final int SWIPE_THRESHOLD_VELOCITY = 150; //Sensitivity
	
	LearningProject lp;
	int itemsShown;
	TextView prompt, answer, other, status;
	//Button advance, okay;
	
	private GestureDetector gestureScanner;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Log.d(TAG, "Entering onCreate");

        gestureScanner = new GestureDetector(this);
        
        itemsShown = 0;
        prompt  = (TextView) findViewById(R.id.promptTextView);
        status  = (TextView) findViewById(R.id.statusTextView);
        other   = (TextView) findViewById(R.id.otherTextView);
        answer  = (TextView) findViewById(R.id.answerTextView);
        //advance  = (Button) findViewById(R.id.advanceButton);
        //okay     = (Button) findViewById(R.id.okayButton);
    	   
    	//findViewById(R.id.advanceButton).setOnClickListener(this);
    	//findViewById(R.id.okayButton).setOnClickListener(this);
    	
    	//findViewById(R.id.promptTextView).setOnLongClickListener(this);
    	//findViewById(R.id.answerTextView).setOnLongClickListener(this);
    	//findViewById(R.id.otherTextView).setOnLongClickListener(this);
    	
    	if (MainActivity.mode.equals("ec"))
    		lp = new EnglishChineseProject(ECDECKSIZE);	
    	else
    		lp = new ChineseEnglishProject(CEDECKSIZE);
    	clearContent();
    	doAdvance();
    }
    
    @Override
	public boolean onTouchEvent(MotionEvent e){
		return gestureScanner.onTouchEvent(e);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    
    //Private variable "advance" not defined in gesture. Delete it from declaration up top. (Commented it out at the moment)
	private void doAdvance(){
		if (itemsShown == 0){
			if (lp.next()){
				prompt.setText(lp.prompt());
				status.setText(lp.deckStatus());
				itemsShown++;
			} else {
				Log.d(TAG, "Error: Deck starts empty");
				throw new IllegalStateException("Error: Deck starts empty.");
			}
		} else if (itemsShown == 1){
			answer.setText(lp.answer());
			itemsShown++;
		} else if (itemsShown == 2){
			other.setText(lp.other());
			//advance.setText("next");
			itemsShown++;
		} else if (itemsShown == 3){
			//If items are 3, advance to next card. Slide animation below.
			
			// Got it wrong
			//advance.setText("show");
			lp.wrong();
			lp.next();
			clearContent();
			prompt.setText(lp.prompt());
			itemsShown = 1;
			status.setText(lp.deckStatus());
		}
	}
	//Do undo here
	private void doUndo(){
		
	}
	
	private void clearContent(){
		prompt.setText("");
		answer.setText("");
		other.setText("");
	}
	
	private void doOkay(){
		//if (okay.getText().equals("done"))
		if(true)
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
		} else {
			//((ViewManager) advance.getParent()).removeView(advance);
			status.setText("");
			//okay.setText("done");
			clearContent();
		}
	}
    
	//Instead of onClick, use swipe (fling) gestures to call doAdvance();
	
/*    public void onClick(View v){
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
    */

    public boolean onLongClick(View v){
    	switch (v.getId()){
    	case R.id.promptTextView:
    	case R.id.answerTextView:
    	case R.id.otherTextView:
    		Toast.makeText(this, "Item index: "+lp.currentIndex(), Toast.LENGTH_LONG).show();
    		break;
    	}
    	return true;
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
        } else {
        	return super.onKeyDown(keyCode, event);
        }
    }

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("HELLO BLUE ONDOWN!!");
		return false;
	}

		//Swipe OK. Effects ok.
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        try {

        	//right to left. ->next Card
        	if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
        		if(itemsShown>1){
        			itemsShown=3;
        			Animation a1;
                	a1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_to_left_slide);
                	prompt.startAnimation(a1);
                	status.startAnimation(a1);
                	doAdvance();
            		Toast.makeText(getApplicationContext(), "Next Card!!", Toast.LENGTH_SHORT).show();
        		}
            	
            }
        	//left to right -> undo
        	else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY)
        	{
        		Toast.makeText(getApplicationContext(), "Doing Undo!!", Toast.LENGTH_SHORT).show();
        		doUndo();
        	}
            // bottom to top ->get rid of card only if itemsShown is at least 1
            else if(e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
            		itemsShown=3;
            		Animation a1;
            		a1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.bottom_to_top_slide);
            		prompt.startAnimation(a1);
            		status.startAnimation(a1);
            		
            		Toast.makeText(getApplicationContext(), "Card Removed!!", Toast.LENGTH_SHORT).show();
            		//Do remove here? Instead of doAdvance()
            		doAdvance();
            }
        	//top to bottom ->show next
            else if(e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY)
            {
            	if(itemsShown<3)
            		doAdvance();
            }
            
        } catch (Exception e) {

        }
        return false;
     }


	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("HELLO BLUE LONG PRESS!!");

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		System.out.println("HELLO BLUE SCROLL!!");

		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		
		return false;
	}

}

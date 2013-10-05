
package com.MeadowEast.xue;

import java.io.File;
 
import android.app.Activity;

import android.util.Log;
 
public class CardUpdater extends Activity {
 
	static final String TAG = "Xue UpdateCardsActivity";
    // button to show progress dialog
    //Button btnShowProgress;

    
    private static String strVocabDestPath = MainActivity.filesDir + MainActivity.gStrVocabFileName;
 
    public void cancelUpdate( ) {
		
    	deleteTemp();
    	// finish the activity
		finish();
       	
	 }
    
    public boolean shouldUpdate() {
    	
    	// TODO, move url update to a server with a mongodb database, store the sha1 with the file name in a collection
    	// create a method service to return url and corresponding hash in JSON
    	// Parse the JSON, retrive the data and compare to current data on the external sd
    	return true;
    }
    
    public void deleteTemp() {
    	// delete the tmp file
    	File fileTmpVocab = new File( strVocabDestPath + ".tmp" );
    	if ( fileTmpVocab.exists() ) {
	    	if ( !fileTmpVocab.delete() )
	    		Log.d (TAG, "Update Cancelled, unable to delete tmp file.  Not created?");
    	}
    }


}
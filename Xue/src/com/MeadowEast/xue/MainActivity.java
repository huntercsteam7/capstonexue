package com.MeadowEast.xue;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	Button ecButton, ceButton, ecLogButton, ceLogButton, exitButton;
	public static File filesDir;
	public static String mode;
	static final String TAG = "XUE MainActivity";
	public static String gStrVocabFileName;
	
	private Button 			_btnUpdateVocab;
	private NetworkManager 	_networkManager = null;
	private SoundManager 	_soundManager = null;
	private static boolean 	_bSDCardOkay;
	
	// Progress Dialog
    private ProgressDialog pDialog;
    
    private CardUpdater _updateCards;

    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0; 
 
    // File url to download
    private static String _strVocabURL = "http://www.meadoweast.com/capstone/vocabUTF8.txt";
    private String _strVocabDestPath;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ecButton   = (Button) findViewById(R.id.ecButton);
        ceButton   = (Button) findViewById(R.id.ceButton);
        exitButton = (Button) findViewById(R.id.exitButton);
        ecLogButton = (Button) findViewById(R.id.ecLogButton);
        ceLogButton = (Button) findViewById(R.id.ceLogButton);
    	ecButton.setOnClickListener(this);
    	ceButton.setOnClickListener(this);
    	ecLogButton.setOnClickListener(this);
    	ceLogButton.setOnClickListener(this);
    	exitButton.setOnClickListener(this);
    	
    	// Make sure the SD Card is available and writeable, if not, kill the app
    	checkSDCard();
    	
        File sdCard = Environment.getExternalStorageDirectory();
		filesDir = new File ( sdCard.getAbsolutePath() + "/Android/data/com.MeadowEast.xue/files" );
		gStrVocabFileName = "vocab.txt";
		_strVocabDestPath = filesDir + gStrVocabFileName;
		File fileVocab = new File( filesDir.getPath() + gStrVocabFileName );
		//get the file from the resource folder and copy it to the app dir if it doesn't exist already
		if ( !fileVocab.exists() ) {
		
			try
			{
				Common.CopyStreamToFile( this.getResources().openRawResource( R.raw.vocab ), 
												filesDir, gStrVocabFileName );
			}
			catch( Exception ex )
			{
				Log.d( TAG, ex.getMessage() );
			}
    	}
		Log.d(TAG, "xxx filesDir="+filesDir);
		
		
		Log.d(TAG, "Initializing Sound Manager.");
        _soundManager = SoundManager.getInstance();
        _soundManager.init( this.getApplicationContext() );
		
		Log.d(TAG, "Initializing Network Manager.");
		_networkManager = NetworkManager.getInstance();
        _networkManager.init();
    }

	public void UpdateVocab( View view ) throws MalformedURLException, IOException {
		
		_updateCards = new CardUpdater();
	     // Get the new vocabulary
		this._btnUpdateVocab = (Button)this.findViewById( R.id.btnGetVocab );
		  
		if ( !_updateCards.shouldUpdate() ) {
        	Toast toast = Toast.makeText( this, "Already up to date.", Toast.LENGTH_SHORT );
    		toast.show();
        }
        else {
	        // Start the update
	        DownloadFileFromURL task = new DownloadFileFromURL( this );
	        try {
	        	task.execute( _strVocabURL );
	        }
	        catch( Exception ex ) {
	        	Toast toast = Toast.makeText( this, "Error Updating File.", Toast.LENGTH_SHORT );
	    		toast.show();
	    		dismissDialog( progress_bar_type );
	        }
        }
		
	 }
	
    public void onClick( View view ) {
    	
    	Intent i;
    	switch ( view.getId() ){
    	case R.id.ecButton:
    		mode = "ec";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.ceButton:
    		mode = "ce";
    		i = new Intent(this, LearnActivity.class);
    		startActivity(i);
			break;
    	case R.id.ecLogButton:
    		mode = "ec";
    		i = new Intent(this, LogActivity.class);
    		startActivity(i);
    		break;
    	case R.id.ceLogButton:
    		mode = "ce";
    		i = new Intent(this, LogActivity.class);
    		startActivity(i);
    		break;
    	case R.id.exitButton:
    		finish();
			break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }   
    
    
    
    /*
     * Check if SD card is mounted and writable
     */
    private boolean checkSDCard(){
    
       String state = android.os.Environment.getExternalStorageState();
       boolean sdcard_avail =  state.equals(android.os.Environment.MEDIA_MOUNTED);
       boolean sdcard_readonly =  state.equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY);
    
           if ( ! sdcard_avail || sdcard_readonly ){
        	   _bSDCardOkay = false;
        	   
               LayoutInflater inflater = getLayoutInflater();
	           View dview = inflater.inflate(R.layout.alert_card, null);
	           
	           final Dialog dialog = new Dialog(this);
	           dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	           dialog.setContentView(dview);
	           
	           TextView tv = (TextView)  dview.findViewById(R.id.alert);
	           tv.setText( !sdcard_avail ? R.string.no_sdcard :  R.string.sdcard_read_only);
	           
	           Button btn_ok = (Button) dview.findViewById(R.id.btn_ok);
	           btn_ok.setOnClickListener( new View.OnClickListener() {
	               public void onClick( View v ) {
	               dialog.dismiss();
	               MainActivity.this.finish();
	              }
	           });
	           dialog.show();
	           }
           else {
        	   _bSDCardOkay = true;
           }
           return _bSDCardOkay;
    
   }
    /**
     * Showing Dialog
     * */
    @Override
    protected Dialog onCreateDialog( int id ) {
        switch ( id ) {
        case progress_bar_type: // we set this to 0
            pDialog = new ProgressDialog(this);
            pDialog.setMessage("Downloading cards...");
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.setCancelable(true);
            pDialog.show();
            //ViewGroup.LayoutParams lay = new ViewGroup.LayoutParams(1, 0);
            //pDialog.addContentView( (Button)this.findViewById( R.id.btnCancelUpdate ), lay);
            //pDialog.
            return pDialog;
            
        default:
            return null;
        }
    }
    
    
    /**
     * Background Async Task to download file
     * */
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
 
    	private Context _context;
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @SuppressWarnings("deprecation")
		@Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog( progress_bar_type );
        }
 
        public DownloadFileFromURL( Context context ) {
        	_context = context;
        } 
        
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground( String... strURL ) {
    		
    		int count;
            InputStream input = null;
            OutputStream output = null;
    		URLConnection urlConnection = null;   					
        	URL url = null;

        	// open the address
        	try {
        		
        		// Check the internet connection
        		if ( !_networkManager.isOnline( _context.getApplicationContext() ) )
        			throw new ConnectException( "Not connected to the internet." );
        		
        		url = new URL( strURL[0] );
        		urlConnection = url.openConnection();
        		
        		// this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = urlConnection.getContentLength();

                // download the file
                input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream
                output = new FileOutputStream( _strVocabDestPath + ".tmp" );
 
                byte data[] = new byte[1024];
 
                long total = 0;
 
                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
 
                    // writing data to file
                    output.write(data, 0, count);
                }
 
                // flushing output
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                //finish();
            }
 
            return null;
        }
 
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            pDialog.setProgress(Integer.parseInt(progress[0]));
       }
 
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @SuppressWarnings("deprecation")
		@Override
        protected void onPostExecute(String file_url) {
        	// Rename the txt file
        	File fileOld = new File( _strVocabDestPath + ".tmp");
        	File fileUpdated = new File( _strVocabDestPath );
        	try {
        		fileOld.renameTo( fileUpdated );
        	}
        	catch( Exception ex ) {
        		Log.e( TAG, "Unable to save updated file." + _strVocabDestPath );
        		Toast toast = Toast.makeText( _context, "Update Error!", Toast.LENGTH_SHORT );
        		toast.show();
        		// get rid of the temp file if it exists
        		_updateCards.deleteTemp();
        		finish();

        	}
            // dismiss the dialog after the file was downloaded
            dismissDialog( progress_bar_type );
            // Displaying downloaded image into image view
            // Reading image path from sdcard
            //String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile.jpg";
            // setting downloaded into image view
            //my_image.setImageDrawable(Drawable.createFromPath(imagePath));
        }
 
    }

}

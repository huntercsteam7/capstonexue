package com.MeadowEast.xue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkManager {
	
	private static NetworkManager instance = null;
	static final String TAG = "CC NetworkManager";
	
	private NetworkManager() {
	}
	public static NetworkManager getInstance() {

		if( instance == null ) {
	        instance = new NetworkManager();
	        
	      }
	    return instance;
	}
	
	public void init() {
	}
	
	public boolean isOnline( Context contex ) {
        ConnectivityManager connectivity = (ConnectivityManager) contex.getSystemService( Context.CONNECTIVITY_SERVICE );
          if (connectivity != null) {
              NetworkInfo[] info = connectivity.getAllNetworkInfo();
              if (info != null) {
                  for (int i = 0; i < info.length; i++) 
                      if ( info[i].getState() == NetworkInfo.State.CONNECTED )
                      {
                          return true;
                      }
              }
          }
          return false;
    }
	 
}
package com.creatigeek.mapit;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

//import org.geonames.*;
//import org.jdom.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
//import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
//import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

	private GoogleMap mMap = null;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        //final TextView LatLngDisplay = (TextView)findViewById(R.id.textLatLng);
        //final TextView LTimeDisplay = (TextView)findViewById(R.id.textLTime);
        mMap.setOnCameraChangeListener(getCameraChangeListener());
        mMap.setOnMapClickListener(getMapClickListener());     
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public OnCameraChangeListener getCameraChangeListener() {
    	return new OnCameraChangeListener() {
    		@Override
    		public void onCameraChange(CameraPosition position) {
    			LatLng targetPosition = position.target;
    			String listenerCode = "CAMERA";
    			showPositionMsg(targetPosition, listenerCode);
    		}
    	};
    }
    public OnMapClickListener getMapClickListener() {
    	return new OnMapClickListener() {
    		@Override
    		public void onMapClick(LatLng position) {
    			String listenerCode = "MAPCLICK";
    			showPositionMsg(position, listenerCode);
    		}
    	};
    }
    private void showPositionMsg(LatLng position, String listenerCode) {
    	TextView LatLngDisplay = (TextView) findViewById(R.id.textLatLng);
    	NumberFormat nf = NumberFormat.getInstance();
		nf.setMaximumFractionDigits(4);
		nf.setGroupingUsed(false);
		String targetLat = nf.format(position.latitude);
		String targetLng = nf.format(position.longitude);
		//Context context = getApplicationContext();
		//CharSequence positionMsg;
		String positionMsg;
		if (listenerCode == "CAMERA") {
			positionMsg = "Center Lat/Lng: " + targetLat + "/" + targetLng;
		} else { //listenerCode == MAPCLICK
			positionMsg = "Clicked Lat/Lng: " + targetLat + "/" + targetLng;
		}
		//int duration = Toast.LENGTH_LONG;
		//Toast.makeText(context, positionMsg, duration).show();
		LatLngDisplay.setText(positionMsg);
		Log.d(listenerCode, listenerCode + ": Position has changed -" + position.toString());
		getLocalTime(targetLat, targetLng);
    }
    
    // http://android-er.blogspot.com/2011/08/get-time-zone-of-location-from-web.html
    //String rqsurl = "http://api.geonames.org/timezoneJSON?formatted=true" + "&lat=" + lat + "&lng=" + lon + "&username=demo";
    private void getLocalTime(String latitude, String longitude) {
    	String requestUrl = "http://api.geonames.org/timezoneJSON?formatted=true" + "&lat=" + latitude + "&lng=" + longitude + "&username=" + getString(R.string.geo_username);
    	new QueryGeoWebserviceTask().execute(requestUrl);
    }
    
    private class QueryGeoWebserviceTask extends AsyncTask<String, Void, String> {
    	
    	@Override
    	protected String doInBackground(String... urls) {
    		// params come from execute() call: params[0] is the url
    		try {
    			return fetchUrlContents(urls[0]);
    		} catch (IOException e) {
    			return "Unable to retrieve web page contents. URL may be invalid.";
    		}
    	}
    	// onPostExecute displays the results of the AsyncTask
    	@Override
    	protected void onPostExecute(String result) {
    		TextView LTimeDisplay = (TextView) findViewById(R.id.textLTime);
    		//Log.d("AsyncPostExecute", "AsyncPostExecute: " + result);
    		String parsedResult = ParseJSON(result);
    		String localTime = "";
    		try {
    		    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");   
    		    Date date = df.parse(parsedResult); 
    		    DateFormat dftime = new SimpleDateFormat("HH:mm    ddMMMyy");    		    
    		    localTime = dftime.format(date);
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		LTimeDisplay.setText("Local time: " + localTime);
    	}
    }
    private String fetchUrlContents(String myurl) throws IOException {
    	String queryResult = null;
    	HttpClient httpClient = new DefaultHttpClient();
    	HttpGet httpGet = new HttpGet(myurl);
    	
    	try {
    		HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();

        	if (httpEntity != null){
        		InputStream inputStream = httpEntity.getContent();
        		Reader in = new InputStreamReader(inputStream);
        	    BufferedReader bufferedreader = new BufferedReader(in);
        	    StringBuilder stringBuilder = new StringBuilder();
        	    String stringReadLine = null;

        	    while ((stringReadLine = bufferedreader.readLine()) != null) {
        	    	stringBuilder.append(stringReadLine + "\n");	
        	    }
        	    queryResult = stringBuilder.toString();   
        	}	
        } catch (ClientProtocolException e) {
        	e.printStackTrace();	
        } catch (IOException e) {
        	e.printStackTrace();	
        }
		return queryResult;
    }
    
    private String ParseJSON(String json) {
		String jResult = null;
		try {
			JSONObject JsonObject = new JSONObject(json);	
			jResult = JsonObject.getString("time");
		} catch (JSONException e) {
			e.printStackTrace();	
		}
		return jResult;	  	
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    @Override
    public void onResume() {
    	super.onResume();
    }
    @Override
    public void onDestroy() {
    	super.onDestroy();
    }
}

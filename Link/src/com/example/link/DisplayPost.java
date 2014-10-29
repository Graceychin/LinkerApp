package com.example.link;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class DisplayPost extends ActionBarActivity {

	private TextView event_name_text;
	private TextView event_description_text;
	private TextView event_rating_text;
	private TextView testText;
	private String serverURL;
	private boolean promote;
	private int unit_id;
	public int number;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_post);
		
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		//unit_id = getIntent().getExtras().getInt("unit_id");
		//unit_id = getIntent().getIntExtra("event_id",1);
		
        Intent intent = getIntent();
        String id = intent.getStringExtra("event_id");
        number = Integer.parseInt(id);
        
		
		final ImageButton button1 = (ImageButton) findViewById(R.id.up);
        button1.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		promote = true;
        		promote();
        		
        		Intent intent = getIntent();
        		finish();
        		startActivity(intent);
        	}
        });
        
        final ImageButton button2 = (ImageButton) findViewById(R.id.down);
        button2.setOnClickListener(new View.OnClickListener() {
        	public void onClick(View v) {
        		promote = false;
        		demote();
        		
        		Intent intent = getIntent();
        		finish();
        		startActivity(intent);
        	}
        });
		
		testText = new TextView(this);
		
		event_name_text = (TextView) findViewById(R.id.event_name);
		event_description_text = (TextView) findViewById(R.id.description);
		event_rating_text = (TextView) findViewById(R.id.rating_value);

		
			
		serverURL = "http://162.243.27.139/get_post_by_id.php";
        
        // Create Object and call AsyncTask execute Method
        new LongOperation().execute(serverURL);
        
       
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_post, menu);
		return true;
	}
	
	public boolean promote(){
		
		serverURL = "http://162.243.27.139/rate_post.php";
		new LongOperation().execute(serverURL);
		
		return true;
	}
	
	public boolean demote(){
		serverURL = "http://162.243.27.139/rate_post.php";
		new LongOperation().execute(serverURL);
		
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class LongOperation  extends AsyncTask<String, Void, Void> {
        
		HttpClient httpclient = new DefaultHttpClient(); 
        HttpPost httppost = new HttpPost(serverURL);
        
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(DisplayPost.this);
         
         
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
             
            //UI Element
            Dialog.setMessage("Loading...");
            Dialog.show();
            
        }
 
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("event_id", Integer.toString(number)));
                if(serverURL == "http://162.243.27.139/rate_post.php" && promote)
                	nameValuePairs.add(new BasicNameValuePair("rate", "Promote"));
                else if(serverURL == "http://162.243.27.139/rate_post.php" && !promote)
                	nameValuePairs.add(new BasicNameValuePair("rate", "Demote"));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                Content = httpclient.execute(httppost, responseHandler);
                 
            } catch (ClientProtocolException e) {
                Error = e.getMessage();
                cancel(true);
            } catch (IOException e) {
                Error = e.getMessage();
                cancel(true);
            }
             
            return null;
        }
         
        protected void onPostExecute(Void unused) {
            // NOTE: You can call UI Element here.
             
            // Close progress dialog
            Dialog.dismiss();
            
            if (Error != null) {
                 
            	event_name_text.setText(Error);
                 
            } else {
                 
                String name = "";
                String description = "";
                String event_rating = "";
                
                try {
                	if(serverURL=="http://162.243.27.139/get_post_by_id.php")
                	{
                		JSONObject obj = new JSONObject(Content);
                		if(obj.has("success")){
                			
                			name = obj.getJSONObject("events").getString("name");
                			description = obj.getJSONObject("events").getString("description");
                			event_rating = obj.getJSONObject("events").getString("event_rating");
                		} else if(obj.has("error")){
                			name = obj.getString("error_msg");
                			description = "";
                			event_rating = "0";
                		} else {
                			name = "Error Getting Event Info";
                			description = "";
                			event_rating = "0";
                		}
                		event_name_text.setText(name);
                		event_description_text.setText(description);
                		event_rating_text.setText(event_rating);
                	}
        			
        		} catch (JSONException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
                 
             }
        }
         
    }
}
	
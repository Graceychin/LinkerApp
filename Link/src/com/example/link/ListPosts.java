package com.example.link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ListPosts extends ActionBarActivity {
	
	private String latitude = null;
	private String longitude = null;
	private String responseResult = null;
	ListView posts = null;
	ArrayList<singleItem> itemList = null;
    String event_id = null;
    String event_name = null;
    String event_description = null;
    String event_longitude = null;
    String event_latitude = null;
    String event_rating = null;
    String event_time = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_posts);
		
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		posts = (ListView) findViewById(R.id.listPosts);

		itemList = new ArrayList<singleItem>();

		getLocation();

		try {
			AsyncTask<String, Void, String> task = new LongOperation().execute("http://162.243.27.139/get_posts.php");
			jsonParser(task.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final singleItemAdapter adapter = new singleItemAdapter(this, R.layout.activity_list_single, itemList);
		
		posts.setAdapter(adapter); 
		
		posts.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?>parent,View v, int position, long id){

			singleItem item = adapter.getItem(position);
			Intent intent = new Intent(ListPosts.this, DisplayPost.class);
			intent.putExtra("event_id", item.id);
	        Bundle extras = new Bundle();
	        extras.putString("status", "Data Received!");
	        intent.putExtras(extras);
			startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_posts, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.logout){
			Intent intent = new Intent(this, Login.class);
			this.startActivity(intent);
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void getLocation()
	{
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		
		longitude = String.valueOf(location.getLongitude());
		latitude = String.valueOf(location.getLatitude());
	}
	
	public void jsonParser(String response)
	{
		JSONObject obj;
		try {
			obj = new JSONObject(response);

			JSONArray arr = obj.getJSONArray("events");
			for (int i = 0; i < arr.length(); i++)
			{
			    event_id = arr.getJSONObject(i).getString("event_id");
			    event_name = arr.getJSONObject(i).getString("name");
			    event_description = arr.getJSONObject(i).getString("description");
			    event_longitude = arr.getJSONObject(i).getString("longitude");
			    event_latitude = arr.getJSONObject(i).getString("latitude");
			    event_rating = arr.getJSONObject(i).getString("event_rating");
			    event_time = arr.getJSONObject(i).getString("time");
			    
			    Log.v("event_id", event_id);
			    Log.v("event_name", event_name);
			    Log.v("event_description", event_description);
			    Log.v("event_longitude", event_longitude);
			    Log.v("event_latitude", event_latitude);
			    Log.v("event_rating", event_rating);
			    Log.v("event_time", event_time);
			    
			    singleItem post = new singleItem(event_name, event_time, event_rating, event_id);
			    itemList.add(post);
			} 
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	   // Class with extends AsyncTask class
 public class LongOperation  extends AsyncTask<String, Void, String> {

     private ProgressDialog Dialog = new ProgressDialog(ListPosts.this);
	 HttpClient httpclient = new DefaultHttpClient();
	 HttpPost httppost = new HttpPost("http://162.243.27.139/get_posts.php");
      
     protected void onPreExecute() {

    	 Toast.makeText(ListPosts.this, "Downloading source...", Toast.LENGTH_LONG).show();
     }

     // Call after onPreExecute method
     public String doInBackground(String... urls) {
         try {
         	
 	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
 	        nameValuePairs.add(new BasicNameValuePair("longitude", longitude));
 	        nameValuePairs.add(new BasicNameValuePair("latitude", latitude));
 	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
 	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
 	        
 	       responseResult = httpclient.execute(httppost,responseHandler);
 	       Log.v("Response", responseResult);

 	       return responseResult;
              
         } catch (ClientProtocolException e) {
             Dialog.setMessage(e.getMessage().toString());
             cancel(true);
         } catch (IOException e) {
        	 Dialog.setMessage(e.getMessage().toString());
             cancel(true);
         }
		return null;
     }
      
     protected void onPostExecute(Void unused) {;
     }
      
 }
	class singleItem
	{
		String postTitle;
		String postTime;
		String postRating;
		String id;
		public singleItem(String title, String time, String rating, String id)
		{
			postTitle = title;
			postTime = time;
			postRating = rating;
			this.id = id;
		}
		
	}
	
	public class singleItemAdapter extends ArrayAdapter<singleItem> {
		public ArrayList<singleItem> items;
        public itemViewHolder itemHolder;

        public class itemViewHolder {
            TextView title;
            TextView time; 
            TextView rating;
        }

        public singleItemAdapter(Context context, int tvResId, ArrayList<singleItem> items) {
            super(context, tvResId, items);
            this.items = items;
        }

        public singleItem getItem(int position){

        	return items.get(position);
        }
        
        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.activity_list_single, null);
                itemHolder = new itemViewHolder();
                itemHolder.title = (TextView)v.findViewById(R.id.postTitle);
                itemHolder.time = (TextView)v.findViewById(R.id.postTime);
                itemHolder.rating = (TextView)v.findViewById(R.id.postRatingValue);
                v.setTag(itemHolder);
            } else itemHolder = (itemViewHolder)v.getTag(); 

            singleItem item = items.get(pos);
            if(item != null)
            {
            	itemHolder.title.setText(item.postTitle);
            	itemHolder.time.setText(item.postTime);
            	itemHolder.rating.setText(item.postRating);
            	
            }

            return v;
        }
    }
	
}
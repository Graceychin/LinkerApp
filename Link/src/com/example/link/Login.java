package com.example.link;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import library.JSONParser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


/*public class Trying extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trying);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trying, menu);
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
}*/

public class Login extends ActionBarActivity {
	
	public Boolean login = false;
	public Boolean register = false;
	String username = null;
	String password = null;
	private String responseResult = null;
	final Context context = this;
	public Boolean is_right_password = true;
	 

    @Override
    public void onCreate(Bundle savedInstanceState) {
     
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
         
        final Button createAccountButton = (Button) findViewById(R.id.button_create_account);
        final Button signInButton = (Button) findViewById(R.id.button_sign_in);
        
		
        createAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	register = true;
        	
                //Intent intent = new Intent(Login.this, MainMenu.class);
                //startActivity(intent);
                
                final Dialog dialog = new Dialog(context);
				dialog.setContentView(R.layout.activity_account_created);
				dialog.setTitle("Account Created!");
	 
				Button dialogButtonOK = (Button) dialog.findViewById(R.id.dialogButtonOK);
				// if button is clicked, close the custom dialog
				dialogButtonOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();           
					}
				});
				dialog.show();
				
                // Server Request URL
                String serverURL = "http://162.243.27.139/login.php";
                 
                // Create Object and call AsyncTask execute Method
                new LongOperation().execute(serverURL);

            }

        });   
        
        signInButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	login = true;
            	AsyncTask<String, Void, String> task = new LongOperation().execute("http://162.243.27.139/login.php");
            	try {
            		Thread.sleep(2000);
            	} catch(InterruptedException ex){
            		Thread.currentThread().interrupt();
            	}
            	
            	if (is_right_password == false){
                    final Dialog dialog = new Dialog(context);
    				dialog.setContentView(R.layout.activity_wrong_password_dialog);
    				dialog.setTitle("Wrong Password: Try Again.");
    	 
    				Button dialogButtonOK2 = (Button) dialog.findViewById(R.id.dialogButtonOK2);
    				// if button is clicked, close the custom dialog
    				dialogButtonOK2.setOnClickListener(new OnClickListener() {
    					@Override
    					public void onClick(View v) {
    						dialog.dismiss();           
    					}
    				});
    				dialog.show();
    				is_right_password = true;
            	}
            	//Intent intent = new Intent(Login.this, MainMenu.class);
            	//startActivity(intent);
                // Server Request URL
            	
                //String serverURL = "http://162.243.27.139/login.php";
                 
                // Create Object and call AsyncTask execute Method
                //new LongOperation().execute(serverURL);
            	
            }
        		


        });
         
    }
    
    /** Called when the activity is first created. */
    /*public void sign_in_clicked(View view) {
		try {
			AsyncTask<String, Void, String> task = new LongOperation().execute("http://162.243.27.139/login.php");
			jsonParser(task.get());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
    	Intent intent = new Intent(Login.this, MainMenu.class);
    	startActivity(intent);
     
    }*/
    
	/*public void jsonParser(String response)
	{
		JSONObject obj;
		try {
			obj = new JSONObject(response);

			JSONArray arr = obj.getJSONArray("users");
			JSONArray success = obj.getJSONArray("success");
			for (int i = 0; i < arr.length(); i++)
			{
				username = arr.getJSONObject(i).getString("username");
				password = arr.getJSONObject(i).getString("password");
			    
				Log.v("username", username);
			    Log.v("password", password);
			    
			   
			} 
			System.out.println(success);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
    // Class with extends AsyncTask class
    private class LongOperation  extends AsyncTask<String, Void, String> {
         
        private final HttpClient Client = new DefaultHttpClient();
        private String Content;
        private String Error = null;
        private ProgressDialog Dialog = new ProgressDialog(Login.this);
        private String register_tag = "register";
        private String login_tag = "login";
        EditText inputUsername = (EditText) findViewById(R.id.user_name);
        EditText inputPassword = (EditText) findViewById(R.id.login_password);
        
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://162.243.27.139/login.php");
        private JSONParser jsonParser;
         
        //TextView uiUpdate = (TextView) findViewById(R.id.output);
         
        protected void onPreExecute() {
            // NOTE: You can call UI Element here.
             
            //UI Element
            //uiUpdate.setText("Output : ");
            //Dialog.setMessage("Downloading source..");
            //Dialog.show();
        }
 
        // Call after onPreExecute method
        protected String doInBackground(String... urls) {
        	//jsonParser = new JSONParser();
            try {

                // Call long running operations here (perform background computation)
                // NOTE: Don't call UI Element here.
            	/*if (login == true){
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("tag", login_tag));
                    params.add(new BasicNameValuePair("username", inputUsername.getText().toString()));
	            	params.add(new BasicNameValuePair("password",inputPassword.getText().toString()));
                    JSONObject json = jsonParser.getJSONFromUrl("http://162.243.27.139/login.php", params);
                    try {
	                    if (json.getString("success") != null) {
	                        String res = json.getString("success"); 
	                        if(Integer.parseInt(res) == 1){
	                            // user successfully logged in
	                            // Store user details in SQLite Database
	                            JSONObject json_user = json.getJSONObject("user");
	                              
	                            // Launch Dashboard Screen
	                            Intent dashboard = new Intent(getApplicationContext(), MainMenu.class);
	                             
	                            // Close all views before launching Dashboard
	                            dashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	                            startActivity(dashboard);
	                             
	                            // Close Login Screen
	                            finish();
	                        }else{
	                            // Error in login
	                            inputUsername.setText("Incorrect, try again.");
	                        }
	                    }           
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    
            	}*/
            	if (login == true){
	            	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
	            	params.add(new BasicNameValuePair("tag", login_tag));
	            	params.add(new BasicNameValuePair("username",inputUsername.getText().toString()));
	            	params.add(new BasicNameValuePair("password",inputPassword.getText().toString()));
	                httppost.setEntity(new UrlEncodedFormEntity(params));
	     	        ResponseHandler<String> responseHandler = new BasicResponseHandler();
	     	        
	      	        responseResult = httpclient.execute(httppost,responseHandler);
	      	        Log.v("Response", responseResult);
	      	        
	      	        Boolean result = responseResult.contains("success");
	      	        System.out.println(result);
	      	        if (result == true){
	      	        	Intent intent = new Intent(Login.this, MainMenu.class);
	      	        	startActivity(intent);
	      	        }
	      	        else{
	      	        	is_right_password = false;
	    				return responseResult;
	      	        }

	      	        return responseResult;
            		
            	}
            	
            	if (register == true){
	            	List<NameValuePair> params = new ArrayList<NameValuePair>(2);
	            	params.add(new BasicNameValuePair("tag", register_tag));
	            	params.add(new BasicNameValuePair("username",inputUsername.getText().toString()));
	            	params.add(new BasicNameValuePair("password",inputPassword.getText().toString()));
	                httppost.setEntity(new UrlEncodedFormEntity(params));
	                // Execute HTTP Post Request
	                HttpResponse response = httpclient.execute(httppost);
            	}
	                
	                // Server url call by GET method
	                //HttpGet httpget = new HttpGet(urls[0]);
	                //ResponseHandler<String> responseHandler = new BasicResponseHandler();
	                //Content = Client.execute(httpget, responseHandler);
                 
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
            //Dialog.dismiss();
             
            /*if (Error != null) {
                 
                uiUpdate.setText("Output : "+Error);
                 
            } else {
                 
                uiUpdate.setText("Output : "+Content);
                 
             }*/
        }
         
    }
}

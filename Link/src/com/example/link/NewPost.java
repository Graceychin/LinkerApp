package com.example.link;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewPost extends ActionBarActivity {
	
	private static final String TAG = "CallCamera";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQ = 0;
		
	Uri fileUri = null;
	ImageView photoImage = null;
	TextView latitude = null;
	TextView longitude = null;
	EditText title = null;
	EditText description = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_post);
		final Context context = this;
		
	    ActionBar actionBar = getSupportActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		
		final CheckBox checkBox = (CheckBox) findViewById( R.id.addImageBox );
		description = (EditText) findViewById(R.id.descriptionContent);
		title = (EditText) findViewById(R.id.NewPostTitleContent);
		latitude = (TextView) findViewById(R.id.latitudeValue);
		longitude = (TextView) findViewById(R.id.longitudeValue);
		
		photoImage = (ImageView) findViewById(R.id.newPostImage);
		photoImage.setImageDrawable(null);
		
		getLocation();
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
		    {
		        if (isChecked)
		        {
		        	// custom dialog
					final Dialog dialog = new Dialog(context);
					dialog.setContentView(R.layout.activity_dialog_addimage);
					dialog.setTitle("Choose Image Source");
		 
					Button dialogButtonCancel = (Button) dialog.findViewById(R.id.dialogButtonCancel);
					Button dialogButtonPicture = (Button) dialog.findViewById(R.id.dialogButtonPicture);
					Button dialogButtonCamera = (Button) dialog.findViewById(R.id.dialogButtonCamera);
					// if button is clicked, close the custom dialog
					dialogButtonCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							// UnCheck if Cancel
							checkBox.toggle();
						}
					});
					
					dialogButtonPicture.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					
					dialogButtonCamera.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
							
							Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					        File file = getOutputPhotoFile();
					        fileUri = Uri.fromFile(file);
					        i.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
					        startActivityForResult(i, CAPTURE_IMAGE_ACTIVITY_REQ );

						}
					});
		 
					dialog.show();
		        }

		    }
		});
		
		final Button button1 = (Button) findViewById(R.id.submitButton1);
        button1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Intent intent=new Intent(NewPost.this, SaveNewPost.class);
		        intent.putExtra("description", description.getText().toString());
		        intent.putExtra("title", title.getText().toString());
		        intent.putExtra("photoUri", fileUri.toString());
		        Bundle extras = new Bundle();
		        extras.putString("status", "Data Received!");
		        intent.putExtras(extras);
		        startActivity(intent);
				
		        new LongOperation().execute("http://162.243.27.139/new_post.php");
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_post, menu);
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
		
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQ) {
		    if (resultCode == RESULT_OK) {
		      Uri photoUri = null;
		      if (data == null) {
		        // A known bug here! The image should have saved in fileUri
		        Toast.makeText(this, "Image saved successfully"+fileUri, 
		                       Toast.LENGTH_LONG).show();
		        photoUri = fileUri;
		      } else {
		        photoUri = data.getData();
		        Toast.makeText(this, "Image saved successfully in: " + data.getData(), 
		                       Toast.LENGTH_LONG).show();
		      }
		    showPhoto(photoUri);
		    } else if (resultCode == RESULT_CANCELED) {
		      Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
		    } else {
		      Toast.makeText(this, "Callout for image capture failed!", 
		                     Toast.LENGTH_LONG).show();
		    }
		  }
		}
	
	private File getOutputPhotoFile() {
	  File directory = new File(Environment.getExternalStoragePublicDirectory(
	                Environment.DIRECTORY_PICTURES), getPackageName());
	  if (!directory.exists()) {
	    if (!directory.mkdirs()) {
	      Log.e(TAG, "Failed to create storage directory.");
	      return null;
	    }
	  }
	  String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss", Locale.UK).format(new Date());
	  return new File(directory.getPath() + File.separator + "IMG_"  
	                    + timeStamp + ".jpg");
	}
	
	private void showPhoto(Uri photoUri) {
		String photoPath = photoUri.getEncodedPath(); 
		File imageFile = new File(photoPath);
		  if (imageFile.exists()){
		     Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
		     
		     int pic_width = bitmap.getWidth();
		     int pic_height = bitmap.getHeight();
		     
		     DisplayMetrics metrics = new DisplayMetrics();
		     getWindowManager().getDefaultDisplay().getMetrics(metrics);
		     Matrix matrix = new Matrix();
		     matrix.postRotate(90);
		     matrix.postScale(0.25f, 0.25f);
		     bitmap =Bitmap.createBitmap(bitmap, 0, 0,pic_width, pic_height, matrix, true);
		     photoImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
		     photoImage.setImageBitmap(bitmap);
		  }  
	}
	
	private void getLocation()
	{
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		longitude.setText(String.valueOf(location.getLongitude()));
		latitude.setText(String.valueOf(location.getLatitude()));
	}
	
	
	   // Class with extends AsyncTask class
    private class LongOperation  extends AsyncTask<String, Void, Void> {
         
        private ProgressDialog Dialog = new ProgressDialog(NewPost.this);
        
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost("http://162.243.27.139/new_post.php");
	    
        protected void onPreExecute() {
            Dialog.setMessage("Uploading source..");
            Dialog.show();
        }
 
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            try {
                   	
    	        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    	        nameValuePairs.add(new BasicNameValuePair("name", title.getText().toString()));
    	        nameValuePairs.add(new BasicNameValuePair("description", description.getText().toString()));
    	        nameValuePairs.add(new BasicNameValuePair("longitude", longitude.getText().toString()));
    	        nameValuePairs.add(new BasicNameValuePair("latitude", latitude.getText().toString()));
    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
    	        
    	        //Execute HTTP Post Request
    	        httpclient.execute(httppost);
                 
            } catch (ClientProtocolException e) {
            	Dialog.setMessage(e.getMessage().toString());
                cancel(true);
            } catch (IOException e) {
            	Dialog.setMessage(e.getMessage().toString());
                cancel(true);
            }
             
            return null;
        }
         
        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
        }
         
    }
}

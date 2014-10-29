package com.example.link;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SaveNewPost extends ActionBarActivity {

	ImageView photoImage = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save_new_post);
		
		photoImage = (ImageView) findViewById(R.id.saveImage);
		
		// 1. get passed intent 
        Intent intent = getIntent();
 
        // 2. get message value from intent
        String description = intent.getStringExtra("description");
        String title = intent.getStringExtra("title");
        String photoUri = intent.getStringExtra("photoUri");
        
        
 
        // 3. show message on textView 
        ((TextView)findViewById(R.id.saveDescriptionContent)).setText(description);
        ((TextView)findViewById(R.id.saveTitle)).setText(title);
        showPhoto(Uri.parse(photoUri));
 
        // 4. get bundle from intent
        Bundle bundle = intent.getExtras();
 
        // 5. get status value from bundle
        String status = bundle.getString("status");
 
        // 6. show status on Toast
        Toast toast = Toast.makeText(this, status, Toast.LENGTH_LONG);
        toast.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.save_new_post, menu);
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
		//photoImage.setImageURI(Uri.parse(photoUri.toString()));
	}
}
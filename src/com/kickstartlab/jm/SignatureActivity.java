package com.kickstartlab.jm;

import java.io.File;
import java.io.FileOutputStream;
import java.util.UUID;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SignatureActivity extends Activity implements OnClickListener{

	SignatureView signature;
	String sign_file;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signature);
		
		Intent intent = getIntent();
		
		sign_file = intent.getStringExtra("signature_file");
		
		Log.i("sign file", sign_file);
		
		signature = (SignatureView) findViewById(R.id.signature);
		
		Button btClear = (Button) findViewById(R.id.signClear);
		Button btCancel = (Button) findViewById(R.id.signCancel);
		Button btCommit = (Button) findViewById(R.id.signCommit);
		
		btClear.setOnClickListener(this);
		btCancel.setOnClickListener(this);
		btCommit.setOnClickListener(this);		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.signClear:
				signature.clear();
				break;
			case R.id.signCancel:
				setResult(Activity.RESULT_CANCELED);
				finish();
				break;
			case R.id.signCommit:
				signature.setDrawingCacheEnabled(true);
				/*
				String imgSaved = MediaStore.Images.Media.insertImage(
					    getContentResolver(), signature.getDrawingCache(),
					    sign_file, "drawing");
				*/
				Bitmap bitmap = signature.getDrawingCache();				
				FileOutputStream ostream;
				try {
					File sfile = new File(sign_file);
				    sfile.createNewFile();
				    ostream = new FileOutputStream(sign_file);
				    bitmap.compress(CompressFormat.JPEG, 100, ostream);
				    ostream.flush();
				    ostream.close();
				    Toast.makeText(getApplicationContext(), "image saved", Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
				    e.printStackTrace();
				    Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
				}
				/*
				if(imgSaved!=null){
				    Toast.makeText(this,"Drawing saved to Gallery!", Toast.LENGTH_SHORT).show();
				}
				else{
				    Toast.makeText(this,"Oops! Image could not be saved.", Toast.LENGTH_SHORT).show();
				}*/
				signature.destroyDrawingCache();

				setResult(Activity.RESULT_OK);
				finish();
				break;
		}
		
	}
	
	


}

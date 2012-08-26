package com.kickstartlab.jm;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ChangePassActivity extends Activity implements OnClickListener{
	EditText edOldPass,edNewPass;

	private SharedPreferences jexPrefs;
	private SharedPreferences.Editor editor;

	String oldkey,newkey;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.changepasslayout);
        
        edOldPass = (EditText)findViewById(R.id.edOldPass);
        edNewPass = (EditText)findViewById(R.id.edNewPass);
        Button btSetNewPass = (Button) findViewById(R.id.btChangeKey);
        
        btSetNewPass.setOnClickListener(this);
        
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        editor = jexPrefs.edit();
        
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btChangeKey:
				String inoldkey = edOldPass.getText().toString();
				String innewkey = edNewPass.getText().toString();

		        oldkey = jexPrefs.getString("passkey", "98765");
				
				if(inoldkey.equals(oldkey)){
					if(innewkey.trim() != ""){
						editor.putString("passkey", innewkey);
						editor.commit();
						Toast.makeText(this, "New Passkey is Set "+ innewkey, Toast.LENGTH_SHORT).show();
					}
				}else{
					Toast.makeText(this, "Invalid old passkey", Toast.LENGTH_SHORT).show();
				}
				
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
		}
		
	}

	
}

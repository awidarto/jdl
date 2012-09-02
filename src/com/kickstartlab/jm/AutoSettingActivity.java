package com.kickstartlab.jm;

import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class AutoSettingActivity extends Activity implements OnClickListener{

    private SharedPreferences jexPrefs;
    boolean cancelOnExit;
    Long currentInterval;
    CheckBox cbCancelOnExit;
    EditText edInterval;
    Button btSaveSetting;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autoreportlayout);
        
        jexPrefs = this.getApplicationContext().getSharedPreferences("jexprefs", MODE_PRIVATE);
        cancelOnExit = jexPrefs.getBoolean("cancelonexit", true);
        currentInterval = jexPrefs.getLong("autoreportinterval", 600);
        
        cbCancelOnExit = (CheckBox) findViewById(R.id.chkAutoCancel);
        edInterval = (EditText) findViewById(R.id.edInterval);        
        btSaveSetting = (Button) findViewById(R.id.btSaveSetting);        
        btSaveSetting.setOnClickListener(this);
        
        edInterval.setText(currentInterval.toString());
        cbCancelOnExit.setChecked(cancelOnExit);
        
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.btSaveSetting:
				cancelOnExit = (cbCancelOnExit.isChecked())?true:false;
				currentInterval = Long.parseLong(edInterval.getText().toString());
				SharedPreferences.Editor editor = jexPrefs.edit();
				editor.putBoolean("cancelonexit", cancelOnExit);
				editor.putLong("autoreportinterval", currentInterval);
				editor.commit();				
				Toast.makeText(this, "Auto Report Setting saved", Toast.LENGTH_SHORT).show();
				finish();
				break;
			default:
				Toast.makeText(this, "None to Do", Toast.LENGTH_SHORT).show();
				break;
		}
	}

}

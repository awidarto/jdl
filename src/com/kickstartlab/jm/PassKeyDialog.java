package com.kickstartlab.jm;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;

public class PassKeyDialog extends Dialog implements android.view.View.OnClickListener{
	public static EditText inPassKey;
	public String passkey;
	OnPassKeyResult mDialogResult;
	
	public PassKeyDialog(Context context,String name) {
		super(context);
	}
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.passkeydialog);
		setTitle("Enter Passkey");
		inPassKey = (EditText) findViewById(R.id.inPassKey);
		Button btPassKey = (Button) findViewById(R.id.btPassKey);
		
		btPassKey.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.btPassKey:
				if(mDialogResult != null){
					String inPass = inPassKey.getText().toString();
					inPassKey.setText("");
					mDialogResult.finish(String.valueOf(inPass));
				}
				PassKeyDialog.this.dismiss();
				break;
			default :
				break;
		}
		
	}
	
	public void setDialogResult(OnPassKeyResult dialogResult){
		mDialogResult = dialogResult;
	}
	
	public interface OnPassKeyResult{
		void finish(String result);
	}

	
}

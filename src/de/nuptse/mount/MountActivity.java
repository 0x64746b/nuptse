package de.nuptse.mount;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.nuptse.R;


public class MountActivity extends Activity {

	private final static String CLASS = MountActivity.class.getSimpleName();
	
	public final String mDevice = "/dev/block/mmcblk1p1";
	public final String mMountPoint = "/storage/sdcard1";
	public final String mType = "ext4";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button)findViewById(R.id.button_refresh)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new CheckStateTask(MountActivity.this).execute();
			}
		});
	}

	protected void onResume() {
		super.onResume();
		new CheckStateTask(this).execute();
	}

	protected void showMountButton() {
		Button button = (Button)findViewById(R.id.button_mount);
		button.setVisibility(View.VISIBLE);

		button.setText(R.string.mount);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(CLASS, String.format("Mounting '%s'", mDevice));
				///TODO: new MountTask().execute();
			}
		});
	}

	protected void showUnmountButton() {
		Button button = (Button)findViewById(R.id.button_mount);
		button.setVisibility(View.VISIBLE);

		button.setText(R.string.unmount);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(CLASS, String.format("Unmounting '%s'", mDevice));
				new UnmountTask(MountActivity.this).execute();
			}
		});
	}

	protected void displayMessage(String message) {
		TextView guiOutput = (TextView)findViewById(R.id.text_output);
		guiOutput.setTextColor(Color.WHITE);
		guiOutput.setText(message);		
	}

	protected void displayError(String error) {
		TextView guiOutput = (TextView)findViewById(R.id.text_output);
		guiOutput.setTextColor(Color.RED);
		guiOutput.setText(error);
	}
}

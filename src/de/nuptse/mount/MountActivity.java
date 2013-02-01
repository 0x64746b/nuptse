package de.nuptse.mount;

import settings.SettingsActivity;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.activity_main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.settings_menu:
	            Intent settingsActivity = new Intent(this, SettingsActivity.class);
	            startActivity(settingsActivity);
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	protected void showMountButton() {
		Button button = (Button)findViewById(R.id.button_mount);
		button.setVisibility(View.VISIBLE);

		button.setText(R.string.mount);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(CLASS, String.format("Mounting '%s'", mDevice));
				new MountTask(MountActivity.this).execute();
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

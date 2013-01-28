package de.nuptse;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.Shell;


public class MainActivity extends Activity {

	private final static String CLASS = MainActivity.class.getSimpleName();
	
	private final String mDevice = "/dev/block/mmcblk1p1";
	private final String mMountPoint = "/storage/sdcard1";
	private final String mType = "ext4";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		((Button)findViewById(R.id.button_refresh)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new CheckStateTask().execute();
			}
		});
	}

	protected void onResume() {
		super.onResume();
		new CheckStateTask().execute();
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
				new UnmountTask().execute();
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


	private class CheckStateTask extends AsyncTask<Void, Void, Boolean> {

		private final String CLASS = CheckStateTask.class.getSimpleName();
		
		private Boolean mMounted = false;
		private Exception mError = null;
		
		@Override
		protected Boolean doInBackground(Void... params) {
			Log.d(CLASS, "Initializing RootTools");
			
			Shell rootShell = null;
			
			Command mount_command = new Command(0, "mount") {
				
				@Override
				public void output(int id, String line) {
					Log.d(CLASS, String.format("Checking output of mount: %s", line));
					if (line.matches(String.format("^%s %s %s.*", mDevice, mMountPoint, mType))) {
						Log.d(CLASS, String.format("Device is mounted: %s", line));
						mMounted = true;
					}
				}
			};
			
			try {
				Log.d(CLASS, "Getting root shell");
				rootShell = RootTools.getShell(true);
				Log.d(CLASS, "adding command");
				rootShell.add(mount_command);
				Log.d(CLASS, "waiting for process to finish");
				mount_command.waitForFinish();
			} catch (Exception e) { 
			    mError = e;
			    cancel(false);
			}
			
			return mMounted;
		}
		
		@Override
		protected void onPostExecute(Boolean mounted) {
			String message = String.format("%s is mounted: %s", mDevice, mounted);
			Log.d(CLASS, message);
			
			if (mounted) {
				showUnmountButton();
			} else {
				showMountButton();
			}

			displayMessage(message);
		}
		
		@Override
		protected void onCancelled() {
			String message = mError.getMessage();
			Log.e(CLASS, message);
			displayError(message);
		}
	}
	
	
	private class UnmountTask extends AsyncTask<Void, Void, ShellCommandResult> {

		private Exception mError = null;
		
		@Override
		protected ShellCommandResult doInBackground(Void... params) {
			
			String umountCommand = String.format("umount %s", mMountPoint);
			CommandCapture unmountCommand = new CommandCapture(0, umountCommand);
			ShellCommandResult result = null;

			try {
				RootTools.getShell(true).add(unmountCommand);
				unmountCommand.waitForFinish();
				Log.d(CLASS, String.format("umount [%d] %s", unmountCommand.exitCode(), unmountCommand.toString()));
				result = new ShellCommandResult(unmountCommand.exitCode(), unmountCommand.toString());
			} catch (Exception exception) {
				mError = exception;
				cancel(false);
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(ShellCommandResult result) {
			int exitCode = result.getExitCode();
			String output = result.getOutput();
			
			if (exitCode == 0) {
				displayMessage(String.format("Successfully unmounted '%s'", mDevice));
			} else {
				String error = String.format("Failed to unmount %s. umount exited with code %d: %s",
											 mDevice, exitCode, output);
				displayError(error);
			}
		}
		
		@Override
		protected void onCancelled() {
			String message = mError.getMessage();
			Log.e(CLASS, message);
			displayError(message);
		}
	}
	
	private class ShellCommandResult {
		private final int mExitCode;
		private final String mOutput;
		
		public ShellCommandResult(int exitCode, String output) {
			mExitCode = exitCode;
			mOutput = output;
		}
		
		public int getExitCode() {
			return mExitCode;
		}
		
		public String getOutput() {
			return mOutput;
		}
	}
}

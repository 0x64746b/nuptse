package de.nuptse.mount;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

import de.nuptse.R;

public class MountTask extends AsyncTask<Void, Void, ShellCommandResult> {

	private final static String CLASS = MountTask.class.getSimpleName();

	private MountActivity mParent = null;
	private String mDevice = null;
	private String mMountPoint = null;
	private String mFSType = null;
	private Exception mError = null;


	public MountTask(MountActivity parent) {
		mParent = parent;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mParent);

		String key = mParent.getResources().getString(R.string.settings_key_device);
		String dflt = mParent.getResources().getString(R.string.settings_default_device);
		mDevice = settings.getString(key, dflt);

		key = mParent.getResources().getString(R.string.settings_key_mountpoint);
		dflt = mParent.getResources().getString(R.string.settings_default_mountpoint);
		mMountPoint = settings.getString(key, dflt);

		key = mParent.getResources().getString(R.string.settings_key_fstype);
		dflt = mParent.getResources().getString(R.string.settings_default_fstype);
		mFSType = settings.getString(key, dflt);
	}

	@Override
	protected ShellCommandResult doInBackground(Void... params) {

		String mountCommandline = String.format("mount -t %s %s %s",
												 mFSType,
												 mDevice,
												 mMountPoint);
		CommandCapture mountCommand = new CommandCapture(0, mountCommandline);
		ShellCommandResult result = null;

		try {
			RootTools.getShell(true).add(mountCommand);
			mountCommand.waitForFinish();
			Log.d(CLASS, String.format("mount [%d] %s", mountCommand.exitCode(), mountCommand.toString()));
			result = new ShellCommandResult(mountCommand.exitCode(), mountCommand.toString());
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
			mParent.showUnmountButton();
			mParent.displayMessage(String.format("Successfully mounted '%s'", mDevice));
		} else {
			String error = String.format("Failed to mount %s. mount exited with code %d: %s",
										 mDevice, exitCode, output);
			mParent.displayError(error);
		}
	}

	@Override
	protected void onCancelled() {
		String message = mError.getMessage();
		Log.e(CLASS, message);
		mParent.displayError(message);
	}
}

package de.nuptse.mount;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

import de.nuptse.R;

class UnmountTask extends AsyncTask<Void, Void, ShellCommandResult> {

	private final static String CLASS = UnmountTask.class.getSimpleName();

	private MountActivity mParent = null;
	private String mDevice = null;
	private String mMountPoint = null;
	private Exception mError = null;


	public UnmountTask(MountActivity parent) {
		mParent = parent;
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mParent);

		String key = mParent.getResources().getString(R.string.settings_key_device);
		String dflt = mParent.getResources().getString(R.string.settings_default_device);
		mDevice = settings.getString(key, dflt);

		key = mParent.getResources().getString(R.string.settings_key_mountpoint);
		dflt = mParent.getResources().getString(R.string.settings_default_mountpoint);
		mMountPoint = settings.getString(key, dflt);
	}

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
			mParent.showMountButton();
			mParent.displayMessage(String.format("Successfully unmounted '%s'", mDevice));
		} else {
			String error = String.format("Failed to unmount %s. umount exited with code %d: %s",
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

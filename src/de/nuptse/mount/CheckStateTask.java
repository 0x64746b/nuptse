package de.nuptse.mount;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.Shell;

import de.nuptse.R;


class CheckStateTask extends AsyncTask<Void, Void, Boolean> {

	private final String CLASS = CheckStateTask.class.getSimpleName();

	private MountActivity mParent = null;
	private String mDevice = null;
	private String mMountPoint = null;
	private String mFSType = null;
	private Boolean mMounted = false;
	private Exception mError = null;

	public CheckStateTask(MountActivity parent) {
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
	protected Boolean doInBackground(Void... params) {
		Log.d(CLASS, "Initializing RootTools");

		Shell rootShell = null;

		Command mount_command = new Command(0, "mount") {

			@Override
			public void output(int id, String line) {
				Log.d(CLASS, String.format("Checking output of mount: %s", line));
				String regex = String.format("^%s %s %s.*", mDevice,
						                     mMountPoint, mFSType);
				if (line.matches(regex)) {
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
			mParent.showUnmountButton();
		} else {
			mParent.showMountButton();
		}

		mParent.displayMessage(message);
	}

	@Override
	protected void onCancelled() {
		String message = mError.getMessage();
		Log.e(CLASS, message);
		mParent.displayError(message);
	}
}

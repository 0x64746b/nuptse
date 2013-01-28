package de.nuptse;

import android.os.AsyncTask;
import android.util.Log;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.Shell;


class CheckStateTask extends AsyncTask<Void, Void, Boolean> {

	private final String CLASS = CheckStateTask.class.getSimpleName();

	private MainActivity mParent = null;
	private Boolean mMounted = false;
	private Exception mError = null;

	public CheckStateTask(MainActivity parent) {
		mParent = parent;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		Log.d(CLASS, "Initializing RootTools");

		Shell rootShell = null;

		Command mount_command = new Command(0, "mount") {

			@Override
			public void output(int id, String line) {
				Log.d(CLASS, String.format("Checking output of mount: %s", line));
				String regex = String.format("^%s %s %s.*", mParent.mDevice,
						                     mParent.mMountPoint, mParent.mType);
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
		String message = String.format("%s is mounted: %s", mParent.mDevice, mounted);
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
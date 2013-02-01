package de.nuptse.mount;

import android.os.AsyncTask;
import android.util.Log;

import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

public class MountTask extends AsyncTask<Void, Void, ShellCommandResult> {

	private final static String CLASS = MountTask.class.getSimpleName();

	private MountActivity mParent = null;
	private Exception mError = null;


	public MountTask(MountActivity parent) {
		mParent = parent;
	}

	@Override
	protected ShellCommandResult doInBackground(Void... params) {

		String mountCommandline = String.format("mount -t %s %s %s",
												 mParent.mType,
												 mParent.mDevice,
												 mParent.mMountPoint);
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
			mParent.displayMessage(String.format("Successfully mounted '%s'", mParent.mDevice));
		} else {
			String error = String.format("Failed to mount %s. mount exited with code %d: %s",
										 mParent.mDevice, exitCode, output);
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

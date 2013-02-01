package settings;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import de.nuptse.R;

public class SettingsActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
		// Show the Up button in the action bar.
	}
}

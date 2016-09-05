package info.schnatterer.logbackandroiddemo;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import info.schnatterer.logbackandroiddemo.util.Logs;

/**
 * Activity that realizes the developer settings.
 */
public class PreferencesDeveloperActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Display the back arrow in the header (left of the icon)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            onCreatePreferenceActivity();
        } else {
            onCreatePreferenceFragment();
        }
    }

    /**
     * Wraps legacy {@link #onCreate(Bundle)} code for Android < 3 (i.e. API lvl
     * < 11).
     */
    @SuppressWarnings("deprecation")
    private void onCreatePreferenceActivity() {
        addPreferencesFromResource(R.xml.preferences_developer);
        findPreference(getString(R.string.preferences_key_log_level))
            .setOnPreferenceChangeListener(new RootLogLevelPreferenceChangedListener());
        findPreference(getString(R.string.preferences_key_log_level_logcat))
            .setOnPreferenceChangeListener(new LogCatLogLevelPreferenceChangedListener(this));
    }

    /**
     * Wraps {@link #onCreate(Bundle)} code for Android >= 3 (i.e. API lvl >=
     * 11).
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void onCreatePreferenceFragment() {
        getFragmentManager()
            .beginTransaction()
            .replace(
                android.R.id.content,
                Fragment.instantiate(this,
                    NusicPreferencesDeveloperFragment.class
                        .getName())).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // When the back arrow in the header (left of the icon) is clicked,
            // "go back one activity"
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Listens for a change in the preference that contains the log level of the root logger.
     */
    private static class RootLogLevelPreferenceChangedListener implements
        Preference.OnPreferenceChangeListener {
        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Logs.setRootLogLevel(newValue.toString());
            //  update the state of the Preference with the new value
            return true;
        }
    }

    /**
     * Listens for a change in the preference that contains the log level of the root logger.
     */
    private static class LogCatLogLevelPreferenceChangedListener implements
        Preference.OnPreferenceChangeListener {

        private final Context context;

        public LogCatLogLevelPreferenceChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Logs.setLogCatLevel(newValue.toString(), context);
            //  update the state of the Preference with the new value
            return true;
        }
    }

    @SuppressLint("NewApi")
    public static class NusicPreferencesDeveloperFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences_developer);

            findPreference(getString(R.string.preferences_key_log_level))
                .setOnPreferenceChangeListener(new RootLogLevelPreferenceChangedListener());
            findPreference(getString(R.string.preferences_key_log_level_logcat))
                .setOnPreferenceChangeListener(new LogCatLogLevelPreferenceChangedListener(getActivity()));
        }
    }

}

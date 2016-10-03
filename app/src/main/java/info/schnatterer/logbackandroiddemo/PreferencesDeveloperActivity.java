/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Johannes Schnatterer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

import info.schnatterer.logbackandroidutils.Logs;

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
        findPreference(getString(R.string.preferences_key_log_level_file))
            .setOnPreferenceChangeListener(new FileLevelPreferenceChangedListener(this));
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

    /**
     * Listens for a change in the preference that contains the log level of the root logger.
     */
    private static class FileLevelPreferenceChangedListener implements
        Preference.OnPreferenceChangeListener {
        /**
         * Name of the logcat appender, as configured in logback.xml
         */
        private static final String FILE_APPENDER_NAME = "file";

        private final Context context;

        public FileLevelPreferenceChangedListener(Context context) {
            this.context = context;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Logs.setThresholdFilterLevel(newValue.toString(), FILE_APPENDER_NAME, context);
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
            findPreference(getString(R.string.preferences_key_log_level_file))
                .setOnPreferenceChangeListener(new FileLevelPreferenceChangedListener(getActivity()));
        }
    }

}

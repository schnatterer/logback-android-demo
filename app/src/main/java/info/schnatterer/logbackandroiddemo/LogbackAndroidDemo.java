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

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.slf4j.bridge.SLF4JBridgeHandler;

import info.schnatterer.logbackandroidutils.Logs;

/**
 * Application class, takes care of one-time initializations.
 */
public class LogbackAndroidDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        setLogLevelsFromSharedPreferences();

        installSlf4jJulHandler();
    }

    /**
     * Set the log levels to file and logcat appender from shared preferences.
     */
    void setLogLevelsFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Set root log level
        String logLevelRoot =
            sharedPreferences.getString(getString(R.string.preferences_key_log_level),
                getString(R.string.preferences_default_log_level));
        Logs.setRootLogLevel(logLevelRoot);

        // Set log level for file appender
        String logLevelFile =
            sharedPreferences.getString(getString(R.string.preferences_key_log_level_file),
                getString(R.string.preferences_default_log_level_file));
        Logs.setThresholdFilterLevel(logLevelFile, Constants.FILE_APPENDER_NAME, this);

        // Set log level for logcat appender
        String logLevelLogCat =
            sharedPreferences.getString(getString(R.string.preferences_key_log_level_logcat),
                getString(R.string.preferences_default_log_level_logcat));
        Logs.setLogCatLevel(logLevelLogCat, this);
    }

    /**
     * Installs the java.util.logging (jul-to-slf4j) {@link SLF4JBridgeHandler}.
     * As a result, all (yes, also {@link java.util.logging.Level#FINE}, etc.)
     * Level JUL log statements are routed to SLF4J.
     */
    private void installSlf4jJulHandler() {
        /*
         * add SLF4JBridgeHandler to j.u.l's root logger, should be done once
         * during the initialization phase of your application
         */
        SLF4JBridgeHandler.install();
    }
}

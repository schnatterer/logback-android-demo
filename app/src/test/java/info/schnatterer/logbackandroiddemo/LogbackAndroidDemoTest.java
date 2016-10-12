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
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.*;

import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.joran.spi.JoranException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * Tests for {@link LogbackAndroidDemo}.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.M)
public class LogbackAndroidDemoTest {

    LogbackAndroidDemo logbackAndroidDemo;

    @Before
    public void before() throws IOException, JoranException {
        initializeLogbackFromRobolectricAssets();
        logbackAndroidDemo = spy((LogbackAndroidDemo) RuntimeEnvironment.application);
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the file appender's log level is set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesFileAppender() throws Exception {
        String expectedLevel = "ERROR";
        putSharedPreference(R.string.preferences_key_log_level_file, expectedLevel);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Appender<ILoggingEvent> appender = assertAppender("file");
        assertAppenderThresholdFilterLevel(appender,
            Level.toLevel(expectedLevel));
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the file appender's log level is not set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesFileAppenderNotSet() throws Exception {
        String expectedLevel = "WARN";
        when(logbackAndroidDemo.getString(R.string.preferences_default_log_level_file))
            .thenReturn(expectedLevel);
        // Use default
        putSharedPreference(R.string.preferences_key_log_level_file, null);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Appender<ILoggingEvent> appender = assertAppender("file");
        assertAppenderThresholdFilterLevel(appender,
            Level.toLevel(expectedLevel));
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the logcat appender's log level is set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesLogCatAppender() throws Exception {
        String expectedLevel = "ERROR";
        putSharedPreference(R.string.preferences_key_log_level_logcat, expectedLevel);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Appender<ILoggingEvent> appender = assertAppender("logcat");
        assertAppenderThresholdFilterLevel(appender,
            Level.toLevel(expectedLevel));
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the logcat appender's log level is not set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesLogcatAppenderNotSet() throws Exception {
        String expectedLevel = "WARN";
        when(logbackAndroidDemo.getString(R.string.preferences_default_log_level_logcat))
            .thenReturn(expectedLevel);
        // Use default
        putSharedPreference(R.string.preferences_key_log_level_logcat, null);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Appender<ILoggingEvent> appender = assertAppender("logcat");
        assertAppenderThresholdFilterLevel(appender,
            Level.toLevel(expectedLevel));
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the root logger's log level is set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesRootLogger() throws Exception {
        String expectedLevel = "ERROR";
        putSharedPreference(R.string.preferences_key_log_level, expectedLevel);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals("Unexpected level set in logcat appender", Level.toLevel(expectedLevel),
            root.getLevel());
    }

    /**
     * Test for {@link LogbackAndroidDemo#setLogLevelsFromSharedPreferences()},
     * where the root logger's log level is not set.
     */
    @Test
    public void setLogLevelsFromSharedPreferencesRootNotSet() throws Exception {
        String expectedLevel = "WARN";
        when(logbackAndroidDemo.getString(R.string.preferences_default_log_level))
            .thenReturn(expectedLevel);
        // Use default
        putSharedPreference(R.string.preferences_key_log_level, null);

        // Call method under test
        logbackAndroidDemo.setLogLevelsFromSharedPreferences();

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        assertEquals("Unexpected level set in logcat appender", Level.toLevel(expectedLevel),
            root.getLevel());
    }

    /**
     * Makes sure logback is initialized from the same logback.xml as productive app.
     *
     * @throws IOException
     * @throws JoranException
     */
    private void initializeLogbackFromRobolectricAssets() throws IOException, JoranException {
    /* For some reasons, logback.xml is not found on the class path automatically.
     * So, set it explicitly...
     */
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        InputStream logbackXmlStream = null;
        try {
            logbackXmlStream = RuntimeEnvironment.application.getAssets().open("logback.xml");
            configurator.doConfigure(logbackXmlStream); // loads logback file
        } finally {
            if (logbackXmlStream != null) {
                logbackXmlStream.close();
            }
        }
    }

    /**
     * Asserts that <code>appender</code>'s level is equals <code>expected</code>.
     *
     * @param appender
     * @param expected
     */
    private void assertAppenderThresholdFilterLevel(Appender<ILoggingEvent> appender, Level expected) {
        ThresholdFilter thresholdFilter = getThresholdFilter(appender);
        if (thresholdFilter == null) {
            fail("Logcat appender did not contain a threshold filter");
        }

        // Unfortunately, we cant get the level from Threshold Filter in logback 1.1.1
        Field f;
        try {
            f = thresholdFilter.getClass().getDeclaredField("level");
            f.setAccessible(true);
            Level actual = (Level) f.get(thresholdFilter);
            assertEquals("Unexpected level set in logcat appender", expected, actual);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Unable to get level from logcat appender: " + e.getMessage());
        }
    }

    /**
     * @return the first Threshold filter from <code>appender</code> or <code>null</code> if
     * there is none.
     */
    private ThresholdFilter getThresholdFilter(Appender<ILoggingEvent> appender) {
        for (Filter<ILoggingEvent> filter : appender.getCopyOfAttachedFiltersList()) {
            if (filter instanceof ThresholdFilter) {
                return (ThresholdFilter) filter;
            }
        }
        return null;
    }

    /**
     * Asserts an appender is not <code>null</code>.
     *
     * @param appenderName
     * @return
     */
    @NonNull
    private Appender<ILoggingEvent> assertAppender(String appenderName) {
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        Appender<ILoggingEvent> fileAppender = root.getAppender(appenderName);
        assertNotNull("Not appender \"" + appenderName + "\" configured", fileAppender);
        return fileAppender;
    }

    /**
     * Wraps robolectric code for putting a String value to default shared preferences.
     */
    @SuppressLint("CommitPrefEdits")
    private void putSharedPreference(int resId, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RuntimeEnvironment.application);
        sharedPreferences.edit().putString(RuntimeEnvironment.application.getString(resId), value).commit();
    }
}

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
package info.schnatterer.logbackandroiddemo.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowToast;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.android.LogcatAppender;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Tests for {@link Logs}.
 * Hint: For development, start tests with <code>-Dlogback.debug=true</code> to get more
 * verbose output.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, emulateSdk = 18)
public class LogsTest {

    public static final URL LOGBACK_XML = LogsTest.class.getClassLoader().getResource("logback.xml");

    static {
        /* Thanks to gradle's "sourceFolderJavaResources" folder logback is not found on the class path
         * automatically. So, set it explicitly...
         */
        System.setProperty("logback.configurationFile", LOGBACK_XML.toString());
    }

    /**
     * The "newests" log file
     */
    String expectedPath = "test2015-12-01.log";

    /**
     * Test for {@link Logs#setRootLogLevel(String)}.
     */
    @Test
    public void testSetRootLogLevel() {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.OFF);
        Logs.setRootLogLevel("ALL");
        assertEquals("Unexpected root level set", Level.ALL, root.getLevel());
    }

    /**
     * Test for {@link Logs#setRootLogLevel(String)}, where a string is passed that is not a
     * {@link Level}.
     */
    @Test
    public void testSetRootLogLevelNotALevel() {
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        assertNotEquals("Unexpected initial root level", Level.DEBUG, root.getLevel());
        root.setLevel(Level.OFF);
        Logs.setRootLogLevel("Not a level");
        assertEquals("Unexpected root level set", Level.DEBUG, root.getLevel());
    }

    /**
     * Test for {@link Logs#findNewestLogFile(File[])}.
     */
    @Test
    public void testFindNewestLogFileFileArray() {
        File[] input = new File[]{new File("test2015-01-30.log"),
            new File("test2015-06-20.log"), new File(expectedPath)};
        File actualFile = Logs.findNewestLogFile(input);
        assertEquals("find newest returned unexpected result", expectedPath,
            actualFile.getPath());
    }

    /**
     * Test for {@link Logs#findNewestLogFile(File[])}, where the parameter is <code>null</code>.
     */
    @Test
    @SuppressWarnings("ConstantConditions")
    public void testFindNewestLogFileFileArrayNull() {
        File actualFile = Logs.findNewestLogFile((File[]) null);
        assertNull("find newest returned unexpected result", actualFile);
    }

    /**
     * Test for {@link Logs#findNewestLogFile(File[])}, where the parameter is an empty array.
     */
    @Test
    public void testFindNewestLogFileFileArrayEmpty() {
        File actualFile = Logs.findNewestLogFile(new File[0]);
        assertNull("find newest returned unexpected result", actualFile);
    }

    /**
     * Test for {@link Logs#findNewestLogFile(Context)}.
     */
    @Test
    public void testFindNewestLogFile() throws Exception {
        Context context = mock(Context.class);
        when(context.getFilesDir()).thenReturn(
            new File(getClass().getClassLoader().getResource(Logs.LOG_FOLDER).toURI()).getParentFile());

        // Call method under test
        File newestLogFile = Logs.findNewestLogFile(context);

        // Asserts
        assertTrue(
            "find newest returned unexpected result. Expected " + expectedPath + ", got " + newestLogFile.getPath(),
            newestLogFile.getPath().contains(expectedPath));
    }

    /**
     * Test for {@link Logs#setLogCatLevel(String, Context)}. This contains multiple test cases.
     * <p/>
     * TODO split those into separate tests.
     * Problem: How to re-initialize logback programmatically?
     * <p/>
     * JoranConfigurator.doConfigure(LOGBACK_XML);
     * or
     * ContextInitializer.autoConfig() don't seem to have an effect with Robolectric.
     */
    @Test
    public void testSetLogCatLevelExistingFilter() throws Exception {
        /*
         * Test Case 1: ExistingFilter
         */
        Logger root = (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        LogcatAppender logcatAppender = (LogcatAppender) root.getAppender(Logs.LOGCAT_APPENDER_NAME);
        assertLogcatLevel(logcatAppender, Level.INFO);

        // Call method under test
        Logs.setLogCatLevel("ALL", mock(Context.class));
        // Assert
        assertLogcatLevel(logcatAppender, Level.ALL);

        /*
         * Test Case 2: New Filter
         */
        logcatAppender.clearAllFilters();
        assertNull("Clearing filters failed", getThresholdFilter(logcatAppender));
        Logs.setLogCatLevel("ALL", mock(Context.class));
        // Assert
        assertLogcatLevel(logcatAppender, Level.ALL);

        /*
         * Test Case 3: Not a level
         */
        assertNotEquals("Unexpected initial root level", Level.DEBUG, root.getLevel());
        Logs.setLogCatLevel("Not a level", mock(Context.class));
        // Assert
        assertLogcatLevel(logcatAppender, Level.DEBUG);

        /*
         * Test Case 4: Different log levels
         */
        root.setLevel(Level.ERROR);
        Logs.setLogCatLevel("WARN", mock(Context.class));
        assertToastMatches("Root level.*>.*logcat");

        /*
         * Test Case 5: No logcat appender
         */
        root.detachAppender(logcatAppender);
        assertNull("Removing logcat appender failed", root.getAppender(Logs.LOGCAT_APPENDER_NAME));
        Logs.setLogCatLevel("Not a level", mock(Context.class));
        assertToastMatches(Logs.LOGCAT_APPENDER_NAME);
    }

    private void assertToastMatches(String expectedContainedInToast) {
        String latestToast = ShadowToast.getTextOfLatestToast();
        if (latestToast == null) {
            fail("Expected toast \"" + expectedContainedInToast + "\", but no toast");
        }
        assertTrue(
            String.format("Unexpected toast. Expected to match: \"%s\". Actual result: \"%s\"",
                expectedContainedInToast, latestToast),
            latestToast.matches(".*" + expectedContainedInToast + ".*"));
    }

    /**
     * Asserts that <code>logcatAppender</code>'s level is equals <code>expected</code>.
     *
     * @param logcatAppender
     * @param expected
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void assertLogcatLevel(LogcatAppender logcatAppender, Level expected) {
        ThresholdFilter thresholdFilter = getThresholdFilter(logcatAppender);
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
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            fail("Unable to get level from logcat appender: " + e.getMessage());
        }
    }

    /**
     * @return the first Threshold filter from <code>logcatAppender</code> or <code>null</code> if
     * there is none.
     */
    private ThresholdFilter getThresholdFilter(LogcatAppender logcatAppender) {
        for (Filter<ILoggingEvent> filter : logcatAppender.getCopyOfAttachedFiltersList()) {
            if (filter instanceof ThresholdFilter) {
                return (ThresholdFilter) filter;
            }
        }
        return null;
    }
}

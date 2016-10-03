/**
 * The MIT License (MIT)
 * <p>
 * Copyright (c) 2016 Johannes Schnatterer
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.schnatterer.logbackandroiddemo.util;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import android.content.Context;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.filter.Filter;

/**
 * Basic abstraction of the log mechanism (SLF4J + logback-android) used here.
 */
public final class Logs {
    /**
     * Name of the logcat appender, as configured in logback.xml
     */
    static final String LOGCAT_APPENDER_NAME = "logcat";

    /**
     * Name of the directory where log files are stored under
     * <code>/data/data/appname/files/</code> Create a reference to this
     * directory via {@link android.content.ContextWrapper#getFilesDir()}.
     */
    public static final String LOG_FOLDER = "logs";

    private Logs() {
    }

    /**
     * Set the log level of the root logger. <b>Note</b>: This depends on the
     * actual logging framework.
     *
     * @param level the log level to set
     */
    public static void setRootLogLevel(String level) {
        Logger root = (Logger) LoggerFactory
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        root.info("root.getLevel(): {}", root.getLevel().toString());
        root.info("Setting level to {}", level);
        root.setLevel(Level.toLevel(level));
    }

    /**
     * Returns the current log file from the default folder <code>/data/data/appname/files/logs</code>
     *
     * @param context context to get the app-private files from
     * @return the current log file
     */
    public static File findNewestLogFile(Context context) {
        return findNewestLogFile(getLogFiles(context, LOG_FOLDER));
    }

    /**
     * Returns the current log file.
     *
     * @param context           context to get the app-private files from
     * @param relativeLogFolder name of the directory where log files are stored under
     *                          <code>/data/data/appname/files/</code>
     * @return the current log file
     */
    public static File findNewestLogFile(Context context, String relativeLogFolder) {
        return findNewestLogFile(getLogFiles(context, relativeLogFolder));
    }

    /**
     * Returns all log files from the default folder <code>/data/data/appname/files/logs</code>
     *
     * @param context context to get the app-private files from
     * @return all log files
     */
    public static File[] getLogFiles(Context context) {
        return getLogFiles(context, LOG_FOLDER);
    }

    /**
     * Returns all log files.
     *
     * @param context           context to get the app-private files from
     * @param relativeLogFolder name of the directory where log files are stored under
     *                          <code>/data/data/appname/files/</code>
     * @return all log files
     */
    public static File[] getLogFiles(Context context, String relativeLogFolder) {
        return getLogFileDirectory(context, relativeLogFolder).listFiles();
    }

    /**
     * Returns the folder where log files are stored.
     *
     * @param context           context to get the app-private files from
     * @param relativeLogFolder name of the directory where log files are stored under
     *                          <code>/data/data/appname/files/</code>
     * @return the directory that contains log files
     */
    public static File getLogFileDirectory(Context context, String relativeLogFolder) {
        return new File(context.getFilesDir(), relativeLogFolder);
    }

    /**
     * Sets the log level for a {@link ThresholdFilter} of an appender called "logcat. Note:
     * <ul>
     * <li>If there is no such appender a warning is toasted.</li>
     * <li>If the appender does not have such a filter, a new one is added.</li>
     * <li>Note: The log level must be >= Root Log Level, because the root log level takes
     * precedence. If not, a warning is toasted.</li>
     * </ul>
     *
     * @param logLevelLogCat the threshold level to set for the logCat appender
     * @param context        (optional) context needed for toasting warnings. If
     *                       <code>null</code> no toast is displayed
     */
    public static void setLogCatLevel(String logLevelLogCat, Context context) {
        setThresholdFilterLevel(logLevelLogCat, LOGCAT_APPENDER_NAME, context);
    }

    /**
     * Sets the log level for a {@link ThresholdFilter} of an appender. Note:
     * <ul>
     * <li>If there is no such appender a warning is toasted.</li>
     * <li>If the appender does not have such a filter, a new one is added.</li>
     * <li>Note: The log level must be >= Root Log Level, because the root log level takes
     * precedence. If not, a warning is toasted.</li>
     * </ul>
     *
     * @param logLevel        the threshold level to set for the appender
     * @param logAppenderName name of the appender, as configured in logback.xml
     * @param context         (optional) context needed for toasting warnings. If
     *                        <code>null</code> no toast is displayed
     */
    public static void setThresholdFilterLevel(String logLevel, String logAppenderName, Context context) {
        /* Find appender */
        Logger root = (Logger) LoggerFactory
            .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        Appender<ILoggingEvent> appender = root.getAppender(logAppenderName);
        if (appender == null) {
            warnAndToast(context, root,
                "No appender \"" + logAppenderName + "\" configured. Can't change threshold");
            return;
        }

        setThresholdFilterLevel(logLevel, appender, root);

        /* Result */
        root.info("Setting appender \"{}\" level to {}. root.getLevel(): {}",
            // Avoid compile error "incompatible types: String cannot be converted to Marker"
            new Object[]{logAppenderName, logLevel, root.getLevel().toString()});

        if (greaterThan(root.getLevel(), Level.toLevel(logLevel))) {
            warnAndToast(context, root,
                String.format("Root level(%s) > appender \"%s\" level (%s)!",
                    root.getLevel(), logAppenderName, logLevel)
            );
        }
    }

    /**
     * Actually sets the threshold filter level. This in realized using a {@link ThresholdFilter}.
     *
     * @param logLevel the level to set to the threshold filter
     * @param appender appender to change
     * @param root     root logger, needed as context and for logging changes
     */
    private static void setThresholdFilterLevel(String logLevel,
                                                Appender<ILoggingEvent> appender, Logger root) {
        /* Find and change filter */
        List<Filter<ILoggingEvent>> filters = appender
            .getCopyOfAttachedFiltersList();
        ThresholdFilter threshold = null;
        for (Filter<ILoggingEvent> filter : filters) {
            if (filter instanceof ThresholdFilter) {
                threshold = (ThresholdFilter) filter;
                break;
            }
        }
        if (threshold == null) {
            root.info("No threshold filter in appender \"{}\" configured. Creating new one", appender.getName());
            threshold = new ThresholdFilter();
            threshold.setContext(root.getLoggerContext());
            filters.add(threshold);
        }
        threshold.setLevel(logLevel);
        threshold.start();

        /* Apply changed filter to appender */
        appender.clearAllFilters();
        for (Filter<ILoggingEvent> filter : filters) {
            appender.addFilter(filter);
        }
    }

    /**
     * @return <code>true</code> if <code>a</code> greater than <code>b</code>. Otherwise <code>false</code>
     */
    private static boolean greaterThan(Level a, Level b) {
        return a.toInt() > b.toInt();
    }

    /**
     * Writes message to {@link Logger#warn(Marker, String, Object)} and
     * {@link android.widget.Toast}.
     */
    private static void warnAndToast(Context context, Logger logger, String message) {
        logger.warn(message);
        if (context != null) {
            android.widget.Toast.makeText(context, message, android.widget.Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Sort array of files descending by name and returns the first one. For
     * file names like <code>2015-06-25.log</code> and
     * <code>2015-06-26.log</code> this returns the newest log file, for example
     * <code>2015-06-26.log</code>.
     *
     * @param logFiles the array of log files
     * @return the newest log file or <code>null</code> if <code>logFiles</code> is <code>null</code>
     */
    static File findNewestLogFile(File[] logFiles) {
        // Sort descendingly
        if (logFiles == null || logFiles.length == 0) {
            return null;
        }
        Arrays.sort(logFiles, Collections.reverseOrder());
        return logFiles[0];
    }
}

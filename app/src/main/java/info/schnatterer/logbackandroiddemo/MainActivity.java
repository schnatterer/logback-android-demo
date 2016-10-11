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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import info.schnatterer.logbackandroidutils.Logs;

public class MainActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(new ScrollingMovementMethod());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new ReadLogOnClickListener(textView, this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, PreferencesDeveloperActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * {@link View.OnClickListener}, that reads the newest logfile and writes it to a
     * {@link TextView}.
     */
    private static class ReadLogOnClickListener implements View.OnClickListener {
        private final TextView textView;
        private final Context context;

        /**
         * Creates a {@link View.OnClickListener}, that reads the newest logfile and writes it to a
         * {@link TextView}.
         *
         * @param textView the view to write the log to
         */
        public ReadLogOnClickListener(TextView textView, Context context) {
            this.textView = textView;
            this.context = context;
            // Init text
            setText(readLogFile());
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Wrote some statements to log", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            LOG.info("SLF4J info");
            jul();

            setText(readLogFile());
        }

        /**
         * Issue some java util logging statements.
         */
        private void jul() {
            java.util.logging.Logger jul = java.util.logging.Logger.getLogger(MainActivity.class.getName());
            jul.log(Level.FINEST, "JUL: FINEST");
            jul.finer("JUL: finer");
            jul.log(Level.FINER, "JUL: FINER");
            jul.log(Level.FINE, "JUL: FINE");
            jul.log(Level.CONFIG, "JUL: CONFIG");
            jul.log(Level.INFO, "JUL: INFO");
            jul.log(Level.WARNING, "JUL: WARNING");
            jul.log(Level.SEVERE, "JUL: SEVERE");
        }

        /**
         * @return the contents of the newest logfile
         */
        private String readLogFile() {
            String fileContent = "";
            try {
                File newestLogFile = Logs.findNewestLogFile(context);
                if (newestLogFile != null) {
                    fileContent = FileUtils.readFileToString(newestLogFile, "UTF-8");
                }
            } catch (IOException e) {
                LOG.warn("Unable to print log file", e);
                fileContent = e.getMessage();
            }
            return fileContent;
        }

        /**
         * <code>null</code>-safe version of {@link TextView#setText(CharSequence)}.
         */
        private void setText(CharSequence text) {
            if (textView != null) {
                textView.setText(text);
            }
        }
    }
}

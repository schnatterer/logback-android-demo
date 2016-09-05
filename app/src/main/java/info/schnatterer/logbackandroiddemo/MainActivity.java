package info.schnatterer.logbackandroiddemo;

import android.content.Context;
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

import info.schnatterer.logbackandroiddemo.util.Logs;

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
        }

        @Override
        public void onClick(View view) {
            Snackbar.make(view, "Wrote some statements to log", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            LOG.info("SLF4J info");
            jul();

            if (textView != null) {
                textView.setText(readLogFile());
                // TODO scroll to end of text view
            }
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
            String fileContent;
            try {
                File newestLogFile = Logs.findNewestLogFile(context);
                fileContent = FileUtils.readFileToString(newestLogFile, "UTF-8");
            } catch (IOException e) {
                LOG.warn("Unable to print log file", e);
                fileContent = e.getMessage();
            }
            return fileContent;
        }
    }
}

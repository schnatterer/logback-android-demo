package info.schnatterer.logbackandroiddemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.logging.Level;

public class MainActivity extends AppCompatActivity {

    private static final Logger LOG = LoggerFactory.getLogger(MainActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                LOG.info("SLF4J info");

                jul();
            }

            /**
             * Issue some java util logging statements.
             */
            private void jul() {
                java.util.logging.Logger jul = java.util.logging.Logger.getLogger(MainActivity.class.getName());
                jul.log( Level.FINEST, "JUL: FINEST");
                jul.finer("JUL: finer");
                jul.log( Level.FINER, "JUL: FINER");
                jul.log( Level.FINE, "JUL: FINE");
                jul.log( Level.CONFIG, "JUL: CONFIG");
                jul.log( Level.INFO, "JUL: INFO");
                jul.log( Level.WARNING, "JUL: WARNING");
                jul.log( Level.SEVERE, "JUL: SEVERE");
            }
        });
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
}

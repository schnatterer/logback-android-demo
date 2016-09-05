package info.schnatterer.logbackandroiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;

import info.schnatterer.logbackandroiddemo.util.Logs;

/**
 * Sends an intent that contains the newest log file as <code>text/plain</code>.
 * Note that you need to set up the {@link FileProvider} in your android
 * manifest. For example: <br/>
 *
 * <pre>
 *    &lt;!-- Expose log files for email clients --&gt;
 *    &lt;provider
 *         android:name=&quot;android.support.v4.content.FileProvider&quot;
 *         android:authorities=&quot;@string/authority_log_file_provider&quot;
 *         android:exported=&quot;false&quot;
 *         android:grantUriPermissions=&quot;true&quot; &gt;
 *         &lt;meta-data
 *             android:name=&quot;android.support.FILE_PROVIDER_PATHS&quot;
 *             android:resource=&quot;@xml/logpath&quot; /&gt;
 *     &lt;/provider&gt;
 * </pre>
 */
public class OpenLogActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get URIs for log files using android.support.v4.content.FileProvider */
        final Intent openFile = new Intent(Intent.ACTION_VIEW);
        Uri uriForFile = FileProvider.getUriForFile(this,
                getString(R.string.authority_log_file_provider),
                Logs.findNewestLogFile(this));
        openFile.setDataAndType(uriForFile, "text/plain");
        openFile.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(openFile);

        finish();
    }
}

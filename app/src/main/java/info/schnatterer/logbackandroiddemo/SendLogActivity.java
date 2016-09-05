package info.schnatterer.logbackandroiddemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;

import info.schnatterer.logbackandroiddemo.util.Logs;

/**
 * Sends an intent that contains all available log files. It is supposed to be
 * consumed by an emailer app. Note that you need to set up the
 * {@link FileProvider} in your android manifest. For example: <br/>
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
public class SendLogActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get URIs for log files using android.support.v4.content.FileProvider */
        ArrayList<Uri> uris = new ArrayList<>();
        for (final File fileEntry : Logs.getLogFiles(this)) {
            // Don't recurse!
            if (!fileEntry.isDirectory()) {
                // Create content provider URI
                uris.add(FileProvider.getUriForFile(this,
                        getString(R.string.authority_log_file_provider),
                        fileEntry));
            }
        }

        final Intent email = new Intent(Intent.ACTION_SEND_MULTIPLE);
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL,
                new String[] { "a@b.c" });
        email.putExtra(Intent.EXTRA_SUBJECT, getString(getApplicationInfo().labelRes));
        email.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        email.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(email);

        finish();
    }
}

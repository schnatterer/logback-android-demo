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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBarActivity;

import info.schnatterer.logbackandroidutils.Logs;

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

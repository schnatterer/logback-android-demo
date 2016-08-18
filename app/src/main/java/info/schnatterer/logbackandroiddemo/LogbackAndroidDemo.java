package info.schnatterer.logbackandroiddemo;

import android.app.Application;

import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Application class, takes care of one-time initializations.
 */
public class LogbackAndroidDemo extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        installSlf4jJulHandler();
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

logback-android-demo
====================
An example that shows some possibilities of using [logback-android](http://tony19.github.io/logback-android/):
- The app issues log statements when pressing a button and displays those in a the main text view (see [MainActivity](app/src/main/java/info/schnatterer/logbackandroiddemo/MainActivity.java)).
- There is an exemplary [logback.xml](app/src/main/assets/logback.xml),
  - writing to a file (that is rolled over daily) and
  - logcat. It also
  - forwards JUL to SLF4J/logback.
- The app also provides a [PreferencesDeveloperActivity](app/src/main/java/info/schnatterer/logbackandroiddemo/PreferencesDeveloperActivity.java) / [preferences_developer.xml](app/src/main/res/xml/preferences_developer.xml) that allows for
  - Setting the root/logcat levels at runtime
  - Opening the current logfile in an editor: [OpenLogActivity](app/src/main/java/info/schnatterer/logbackandroiddemo/OpenLogActivity.java)
  - Sending all logfiles via email: [SendLogActivity](app/src/main/java/info/schnatterer/logbackandroiddemo/SendLogActivity.java)

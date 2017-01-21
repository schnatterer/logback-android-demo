logback-android-demo
====================

 [![Build Status](https://jenkins.schnatterer.info/job/logback-android-demo/badge/icon)](https://jenkins.schnatterer.info/job/logback-android-demo/)
 [![License](https://img.shields.io/github/license/schnatterer/logback-android-demo.svg)](LICENSE)
 [<img alt="powered by openshift" align="right" src="https://www.openshift.com/images/logos/powered_by_openshift.png"/>](https://www.openshift.com/)

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
- The central logback-android logic is encapsulated in a separate JAR file: [logback-android-utils](https://github.com/schnatterer/logback-android-utils)


## Jenkins
Running [Jenkinsfile](Jenkinsfile) with the [pipeline plugin](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin) (tested with version 2.4) requires
- A JDK defined as Jenkins tool  (see [Jenkinsfile](Jenkinsfile) for name of JDK tool)
- Android SDK installed on Jenkins salve, matching the `compileSdkVersion` and `buildToolsVersion` as defined in [app/build.gradle](app/build.gradle)
- Optional: You can add a build parameter `RECIPIENTS` that contains a comma-separated list of all email recipie

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Programme\android-sdk\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# general logging
-keep class ch.qos.** { *; }
-keep class org.slf4j.** { *; }
-keepattributes *Annotation*
-dontwarn ch.qos.logback.core.net.*

-dontwarn org.slf4j.**

# Get rid of "warning: Ignoring InnerClasses attribute for an anonymous inner class"
-keepattributes EnclosingMethod

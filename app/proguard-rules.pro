# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
-renamesourcefileattribute SourceFile

 -keep class androidx.core.app.CoreComponentFactory { *; }
#-keep public class mobile.bullstop.io.malik.apis.** { *; }
#-keep public class app.vsptracker.others.** { *; }

# ----------------------- AWS Rules Started ---------------------------------
# These options are the minimal options for a functioning application
# using Proguard and the AWS SDK 2.1.5 for Android

#-keep class org.apache.commons.logging.**               { *; }
#-keep class com.amazonaws.org.apache.commons.logging.** { *; }
#-keep class com.amazonaws.services.sqs.QueueUrlHandler  { *; }
#-keep class com.amazonaws.javax.xml.transform.sax.*     { public *; }
#-keep class com.amazonaws.javax.xml.stream.**           { *; }
#-keep class com.amazonaws.services.**.model.*Exception* { *; }
#-keep class com.amazonaws.internal.**                   { *; }
#-keep class org.codehaus.**                             { *; }
#-keep class org.joda.time.tz.Provider                   { *; }
#-keep class org.joda.time.tz.NameProvider               { *; }
-keepattributes Signature,*Annotation*,EnclosingMethod
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class com.amazonaws.** { *; }

-dontwarn com.fasterxml.jackson.databind.**
-dontwarn javax.xml.stream.events.**
-dontwarn org.codehaus.jackson.**
-dontwarn org.apache.commons.logging.impl.**
-dontwarn org.apache.http.conn.scheme.**
-dontwarn org.apache.http.annotation.**
-dontwarn org.ietf.jgss.**
-dontwarn org.joda.convert.**
-dontwarn com.amazonaws.org.joda.convert.**
-dontwarn org.w3c.dom.bootstrap.**

#SDK split into multiple jars so certain classes may be referenced but not used
-dontwarn com.amazonaws.services.s3.**
-dontwarn com.amazonaws.services.sqs.**

-dontnote com.amazonaws.services.sqs.QueueUrlHandler

-keep class com.amazonaws.auth.AWS4Signer
-keep class com.amazonaws.services.s3.internal.S3Signer
# ----------------------------------------  AWS Rules Ended -------------------------------------

# ----------------------------------------  GSON Rules Started --------------------------------------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
#-keepattributes Signature

# Gson specific classes
#-keep class sun.misc.Unsafe { *; }
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
 -keep class app.vsptracker.apis.** { *; }
# ----------------------------------------  GSON Rules Ended  ------------------------------------------
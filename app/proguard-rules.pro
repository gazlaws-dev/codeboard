# Project-specific ProGuard rules for Codeboard
# These rules ensure that essential classes are kept to prevent installation and runtime issues.

# Keep all project classes to ensure the IME and main activities function correctly
-keep class com.gazlaws.codeboard.** { *; }

# Keep Input Method Service and its internal implementations
-keep class * extends android.inputmethodservice.InputMethodService
-keep class * extends android.inputmethodservice.InputMethodService$InputMethodImpl

# AndroidX and Material Design components keep rules (Prevents issues with inflation and reflection)
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.preference.** { *; }
-keep class androidx.annotation.** { *; }
-keep class androidx.core.** { *; }

# External Libraries: AppIntro and Color Picker
-keep class com.github.appintro.** { *; }
-keep class com.github.evilbunny2008.** { *; }

# Maintain standard Android component entry points
-keepattributes *Annotation*, Signature, SourceFile, LineNumberTable
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

# Support for Javascript interfaces if utilized in WebViews
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve R class for resource access via reflection or dynamic lookup
-keep class **.R$* { *; }

# Maintain View constructors and setters for XML layout inflation
-keepclassmembers class * extends android.view.View {
   public <init>(android.content.Context);
   public <init>(android.content.Context, android.util.AttributeSet);
   public <init>(android.content.Context, android.util.AttributeSet, int);
   public void set*(...);
}

# Kotlin metadata preservation for hybrid interoperability
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-dontwarn org.jetbrains.kotlin.**

# Suppress warnings from common libraries that may be safely ignored during minification
-dontwarn androidx.**
-dontwarn com.google.android.material.**

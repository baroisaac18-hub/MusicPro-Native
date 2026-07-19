# ProGuard rules for MusicPro Native
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep ExoPlayer
-keep class androidx.media3.** { *; }
-dontwarn androidx.media3.**

# Keep Coil
-keep class coil.** { *; }
-dontwarn coil.**

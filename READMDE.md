# MusicPro Native - Android App

مشغل موسيقى أصلي بالكامل لنظام Android، مبني بلغة Kotlin + Jetpack Compose. إعادة بناء كاملة للتطبيق الهجين السابق (React+Capacitor) إلى تطبيق Android أصلي.

## التقنيات المستخدمة

| المكون | التقنية |
|--------|---------|
| اللغة | Kotlin |
| واجهة المستخدم | Jetpack Compose + Material 3 |
| تشغيل الصوت | AndroidX Media3 ExoPlayer |
| مكتبة الموسيقى | MediaStore API |
| الإشعارات | MediaSession + NotificationCompat |
| التنقل | Compose Tabs |
| تحميل الصور | Coil |

## الميزات

- 4 علامات تبويب: الرئيسية، المكتبة، البحث، المفضلة
- مشغل كامل مع غلاف الألبوم وتحكم بالسحب
- مشغل مصغر مع أزرار التشغيل
- دعم أنماط التكرار: بدون/الكل/أغنية واحدة
- تشغيل عشوائي
- وضع العلامات المرجعية (Bookmarks - Mark This Moment)
- المفضلة
- إشعارات الوسائط مع أزرار التحكم
- ترتيب المكتبة حسب: العنوان/الفنان/الألبوم/المدة/تاريخ الإضافة
- بحث فوري
- واجهة Glassmorphism داكنة

## البناء

افتح المشروع في Android Studio وقم بالبناء كالمعتاد. المتطلبات:

- Android Studio Hedgehog (2023.1.1) أو أحدث
- JDK 17
- Gradle 8.5 (مضمن في wrapper)

```
./gradlew assembleRelease
```

## الهيكل

```
src/main/java/com/musicpro/native/
├── MusicProApp.kt          # Application class
├── MainActivity.kt         # Activity الرئيسية
├── data/
│   ├── Song.kt             # نماذج البيانات
│   └── MusicRepository.kt  # محمل الموسيقى من الجهاز
├── player/
│   └── AudioPlayer.kt      # مدير تشغيل الصوت (ExoPlayer)
├── notification/
│   ├── MusicNotificationManager.kt  # إدارة الإشعارات
│   └── NotificationReceiver.kt      # مستقبل أزرار الإشعارات
├── theme/
│   ├── Color.kt            # لوحة الألوان
│   └── Theme.kt            # السمة والنمط
└── ui/
    ├── MusicProScreen.kt   # الشاشة الرئيسية + المشغل المصغر
    ├── home/HomeScreen.kt  # علامة التبويب الرئيسية
    ├── library/LibraryScreen.kt  # علامة تبويب المكتبة
    ├── search/SearchScreen.kt    # علامة تبويب البحث
    ├── favorites/FavoritesScreen.kt  # علامة تبويب المفضلة
    └── player/FullPlayerScreen.kt   # المشغل الكامل
```

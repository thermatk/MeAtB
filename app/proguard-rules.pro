# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
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
-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class * { *; }
-dontwarn javax.**
-dontwarn io.realm.**

-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

-keep class android.support.v7.widget.RoundRectDrawable { *; }

-keep class com.mikepenz.iconics.** { *; }
-keep class com.mikepenz.community_material_typeface_library.CommunityMaterial
-keep class com.mikepenz.fontawesome_typeface_library.FontAwesome
-keep class com.mikepenz.google_material_typeface_library.GoogleMaterial
-keep class com.mikepenz.meteocons_typeface_library.Meteoconcs
-keep class com.mikepenz.octicons_typeface_library.Octicons

-keep class cz.msebera.android.httpclient.** { *; }
-keep class com.loopj.android.http.** { *; }


-keep class com.github.tibolte.agendacalendarview.** { *; }


-keep public interface com.github.tibolte.agendacalendarview.models.IWeekItem { *; }
-keep public interface com.github.tibolte.agendacalendarview.models.IDayItem { *; }
-keep public class com.github.tibolte.agendacalendarview.models.** {*; }
-keep public class com.github.tibolte.agendacalendarview.render.** {*; }
-keep public class com.github.tibolte.agendacalendarview.agenda.** {*; }
-keep public class com.github.tibolte.agendacalendarview.agenda.AgendaAdapter {*; }
-keep public class com.github.tibolte.agendacalendarview.render.EventRenderer {*; }

-dontwarn java.lang.invoke.*
-dontwarn sun.misc.Unsafe

-keep public class se.emilsjolander.stickylistheaders.** {*; }


-keep public class com.thermatk.android.meatb.agenda.** { *; }
-keep public class com.thermatk.android.meatb.agenda.ACVDay { *; }
-keep public class com.thermatk.android.meatb.agenda.ACVWeek { *; }
-keep public class com.thermatk.android.meatb.agenda.BocconiCalendarEvent { *; }

-keep class com.thermatk.android.meatb.agenda.** { *; }
-keep class com.thermatk.android.meatb.agenda.ACVDay { *; }
-keep class com.thermatk.android.meatb.agenda.ACVWeek { *; }
-keep class com.thermatk.android.meatb.agenda.BocconiCalendarEvent { *; }

-keepattributes Signature
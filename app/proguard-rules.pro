# Retrofit
-keepattributes Signature
-keepattributes Exceptions

# Gson
-keepattributes *Annotation*
-keep class com.scrapeverything.app.data.model.** { *; }
-keep class com.scrapeverything.app.data.repository.BackupData { *; }
-keep class com.scrapeverything.app.data.repository.BackupCategoryData { *; }
-keep class com.scrapeverything.app.data.repository.BackupScrapData { *; }

# Gson 역직렬화 대상: @SerializedName 필드 보호
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

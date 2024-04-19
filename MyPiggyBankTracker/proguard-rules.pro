-keepattributes *Annotation*, Signature, Exception, SourceFile, LineNumberTable
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
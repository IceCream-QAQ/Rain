package rain.di.fun;

import kotlin.Metadata;
import kotlin.reflect.jvm.internal.impl.km.ClassKind;
import kotlin.reflect.jvm.internal.impl.km.KmClass;
import kotlin.reflect.jvm.internal.impl.km.jvm.KotlinClassMetadata;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags;import org.jetbrains.annotations.NotNull;import org.jetbrains.annotations.Nullable;

public class KHelper {

    public static @Nullable KmClass findKmClass(@NotNull Class<?> clazz) {
        Metadata annotation = clazz.getAnnotation(Metadata.class);
        if (annotation == null) return null;
        return parseKmClass(annotation);
    }

    public static @Nullable KmClass parseKmClass(@NotNull Metadata metadata) {
        KotlinClassMetadata kcm = KotlinClassMetadata.Companion.readLenient(metadata);
        if (!(kcm instanceof KotlinClassMetadata.Class)) return null;
        return ((KotlinClassMetadata.Class) kcm).getKmClass();
    }

    public static int getKmClassFlags(@NotNull KmClass kmClass) {
        return kmClass.getFlags$kotlin_metadata();
    }

    public static int getKmKindFlag(@NotNull KmClass kmClass) {
        int flags = getKmClassFlags(kmClass);
        Flags.FlagField<ProtoBuf.Class.Kind> flag = Flags.CLASS_KIND;
        int offset = flag.offset;
        int len = flag.bitWidth;
        return (flags >> offset) & ((1 << len) - 1);
    }

    public static Integer getKmKindFlag(@NotNull Class<?> clazz) {
        KmClass kmClass = findKmClass(clazz);
        if (kmClass == null) return null;
        return getKmKindFlag(kmClass);
    }

    public static @Nullable ClassKind getKmClassKind(@NotNull KmClass kmClass) {
        int value = getKmKindFlag(kmClass);
        return ClassKind.getEntries()
                .stream()
                .filter(it -> it.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value)
                .findFirst()
                .orElse(null);
    }

    public static boolean isObject(int value) {
        return ClassKind.OBJECT.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value;
    }

    public static boolean isObject(@NotNull ClassKind value) {
        return ClassKind.OBJECT.equals(value);
    }

    public static boolean isObjectOrCompanionObject(Metadata metadata) {
        KmClass kmClass = parseKmClass(metadata);
        if (kmClass == null) return false;
        int kind = getKmKindFlag(kmClass);
        return isObject(kind) || isCompanionObject(kind);
    }

    public static boolean isCompanionObject(int value) {
        return ClassKind.COMPANION_OBJECT.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value;
    }

    public static boolean isCompanionObject(@NotNull ClassKind value) {
        return ClassKind.COMPANION_OBJECT.equals(value);
    }

}

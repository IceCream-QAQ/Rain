package rain.fun.kt.metadata.v2_0_0;

import kotlin.Metadata;
import kotlin.reflect.jvm.internal.impl.km.ClassKind;
import kotlin.reflect.jvm.internal.impl.km.KmClass;
import kotlin.reflect.jvm.internal.impl.km.jvm.KotlinClassMetadata;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rain.fun.kt.metadata.KHelper;

public class KHelperImpl implements KHelper {

    public @Nullable KmClass findKmClass(@NotNull Class<?> clazz) {
        Metadata annotation = clazz.getAnnotation(Metadata.class);
        if (annotation == null) return null;
        return parseKmClass(annotation);
    }

    public @Nullable KmClass parseKmClass(@NotNull Metadata metadata) {
        KotlinClassMetadata kcm = KotlinClassMetadata.Companion.readLenient(metadata);
        if (!(kcm instanceof KotlinClassMetadata.Class)) return null;
        return ((KotlinClassMetadata.Class) kcm).getKmClass();
    }

    public int getKmClassFlags(@NotNull KmClass kmClass) {
        return kmClass.getFlags$kotlin_metadata();
    }

    public int getKmKindFlag(@NotNull KmClass kmClass) {
        int flags = getKmClassFlags(kmClass);
        Flags.FlagField<ProtoBuf.Class.Kind> flag = Flags.CLASS_KIND;
        int offset = flag.offset;
        int len = flag.bitWidth;
        return (flags >> offset) & ((1 << len) - 1);
    }

    public Integer getKmKindFlag(@NotNull Class<?> clazz) {
        KmClass kmClass = findKmClass(clazz);
        if (kmClass == null) return null;
        return getKmKindFlag(kmClass);
    }

    public @Nullable ClassKind getKmClassKind(@NotNull KmClass kmClass) {
        int value = getKmKindFlag(kmClass);
        return ClassKind.getEntries()
                .stream()
                .filter(it -> it.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value)
                .findFirst()
                .orElse(null);
    }

    public boolean isObject(int value) {
        return ClassKind.OBJECT.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value;
    }

    public boolean isObject(@NotNull ClassKind value) {
        return ClassKind.OBJECT.equals(value);
    }

    public boolean isObjectOrCompanionObject(Metadata metadata) {
        KmClass kmClass = parseKmClass(metadata);
        if (kmClass == null) return false;
        int kind = getKmKindFlag(kmClass);
        return isObject(kind) || isCompanionObject(kind);
    }

    public boolean isCompanionObject(int value) {
        return ClassKind.COMPANION_OBJECT.getFlag$kotlin_metadata().getValue$kotlin_metadata() == value;
    }

    public boolean isCompanionObject(@NotNull ClassKind value) {
        return ClassKind.COMPANION_OBJECT.equals(value);
    }

}

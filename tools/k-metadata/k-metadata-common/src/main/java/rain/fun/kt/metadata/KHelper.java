package rain.fun.kt.metadata;

import kotlin.Metadata;
import kotlin.reflect.jvm.internal.impl.km.ClassKind;
import kotlin.reflect.jvm.internal.impl.km.KmClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface KHelper {
    @Nullable KmClass findKmClass(@NotNull Class<?> clazz);

    @Nullable KmClass parseKmClass(@NotNull Metadata metadata);

    int getKmClassFlags(@NotNull KmClass kmClass);

    int getKmKindFlag(@NotNull KmClass kmClass);

    Integer getKmKindFlag(@NotNull Class<?> clazz);

    @Nullable ClassKind getKmClassKind(@NotNull KmClass kmClass);

    boolean isObject(int value);

    boolean isObject(@NotNull ClassKind value);

    boolean isObjectOrCompanionObject(Metadata metadata);

    boolean isCompanionObject(int value);

    boolean isCompanionObject(@NotNull ClassKind value);
}

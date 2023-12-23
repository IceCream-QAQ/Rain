package rain.hook;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HookItem {

    private String type;

    private String basedClass;
    private String basedMethod;
    private String basedDesc;

    private String runnable;

    @NotNull
    public String getType() {
        return type;
    }

    @NotNull
    public String getBasedClass() {
        return basedClass;
    }

    @Nullable
    public String getBasedMethod() {
        return basedMethod;
    }

    @Nullable
    public String getBasedDesc() {
        return basedDesc;
    }

    @NotNull
    public String getRunnable() {
        return runnable;
    }

    @Deprecated
    public HookItem(String className, String methodName, String runnable) {
        this.type = "method";
        this.basedClass = className;
        this.basedMethod = methodName;
        this.runnable = runnable;
    }

    private HookItem(String type, String basedClass, String basedMethod, String basedDesc, String runnable) {
        this.type = type;
        this.basedClass = basedClass;
        this.basedMethod = basedMethod;
        this.basedDesc = basedDesc;
        this.runnable = runnable;
    }

    public static HookItem hookMethod(@NotNull String className, @NotNull String methodName, @Nullable String descriptor, @NotNull String runnable) {
        return new HookItem("method", className, methodName, descriptor, runnable);
    }

    public static HookItem HookInterface(@NotNull String className, @NotNull String methodName, @Nullable String descriptor, @NotNull String runnable) {
        return new HookItem("interface", className, methodName, descriptor, runnable);
    }

    @Override
    public String toString() {
        return "HookItem{" +
                "type='" + type + '\'' +
                ", basedClass='" + basedClass + '\'' +
                ", basedMethod='" + basedMethod + '\'' +
                ", basedDesc='" + basedDesc + '\'' +
                ", runnable='" + runnable + '\'' +
                '}';
    }
}

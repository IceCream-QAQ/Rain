package com.IceCreamQAQ.Yu.event;

import com.IceCreamQAQ.Yu.event.events.Event;
import com.IceCreamQAQ.Yu.loader.SpawnClassLoader;
import lombok.val;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;


public class EventInvokerCreator {

    private static final String listererClassName = Type.getInternalName(EventInvoker.class);
    private static final String HANDLER_FUNC_DESC = Type.getMethodDescriptor(EventInvoker.class.getDeclaredMethods()[0]);

    private Integer num = 0;

    @Inject
    private EventBus eventBus;

    @Inject
    private SpawnClassLoader classLoader;

    List<EventInvoker>[] register(Object o){
        try {
            return registerEventListener(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    void invoke(Event event, Integer level) {
//        List<EventInvoker> listeners;
//        switch (level) {
//            case 0:
//                listeners = height;
//                break;
//            case 1:
//                listeners = normal;
//                break;
//            case 2:
//                listeners = low;
//                break;
//            default:
//                listeners = null;
//        }
//        if (listeners==null)return;
//        for (EventInvoker listener : listeners) {
//            listener.invoke(event);
//            if (event.cancelAble() && event.cancel)return;
//        }
//    }


    private List<EventInvoker>[] registerEventListener(Object object) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Class<?> pluginEventClass = object.getClass();

        List<EventInvoker> height = new ArrayList<>();
        List<EventInvoker> normal = new ArrayList<>();
        List<EventInvoker> low = new ArrayList<>();

        List<EventInvoker>[] eventInvokers = new List[]{height,normal,low};
        for (Method method : pluginEventClass.getMethods()) {
            val e = method.getAnnotation(com.IceCreamQAQ.Yu.annotation.Event.class);
            if (e == null) continue;
            int methodParaCount = method.getParameterCount();
            if (methodParaCount != 1) continue;
            EventInvoker invoker;
            if (Modifier.isStatic(method.getModifiers()))
                invoker = (EventInvoker) createEventHandlerInvokerClass(method).newInstance();
            else
                invoker = (EventInvoker) createEventHandlerInvokerClass(method).getConstructor(Object.class).newInstance(object);

            switch (e.weight()) {
                case low:
                    if (low == null) low = new ArrayList<>();
                    low.add(invoker);
                    break;
                case normal:
                    if (normal == null) normal = new ArrayList<>();
                    normal.add(invoker);
                    break;
                case height:
                case high:
                    if (height == null) height = new ArrayList<>();
                    height.add(invoker);
                    break;
                default:
                    break;
            }
        }
        return eventInvokers;
    }

    private Class<?> createEventHandlerInvokerClass(Method method) {
        ClassWriter cw = new ClassWriter(0);
        MethodVisitor mv;

        boolean isStatic = Modifier.isStatic(method.getModifiers());
        String name = getUniqueName(method);
        String desc = name.replace('.', '/');
        String instType = Type.getInternalName(method.getDeclaringClass());
        String eventType = Type.getInternalName(method.getParameterTypes()[0]);


        cw.visit(V1_6, ACC_PUBLIC | ACC_SUPER, desc, null, "java/lang/Object", new String[]{listererClassName});

        cw.visitSource("YuCoreAutoCreatedEventInvoker.java", null);
        {
            if (!isStatic)
                cw.visitField(ACC_PUBLIC, "instance", "Ljava/lang/Object;", null, null).visitEnd();
        }
        {
            mv = cw.visitMethod(ACC_PUBLIC, "<init>", isStatic ? "()V" : "(Ljava/lang/Object;)V", null, null);
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
            if (!isStatic) {
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitFieldInsn(PUTFIELD, desc, "instance", "Ljava/lang/Object;");
            }
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        {
            String paraClass = Type.getInternalName(method.getParameterTypes()[0]);
            mv = cw.visitMethod(ACC_PUBLIC, "invoke", HANDLER_FUNC_DESC, null, null);
            mv.visitCode();
            Label label0 = new Label();
            mv.visitLabel(label0);
            mv.visitLineNumber(16, label0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(INSTANCEOF, paraClass);
            Label label1 = new Label();
            mv.visitJumpInsn(IFNE, label1);
            mv.visitInsn(RETURN);
            mv.visitLabel(label1);
            mv.visitLineNumber(17, label1);
            mv.visitVarInsn(ALOAD, 0);
            if (!isStatic) {
                mv.visitFieldInsn(GETFIELD, desc, "instance", "Ljava/lang/Object;");
                mv.visitTypeInsn(CHECKCAST, instType);
            }
            mv.visitVarInsn(ALOAD, 1);
            mv.visitTypeInsn(CHECKCAST, eventType);
            mv.visitMethodInsn(isStatic ? INVOKESTATIC : INVOKEVIRTUAL, instType, method.getName(), Type.getMethodDescriptor(method), false);
            mv.visitInsn(RETURN);
            mv.visitMaxs(2, 2);
            mv.visitEnd();
        }
        cw.visitEnd();
        Class<?> ret = classLoader.define(name, cw.toByteArray());
        return ret;
    }


    private String getUniqueName(Method callback) {
        return String.format("YuQ_EventHandlerClass_%d_%s_%s_%s_IceCreamQAQ_OpenSource_YuFramework",
                num,
                callback.getDeclaringClass().getSimpleName(),
                callback.getName(),
                callback.getParameterTypes()[0].getSimpleName());
    }

}

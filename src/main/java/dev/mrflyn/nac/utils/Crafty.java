package dev.mrflyn.nac.utils;

import org.bukkit.Bukkit;
import org.checkerframework.common.reflection.qual.ForName;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public final class Crafty {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static final String PREFIX_NMS = "net.minecraft.server";

    private static final String PREFIX_CRAFTBUKKIT = "org.bukkit.craftbukkit";

    private static final String CRAFT_SERVER = "CraftServer";

    private static final String VERSION;

    static {
        Class<?> serverClass = Bukkit.getServer().getClass();
        if (!serverClass.getSimpleName().equals("CraftServer")) {
            VERSION = null;
        } else if (serverClass.getName().equals("org.bukkit.craftbukkit.CraftServer")) {
            VERSION = ".";
        } else {
            String name = serverClass.getName();
            name = name.substring("org.bukkit.craftbukkit".length());
            name = name.substring(0, name.length() - "CraftServer".length());
            VERSION = name;
        }
    }

    public static Class<?> needNMSClassOrElse(String nms, String... classNames) throws RuntimeException {
        Class<?> nmsClass = findNmsClass(nms);
        if (nmsClass != null)
            return nmsClass;
        for (String name : classNames) {
            Class<?> maybe = findClass(name);
            if (maybe != null)
                return maybe;
        }
        throw new IllegalStateException(String.format("Couldn't find a class! NMS: '%s' or '%s'.", nms,
                Arrays.toString(classNames)));
    }

    @ForName
    public static Class<?> findClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public static boolean hasClass(String className) {
        return (findClass(className) != null);
    }

    public static MethodHandle findMethod(Class<?> holderClass, String methodName, Class<?> returnClass, Class<?>... parameterClasses) {
        if (holderClass == null || returnClass == null)
            return null;
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null)
                return null;
        }
        try {
            return LOOKUP.findVirtual(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
        } catch (NoSuchMethodException|IllegalAccessException e) {
            return null;
        }
    }

    public static MethodHandle findStaticMethod(Class<?> holderClass, String methodName, Class<?> returnClass, Class<?>... parameterClasses) {
        if (holderClass == null || returnClass == null)
            return null;
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null)
                return null;
        }
        try {
            return LOOKUP.findStatic(holderClass, methodName, MethodType.methodType(returnClass, parameterClasses));
        } catch (NoSuchMethodException|IllegalAccessException e) {
            return null;
        }
    }

    public static boolean hasField(Class<?> holderClass, String name, Class<?> type) {
        if (holderClass == null)
            return false;
        try {
            Field field = holderClass.getDeclaredField(name);
            return (field.getType() == type);
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public static boolean hasMethod(Class<?> holderClass, String methodName, Class<?>... parameterClasses) {
        if (holderClass == null)
            return false;
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null)
                return false;
        }
        try {
            holderClass.getMethod(methodName, parameterClasses);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static MethodHandle findConstructor(Class<?> holderClass, Class<?>... parameterClasses) {
        if (holderClass == null)
            return null;
        for (Class<?> parameterClass : parameterClasses) {
            if (parameterClass == null)
                return null;
        }
        try {
            return LOOKUP.findConstructor(holderClass, MethodType.methodType(void.class, parameterClasses));
        } catch (NoSuchMethodException|IllegalAccessException e) {
            return null;
        }
    }

    public static Field needField(Class<?> holderClass, String fieldName) throws NoSuchFieldException {
        Field field = holderClass.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field;
    }

    public static Field findField(Class<?> holderClass, String fieldName) {
        return findField(holderClass, fieldName, null);
    }

    public static Field findField(Class<?> holderClass, String fieldName, Class<?> expectedType) {
        Field field;
        if (holderClass == null)
            return null;
        try {
            field = holderClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            return null;
        }
        field.setAccessible(true);
        if (expectedType != null && !expectedType.isAssignableFrom(field.getType()))
            return null;
        return field;
    }

    public static MethodHandle findSetterOf(Field field) {
        if (field == null)
            return null;
        try {
            return LOOKUP.unreflectSetter(field);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static MethodHandle findGetterOf(Field field) {
        if (field == null)
            return null;
        try {
            return LOOKUP.unreflectGetter(field);
        } catch (IllegalAccessException e) {
            return null;
        }
    }

    public static Object findEnum(Class<?> enumClass, String enumName) {
        return findEnum(enumClass, enumName, 2147483647);
    }

    public static Object findEnum(Class<?> enumClass, String enumName, int enumFallbackOrdinal) {
        if (enumClass == null || !Enum.class.isAssignableFrom(enumClass))
            return null;
        try {
            return Enum.valueOf((Class)enumClass.asSubclass(Enum.class), enumName);
        } catch (IllegalArgumentException e) {
            Object[] constants = enumClass.getEnumConstants();
            if (constants.length > enumFallbackOrdinal)
                return constants[enumFallbackOrdinal];
            return null;
        }
    }

    public static boolean isCraftBukkit() {
        return (VERSION != null);
    }

    public static String findCraftClassName(String className) {
        return isCraftBukkit() ? ("org.bukkit.craftbukkit" + VERSION + className) : null;
    }

    @ForName
    public static Class<?> findCraftClass(String className) {
        String craftClassName = findCraftClassName(className);
        if (craftClassName == null)
            return null;
        return findClass(craftClassName);
    }

    @ForName
    public static <T> Class<? extends T> findCraftClass(String className, Class<T> superClass) {
        Class<?> craftClass = findCraftClass(className);
        if (craftClass == null || !((Class)Objects.<Class<?>>requireNonNull(superClass, "superClass")).isAssignableFrom(craftClass))
            return null;
        return craftClass.asSubclass(superClass);
    }

    @ForName
    public static Class<?> needCraftClass(String className) {
        return Objects.<Class<?>>requireNonNull(findCraftClass(className), "Could not find org.bukkit.craftbukkit class " + className);
    }

    public static String findNmsClassName(String className) {
        return isCraftBukkit() ? ("net.minecraft.server" + VERSION + className) : null;
    }

    @ForName
    public static Class<?> findNmsClass(String className) {
        String nmsClassName = findNmsClassName(className);
        if (nmsClassName == null)
            return null;
        return findClass(nmsClassName);
    }

    @ForName
    public static Class<?> needNmsClass(String className) {
        return Objects.<Class<?>>requireNonNull(findNmsClass(className), "Could not find net.minecraft.server class " + className);
    }

    public static MethodHandles.Lookup lookup() {
        return LOOKUP;
    }
}

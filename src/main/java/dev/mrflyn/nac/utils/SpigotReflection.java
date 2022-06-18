package dev.mrflyn.nac.utils;


import org.bukkit.entity.Player;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import static dev.mrflyn.nac.utils.Crafty.needCraftClass;
import static dev.mrflyn.nac.utils.Crafty.needNmsClass;


public final class SpigotReflection {
    private static SpigotReflection INSTANCE;

    public static SpigotReflection get() {
        if (INSTANCE == null) {
            synchronized (SpigotReflection.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SpigotReflection();
                }
            }
        }
        return INSTANCE;
    }

    private SpigotReflection() {
    }

    private final Class<?> MinecraftServer_class = needNmsClass("MinecraftServer");
    private final Class<?> CraftPlayer_class = needCraftClass("entity.CraftPlayer");
    private final Class<?> EntityPlayer_class = needNmsClass("EntityPlayer");

    private final MethodHandle CraftPlayer_getHandle_method = needMethod(this.CraftPlayer_class, "getHandle", this.EntityPlayer_class);
    private final MethodHandle MinecraftServer_getServer_method = needStaticMethod(this.MinecraftServer_class, "getServer", this.MinecraftServer_class);

    private final Field EntityPlayer_ping_field = needField(this.EntityPlayer_class, "ping");
    private final Field MinecraftServer_recentTickTimes_field = needField(this.MinecraftServer_class, "h");
    private final Field MinecraftServer_recentTps_field = needField(this.MinecraftServer_class, "recentTps");

    public int ping(final Player player) {
        final Object nmsPlayer = invokeOrThrow(this.CraftPlayer_getHandle_method, player);
        try {
            return this.EntityPlayer_ping_field.getInt(nmsPlayer);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException(String.format("Failed to get ping for player: '%s'", player.getName()), e);
        }
    }


    public double[] recentTps() {
        final Object server = invokeOrThrow(this.MinecraftServer_getServer_method);
        try {
            return (double[]) this.MinecraftServer_recentTps_field.get(server);
        } catch (final IllegalAccessException e) {
            throw new IllegalStateException("Failed to get server TPS", e);
        }
    }


    private static MethodHandle needMethod(final Class<?> holderClass, final String methodName, final Class<?> returnClass, final Class<?>  ... parameterClasses) {
        return Objects.requireNonNull(
                Crafty.findMethod(holderClass, methodName, returnClass, parameterClasses),
                String.format(
                        "Could not locate method '%s' in class '%s'",
                        methodName,
                        holderClass.getCanonicalName()
                )
        );
    }

    private static MethodHandle needStaticMethod(final  Class<?> holderClass, final String methodName, final Class<?> returnClass, final Class<?> ... parameterClasses) {
        return Objects.requireNonNull(
                Crafty.findStaticMethod(holderClass, methodName, returnClass, parameterClasses),
                String.format(
                        "Could not locate static method '%s' in class '%s'",
                        methodName,
                        holderClass.getCanonicalName()
                )
        );
    }

    public static Field needField(final Class<?> holderClass, final String fieldName) {
        final Field field;
        try {
            field = holderClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (final NoSuchFieldException e) {
            throw new IllegalStateException(String.format("Unable to find field '%s' in class '%s'", fieldName, holderClass.getCanonicalName()), e);
        }
    }

    private static Object invokeOrThrow(final MethodHandle methodHandle, final Object ... params) {
        try {
            if (params.length == 0) {
                return methodHandle.invoke();
            }
            return methodHandle.invokeWithArguments(params);
        } catch (final Throwable throwable) {
            throw new IllegalStateException(String.format("Unable to invoke method with args '%s'", Arrays.toString(params)), throwable);
        }
    }
}
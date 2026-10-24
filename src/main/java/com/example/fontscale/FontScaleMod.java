package com.example.fontscale;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class FontScaleMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("fontscale");
    public static final float MIN = 1.0f;
    public static final float MAX = 100.0f;

    private static float scale = 1.0f;

    // Estado interno del mixin (evita escalar dos veces si un método llama a otro)
    public static int depth = 0;
    public static boolean pushed = false;

    public static float getScale() {
        return scale;
    }

    public static void setScale(float value) {
        scale = Math.max(MIN, Math.min(MAX, value));
        save();
    }

    /** Cambia el valor sin escribir a disco (para el slider mientras se arrastra). */
    public static void setScaleNoSave(float value) {
        scale = Math.max(MIN, Math.min(MAX, value));
    }

    public static void saveNow() {
        save();
    }

    /** La pantalla de ajustes no se escala a sí misma, para que siempre sea usable. */
    public static boolean shouldScale() {
        return !(MinecraftClient.getInstance().currentScreen instanceof FontScaleScreen);
    }

    private static Path configPath() {
        return FabricLoader.getInstance().getConfigDir().resolve("fontscale.properties");
    }

    private static void load() {
        Path p = configPath();
        if (!Files.exists(p)) return;
        try (InputStream in = Files.newInputStream(p)) {
            Properties props = new Properties();
            props.load(in);
            scale = Math.max(MIN, Math.min(MAX, Float.parseFloat(props.getProperty("scale", "1"))));
        } catch (IOException | NumberFormatException e) {
            LOGGER.warn("No se pudo leer la configuración, usando x1", e);
        }
    }

    private static void save() {
        try (OutputStream out = Files.newOutputStream(configPath())) {
            Properties props = new Properties();
            props.setProperty("scale", Float.toString(scale));
            props.store(out, "Font Scale (1 a 100)");
        } catch (IOException e) {
            LOGGER.warn("No se pudo guardar la configuración", e);
        }
    }

    private static String fmt(float v) {
        return (v == (long) v) ? Long.toString((long) v) : String.format("%.2f", v);
    }

    @Override
    public void onInitializeClient() {
        load();

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
            dispatcher.register(ClientCommandManager.literal("fontscale")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(Text.literal("Tamaño de fuente actual: x" + fmt(scale)
                        + "  (uso: /fontscale <1-100> | /fontscale reset)"));
                    return 1;
                })
                .then(ClientCommandManager.literal("reset").executes(ctx -> {
                    setScale(1.0f);
                    ctx.getSource().sendFeedback(Text.literal("Tamaño de fuente restablecido a x1"));
                    return 1;
                }))
                .then(ClientCommandManager.argument("valor", FloatArgumentType.floatArg(MIN, MAX))
                    .executes(ctx -> {
                        float v = FloatArgumentType.getFloat(ctx, "valor");
                        setScale(v);
                        ctx.getSource().sendFeedback(Text.literal("Tamaño de fuente: x" + fmt(scale)));
                        return 1;
                    }))));
    }
}

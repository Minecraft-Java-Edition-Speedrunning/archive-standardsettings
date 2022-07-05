package com.kingcontaria.standardsettings;

import com.kingcontaria.standardsettings.mixins.BakedModelManagerAccessor;
import com.kingcontaria.standardsettings.mixins.MinecraftClientAccessor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.options.*;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Arm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

@Environment(value= EnvType.CLIENT)
public class StandardSettings {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final MinecraftClient client = MinecraftClient.getInstance();
    public static final GameOptions options = client.options;
    private static final Window window = client.getWindow();
    public static final File standardoptionsFile = new File("config/standardoptions.txt");
    public static final File optionsFile = new File("options.txt");
    public static boolean changeOnGainedFocus = false;
    private static int renderDistanceOnWorldJoin;
    private static double fovOnWorldJoin;

    public static void load() {
        long start = System.nanoTime();

        fovOnWorldJoin = renderDistanceOnWorldJoin = 0;

        try {
            if (!standardoptionsFile.exists()) {
                LOGGER.error("standardoptions.txt is missing");
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(standardoptionsFile));

            String string = bufferedReader.readLine();

            if (new File(string).exists()) {
                bufferedReader = new BufferedReader(new FileReader(string));
                string = bufferedReader.readLine();
            }

            do {
                String[] strings = string.split(":");
                String[] string0_split = strings[0].split("_");
                try {
                    switch(string0_split[0]){
                        case "autoJump" -> options.autoJump = Boolean.parseBoolean(strings[1]);
                        case "autoSuggestions" -> options.autoSuggestions = Boolean.parseBoolean(strings[1]);
                        case "chatColors" -> options.chatColors = Boolean.parseBoolean(strings[1]);
                        case "chatLinks" -> options.chatLinks = Boolean.parseBoolean(strings[1]);
                        case "chatLinksPrompt" -> options.chatLinksPrompt = Boolean.parseBoolean(strings[1]);
                        case "enableVsync" -> window.setVsync(options.enableVsync = Boolean.parseBoolean(strings[1]));
                        case "entityShadows" -> options.entityShadows = Boolean.parseBoolean(strings[1]);
                        case "forceUnicodeFont" -> Option.FORCE_UNICODE_FONT.set(options, strings[1]);
                        case "discrete_mouse_scroll" -> options.discreteMouseScroll = Boolean.parseBoolean(strings[1]);
                        case "invertYMouse" -> options.invertYMouse = Boolean.parseBoolean(strings[1]);
                        case "reducedDebugInfo" -> options.reducedDebugInfo = Boolean.parseBoolean(strings[1]);
                        case "showSubtitles" -> options.showSubtitles = Boolean.parseBoolean(strings[1]);
                        case "touchscreen" -> options.touchscreen = Boolean.parseBoolean(strings[1]);
                        case "fullscreen" -> {
                            if (window.isFullscreen() != Boolean.parseBoolean(strings[1])) {
                                if (client.isWindowFocused()) {
                                    window.toggleFullscreen();
                                    options.fullscreen = Boolean.parseBoolean(strings[1]);
                                } else {
                                    LOGGER.error("Could not reset fullscreen mode because window wasn't focused!");
                                }
                            }
                        }
                        case "bobView" -> options.bobView = Boolean.parseBoolean(strings[1]);
                        case "toggleCrouch" -> options.sneakToggled = Boolean.parseBoolean(strings[1]);
                        case "toggleSprint" -> options.sprintToggled = Boolean.parseBoolean(strings[1]);
                        case "mouseSensitivity" -> options.mouseSensitivity = Double.parseDouble(strings[1]);
                        case "fov" -> options.fov = Double.parseDouble(strings[1]) * 40.0f + 70.0f;
                        case "gamma" -> options.gamma = Double.parseDouble(strings[1]);
                        case "renderDistance" -> options.viewDistance = Integer.parseInt(strings[1]);
                        case "guiScale" -> options.guiScale = Integer.parseInt(strings[1]);
                        case "particles" -> options.particles = ParticlesOption.byId(Integer.parseInt(strings[1]));
                        case "maxFps" -> window.setFramerateLimit(options.maxFps = Integer.parseInt(strings[1]));
                        case "fancyGraphics" -> options.fancyGraphics = Boolean.parseBoolean(strings[1]);
                        case "ao" -> options.ao = Integer.parseInt(strings[1]) == 0 ? AoOption.OFF : Integer.parseInt(strings[1]) == 1 ? AoOption.MIN : AoOption.MAX;
                        case "renderClouds" -> options.cloudRenderMode = strings[1].equals("true") ? CloudRenderMode.FANCY : strings[1].equals("false") ? CloudRenderMode.OFF : CloudRenderMode.FAST;
                        case "attackIndicator" -> options.attackIndicator = AttackIndicator.byId(Integer.parseInt(strings[1]));
                        case "lang" -> {
                            client.getLanguageManager().setLanguage(client.getLanguageManager().getLanguage((options.language = strings[1])));
                            client.getLanguageManager().apply(client.getResourceManager());
                        }
                        case "chatVisibility" -> options.chatVisibility = ChatVisibility.byId(Integer.parseInt(strings[1]));
                        case "chatOpacity" -> options.chatOpacity = Double.parseDouble(strings[1]);
                        case "textBackgroundOpacity" -> options.textBackgroundOpacity = Double.parseDouble(strings[1]);
                        case "backgroundForChatOnly" -> options.backgroundForChatOnly = Boolean.parseBoolean(strings[1]);
                        case "advancedItemTooltips" -> options.advancedItemTooltips = Boolean.parseBoolean(strings[1]);
                        case "pauseOnLostFocus" -> options.pauseOnLostFocus = Boolean.parseBoolean(strings[1]);
                        case "chatHeightFocused" -> options.chatHeightFocused = Double.parseDouble(strings[1]);
                        case "chatHeightUnfocused" -> options.chatHeightUnfocused = Double.parseDouble(strings[1]);
                        case "chatScale" -> options.chatScale = Double.parseDouble(strings[1]);
                        case "chatWidth" -> options.chatWidth = Double.parseDouble(strings[1]);
                        case "mipmapLevels" -> {
                            if (options.mipmapLevels != Integer.parseInt(strings[1])) {
                                client.resetMipmapLevels(options.mipmapLevels = Integer.parseInt(strings[1]));
                                ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
                            }
                        }
                        case "mainHand" -> options.mainArm = "left".equals(strings[1]) ? Arm.LEFT : Arm.RIGHT;
                        case "narrator" -> options.narrator = NarratorOption.byId(Integer.parseInt(strings[1]));
                        case "biomeBlendRadius" -> options.biomeBlendRadius = Integer.parseInt(strings[1]);
                        case "mouseWheelSensitivity" -> options.mouseWheelSensitivity = Double.parseDouble(strings[1]);
                        case "rawMouseInput" -> Option.RAW_MOUSE_INPUT.set(options, strings[1]);
                        case "chunkborders" -> {
                            if (client.debugRenderer.toggleShowChunkBorder() != Boolean.parseBoolean(strings[1])) {
                                client.debugRenderer.toggleShowChunkBorder();
                            }
                        }
                        case "hitboxes" -> client.getEntityRenderManager().setRenderHitboxes(Boolean.parseBoolean(strings[1]));
                        case "perspective" -> options.perspective = Integer.parseInt(strings[1]);
                        case "piedirectory" -> ((MinecraftClientAccessor)client).setOpenProfilerSection(strings[1].replace(".",""));
                        case "fovOnWorldJoin" -> fovOnWorldJoin = Double.parseDouble(strings[1]);
                        case "renderDistanceOnWorldJoin" -> renderDistanceOnWorldJoin = Integer.parseInt(strings[1]);
                        case "key" -> {
                            for (KeyBinding keyBinding : options.keysAll) {
                                if (string0_split[1].equals(keyBinding.getId())) {
                                    keyBinding.setKeyCode(InputUtil.fromName(strings[1])); break;
                                }
                            }
                        }
                        case "soundCategory" -> {
                            for (SoundCategory soundCategory : SoundCategory.values()) {
                                if (string0_split[1].equals(soundCategory.getName())) {
                                    options.setSoundVolume(soundCategory, Float.parseFloat(strings[1]));
                                }
                            }
                        }
                        case "modelPart" -> {
                            for (PlayerModelPart playerModelPart : PlayerModelPart.values()) {
                                if (strings[0].equals("modelPart_" + playerModelPart.getName())) {
                                    options.setPlayerModelPart(playerModelPart, Boolean.parseBoolean(strings[1])); break;
                                }
                            }
                        }
                        // Some options.txt settings which aren't accessible in vanilla Minecraft and some unnecessary settings (like Multiplayer stuff) are not included.
                    }
                } catch (Exception exception) {
                    if (!string.equals("chunkborders:") && !string.equals("hitboxes:") && !string.equals("perspective:") && !string.equals("piedirectory:") && !string.equals("renderDistanceOnWorldJoin:") && !string.equals("fovOnWorldJoin:") && !string.equals("lastServer:")) {
                        LOGGER.warn("Skipping bad StandardSetting: " + string);
                    }
                }
            } while ((string = bufferedReader.readLine()) != null);
            KeyBinding.updateKeysByCode();
            bufferedReader.close();
            LOGGER.info("Finished loading StandardSettings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        } catch (Exception exception2) {
            LOGGER.error("Failed to load StandardSettings", exception2);
        }
    }

    public static void changeSettingsOnJoin() {
        long start = System.nanoTime();

        if (renderDistanceOnWorldJoin != 0) {
            Option.RENDER_DISTANCE.set(options, renderDistanceOnWorldJoin);
        }
        if (fovOnWorldJoin != 0) {
            Option.FOV.set(options, fovOnWorldJoin);
        }
        if (fovOnWorldJoin != 0 || renderDistanceOnWorldJoin != 0) {
            options.write();
            LOGGER.info("Changed Settings on World Join ({} ms)", (System.nanoTime() - start) / 1000000.0f);
        }
    }

    public static void checkSettings() {
        long start = System.nanoTime();

        options.mouseSensitivity = Check("Sensitivity", options.mouseSensitivity, 0, 1);
        options.fov = Math.round(Check("FOV", options.fov, 30, 110));
        options.gamma = Check("Brightness", options.gamma, 0, 5);
        options.viewDistance = Check("Render Distance", options.viewDistance, 2, 32);
        options.guiScale = Check("GUI Scale", options.guiScale, 0, 4);
        // Because of DynamicFPS/SleepBackground I will not mess with adjusting FPS :)
        options.biomeBlendRadius = Check("Biome Blend Radius", options.biomeBlendRadius, 0, 7);
        options.chatOpacity = Check("Chat Opacity", options.chatOpacity, 0, 1);
        options.textBackgroundOpacity = Check("Text Background Opacity", options.textBackgroundOpacity, 0, 1);
        options.chatHeightFocused = Check("(Chat) Focused Height", options.chatHeightFocused, 0, 1);
        options.chatHeightUnfocused = Check("(Chat) Unfocused Height", options.chatHeightUnfocused, 0, 1);
        options.chatScale = Check("Chat Text Size", options.chatScale, 0, 1);
        options.chatWidth = Check("Chat Width", options.chatWidth, 0, 1);
        if (options.mipmapLevels != (options.mipmapLevels = Check("Mipmap Levels", options.mipmapLevels, 0, 4))) {
            client.resetMipmapLevels(options.mipmapLevels);
            ((BakedModelManagerAccessor)client.getBakedModelManager()).callApply(((BakedModelManagerAccessor)client.getBakedModelManager()).callPrepare(client.getResourceManager(), client.getProfiler()), client.getResourceManager(), client.getProfiler());
        }
        options.mouseWheelSensitivity = Check("Scroll Sensitivity", options.mouseWheelSensitivity, 0.01, 10);
        for (SoundCategory soundCategory : SoundCategory.values()) {
            options.setSoundVolume(soundCategory, Check(soundCategory.getName(), options.getSoundVolume(soundCategory)));
        }

        if (renderDistanceOnWorldJoin != 0) {
            renderDistanceOnWorldJoin = Check("Render Distance (On World Join)", renderDistanceOnWorldJoin, 2, 32);
        }
        if (fovOnWorldJoin != 0) {
            fovOnWorldJoin = Math.round(Check("FOV (On World Join)", fovOnWorldJoin, 30, 110));
        }

        window.setScaleFactor(window.calculateScaleFactor(options.guiScale, client.forcesUnicodeFont()));
        LOGGER.info("Finished checking Settings ({} ms)", (System.nanoTime() - start) / 1000000.0f);
    }

    private static double Check(String settingName, double setting, double min, double max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting>max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }

    private static float Check(String settingName, float setting) {
        if (setting < 0) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return 0;
        }
        if (setting > 1) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return 1;
        }
        return setting;
    }

    private static int Check(String settingName, int setting, int min, int max) {
        if (setting < min) {
            LOGGER.warn(settingName + " was too low! ({})", setting);
            return min;
        }
        if (setting > max) {
            LOGGER.warn(settingName + " was too high! ({})", setting);
            return max;
        }
        return setting;
    }
}
package com.lizardclient;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class LizardClient implements ClientModInitializer {
    private static List<Module> modules;
    private static KeyBinding openGuiKey;

    @Override
    public void onInitializeClient() {
        modules = ModuleLoader.loadModules();
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.lizardclient.gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.wasPressed()) {
                openGui(client);
            }
            // handle keybind toggles
            if (client.currentScreen == null) {
                for (Module m : modules) {
                    if (m.keyCode != 0 && InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow().getHandle(), m.keyCode)) {
                        m.enabled = !m.enabled;
                        // simple debounce per tick: rely on press detection only when keybinding helper used? keep simple.
                    }
                }
            }
        });
    }

    private void openGui(MinecraftClient client) {
        if (client != null) {
            // reload modules from disk each time the GUI opens, so only modules present in folder are shown
            modules = ModuleLoader.loadModules();
            client.setScreen(new LizardScreen(modules));
        }
    }
}

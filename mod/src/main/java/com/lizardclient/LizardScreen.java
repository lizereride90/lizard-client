package com.lizardclient;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import com.lizardclient.widgets.LizardSwitch;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class LizardScreen extends Screen {
    private final List<Module> modules;
    private int scroll = 0;

    protected LizardScreen(List<Module> modules) {
        super(Text.literal("Lizard Client"));
        this.modules = modules;
    }

    @Override
    protected void init() {
        super.init();
        renderModuleButtons();
    }

    private void renderModuleButtons() {
        this.clearChildren();
        int y = 40 - scroll;
        int i = 0;
        for (Module m : modules) {
            int x = 20;
            int w = 200;
            addDrawableChild(ButtonWidget.builder(Text.literal((m.enabled ? "[ON] " : "[OFF] ") + m.name), btn -> {
                m.enabled = !m.enabled;
                btn.setMessage(Text.literal((m.enabled ? "[ON] " : "[OFF] ") + m.name));
            }).dimensions(x, y, w, 20).build());

            // Keybind button
            int key = m.keyCode == 0 ? GLFW.GLFW_KEY_UNKNOWN : m.keyCode;
            String keyName = key == GLFW.GLFW_KEY_UNKNOWN ? "(none)" : InputUtil.fromKeyCode(key, 0).getTranslationKey();
            addDrawableChild(ButtonWidget.builder(Text.literal("Key: " + keyName), btn -> {
                this.client.setScreen(new KeyCaptureScreen(this, m));
            }).dimensions(x + w + 10, y, 100, 20).build());

            y += 26;
            // Settings under each module
            for (ModuleSetting setting : m.settings) {
                if (setting instanceof ModuleSetting.BoolSetting b) {
                    addDrawableChild(new LizardSwitch(x + 10, y, 160, 20, b.label(), b.value(), newVal -> b.setValue(newVal)));
                    y += 22;
                } else if (setting instanceof ModuleSetting.NumberSetting n) {
                    addDrawableChild(new SimpleSlider(x + 10, y, 200, 20, n));
                    y += 24;
                }
            }
            y += 8;
            i++;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= verticalAmount * 20;
        scroll = Math.max(0, scroll);
        renderModuleButtons();
        return true;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Background and dim overlay for visibility
        this.renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, 0xA0000000);

        // Title
        context.drawCenteredTextWithShadow(textRenderer, this.title, width / 2, 12, 0xFFFFFF);

        // If no modules, show a helpful message instead of blank UI
        if (modules.isEmpty()) {
            context.drawCenteredTextWithShadow(textRenderer,
                Text.literal("No modules found in /sdcard/Download/modules"),
                width / 2, height / 2, 0xFFDDDDDD);
            return;
        }

        super.render(context, mouseX, mouseY, delta);
    }

    private static class SimpleSlider extends SliderWidget {
        private final ModuleSetting.NumberSetting setting;

        public SimpleSlider(int x, int y, int width, int height, ModuleSetting.NumberSetting setting) {
            super(x, y, width, height, Text.literal(setting.label()), normalize(setting));
            this.setting = setting;
        }

        private static double normalize(ModuleSetting.NumberSetting s) {
            double range = s.max() - s.min();
            if (range == 0) return 0;
            return (s.value() - s.min()) / range;
        }

        @Override
        protected void updateMessage() {
            double range = setting.max() - setting.min();
            double val = setting.min() + this.value * range;
            this.setMessage(Text.literal(setting.label() + ": " + String.format("%.2f", val)));
        }

        @Override
        protected void applyValue() {
            double range = setting.max() - setting.min();
            double val = setting.min() + this.value * range;
            setting.setValue(val);
        }
    }

    private static class KeyCaptureScreen extends Screen {
        private final Screen parent;
        private final Module module;

        protected KeyCaptureScreen(Screen parent, Module module) {
            super(Text.literal("Press a key"));
            this.parent = parent;
            this.module = module;
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            module.keyCode = keyCode;
            if (this.client != null) this.client.setScreen(parent);
            return true;
        }

        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            this.renderBackground(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(textRenderer, Text.literal("Press any key for " + module.name + " (ESC to clear)"), width / 2, height / 2 - 10, 0xFFFFFF);
            super.render(context, mouseX, mouseY, delta);
        }

        @Override
        public boolean shouldPause() { return false; }
    }
}

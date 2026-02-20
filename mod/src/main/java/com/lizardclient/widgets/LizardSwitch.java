package com.lizardclient.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class LizardSwitch extends ButtonWidget {
    private boolean value;
    private final String label;
    private final OnChange onChange;

    public interface OnChange {
        void changed(boolean newValue);
    }

    public LizardSwitch(int x, int y, int width, int height, String label, boolean initial, OnChange onChange) {
        super(x, y, width, height, Text.literal(buildLabel(label, initial)), button -> {}, DEFAULT_NARRATION_SUPPLIER);
        this.value = initial;
        this.label = label;
        this.onChange = onChange;
    }

    private static String buildLabel(String label, boolean value) {
        return label + ": " + (value ? "ON" : "OFF");
    }

    @Override
    public void onClick(double mouseX, double mouseY) {
        value = !value;
        this.setMessage(Text.literal(buildLabel(label, value)));
        onChange.changed(value);
    }
}

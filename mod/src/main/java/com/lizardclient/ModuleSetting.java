package com.lizardclient;

public sealed interface ModuleSetting permits ModuleSetting.BoolSetting, ModuleSetting.NumberSetting {
    String id();
    String label();

    record BoolSetting(String id, String label, boolean value) implements ModuleSetting {}

    record NumberSetting(String id, String label, double value, double min, double max, double step) implements ModuleSetting {}
}

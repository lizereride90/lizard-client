package com.lizardclient;

public interface ModuleSetting {
    String id();
    String label();

    final class BoolSetting implements ModuleSetting {
        private final String id;
        private final String label;
        private boolean value;

        public BoolSetting(String id, String label, boolean value) {
            this.id = id;
            this.label = label;
            this.value = value;
        }

        @Override
        public String id() { return id; }

        @Override
        public String label() { return label; }

        public boolean value() { return value; }

        public void setValue(boolean value) { this.value = value; }
    }

    final class NumberSetting implements ModuleSetting {
        private final String id;
        private final String label;
        private double value;
        private final double min;
        private final double max;
        private final double step;

        public NumberSetting(String id, String label, double value, double min, double max, double step) {
            this.id = id;
            this.label = label;
            this.value = value;
            this.min = min;
            this.max = max;
            this.step = step;
        }

        @Override
        public String id() { return id; }

        @Override
        public String label() { return label; }

        public double value() { return value; }

        public void setValue(double value) { this.value = value; }

        public double min() { return min; }

        public double max() { return max; }

        public double step() { return step; }
    }
}

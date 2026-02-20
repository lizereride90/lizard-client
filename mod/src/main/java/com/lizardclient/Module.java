package com.lizardclient;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public String id;
    public String name;
    public String description;
    public boolean enabled;
    public int keyCode; // GLFW key code
    public List<ModuleSetting> settings = new ArrayList<>();
}

package com.yessumtorah.boilerapp;

public class Boiler {
    private String Id;
    private boolean on;
    private static Boiler single_instance = null;

    private Boiler(String Id) {
        this.Id = Id;
        this.on = false;
    }

    public static Boiler getInstance(String Id) {
        if (single_instance == null)
            single_instance = new Boiler(Id);

        return single_instance;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean state) {
        this.on = state;
    }

    public String getId() {
        return this.Id;
    }


}

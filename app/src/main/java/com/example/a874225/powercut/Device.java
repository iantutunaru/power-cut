package com.example.a874225.powercut;

import java.io.Serializable;

/**
 * Device class represent a new Device that stores its name and energy use.
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class Device implements Serializable {
    private String deviceName;
    private Integer electricityUsage;

    public Device(String deviceName, Integer electricityUsage) {
        this.deviceName = deviceName;
        this.electricityUsage = electricityUsage;
    }

    public void setName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getName() {
        return deviceName;
    }

    public void setUsage(Integer electricityUsage) {
        this.electricityUsage = electricityUsage;
    }

    public int getUsage() {
        return electricityUsage;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

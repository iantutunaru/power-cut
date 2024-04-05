package com.example.a874225.powercut;

/**
 * AwayHour class represents a set time constraint that stores the device that is not being used.
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class AwayHour {
    //the beginning hour when device is not being used
    private Integer hour;
    //the beginning minutes when device is not being used
    private Integer minutes;
    //the day of the week device is not being used
    private String day;
    //the end hour when device is going to start being used
    private String device;
    //the end hour when device is going to start being used
    private Integer endHour;
    //the end minutes when device is going to start being used
    private Integer endMinutes;
    // the day of the week device is being used
    private String endDay;

    /**
     * Constructor that creates a new AwayHour
     * @param hour the beginning hour when device is not being used
     * @param minutes the beginning minutes when device is not being used
     * @param endHour the end hour when device is going to start being used
     * @param endMinutes the end minutes when device is going to start being used
     * @param day the day of the week device is not being used
     * @param device name of the device not being used
     */
    public AwayHour(int hour, int minutes, int endHour, int endMinutes, String day, String device) {
        this.hour = hour;
        this.minutes = minutes;
        this.endHour = endHour;
        this.endMinutes = endMinutes;
        this.day = day;
        this.device = device;
    }

    /**
     * Setter for the hour from which device is not used
     * @param hour hour from which device is not used
     */
    public void setHour(int hour) {
        this.hour = hour;
    }

    /**
     * Setter for the hour from which device is used
     * @param hour hour from which device is used
     */
    public void setEndHour(int hour) {
        this.endHour = hour;
    }

    /**
     * Getter for the hour from which device is not used
     * @return hour from which device is not used
     */
    public int getHour() {
        return this.hour;
    }

    /**
     * Getter for the hour from which device is used
     * @return hour from which device is used
     */
    public int getEndHour() {
        return this.endHour;
    }

    /**
     * Setter for minutes from which device is not being used
     * @param minutes minutes from which device is not being used
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Setter for minutes from which device is used
     * @param minutes minutes from which device is used
     */
    public void setEndMinutes(int minutes) {
        this.endMinutes = minutes;
    }

    /**
     * Getter for minutes from which device is not used
     * @return minutes from which device is not used
     */
    public int getMinutes() {
        return this.minutes;
    }

    /**
     * Getter for minutes from which device is used
     * @return minutes from which device is used
     */
    public int getEndMinutes() {
        return endMinutes;
    }

    /**
     * Setter for the day when Device is not being used
     * @param day day when Device is not being used
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * Setter for the day when Device is being used
     * @param day day when Device is being used
     */
    public void setEndDay(String day) {
        this.endDay = day;
    }

    /**
     * Getter for the day when Device is not being used
     * @return
     */
    public String getDay() {
        return day;
    }

    /**
     * Getter for the day when Device is being used
     * @return
     */
    public String getEndDay() {
        return endDay;
    }

    /**
     * Setter for the device name
     * @param device new device name
     */
    public void setDeviceName(String device) {
        this.device = device;
    }

    /**
     * Getter for the used device name
     * @return used device name
     */
    public String getDeviceName() {
        return this.device;
    }

    /**
     * Method that return a String representation of the AwayHour object
     * @return String representation of the AwayHour object
     */
    @Override
    public String toString() {
        return device + "," + day + "," +
                Integer.toString(hour) + "," + Integer.toString(minutes) + "," +
                Integer.toString(endHour) + "," + Integer.toString(endMinutes) + ";";
    }
}

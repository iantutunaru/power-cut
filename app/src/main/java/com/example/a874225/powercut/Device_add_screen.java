package com.example.a874225.powercut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * Device_add_screen handles the addition of the new device.
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class Device_add_screen extends AppCompatActivity {
    private String deviceName;
    private Integer deviceElecUsage;
    private final String DEVICE_NAME_KEY = "DEVICE_NAME";
    private final String DEVICE_ELEC_USE_KEY = "DEVICE_USE";


    /**
     * Method that creates a new Add Device activity
     * @param savedInstanceState previous saved instance of the activity in a Bundle format
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add_screen);
    }

    /**
     * Method that creates a new device when Add device button is pressed
     * @param view current Activity
     */
    public void createANewDevice(View view) {
        EditText deviceNameField = (EditText) findViewById(R.id.deviceName);
        EditText deviceElecField = (EditText) findViewById(R.id.electricityUsage);

        deviceName = deviceNameField.getText().toString();
        deviceElecUsage = checkDeviceElectUsage(deviceElecField.getText().toString());

        Intent newDevice = new Intent();

        newDevice.putExtra(DEVICE_NAME_KEY, deviceName);
        newDevice.putExtra(DEVICE_ELEC_USE_KEY, deviceElecUsage);

        setResult(RESULT_OK, newDevice);

        finish();
    }

    /**
     * Method that checks that device Usage has been entered in a correct format
     * @param elecUsageString String containing entered energy usage
     * @return elecUsage int containing entered energy usage
     */
    private int checkDeviceElectUsage(String elecUsageString) {
        Integer[] intArray = new Integer[elecUsageString.length()];

        // for loop that converts each number in the String to an int format
        for (int i = 0; i < elecUsageString.length(); i++) {
            intArray[i] = (Character.getNumericValue(elecUsageString.charAt(i)));
        }

        boolean nextIntZero = true;

        // This loop runs until all zeroes has been removed before the number
        while (nextIntZero) {
            // If int is a zero then remove it
            if (intArray[0] == 0) {
                Integer[] tempArray = new Integer[intArray.length - 1];
                // Remove the zero by copying contents to a new array minus the zero in the front.
                for (int i = 0; i < tempArray.length; i++) {
                    tempArray[i] = intArray[i + 1];
                }

                intArray = tempArray;
            // Otherwise break the loop
            } else {
                nextIntZero = false;
            }
        }

        String elecUsageNewVal = "";

        // Recreate the energy use in a String format
        for (int i = 0; i < intArray.length; i++) {
            elecUsageNewVal = elecUsageNewVal + Integer.toString(intArray[i]);
        }

        int elecUsage = Integer.parseInt(elecUsageNewVal);

        return elecUsage;
    }
}

package com.example.a874225.powercut;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import org.joda.time.DateTime;
import org.joda.time.Minutes;

import java.io.Serializable;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * This is MainScreen activity responsible for acting as a main
 * hub of the app, initializing graphs, providing access to initializing
 * new devices and away hours, as well as managing device list and calculating
 * saved energy
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class MainScreen extends AppCompatActivity {
    // Request code that represents new device addition
    private final Integer ADD_DEVICE_REQUEST_CODE = 422;
    // Request code that represents new away hours addition
    private final Integer ADD_HOURS_REQUEST_CODE = 423;
    // String key used when adding a new device
    private final String NEW_DEVICE_NAME_KEY = "DEVICE_NAME";
    // String key used when adding a new device energy usage
    private final String NEW_DEVICE_ELEC_USE_KEY = "DEVICE_USE";
    // String key used when storing away hour list
    private final String NEW_HOURLIST = "HOUR_LIST";
    // String key used when sending a device list to other activities
    private final String SEND_DEVICE_LIST_PASSWORD = "DEVICE_LIST";
    // String key used when saving/loading list of devices
    private final String SAVE_DEVICES = "SAVED_DEVICES";
    // String key used when saving/loading time
    private final String SAVE_TIME = "SAVED_TIME";
    // String key used when saving/ loading away hours list
    private final String SAVE_HOURS = "SAVED_HOURS";
    // Number database used to display the graph chart
    private final Number[] ENERGY_DATABASE = new Number[24];
    // Number array used to represent 24 hour day cycle
    private final Number[] DOMAIN_LABELS = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15,
            16, 17, 18, 19, 20, 21, 22, 23, 24};
    // XYPlot representing an energy chart
    private XYPlot energyChart;
    // Device representing all devices use in total
    private Device allDevice = new Device("All Devices", 0);
    // DateTime storing current date
    private DateTime today;
    // DateTime storing previously saved date
    private DateTime yesterday;
    // ArrayList containing all registered devices in a string format for easier saving process
    private ArrayList<String> deviceSave = new ArrayList<>();
    // ArrayList containing all saved away hours
    private ArrayList<AwayHour> hourList = new ArrayList<>();
    // ArrayList containing all registered devices
    private ArrayList<Device> deviceList = new ArrayList<>();
    // ArrayAdapter used to when displaying devices in a list
    private ArrayAdapter<Device> deviceAdapter;

    /**
     * onCreate method that initializes DeviceList or loads
     * existing one from the saved data. Also initializes graph.
     *
     * @param savedInstanceState previous saved instance in a Bundle format
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        loadDeviceList();

        boolean newList = true;
        // Check if list is new
        if (deviceList.size() != 0) {
            newList = false;
        }

        // If list is new initialize device list and add All device counter.
        if (newList) {
            allDevice.setUsage(calcAllDeviceUsage());
            deviceList.add(allDevice);
        }

        initializeList();
        initGraphics();
    }

    /**
     * Method that runs when Activity regains User attention,
     * redraws graph and device list.
     */
    @Override
    protected void onResume() {
        super.onResume();

        initializeList();
        initGraphics();
    }

    /**
     * Method that runs when User exits the program;
     * saves existing time and device list.
     */
    @Override
    protected void onStop() {
        saveTime();
        saveDeviceList();
        super.onStop();
    }

    /**
     * Method that calculates how much energy is used by all devices.
     *
     * @return elecUsage energy used by all Devices.
     */
    private int calcAllDeviceUsage() {
        //elecUsage energy used by all Devices.
        int elecUsage = 0;

        // Run through Device list and accumulate their energy usage
        for (Device device : deviceList) {
            // Don't add all devices itself
            if (!device.getName().equals("All Devices")) {
                elecUsage += device.getUsage();
            }
        }

        return elecUsage;
    }

    /**
     * Method that initializes graph and total saved energy textbox.
     */
    private void initGraphics() {
        TextView energyDisplay = (TextView) findViewById(R.id.energyDisplay);
        initGraph();

        energyDisplay.setText(Integer.toString(calculateSavedEnergy()) + " kW");
    }

    /**
     * Method that creates the graph and popularizes its database.
     */
    private void initGraph() {
        energyChart = (XYPlot) findViewById(R.id.energyChart);
        energyChart.clear();

        int[] energyTempDatabase = createSavedEnergyDatabase();
        int i = 0;

        // convert int database to Number format
        for (int energyHour : energyTempDatabase) {
            ENERGY_DATABASE[i] = energyHour;
            i++;
        }

        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(ENERGY_DATABASE), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                "Electricity Saved");

        LineAndPointFormatter series1Format = new LineAndPointFormatter(Color.GREEN, Color.GRAY,
                Color.GREEN, null);

        energyChart.addSeries(series1, series1Format);

        energyChart.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
            //Initialize the X domain
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int i = Math.round(((Number) obj).floatValue());
                return toAppendTo.append(DOMAIN_LABELS[i]);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
    }

    /**
     * Method that creates Energy Database in int format
     *
     * @return energyDatabase int format database contacting energy use according to hours
     */
    private int[] createSavedEnergyDatabase() {
        int[] energyDatabase = new int[24];
        int zeroValue = 0;

        // Make sure there is no null values in the database
        for (int i = 0; i < energyDatabase.length; i++) {
            energyDatabase[i] = zeroValue;
        }

        // Go through every AwayHour and record energy usage for every
        for (AwayHour hour : hourList) {
            int startHour = hour.getHour();
            int endHour = hour.getEndHour();
            int deviceUsage = 0;
            // Go through all devices and record their energy use
            for (Device device : deviceList) {

                // If device corresponds to the away hour record it usage
                if (hour.getDeviceName().equals(device.getName())) {
                    deviceUsage = device.getUsage();

                    // Record saved energy for the whole time period
                    for (int i = startHour; i <= endHour; i++) {
                        energyDatabase[i] += deviceUsage;
                    }
                }
            }

            // If AwayHour is for all Devices record its usage from the AllDevice placeholder
            if (hour.getDeviceName().equals("All Devices")) {
                deviceUsage = allDevice.getUsage();

                // Record saved energy for the whole time period
                for (int i = startHour; i <= endHour; i++) {
                    energyDatabase[i] += deviceUsage;
                }
            }
        }

        return energyDatabase;
    }

    /**
     * Method that calculates a total of saved energy
     * @return savedEnergy a totall of all saved energy
     */
    private int calculateSavedEnergy() {
        int savedEnergy = 0;
        int hoursSaved = 0;
        int deviceUsage = 0;
        // Go through each hour and get usage of the corresponding device and add that to the saved
        // energy
        for (AwayHour hour : hourList) {
            // Go through the device list until you find the corresponding device
            for (Device device : deviceList) {
                // If device is the device corresponding to the hour adds its usage
                if (hour.getDeviceName().equals(device.getName())) {
                    deviceUsage += device.getUsage();
                    hoursSaved = Math.abs(hour.getHour() - hour.getEndHour());

                    savedEnergy += deviceUsage * hoursSaved;
                }
            }
        }

        return savedEnergy;
    }

    /**
     * Method that prepares current date for saving process
     */
    private void saveTime() {
        today = new DateTime();
        yesterday = today;
    }

    /**
     * Method that calculates how much time passed since last launch of the program.
     * @return timeDiffrence hours passed since last launch
     */
    private int calcTimeDifferenceHours() {
        int timeDiffrence = calcTimeDifference() / 60;
        return timeDiffrence;
    }

    /**
     * Method that calculates how much time passed since last launch of the program.
     * @return timeDiffrence minutes passed since last launch
     */
    private int calcTimeDifference() {
        int timeDifference = Minutes.minutesBetween(yesterday, today).getMinutes();
        return timeDifference;
    }

    /**
     * Method that opens AwayHours activity and waits for it to return away hour list
     * @param view current Activity
     */
    public void openAwayHours(View view) {
        Intent awayHours = new Intent(this, away_hours_hun_screen.class);

        awayHours.putExtra(SEND_DEVICE_LIST_PASSWORD, (Serializable) deviceList);
        startActivityForResult(awayHours, ADD_HOURS_REQUEST_CODE);
    }

    /**
     * Method that starts an Add Device activity
     * @param view current Activity
     */
    public void addDevice(View view) {
        Intent addDevice = new Intent(this, Device_add_screen.class);
        startActivityForResult(addDevice, ADD_DEVICE_REQUEST_CODE);
    }

    /**
     * Method that initializes the most economic device list
     */
    private void initializeList() {
        ListView topDeviceList = (ListView) findViewById(R.id.topDeviceList);
        deviceAdapter = new ArrayAdapter<Device>(this,
                android.R.layout.simple_list_item_1,
                topDevicesEnergySavedList());

        topDeviceList.setAdapter(deviceAdapter);

        deviceAdapter.notifyDataSetChanged();
    }

    /**
     * Method that calculates the top three most economic devices in the app
     * @return economicDevices list of three most economic devices
     */
    private ArrayList<Device> topDevicesEnergySavedList() {
        ArrayList<Device> economicDevices = new ArrayList<Device>();
        Device mostEconomicDevice = new Device("No Devices", 0);
        Device secondMostEconomicDevice = new Device("No Devices", 0);
        Device thirdMostEconomicDevice = new Device("No Devices", 0);

        // If there is only one device in the list that means it is All Devices placeholder, so
        // ask User to add devices
        if (deviceList.size() == 1) {
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder
                if (mostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    mostEconomicDevice = deviceList.get(i);
                }
            }

            economicDevices.add(mostEconomicDevice);
            // If there is two devices in the list calculate two places
        } else if (deviceList.size() == 2) {
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder
                if (mostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    mostEconomicDevice = deviceList.get(i);
                }
            }

            economicDevices.add(mostEconomicDevice);
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder and not
                // the first device
                if (secondMostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && mostEconomicDevice != deviceList.get(i)
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    secondMostEconomicDevice = deviceList.get(i);
                }
            }

            // Make sure that device that is being added is not the placeholder
            if (!secondMostEconomicDevice.getName().equals("No Devices")) {
                economicDevices.add(secondMostEconomicDevice);
            }

        } else if (deviceList.size() > 2) {
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder
                if (mostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    mostEconomicDevice = deviceList.get(i);
                }
            }

            economicDevices.add(mostEconomicDevice);
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder and not
                // the first device
                if (secondMostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && mostEconomicDevice != deviceList.get(i)
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    secondMostEconomicDevice = deviceList.get(i);
                }
            }
            // Make sure that device that is being added is not the placeholder
            if (!secondMostEconomicDevice.getName().equals("No Devices")) {
                economicDevices.add(secondMostEconomicDevice);
            }
            // Go through the device list and find the most economic device
            for (int i = 0; i < deviceList.size(); i++) {
                // Make sure that device that is being added is not the placeholder and not
                // the first device
                if (thirdMostEconomicDevice.getUsage() <= deviceList.get(i).getUsage()
                        && mostEconomicDevice != deviceList.get(i)
                        && secondMostEconomicDevice != deviceList.get(i)
                        && !deviceList.get(i).getName().equals("All Devices")) {
                    thirdMostEconomicDevice = deviceList.get(i);
                }
            }
            // Make sure that device that is being added is not the placeholder
            if (!thirdMostEconomicDevice.getName().equals("No Devices")) {
                economicDevices.add(thirdMostEconomicDevice);
            }
            // if there is only one device add a No Device notification
        } else if (deviceList.size() == 1 && deviceList.get(0).getName().equals("All Devices")) {
            economicDevices.add(new Device("No devices", 0));
        }

        return economicDevices;
    }

    /**
     * Method that handles feedback from other activities
     * @param requestCode int key that identifies the activity that is sending feedback
     * @param resultCode int key the shows if activity was succesful or not
     * @param data data being sent by other activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If activity sending feedback is the new device activity
        if (requestCode == ADD_DEVICE_REQUEST_CODE) {
            // If new device was successfully created
            if (resultCode == RESULT_OK) {
                String deviceName = data.getStringExtra(NEW_DEVICE_NAME_KEY);
                Integer deviceUsage = data.getIntExtra(NEW_DEVICE_ELEC_USE_KEY, -1);

                Device newDevice = new Device(deviceName, deviceUsage);

                deviceList.add(newDevice);

                Toast.makeText(this,
                        "Added a new device", Toast.LENGTH_LONG).show();
            }
            // If device was not successfully added print a message to the User
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,
                        "Unable to create a new device", Toast.LENGTH_LONG).show();
            }
        // If activity sending the feedback is the away hours activity
        } else if (requestCode == ADD_HOURS_REQUEST_CODE) {
            // If activity was successful
            if (resultCode == RESULT_OK) {
                String hoursString = data.getStringExtra(NEW_HOURLIST);

                reconstructHourList(hoursString);

                Toast.makeText(this,
                        "Away hours added", Toast.LENGTH_LONG).show();
            }
            // If activity was not successful show message to the User
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,
                        "Unable to add new hours", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method that recreates the hour list from the saved string
     * @param hourListString hour list in a String format
     */
    private void reconstructHourList(String hourListString) {
        hourList = new ArrayList<>();
        String[] hours = hourListString.split(";");
        // Go though each line and reconstuct the hour list
        for (String hour : hours) {
            String[] hourElem = hour.split(",");

            String awayHourDevice = hourElem[0];
            String awayHourDay = hourElem[1];
            int awayHourHour = Integer.parseInt(hourElem[2]);
            int awayHourMinute = Integer.parseInt(hourElem[3]);
            int awayHourHourEnd = Integer.parseInt(hourElem[4]);
            int awayHourMinuteEnd = Integer.parseInt(hourElem[5]);
            AwayHour newHour = new AwayHour(awayHourHour, awayHourMinute, awayHourHourEnd,
                    awayHourMinuteEnd, awayHourDay, awayHourDevice);

            hourList.add(newHour);
        }
    }

    /**
     * Creates a String representation of the hour list
     * @return hourListString String representation of the hour list
     */
    private String prepareHourListForSave() {
        String hourListString = "";
        // Go through each hour and add it to the String
        for (AwayHour hour : hourList) {
            hourListString += hour.toString();
        }

        return hourListString;
    }

    /**
     * Method that prepares device list for saving
     */
    private void saveDeviceList() {
        // Make sure that list is not empty
        if (deviceList.size() != 1) {
            SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            prepareDevicesForSave();

            String deviceString = buildNewDeviceString(deviceSave);
            saveTime();
            String timeString = yesterday.toString();
            String hourString = prepareHourListForSave();
            editor.putString(SAVE_TIME, timeString);
            editor.putString(SAVE_DEVICES, deviceString);
            editor.putString(SAVE_HOURS, hourString);

            editor.commit();
        }
    }

    /**
     * Method that loads the device list from the shared preferences
     */
    private void loadDeviceList() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String deviceString = sharedPreferences.getString(SAVE_DEVICES, "error");
        String timeString = sharedPreferences.getString(SAVE_TIME, "error");
        String hourString = sharedPreferences.getString(SAVE_HOURS, "error");

        // Make sure that there were no errors during the device loading
        if ((!deviceString.equals("error") && !deviceString.equals("")) &&
                ((!timeString.equals("error") && !timeString.equals(""))) &&
                ((!hourString.equals("error") && !hourString.equals("")))) {
            reconstructDeviceList(deviceString);
            reconstructTime(timeString);
            reconstructHourList(hourString);
        } else {
            editor.clear();
        }
    }

    /**
     * Method to initialize time from the previous launch
     * @param savedTime previous time in a String format
     */
    private void reconstructTime(String savedTime) {
        yesterday = DateTime.parse(savedTime);
    }

    /**
     * Method that reconstruct a Device list from a String
     * @param savedDevices device list in a String format
     */
    private void reconstructDeviceList(String savedDevices) {
        String[] devicesArray = savedDevices.split(";");

        // Go through each line and reconstruct each device
        for (String deviceString : devicesArray) {
            String[] deviceData = deviceString.split(",");

            Device device = new Device(deviceData[0],
                    Integer.parseInt(deviceData[1]));

            boolean newDevice = true;
            // Go through each device and make sure that device haven't already been added
            for (Device deviceCounter : deviceList) {
                // Check that device hasn't already been added
                if (deviceCounter.getName().equals(device.getName())) {
                    newDevice = false;
                }
            }
            // If device hasn't been added add it to the list
            if (newDevice) {
                deviceList.add(device);
            }
        }

        // Go through device list and recalculate overall device energy usage
        for (Device device : deviceList) {
            if (device.getName().equals("All Devices")) {
                device.setUsage(calcAllDeviceUsage());
            }
        }
    }

    /**
     * Method that prepares device list for saving
     */
    private void prepareDevicesForSave() {
        String deviceName;
        int deviceUsage;

        for (int i = 0; i < deviceList.size(); i++) {
            deviceName = deviceList.get(i).getName();
            deviceUsage = deviceList.get(i).getUsage();
            String deviceNameString = deviceName + ",";
            String deviceUsageString = Integer.toString(deviceUsage) + ";";

            deviceSave.add(deviceNameString);
            deviceSave.add(deviceUsageString);
        }
    }

    /**
     * method that builds a String using a given String array
     * @param list String array to be turned into a String
     * @return String array as one String
     */
    private String buildNewString(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String elem : list) {
            stringBuilder.append(elem);
            stringBuilder.append(",");
        }

        return stringBuilder.toString();
    }

    /**
     * Method that builds new Device String to store a device list
     * @param list list of Devices in a String format
     * @return one String containing all devices in the app
     */
    private String buildNewDeviceString(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String elem : list) {
            stringBuilder.append(elem);
        }

        return stringBuilder.toString();
    }

    /**
     * Method that shows a message dialog containing Devices and allowing User to remove devices
     * @param view current Activity
     */
    public void showDeviceList(View view) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);
        builderSingle.setTitle("Device List");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        // Go through the device list and fill the dispalyed list view.
        for (Device device : deviceList) {
            arrayAdapter.add(device.getName());
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(MainScreen.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Select Action");

                builderInner.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int deletePos = 0;
                        boolean AllDevice = false;

                        // Go through the device list and get the position of the item being deleted
                        for (int i = 0; i < deviceList.size(); i++) {
                            if (deviceList.get(i).getName().equals(strName)) {
                                deletePos = i;
                            }
                        }

                        // Make sure User can't delete All Device placeholder
                        if (strName.equals("All Devices")) {
                            AllDevice = true;
                            Toast.makeText(MainScreen.this,
                                    "Can't delete all devices", Toast.LENGTH_SHORT).show();
                        }
                        // If not an All Device placeholder remove the selected device
                        if (!AllDevice) {
                            deviceList.remove(deletePos);
                            initializeList();
                            initGraphics();
                        }
                        dialog.dismiss();
                    }
                });

                builderInner.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();
            }
        });
        builderSingle.show();
    }
}

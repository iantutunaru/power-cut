package com.example.a874225.powercut;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * away_hour_hun_screen represents a medium between creation of new AwayHours that also allows
 * User to edit or delete already existing AwayHours.
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class away_hours_hun_screen extends AppCompatActivity {
    // Code used to identify a add hours request
    private final Integer ADD_HOURS_REQUEST_CODE = 420;
    // Code used to identify an edit hours request
    private final Integer EDIT_HOURS_REQUEST_CODE = 421;
    // Key used when sending/receiving an AwayHour from other Activities
    private final String AWAY_HOUR_KEY = "AWAY_HOUR";
    // Key used when sending/receiving an AwayHour beginning minutes from other Activity
    private final String AWAY_MINUTES_KEY = "AWAY_MINUTES";
    // Key used when sending/receiving an AwayHour beginning day from other Activity
    private final String AWAY_DAY_KEY = "AWAY_DAY";
    // Key used when sending/receiving an AwayHour ending hour from other Activity
    private final String AWAY_HOUR_END_KEY = "AWAY_END_HOUR";
    // Key used when sending/receiving an AwayHour ending minutes from other Activity
    private final String AWAY_MINUTES_END_KEY = "AWAY_END_MINUTES";
    // Key used when sending/receiving an AwayHour ending day from other Activity
    private final String AWAY_DAY_END_KEY = "AWAY_END_DAY";
    // Key used when sending/receiving an AwayHour used device from other Activity
    private final String AWAY_DEVICE_NAME_KEY = "AWAY_DEVICE_NAME";
    // Key used when sending/receiving a Device List from other Activity
    private final String SEND_DEVICE_LIST_PASSWORD = "DEVICE_LIST";
    // Key used when sending/receiving an AwayHour hour list from other Activity
    private final String NEW_HOURLIST = "HOUR_LIST";
    // Key used when saving beginning hours from AwayHours
    private final String SAVE_HOURS_LIST = "SAVE_HOURS";
    // Key used when saving beginning minutes from AwayHours
    private final String SAVE_MINUTES_LIST = "SAVE_MINUTES";
    // Key used when saving ending hours from AwayHours
    private final String SAVE_END_HOURS = "SAVE_END_HOURS";
    // Key used when saving ending minutes from AwayHours
    private final String SAVE_END_MINUTES_LIST = "SAVE_END_MINUTES";
    // Key used when saving beginning days from AwayHours
    private final String SAVE_DAYS_LIST = "SAVE_DAYS";
    // Key used when saving devices names from AwayHours
    private final String SAVE_DEVICE_LIST = "SAVE_DEVICES";
    // Key used when saving picked time on TimePickers from AwayHours
    private final String SAVE_TIME = "SAVED_TIME";
    // Key used when editing beginning hour for selected AwayHour
    private final String EDIT_HOUR = "EDIT_HOUR";
    // Key used when editing beginning minutes for selected AwayHour
    private final String EDIT_MINUTE = "EDIT_MINUTE";
    // Key used when editing beginning day for selected AwayHour
    private final String EDIT_DAY = "EDIT_DAY";
    // Key used when editing ending hour for selected AwayHour
    private final String EDIT_END_HOUR = "EDIT_END_HOUR";
    // Key used when editing ending minutes for selected AwayHour
    private final String EDIT_END_MINUTE = "EDIT_END_MINUTE";
    // Key used when editing ending day for selected AwayHour
    private final String EDIT_END_DAY = "EDIT_END_DAY";
    // Key used when editing device used for selected AwayHour
    private final String EDIT_DEVICE_NAME = "EDIT_DEVICE_NAME";
    // Key used when editing hour postion on TimePicker for selected AwayHour
    private final String PICKED_HOUR_POSITION = "EDIT_POSITION";

    // AwayHour list containing all of the created AwayHours
    private ArrayList<AwayHour> hourList = new ArrayList<>();
    // ArrayList containing all devices registered in the app
    private ArrayList<Device> deviceList;
    // ArrayList containing a String version of beginning hours for each AwayHour
    ArrayList<String> hoursList = new ArrayList<String>();
    // ArrayList containing a String version of ending hours for each AwayHour
    ArrayList<String> hoursEndList = new ArrayList<String>();
    // ArrayList containing a String version of beginning minutes for each AwayHour
    ArrayList<String> minutesList = new ArrayList<String>();
    // ArrayList containing a String version of ending minutes for each AwayHour
    ArrayList<String> minutesEndList = new ArrayList<String>();
    // ArrayList containing a String version of beginning days for each AwayHour
    ArrayList<String> daysList = new ArrayList<String>();
    // ArrayList containing a String version of devices names for each AwayHour
    ArrayList<String> devicesList = new ArrayList<String>();

    /**
     * Method ran when the class is crated, loading previously saved data if available
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_hours_hun_screen);

        // If device list was sent bind it to the local device list.
        if (getIntent().getSerializableExtra(SEND_DEVICE_LIST_PASSWORD) != null) {
            deviceList =
                    (ArrayList<Device>) getIntent().getSerializableExtra(SEND_DEVICE_LIST_PASSWORD);
        }

        LoadPreferences();
        setListListener();
    }

    /**
     * If User presses the back button save the existing list and close the Activity
     */
    @Override
    public void onBackPressed() {
        SavePreferences();
        super.onBackPressed();
    }

    /**
     * Method that saves the current AwayHour list.
     */
    private void SavePreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        prepareListForSave();

        String hoursString = buildNewString(hoursList);
        String minutesString = buildNewString(minutesList);
        String hoursEndString = buildNewString(hoursEndList);
        String minutesEndString = buildNewString(minutesEndList);
        String daysString = buildNewString(daysList);
        String devicesString = buildNewString(devicesList);

        editor.putString(SAVE_HOURS_LIST, hoursString);
        editor.putString(SAVE_MINUTES_LIST, minutesString);
        editor.putString(SAVE_END_HOURS, hoursEndString);
        editor.putString(SAVE_END_MINUTES_LIST, minutesEndString);
        editor.putString(SAVE_DAYS_LIST, daysString);
        editor.putString(SAVE_DEVICE_LIST, devicesString);

        editor.commit();
    }

    /**
     * Method that checks if the AwayHours only available for existing devices
     */
    private void checkHours(){
        ArrayList<Integer> deletedPositions = new ArrayList<>();

        // Go through AwayHour list and find if there are hours that exist for deleted devices
        for(int i = 0; i < hourList.size();i++ ){
            boolean deviceExists = false;
            // Go through the device list and check that device exists that relates to the hour
            for (Device device : deviceList){
                // If the selected hour uses an existing device make boolean flag true
                if(hourList.get(i).getDeviceName().equals(device.getName())){
                    deviceExists = true;
                }
            }

            //If device doesn't exist add the AwayHour position to the list.
            if(deviceExists == false){
                deletedPositions.add(i);
            }
        }
        // Go through the deleted position list and delete unbound AwayHours
        for (int i = 0; i < deletedPositions.size(); i++){
            int deletedPosition = deletedPositions.get(i);
            hourList.remove(deletedPosition);
        }
    }

    /**
     * Method that recreates AwayHours list from the saved data
     */
    private void LoadPreferences() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String hoursString = sharedPreferences.getString(SAVE_HOURS_LIST, "error");
        String minutesString = sharedPreferences.getString(SAVE_MINUTES_LIST, "error");
        String hoursEndString = sharedPreferences.getString(SAVE_END_HOURS, "error");
        String minutesEndString = sharedPreferences.getString(SAVE_END_MINUTES_LIST, "error");
        String daysString = sharedPreferences.getString(SAVE_DAYS_LIST, "error");
        String devicesString = sharedPreferences.getString(SAVE_DEVICE_LIST, "error");

        // Check that all saved data was loaded correctly
        if (!hoursString.equals("error") && !hoursString.equals("")
                && !hoursEndString.equals("error") && !hoursEndString.equals("")
                && !minutesEndString.equals("error") && !minutesEndString.equals("")
                && !minutesString.equals("error") && !minutesString.equals("")
                && !daysString.equals("error") && !daysString.equals("")
                && !devicesString.equals("error") && !devicesString.equals("")) {
            reconstructHourList(hoursString, minutesString, hoursEndString, minutesEndString,
                    daysString, devicesString);
            populateViewList();
        // Otherwise create a blank list
        } else {
            editor.clear();
        }

    }

    /**
     * Method that creates a new away_hours_screen activity
     * @param view current Activity
     */
    public void addNewHours(View view) {
        Intent addHours = new Intent(this, away_hours_screen.class);
        addHours.putExtra(SEND_DEVICE_LIST_PASSWORD, (Serializable) deviceList);
        startActivityForResult(addHours, ADD_HOURS_REQUEST_CODE);
    }

    /**
     * Method that prepares the list to be sent to the parent Activity
     * @param view current Activity
     */
    public void passHourList(View view) {
        String hourListString = "";
        for (AwayHour hour : hourList) {
            hourListString += hour.toString();
        }
        Intent awayHourList = new Intent();

        awayHourList.putExtra(NEW_HOURLIST, hourListString);

        setResult(RESULT_OK, awayHourList);
        SavePreferences();
        finish();
    }

    /**
     * Method that handles the feedback from away_hours_screen Activity
     * @param requestCode code of the Activity that is being handled
     * @param resultCode code indicating if Activity was successful or not
     * @param data data being recieved from the Activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check if this AwayHour has been created
        if (requestCode == ADD_HOURS_REQUEST_CODE) {
            // Check if AwayHour Activity was successful
            if (resultCode == RESULT_OK) {
                String deviceName = data.getStringExtra(AWAY_DEVICE_NAME_KEY);
                String day = data.getStringExtra(AWAY_DAY_KEY);
                Integer hours = data.getIntExtra(AWAY_HOUR_KEY, -1);
                Integer minutes = data.getIntExtra(AWAY_MINUTES_KEY, -1);
                Integer endHours = data.getIntExtra(AWAY_HOUR_END_KEY, -1);
                Integer endMinutes = data.getIntExtra(AWAY_MINUTES_END_KEY, -1);

                AwayHour newHour = new AwayHour(hours, minutes, endHours, endMinutes, day, deviceName);
                hourList.add(newHour);

                populateViewList();

                Toast.makeText(this,
                        "Added hours", Toast.LENGTH_LONG).show();
            }
            // Check if AwayHour was not created and notify the User
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,
                        "Unable to add new hours", Toast.LENGTH_LONG).show();
            }
        // Check if AwayHour has been edited
        } else if (requestCode == EDIT_HOURS_REQUEST_CODE) {
            // Check if AwayHour was successfully edited
            if (resultCode == RESULT_OK) {
                String deviceName = data.getStringExtra(AWAY_DEVICE_NAME_KEY);
                String day = data.getStringExtra(AWAY_DAY_KEY);
                Integer hours = data.getIntExtra(AWAY_HOUR_KEY, -1);
                Integer minutes = data.getIntExtra(AWAY_MINUTES_KEY, -1);
                Integer endHours = data.getIntExtra(AWAY_HOUR_END_KEY, -1);
                Integer endMinutes = data.getIntExtra(AWAY_MINUTES_END_KEY, -1);
                int hourPosition = data.getIntExtra(PICKED_HOUR_POSITION, -1);

                AwayHour newHour = new AwayHour(hours, minutes, endHours, endMinutes, day,
                                                                                        deviceName);

                hourList.remove(hourPosition);
                hourList.add(newHour);

                populateViewList();

                Toast.makeText(this,
                        "Successfully edited an hour", Toast.LENGTH_LONG).show();
            }
            // Check if AwayHour was not edited
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this,
                        "Unable to edit an hour", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Method that sets action listener that responds when AwayHour list is being clicked
     */
    private void setListListener() {
        ListView hourListView = (ListView) findViewById(R.id.awayHoursList);

        hourListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final int positionPointer = position;
                final AdapterView<?> parentPointer = parent;

                AlertDialog.Builder builder = new AlertDialog.Builder(away_hours_hun_screen.this);

                final CharSequence actions[] = new CharSequence[]{"Edit", "Delete"};

                builder.setTitle("Select action");
                builder.setItems(actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedOption) {
                        // Check if User selected to edit the AwayHour
                        if (selectedOption == 0) {
                            String item = (String) parentPointer.getItemAtPosition(positionPointer);

                            AwayHour selectedHour = reconstructAwayHour(item);

                            Intent editHour = new Intent(away_hours_hun_screen.this, away_hours_screen.class);

                            editHour.putExtra(EDIT_HOUR, selectedHour.getHour());
                            editHour.putExtra(EDIT_MINUTE, selectedHour.getMinutes());
                            editHour.putExtra(EDIT_END_HOUR, selectedHour.getEndHour());
                            editHour.putExtra(EDIT_END_MINUTE, selectedHour.getEndMinutes());
                            editHour.putExtra(EDIT_DAY, selectedHour.getDay());
                            editHour.putExtra(EDIT_DEVICE_NAME, selectedHour.getDeviceName());
                            editHour.putExtra(PICKED_HOUR_POSITION, positionPointer);
                            editHour.putExtra(SEND_DEVICE_LIST_PASSWORD, (Serializable) deviceList);

                            startActivityForResult(editHour, EDIT_HOURS_REQUEST_CODE);
                        // Otherwise remove the selected AwayHour
                        } else {
                            hourList.remove(positionPointer);

                            populateViewList();
                        }
                    }
                });

                builder.show();
            }
        });
    }


    /**
     * Method used to populate the displayed AwayHour ListView with data
     */
    private void populateViewList() {
        ListView hoursDisplayList = (ListView) findViewById(R.id.awayHoursList);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                (List) prepareListForView());

        hoursDisplayList.setAdapter(arrayAdapter);
    }

    /**
     * Method that prepares the AwayHour list to be viewed by the User
     * @return hourBreakdown ArrayList containing String representation of each AwayHour
     */
    private ArrayList<String> prepareListForView() {
        ArrayList<String> hourBreakdown = new ArrayList<>();

        // Go through each AwayHour in the list and add its String representation to the displayed
        // list
        for (AwayHour hour : hourList) {
            hourBreakdown.add(hour.getDay() + " : " + hour.getHour()
                    + " : " + hour.getMinutes() + " : " + hour.getEndHour()
                    + " : " + hour.getEndMinutes() + " : " + hour.getDeviceName());
        }

        return hourBreakdown;
    }

    /**
     * Method that breakdowns each AwayHour and prepares it for the saving process
     */
    private void prepareListForSave() {
        AwayHour hour;

        // Go through each AwayHour and pass its parts to the related String ArrayLists
        for (int i = 0; i < hourList.size(); i++) {
            hour = hourList.get(i);

            hoursList.add(Integer.toString(hour.getHour()));
            minutesList.add(Integer.toString(hour.getMinutes()));
            hoursEndList.add(Integer.toString(hour.getEndHour()));
            minutesEndList.add(Integer.toString(hour.getEndMinutes()));
            daysList.add(hour.getDay());
            devicesList.add(hour.getDeviceName());
        }

    }

    /**
     * Method that recreates an AwayHour from Strings
     * @param savedHours String representation of the beginning hour
     * @param savedMinutes String representation of the beginning minutes
     * @param savedEndHours String representation of the ending hour
     * @param savedEndMinutes String representation of the ending minutes
     * @param savedDays String representation of the beginning day
     * @param savedDevices String representation of the device name
     */
    private void reconstructHourList(String savedHours, String savedMinutes,
                                     String savedEndHours, String savedEndMinutes,
                                     String savedDays, String savedDevices) {
        String[] hoursArray = savedHours.split(",");
        String[] minutesArray = savedMinutes.split(",");
        String[] hoursEndArray = savedEndHours.split(",");
        String[] minutesEndArray = savedEndMinutes.split(",");
        String[] daysArray = savedDays.split(",");
        String[] devicesArray = savedDevices.split(",");

        // Go through all of the arrays and recreate AwayHours
        for (int i = 0; i < hoursArray.length; i++) {
            AwayHour newHour = new AwayHour(Integer.parseInt(hoursArray[i]),
                    Integer.parseInt(minutesArray[i]), Integer.parseInt(hoursEndArray[i]),
                    Integer.parseInt(minutesEndArray[i]), daysArray[i], devicesArray[i]);

            hourList.add(newHour);
        }

        checkHours();
    }

    /**
     * Method that reconstructs AwayHour from a String
     * @param awayHourString String contationg params of the AwayHour
     * @return
     */
    private AwayHour reconstructAwayHour(String awayHourString) {
        String[] hourPartArray = awayHourString.split(" : ");

        String day = hourPartArray[0].replaceAll("\\s+", "");
        Integer hour = Integer.parseInt(hourPartArray[1].replaceAll("\\s+", ""));
        Integer minute = Integer.parseInt(hourPartArray[2].replaceAll("\\s+",
                                                                                    ""));
        Integer endHour = Integer.parseInt(hourPartArray[3].replaceAll("\\s+",
                                                                                    ""));
        Integer endMinute = Integer.parseInt(hourPartArray[4].replaceAll("\\s+",
                                                                                    ""));
        String device = hourPartArray[5];

        return new AwayHour(hour, minute, endHour, endMinute, day, device);
    }

    /**
     * Method that builds a new String from a given ArrayList of String
     * @param list ArrayList of Strings
     * @return String String representation of the given ArrayList
     */
    private String buildNewString(ArrayList<String> list) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String elem : list) {
            stringBuilder.append(elem);
            stringBuilder.append(",");
        }

        return stringBuilder.toString();
    }
}

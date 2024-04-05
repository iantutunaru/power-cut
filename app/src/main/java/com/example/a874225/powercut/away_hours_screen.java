package com.example.a874225.powercut;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * away_hours_screen represents an Activity representing for creating a new AwayHour object from
 * User activity
 *
 * @author Ianis Tutunaru
 * @version 1.0
 * @since 2018-05-05
 */
public class away_hours_screen extends AppCompatActivity {
    // String representing day of the week when device is not being used
    private String dayOfTheWeek;
    // String representing name of the device that is saving energy
    private String deviceName;
    // Selected hour from which device is not being used
    private Integer selectedHour;
    // Selected minutes from which device is not being used
    private Integer selectedMinutes;
    // Selected day from which device is going to be used
    private String dayOfTheWeekEnd;
    // Selected hour from which device is going to be used
    private Integer selectedHourEnd;
    // Selected minutes from which device is going to be used
    private Integer selectedMinutesEnd;
    // Key used when sending away hour back to the parent Activity
    private final String AWAY_HOUR_KEY = "AWAY_HOUR";
    // Key used when sending away hour beginning minutes back to the parent Activity
    private final String AWAY_MINUTES_KEY = "AWAY_MINUTES";
    // Key used when sending away hour beginning day back to the parent Activity
    private final String AWAY_DAY_KEY = "AWAY_DAY";
    // Key used when sending away hour ending hour back to the parent Activity
    private final String AWAY_HOUR_END_KEY = "AWAY_END_HOUR";
    // Key used when sending away hour ending minutes to the parent Activity
    private final String AWAY_MINUTES_END_KEY = "AWAY_END_MINUTES";
    // Key used when sending away hour ending day to the parent Activity
    private final String AWAY_DAY_END_KEY = "AWAY_END_DAY";
    // Key used when sending away hour device name to the parent Activity
    private final String AWAY_DEVICE_NAME_KEY = "AWAY_DEVICE_NAME";
    // Password used when receiving Device list from parent Activity
    private final String SEND_DEVICE_LIST_PASSWORD = "DEVICE_LIST";
    // Key used when editing the beginning hour
    private final String EDIT_HOUR = "EDIT_HOUR";
    // Key used when editing the beginning minute
    private final String EDIT_MINUTE = "EDIT_MINUTE";
    // Key used when editing the beginning day
    private final String EDIT_DAY = "EDIT_DAY";
    // Key used when editing the ending hour
    private final String EDIT_END_HOUR = "EDIT_END_HOUR";
    // Key used when editing the ending minute
    private final String EDIT_END_MINUTE = "EDIT_END_MINUTE";
    // Key used when editing the ending day
    private final String EDIT_END_DAY = "EDIT_END_DAY";
    // Key used when editing the used device
    private final String EDIT_DEVICE_NAME = "EDIT_DEVICE_NAME";
    // Key used to when storing the picked hour position on the TimePicker
    private final String PICKED_HOUR_POSITION = "EDIT_POSITION";
    // TimePicker that used to pick the beginning hour and minutes
    private TimePickerDialog mTimePickerStart;
    // TimePicker that used to pick the ending hour and minutes
    private TimePickerDialog mTimePickerEnd;
    // Integer that stores the picked hour position on the TimePicker
    private Integer hourPosition;
    // ArrayList of devices used in the app
    private ArrayList<Device> deviceList;


    /**
     * Method that creates a new away hours activity or recreates it from a saved state for
     * editing purposes
     * @param savedInstanceState previously saved instance of the Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_away_hours_screen);

        // Recreate a device list from the sent extras
        if (getIntent().getSerializableExtra(SEND_DEVICE_LIST_PASSWORD) != null) {
            deviceList =
                    (ArrayList<Device>) getIntent().getSerializableExtra(SEND_DEVICE_LIST_PASSWORD);
        }

        Button daySelection = (Button) findViewById(R.id.daySelection);
        Button deviceSelection = (Button) findViewById(R.id.deviceSelection);

        // Check if there is no previously saved state
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            // Check if there are no extras and notify the User
            if (extras == null) {
                Toast.makeText(this, "Error while loading away hour data",
                        Toast.LENGTH_LONG);
            // Otherwise check if this a creation of a new AwayHour
            } else {
                // Check if this is an editing call
                if (extras.getString(EDIT_DAY) != null) {
                    mTimePickerStart = initClock();
                    mTimePickerStart.updateTime(extras.getInt(EDIT_HOUR), extras.getInt(EDIT_MINUTE));
                    mTimePickerEnd = initClock();
                    mTimePickerEnd.updateTime(extras.getInt(EDIT_END_HOUR), extras.getInt(EDIT_END_MINUTE));
                    daySelection.setText(extras.getString(EDIT_DAY));
                    deviceSelection.setText(extras.getString(EDIT_DEVICE_NAME));
                    hourPosition = extras.getInt(PICKED_HOUR_POSITION);
                }
            }
        // Check if there is a savedInstance of the Activity
        } else {
            // If there is a savedInstance recreate the Activity
            if (savedInstanceState.getSerializable(EDIT_HOUR) != null) {
                mTimePickerStart = initClock();
                mTimePickerStart.updateTime((Integer) savedInstanceState.getSerializable(EDIT_HOUR),
                        (Integer) savedInstanceState.getSerializable(EDIT_MINUTE));
                mTimePickerEnd = initClock();
                mTimePickerEnd.updateTime((Integer) savedInstanceState.getSerializable(EDIT_END_HOUR),
                        (Integer) savedInstanceState.getSerializable(EDIT_END_MINUTE));
                daySelection.setText((String) savedInstanceState.getSerializable(EDIT_DAY));
                deviceSelection.setText((String) savedInstanceState.getSerializable(EDIT_DEVICE_NAME));
                hourPosition = (Integer) savedInstanceState.getSerializable(PICKED_HOUR_POSITION);
            }
        }
    }

    /**
     * Method that creates a clock that picks the start hour and minutes
     * @return mTimePicker TimePickerDialog that represents the pick of start hour and minutes
     */
    private TimePickerDialog initClock() {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int beginHour, int beginMinute) {
                selectedHour = beginHour;
                selectedMinutes = beginMinute;
            }
        }, hour, minute, true);
        mTimePicker.setTitle("Select Time");

        return mTimePicker;
    }

    /**
     * Method that displays a clock that picks the start hour and minutes from XML call
     * @param view this Activity
     */
    public void setStartHour(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);

        mTimePickerStart = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int beginHour, int beginMinute) {
                selectedHour = beginHour;
                selectedMinutes = beginMinute;
            }
        }, hour, minute, true);
        mTimePickerStart.setTitle("Select Time");
        mTimePickerStart.show();
    }

    /**
     * Method that creates a clock that picks the ending hour and minutes
     * @param view this Activity
     */
    public void setEndHour(View view) {
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;

        mTimePickerEnd = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int endHour, int endMinute) {
                selectedHourEnd = endHour;
                selectedMinutesEnd = endMinute;
            }
        }, hour, minute, true);
        mTimePickerEnd.setTitle("Select Time");
        mTimePickerEnd.show();
    }

    /**
     * Method that sends the picked data back to the parent Activity
     * @param view this Activity
     */
    public void addNewHours(View view) {
        Intent newHour = new Intent();
        Button daySelector = (Button) findViewById(R.id.daySelection);
        Button deviceSelector = (Button) findViewById(R.id.deviceSelection);

        dayOfTheWeek = daySelector.getText().toString();
        deviceName = deviceSelector.getText().toString();

        // Check that User picked acceptable values for day and device params
        if (dayOfTheWeek.equals("Day of the week") || deviceName.equals("Select device")) {
            Toast.makeText(this, "Please select day/ device", Toast.LENGTH_SHORT).show();
        } else {
            newHour.putExtra(AWAY_HOUR_KEY, selectedHour);
            newHour.putExtra(AWAY_MINUTES_KEY, selectedMinutes);
            newHour.putExtra(AWAY_DAY_KEY, dayOfTheWeek);
            newHour.putExtra(AWAY_HOUR_END_KEY, selectedHourEnd);
            newHour.putExtra(AWAY_MINUTES_END_KEY, selectedMinutesEnd);
            newHour.putExtra(AWAY_DAY_END_KEY, dayOfTheWeekEnd);
            newHour.putExtra(AWAY_DEVICE_NAME_KEY, deviceName);
            newHour.putExtra(PICKED_HOUR_POSITION, hourPosition);

            setResult(RESULT_OK, newHour);

            finish();
        }
    }

    /**
     * Method that allows User to pick day of the week when pick a day button is clicked
     * @param view this Activity
     */
    public void selectDay(View view) {
        final Button daySelector = (Button) findViewById(R.id.daySelection);

        final CharSequence days[] = new CharSequence[]{"Monday", "Tuesday", "Wednesday", "Thursday",
                "Friday", "Saturday", "Sunday", "Every day"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Pick a day of the week");
        builder.setItems(days, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedDay) {
                daySelector.setText(days[selectedDay]);
            }
        });

        builder.show();
    }

    /**
     * Method that allows User to select the used Device
     * @param view this Activity
     */
    public void selectDevice(View view) {
        final Button deviceSelector = (Button) findViewById(R.id.deviceSelection);

        final CharSequence devices[] = getDeviceNames().toArray(new CharSequence[deviceList.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Pick a device");
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int selectedDay) {
                deviceSelector.setText(devices[selectedDay]);
            }
        });

        builder.show();
    }

    /**
     * Method that prepares Strings to be presented to the User so a device can be picked
     * @return deviceNames ArrayList of String containing names of all Devices in the system
     */
    private ArrayList<String> getDeviceNames() {
        ArrayList<String> deviceNames = new ArrayList<>();

        for (Device device : deviceList) {
            deviceNames.add(device.getName());
        }

        return deviceNames;
    }
}

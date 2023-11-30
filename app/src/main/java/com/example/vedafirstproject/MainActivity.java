package com.example.vedafirstproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String DEFAULT_TIME_KEY = "default_time";

    TextView textView;
    EditText timeInput;
    AlertDialog.Builder builder;
    CountDownTimer countDownTimer;
    Button startButton;
    long defaultTimeInMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.timer_text_box);
        timeInput = findViewById(R.id.time_input);
        startButton = findViewById(R.id.start_button);
        builder = new AlertDialog.Builder(this);

        // Load the default time from SharedPreferences
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        defaultTimeInMillis = settings.getLong(DEFAULT_TIME_KEY, 0);

        if (defaultTimeInMillis == 0) {
            // If it's the first app launch, ask the user to set a default time
            askUserForDefaultTime();
        } else {
            // If not the first launch, use the stored default time
            startCountdown(defaultTimeInMillis);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = timeInput.getText().toString();
                if (!inputText.isEmpty()) {
                    int countdownTimeInMinutes = Integer.parseInt(inputText) * 60 * 1000; // Convert to milliseconds
                    startCountdown(countdownTimeInMinutes);

                    // Save the entered time as the default time in SharedPreferences
                    saveDefaultTime(countdownTimeInMinutes);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid time", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void askUserForDefaultTime() {
        // Implement a dialog or input form to ask the user for a default time
        // and then call saveDefaultTime to store it in SharedPreferences.
        // This can be a one-time setup when the app is first installed.
    }

    private void saveDefaultTime(long timeInMillis) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(DEFAULT_TIME_KEY, timeInMillis);
        editor.apply();
    }

    private void startCountdown(long countdownTimeInMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(countdownTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                NumberFormat f = new DecimalFormat("00");
                long hour = (millisUntilFinished / 3600000) % 24;
                long min = (millisUntilFinished / 60000) % 60;
                long sec = (millisUntilFinished / 1000) % 60;
                textView.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));
            }

            public void onFinish() {
                textView.setText("00:00:00");
                builder.setMessage("You have exceeded Screen Time. Please enable Dark Mode for your Eyes health!!!")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                openDarkModeSettings();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(), "You choose not to enable Dark Mode", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.setTitle("Alert");
                alert.show();
            }
        }.start();
    }

    private void openDarkModeSettings() {
        Intent intent = new Intent();
        intent.setAction("android.settings.DISPLAY_SETTINGS");
        startActivity(intent);
    }
}

package com.jati.dev.androidalarmmanager;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.jati.dev.androidalarmmanager.utils.CommonUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private TextView tvTime, tvCountdown;
    private Button btStart;
    private long millis;
    private long targetMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUiComponents();
    }

    private void initUiComponents() {
        millis = System.currentTimeMillis();
        tvTime = findViewById(R.id.tv_time);
        tvCountdown = findViewById(R.id.tv_countdown);
        btStart = findViewById(R.id.bt_start);

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(millis);
                final String date = calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DAY_OF_MONTH);
                final int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                TimePickerDialog pickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String timestamp = date + " " + hourOfDay + ":" + minute;
                        tvTime.setText(timestamp);
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        try {
                            Date targetDate = sdf.parse(timestamp);
                            targetMillis = targetDate.getTime();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, hour, minutes, true);
                pickerDialog.show();
            }
        });

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvTime.getText().toString().trim().equals("")) {
                    CommonUtils.showToast(MainActivity.this, getString(R.string.pick_time));
                } else {
                    doCountdown();
                }
            }
        });
    }

    private void doCountdown() {
        millis = System.currentTimeMillis();
        if (targetMillis != 0) {
            long totalCount = targetMillis - millis;
            new CountDownTimer(totalCount, 1000) {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTick(long millisUntilFinished) {
                    btStart.setEnabled(false);
                    tvCountdown.setText(millisUntilFinished / 1000 + "s");
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onFinish() {
                    targetMillis = 0;
                    btStart.setEnabled(true);
                    tvCountdown.setText("00s");
                }
            }.start();

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent("com.jati.dev.androidalarmmanager" + targetMillis), PendingIntent.FLAG_ONE_SHOT);
            BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setMessage("1 Minutes")
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .create();
                    dialog.show();
                }
            };

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, targetMillis - 60000, pendingIntent);
            IntentFilter intentFilter = new IntentFilter("com.jati.dev.androidalarmmanager" + targetMillis);
            intentFilter.setPriority(999);

            registerReceiver(broadcastReceiver, intentFilter);
        }
    }
}

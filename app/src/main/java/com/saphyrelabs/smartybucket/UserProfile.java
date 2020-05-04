package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserProfile extends AppCompatActivity {
    private TextView facebookAccountName, accountId;
    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;
    private Button dailyNotificationBtn;

    public static final String NOTIFICATION_CHANNEL_ID = "200" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        dailyNotificationBtn = findViewById(R.id.notify);
        dailyNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar currentTime = Calendar.getInstance();
                int hour = currentTime.get(Calendar.HOUR_OF_DAY);
                int minute = currentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int selectedHour, int selectedMinutes) {
                                Toast.makeText(UserProfile.this.getApplicationContext(), "Reminder Set!", Toast.LENGTH_LONG).show();
                                startAlarmBroadcastReceiver(UserProfile.this, selectedHour, selectedMinutes);
                            }
                }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String facebookName = userConfigurations.getString("facebookName",null);
        String facebookAccountId = userConfigurations.getString("facebookUid",null);

        facebookAccountName = findViewById(R.id.accountName);
        facebookAccountName.setText(facebookName);

        accountId = findViewById(R.id.accountId);
        accountId.setText(facebookAccountId);

        logoutBtn = findViewById(R.id.logoutBtn);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(view -> {
            mAuth.signOut();
            LoginManager.getInstance().logOut();
            updateUI();
        });

        // Initialize BottomAppBar
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setLabelVisibilityMode(LabelVisibilityMode.LABEL_VISIBILITY_LABELED);

        // Handle onClick event
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.homeNav:
                        Intent home = new Intent(UserProfile.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.scanNav:
                        Intent scanType = new Intent(UserProfile.this, ScanType.class);
                        startActivity(scanType);
                        break;
                    case R.id.expense:
                        Intent expenses = new Intent(UserProfile.this, ViewExpenses.class);
                        startActivity(expenses);
                        break;
                    case R.id.account:
                        Intent account = new Intent(UserProfile.this, UserProfile.class);
                        startActivity(account);
                }
                return false;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null) {
            updateUI();
        }
    }

    public void updateUI() {
        Intent mainActivity = new Intent(UserProfile.this, SignIn.class);
        startActivity(mainActivity);
        Toast.makeText(this, "You have been logged out", Toast.LENGTH_LONG).show();
    }

    public static void startAlarmBroadcastReceiver(Context context, int calendarHour, int calendarMinute) {
        Calendar calendar = Calendar.getInstance();
        Intent _intent = new Intent(context, NotificationBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, _intent, 0);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, calendarHour);
        calendar.set(Calendar.MINUTE, calendarMinute);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000*60*60*24, pendingIntent);
    }
}

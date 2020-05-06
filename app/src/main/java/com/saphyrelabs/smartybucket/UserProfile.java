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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saphyrelabs.smartybucket.model.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class UserProfile extends AppCompatActivity implements SetBudget.SetBudgetListenerInterface, SetPreferences.SetPreferencesListenerInterface{
    private TextView facebookAccountName, accountId;
    private Button logoutBtn;
    private FirebaseAuth mAuth;
    private BottomNavigationView bottomNav;
    private Button dailyNotificationBtn, setBudget, setPreferences;
    private FirebaseFirestore smartyFirestore;
    private static final String TAG = "UserProfileFirestore";

    public static final String NOTIFICATION_CHANNEL_ID = "200" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();


        dailyNotificationBtn = findViewById(R.id.notify);
        setBudget = findViewById(R.id.setBudget);
        setPreferences = findViewById(R.id.setPreferences);

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

        setBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetBudgetPrompt();
            }
        });

        setPreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callSetUserMealPreferencesPrompt();
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

    private void initFirestore() {
        smartyFirestore = FirebaseFirestore.getInstance();
    }

    public void callSetBudgetPrompt() {
        SetBudget budgetPrompt = new SetBudget();
        budgetPrompt.show(getSupportFragmentManager(), "Set Budget Dialog");
    }

    public void callSetUserMealPreferencesPrompt() {
        SetPreferences userMealPreferencesPrompt = new SetPreferences();
        userMealPreferencesPrompt.show(getSupportFragmentManager(), "Set User Meal UserConfigurations Dialog");
    }

    @Override
    public void setData(float userBudget) {
        // Code to set budget data in firestore
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        SharedPreferences.Editor editor = userConfigurations.edit();
        editor.putFloat("budget", userBudget);
        editor.apply();
        String budgetString = Float.toString(userBudget);

        sendDataToFirestore();
    }

    @Override
    public void setData(HashMap<String, Boolean> userMealPreferences) {
        // Code to set user meal preferences in firestore
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        SharedPreferences.Editor editor = userConfigurations.edit();
        editor.putBoolean("userMealPreferencesStatus", true);

        // Converting userMealPreferences into a JSON string to be stored in SharedPreferences
        JSONObject jsonObject = new JSONObject(userMealPreferences);
        String jsonString = jsonObject.toString();
        editor.remove("userMealPreferences").commit();
        editor.putString("userMealPreferences", jsonString);
        editor.commit();

        sendDataToFirestore();
    }

    private Map<String,Boolean> loadMap(){
        Map<String,Boolean> outputMap = new HashMap<String,Boolean>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("userConfigurations", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("userMealPreferences", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Boolean value = (Boolean) jsonObject.get(key);
                    System.out.println(value);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

    public void sendDataToFirestore() {
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("facebookUid","0");
        String userName = userConfigurations.getString("facebookName","0");
        String userEmail = userConfigurations.getString("facebookEmail","0");
        float budget = userConfigurations.getFloat("budget",0);
//        Map<String, String> expenses = new HashMap<>();
//        Date date = new Date();
//        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
//        expenses.put(formatter.format(date), "0");

        DocumentReference docRef = smartyFirestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        docRef.update("budget", budget);
                        docRef.update("userMealPreferences", (HashMap<String, Boolean>) loadMap());
                    } else {
                        Log.d(TAG, "No such document");
                        User newUser = new User(userId, userName, userEmail, (HashMap<String, Boolean>) loadMap(), budget);

                        smartyFirestore.collection("users").document(userId).set(newUser)
                                .addOnSuccessListener(new OnSuccessListener< Void >() {
                                    public void onSuccess(Void aVoid) {
                                        View contextView = findViewById(R.id.scrollView);
                                        Snackbar.make(contextView, "You are all set! ", Snackbar.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            public void onFailure(@NonNull Exception e) {
                                View contextView = findViewById(R.id.scrollView);
                                Snackbar.make(contextView, "ERROR: " + e.toString(), Snackbar.LENGTH_LONG).show();
                                Log.d("TAG", e.toString());
                            }
                        });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

package com.saphyrelabs.smartybucket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.saphyrelabs.smartybucket.model.User;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;


public class MainActivity extends AppCompatActivity implements SetBudget.SetBudgetListenerInterface, SetPreferences.SetPreferencesListenerInterface{
    BottomAppBar bab;
    Button test1;
    private BottomSheetDialog bottomSheetDialog;
    private TextView monthlyBudgetValue;
    private FirebaseFirestore smartyFirestore;
    private LineChart mChart;
    private static final String TAG = "MainActivityFirestore";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable Firestore logging
        FirebaseFirestore.setLoggingEnabled(true);

        initFirestore();

        // Retrieving persistent data
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);

        monthlyBudgetValue = findViewById(R.id.monthlyBudgetValue);

        String modelType = userConfigurations.getString("modelType",null);
        String facebookEmail = userConfigurations.getString("facebookEmail",null);
        String facebookUid = userConfigurations.getString("facebookUid",null);
        float budget = userConfigurations.getFloat("budget",0);
        boolean userMealPreferencesStatus = userConfigurations.getBoolean("userMealPreferencesStatus", false);

        Thread t1 = new Thread(() -> {
            if (modelType == null) {
                // Default User Settings
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("userConfigurations", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("modelType", "float");
                editor.apply();
            }
        });

        Thread t2 = new Thread(() -> {
            if (budget == 0) {
                // New user is prompted to enter budget
                callSetBudgetPrompt();
            } else {
                String budgetString = Float.toString(budget);
                monthlyBudgetValue.setText(budgetString);
                System.out.println("Budget is set to: " + userConfigurations.getFloat("budget", 0));
            }
        });

        Thread t3 = new Thread(() -> {
            if (!userMealPreferencesStatus) {
                callSetUserMealPreferencesPrompt();
            }
        });

        t2.start();
        try {
            t2.join();
            t1.start();
            t1.join();
            t3.start();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("---------------------- DEBUG INFO ----------------------");
        System.out.println("ModelType: " + modelType);
        System.out.println("FacebookEmail: " + facebookEmail);
        System.out.println("FacebookUserId: " + facebookUid);
        System.out.println("--------------------------------------------------------");

        RelativeLayout addItemBanner = findViewById(R.id.add_more_items_banner);
        addItemBanner.setOnClickListener(V -> {
            Intent registerItems = new Intent(MainActivity.this, RegisterItems.class);
            startActivity(registerItems);
        });


        // Initialize BottomAppBar
        bab = findViewById(R.id.bottom_app_bar);

        // Append menu items to BottomAppBar
        bab.replaceMenu(R.menu.menu_items);

        // Handle onClick event
        bab.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            switch (id){
                case R.id.app_bar_inventory:
                    Toast.makeText(MainActivity.this,"Inventory Clicked",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.app_bar_search:
                    Toast.makeText(MainActivity.this,"Search Clicked",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.app_bar_settings:
                    Intent myPreferencesIntent = new Intent(MainActivity.this, UserConfigurations.class);
                    startActivity(myPreferencesIntent);
                    break;
            }
            return false;
        });

        // Register click event for BottomAppBar drawer icon
        bab.setNavigationOnClickListener(v -> openNavigationMenu());

        // Register click event for BottomAppBar FloatingActionButton icon
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MainActivity.this, ScanType.class);
            startActivity(cameraIntent);
        });

        mChart = findViewById(R.id.chart);
        mChart.setTouchEnabled(true);
        mChart.setPinchZoom(true);
        CustomChartMarkerView mv = new CustomChartMarkerView(getApplicationContext(), R.layout.activity_custom_chart_marker_view);
        mv.setChartView(mChart);
        mChart.setMarker(mv);
        renderData();

    }

    public void renderData() {
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        float budget = userConfigurations.getFloat("budget",0);

        LimitLine llXAxis = new LimitLine(30f, "Index 30");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        LimitLine ll2 = new LimitLine(budget / 4, "Weekly Limit");
        ll2.setLineWidth(4f);
        ll2.enableDashedLine(5f, 5f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(10f);


        XAxis xAxis = mChart.getXAxis();
        xAxis.enableGridDashedLine(10f, 10f, 0f);
        xAxis.setAxisMaximum(30f);
        xAxis.setAxisMinimum(0f);
        xAxis.setDrawLimitLinesBehindData(true);

        LimitLine ll1 = new LimitLine(budget, "Monthly Budget");
        ll1.setLineWidth(4f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaximum(budget + 200);
        leftAxis.setAxisMinimum(0f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawLimitLinesBehindData(false);

        mChart.getAxisRight().setEnabled(false);
        setData();
    }

    private void setData() {
        SharedPreferences userConfigurations = getSharedPreferences("userConfigurations", MODE_PRIVATE);
        String userId = userConfigurations.getString("facebookUid","0");

        ArrayList<Entry> values = new ArrayList<>();

        DocumentReference docRef = smartyFirestore.collection("users").document(userId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.get("expenses"));
                        Map<String, String> currentExpense = (HashMap<String, String>) document.get("expenses");
                        System.out.println(currentExpense);
                        if (currentExpense != null) {
                            currentExpense.forEach((k, v) -> {
                                values.add(new Entry(Float.parseFloat(k.substring(0, 2)), Float.parseFloat(v)));
                            });
                        }

                        LineDataSet set1;
                        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
                            set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                            set1.setValues(values);
                            mChart.getData().notifyDataChanged();
                            mChart.notifyDataSetChanged();
                        } else {
                            set1 = new LineDataSet(values, "Monthly Expenses");
                            set1.setDrawIcons(false);
                            set1.enableDashedLine(10f, 5f, 0f);
                            set1.enableDashedHighlightLine(10f, 5f, 0f);
                            set1.setColor(Color.DKGRAY);
                            set1.setCircleColor(Color.DKGRAY);
                            set1.setLineWidth(1f);
                            set1.setCircleRadius(3f);
                            set1.setDrawCircleHole(false);
                            set1.setValueTextSize(9f);
                            set1.setDrawFilled(true);
                            set1.setFormLineWidth(1f);
                            set1.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
                            set1.setFormSize(15.f);

                            if (Utils.getSDKInt() >= 18) {
                                Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.gradient_background_main);
                                set1.setFillDrawable(drawable);
                            } else {
                                set1.setFillColor(Color.DKGRAY);
                            }
                            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
                            dataSets.add(set1);
                            LineData data = new LineData(dataSets);
                            mChart.setData(data);
                        }
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }


    private void openNavigationMenu() {

        //this will get the menu layout
        final View bottomAppBarDrawer = getLayoutInflater().inflate(R.layout.fragment_bottomsheet,null);
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(bottomAppBarDrawer);
        bottomSheetDialog.show();

        //this will find NavigationView from id
        NavigationView navigationView = bottomAppBarDrawer.findViewById(R.id.fragment_bottomsheet);

        //This will handle the onClick Action for the menu item
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            switch (id){
                case R.id.nav1:
                    Intent profileIntent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(profileIntent);
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.nav2:
                    Toast.makeText(MainActivity.this,"Item 2 Clicked",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    break;
                case R.id.nav3:
                    Toast.makeText(MainActivity.this,"Item 3 Clicked",Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    break;
            }
            return MainActivity.super.onOptionsItemSelected(menuItem);
        });
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
        monthlyBudgetValue.setText(budgetString);

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

    /**
     * This function converts a JSON string into a HashMap
     * @return Map<String, Boolean>
     *
     */
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

    /**
     * This function uploads data to Cloud Firestore
     * @params HashMap<String, Boolean> userMealPreferences
     *
     */
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

        System.out.println("userId is set to: " + userId);
        System.out.println("userName is set to: " + userName);
        System.out.println("userEmail is set to: " + userEmail);
        System.out.println("meal preferences is set to: " + loadMap());
        System.out.println("budget is set to: " + budget);
//        System.out.println("expenses is set to: " + expenses);
    }
}

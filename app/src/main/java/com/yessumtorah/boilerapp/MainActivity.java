package com.yessumtorah.boilerapp;

import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yessumtorah.boilerapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.sql.Time;

public class MainActivity extends AppCompatActivity implements SessionListFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String boilerID = "Demo3217"; // For Demo use
    private static final String PREFERENCES = "prefs";
    private static final String BOILER_ON = "boilerOn";
    private static final String TIME_PASSED = "secondsPassed";
    private TextView mTextMessage;
    private Boiler theBoiler;
    private Session mSession;
    private ArrayList<Session> allSessions;
    SessionDatabase db;
    private boolean boilerOn, firstSession;
    private final Handler timer = new Handler();
    private Chronometer boilerChronometer;
    private Time total = new Time(0, 0, 0);


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    boilerChronometer.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_dashboard:
                    boilerChronometer.setVisibility(View.GONE);
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    SessionListFragment fragment = new SessionListFragment();
                    fragmentTransaction.add(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:
                    //dropSessionsTable();
                    return true;
            }
            return false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("boilerChrono", boilerChronometer.getText().toString());
        outState.putBoolean("boilerState", boilerOn);
        outState.putInt("timerSeconds", total.getSeconds());
        super.onSaveInstanceState(outState);
        Log.i("onSaveInstanceState", "boilerOn is " + boilerOn + "\nboilerChronometer text is " + boilerChronometer.getText().toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button customButton = findViewById(R.id.custom_button);
        Switch switchEnable = findViewById(R.id.enable);
        boilerChronometer = (Chronometer) findViewById(R.id.boilerChronometer); // initiate a chronometer
        boilerChronometer.setVisibility(View.VISIBLE);
        /*if (savedInstanceState != null) {
            boilerOn = savedInstanceState.getBoolean("boilerState");
            String timecode = savedInstanceState.getString("boilerChrono");
            boilerChronometer.setText(timecode);
            total.setTime(savedInstanceState.getInt("timerSeconds"));
           boilerChronometer.setBase(SystemClock.elapsedRealtime() - total.getTime());
            boilerChronometer.start();
            Log.i(TAG, "Activity reCreated, boilerOn is " + boilerOn);
        }*/
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!boilerOn)
                    Toast.makeText(MainActivity.this, "Boiler Turned ON", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(MainActivity.this, "Boiler Turned OFF", Toast.LENGTH_SHORT).show();
                boilerButtonHandler(v);
            }
        });

        switchEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    customButton.setEnabled(true);
                } else
                    customButton.setEnabled(false);
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        boilerOn = false;
        firstSession = true;
        allSessions = new ArrayList<Session>();
        db = Room.databaseBuilder(getApplicationContext(),
                SessionDatabase.class, "mySessionsDB").build();
    }

    public void boilerButtonHandler(View v) {
        Log.d(TAG, "boilerButton clicked");
        boilerChronometer.setBase(SystemClock.elapsedRealtime() - 0);
        Runnable runnable = new Runnable() {
            private int tick = 0;
            @Override
            public void run() {
                if (boilerOn) {
                    mSession = new Session();
                    mSession.setDate(new Date().toString());
                    boilerChronometer.start();
                    boilerChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {

                        @Override
                        public void onChronometerTick(Chronometer chronometer) {
                            Log.d("ChronoTickListenerDebug", boilerChronometer.getContentDescription().toString());
                            tick += Session.SECOND;
                            total.setTime(tick);
                        }
                    });
                }
            }
        };
        if (!boilerOn) {
            boilerOn = true;

            if (firstSession) {
                theBoiler = Boiler.getInstance(boilerID);
                firstSession = false;
            }
            timer.postDelayed(runnable, Session.SECOND); // DON'T REMOVE
        } else {
            boilerOn = false;
            boilerChronometer.stop();
            boilerChronometer.setText("00:00");
            timer.removeCallbacksAndMessages(runnable); // DON'T REMOVE
            mSession.setTotalTime((int)total.getTime()/Session.SECOND);
            Log.d(TAG, "Total time for this session: " + mSession.getTotalTime() + " at date " + mSession.getDate());

            // Save current session in local DB
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db.daoAccess().insertSession(mSession);
                    List<Session> retrievedSessions = db.daoAccess().listSessions();
                    for (Session s : retrievedSessions) {
                        Log.d(TAG, s.toString());
                    }
                }
            });
            t.start();

            allSessions.add(mSession);
            Log.d(TAG, "Session " + mSession + " with total time of " + mSession.getTotalTime()/Session.SECOND + " added to allSessions " + allSessions.size());
        }
    }

    public ArrayList<Session> getAllSessions() {
        return allSessions;
    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {
        Log.d(TAG, "onListFragmentInteraction callback");
    }

    private void dropSessionsTable() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                db.daoAccess().deleteAll();
                Log.d(TAG, "Sessions table dropped");
            }
        });
        t.start();
    }

}



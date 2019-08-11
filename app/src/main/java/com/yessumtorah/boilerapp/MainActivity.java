package com.yessumtorah.boilerapp;

import android.arch.persistence.room.Room;
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
                    //   FragmentOne f1 = new FragmentOne();
                    SessionListFragment fragment = new SessionListFragment();
                    fragmentTransaction.add(R.id.container, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;
                case R.id.navigation_notifications:

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button customButton = findViewById(R.id.custom_button);
        Switch switchEnable = findViewById(R.id.enable);
        boilerChronometer = (Chronometer) findViewById(R.id.boilerChronometer); // initiate a chronometer
        boilerChronometer.setVisibility(View.VISIBLE);
        customButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Boiler Button Clicked", Toast.LENGTH_SHORT).show();
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
            private int time = 0;
            @Override
            public void run() {
                if (boilerOn) {
                    time += 1000;
                    boilerChronometer.start();
                    mSession = new Session();
                    Log.d("TimerExample", "Going for... " + time);
                    timer.postDelayed(this, 1000);
                    total.setTime(time);
                }
            }
        };
        if (!boilerOn) {
            boilerOn = true;

            if (firstSession) {
                theBoiler = Boiler.getInstance(boilerID);
                theBoiler.setOn(true);
                firstSession = false;
            }
            timer.postDelayed(runnable, Session.SECOND);
        } else {
            boilerOn = false;
            boilerChronometer.stop();
            boilerChronometer.setText("00:00");
            theBoiler.setOn(false);
            timer.removeCallbacksAndMessages(runnable);
            mSession.setTotalTime((int)total.getTime()/Session.SECOND);
            mSession.setDate(new Date().toString());
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

    private class boilerTask extends TimerTask {
    private Date date;

        public boilerTask(Date date) {
           this.date = date;
        }

        @Override
        public void run() {
            mSession = new Session();
            mSession.setDate(date.toString());
            mSession.setUser("Primary User");
            theBoiler.setOn(true);
        }

    }

}



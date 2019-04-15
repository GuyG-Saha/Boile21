package com.yessumtorah.boilerapp;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.os.Handler;
import android.os.health.TimerStat;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.yessumtorah.boilerapp.dummy.DummyContent;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements ItemFragment.OnListFragmentInteractionListener {
    private static final String TAG = "MainActivity";
    private static final String boilerID = "Demo3217"; // For Demo use
    private TextView mTextMessage;
    private Boiler theBoiler;
    private Session mSession;
    private ArrayList<Session> allSessions;
    SessionDatabase db;
    private boolean boilerOn, firstSession;
    private final Handler timer = new Handler();
    private int total = 0;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:

                    return true;
                case R.id.navigation_dashboard:
                    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                 //   FragmentOne f1 = new FragmentOne();
                    ItemFragment fragment = new ItemFragment();
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
        Runnable runnable = new Runnable() {
            private int time = 0;
            @Override
            public void run() {
                if (boilerOn) {
                    time += 1000;
                    Log.d("TimerExample", "Going for... " + time);
                    timer.postDelayed(this, 1000);
                    total = time;
                }
            }
        };
        if (!boilerOn) {
            boilerOn = true;
            mSession = new Session();
            if (firstSession) {
                theBoiler = Boiler.getInstance(boilerID);
                theBoiler.setOn(true);
                firstSession = false;
            }
            timer.postDelayed(runnable, 1000);
        } else {
            boilerOn = false;
            theBoiler.setOn(false);
            timer.removeCallbacksAndMessages(runnable);
            mSession.setTotalTime(total);
            mSession.setDate(new Date().toString());
            Log.d(TAG, "Total time for this session: " + total);

            // Save current session in local DB
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    db.daoAccess().insertSession(mSession);
                }
            });
            t.start();

            allSessions.add(mSession);
            Log.d(TAG, "Session " + mSession + " with total time of " + mSession.getTotalTime()/1000 + "s added to allSessions " + allSessions.size());
        }
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



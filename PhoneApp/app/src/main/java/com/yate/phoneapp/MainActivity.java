package com.yate.phoneapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MainActivity extends AppCompatActivity {

    private Spinner main_spinner;
    private static final String[]paths = {"Buy","Rent","New Listing","Recent Sold", "Featured", "Commercial", "EB-5"};

    //butterknife library -- makes getting to access to views from two lines of code to one
    @InjectView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.drawer_recyclerView) RecyclerView drawerRecyclerView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RelativeLayout backGround = (RelativeLayout) findViewById(R.id.main_layout);
        backGround.setBackgroundResource(R.drawable.background_main);

        ButterKnife.inject(this); //gives access to views defined above

        setSupportActionBar(toolbar);
        //addItemsOnSpinner();
        //gives us buttons and lets us set action bar
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.title, R.string.title);
        //Attaching the drawerToggle to the drawerLayout
        drawerLayout.setDrawerListener(drawerToggle);
        //Prepare command, ready to go
        drawerToggle.syncState();

        List<String> rows = new ArrayList<>();
        rows.add("Home");
        rows.add("Favorites");
        rows.add("Map");
        rows.add("About");
        rows.add("Saved Pages");

        DrawerAdapter drawerAdapter = new DrawerAdapter(rows);
        drawerRecyclerView.setAdapter(drawerAdapter);
        drawerRecyclerView.setHasFixedSize(true);
        drawerRecyclerView.setLayoutManager(new LinearLayoutManager(this));




        //Click event and Fragments
        final GestureDetector mGestureDetector = new GestureDetector(MainActivity.this, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e){
                return true;
            }
        });

        drawerRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {

            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                View child = drawerRecyclerView.findChildViewUnder(e.getX(), e.getY());

                if (child != null && mGestureDetector.onTouchEvent(e)) {
                    drawerLayout.closeDrawers();

                    switch (drawerRecyclerView.getChildLayoutPosition(child)) {
                        case 1:
                            Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                            break;
                        case 2:
                            Toast.makeText(MainActivity.this, "Favorites", Toast.LENGTH_SHORT).show();
                            break;
                        case 3:
                            Toast.makeText(MainActivity.this, "Map", Toast.LENGTH_SHORT).show();
                            break;
                        case 4:
                            Toast.makeText(MainActivity.this, "About", Toast.LENGTH_SHORT).show();
                            break;
                        case 5:
                            Toast.makeText(MainActivity.this, "Saved Pages", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });





        //******************************Spinner Adapter for main
        main_spinner = (Spinner) findViewById(R.id.options_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,paths){
            public View getView(int position, View convertView,
                                ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //((TextView) v).setTextSize(16);
                ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View v = super.getDropDownView(position, convertView,
                        parent);

                ((TextView) v).setTextColor(Color.WHITE);
                //v.setBackgroundResource(R.drawable.spinner_bg);

                //((TextView) v).setTextColor(getResources().getColorStateList(
                //        R.color.spinner_text));
                //((TextView) v).setTypeface(fontStyle);
                //((TextView) v).setGravity(Gravity.CENTER);

                return v;
            }
        };

        //**********************Spinner Adapter
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        main_spinner.setAdapter(adapter);
        //***************Spinner activity
        main_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /*
    public void addItemsOnSpinner(Bundle savedInstanceState) {
        //super.addItemsOnSpinner(savedInstanceState);
        Spinner spinner = (Spinner) findViewById(R.id.options_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.App_Options, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }
    */
    //**********************button activity
    public void sendMessage(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    //************************button activity end
}

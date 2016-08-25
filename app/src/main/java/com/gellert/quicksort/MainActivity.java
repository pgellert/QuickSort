package com.gellert.quicksort;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);
                fragment.SortValues();
                fab.hide();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment);

        switch (id){
            case R.id.action_settings:
                fragment.stopSorting();

                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_randomize:
                fragment.fillRandomValues();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

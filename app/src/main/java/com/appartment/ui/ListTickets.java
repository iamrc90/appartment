package com.appartment.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.appartment.R;
import com.appartment.adaptor.ViewPagerAdaptor;
import com.appartment.helpers.SessionManager;
import com.appartment.helpers.Utils;

import java.util.List;

public class ListTickets extends AppCompatActivity {
    private final String TAG = ListTickets.class.getSimpleName();
    private Toolbar toolBar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ViewPagerAdaptor viewPagerAdaptor;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_tickets);
        if (!Utils.isNetworkAvailable(ListTickets.this)) {
            Utils.showDialog(ListTickets.this, ListTickets.class);
        }
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager());
        viewPagerAdaptor.addFragments(new ActiveTicketsFragment(),getResources().getString(R.string.tab_title_active_tickets));
        viewPagerAdaptor.addFragments(new InactiveTicketsFragment(),getResources().getString(R.string.tab_title_inactive_tickets));
        viewPager.setAdapter(viewPagerAdaptor);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.complaints_act_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                logout();
        }
        return false;
    }

    private void logout() {
        session = new SessionManager(getApplicationContext());
        session.setLogin(false,0);
        Intent intent = new Intent(ListTickets.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}

package com.appartment.ui;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.appartment.R;

public class TicketsResultActivity extends AppCompatActivity {

    public static final String SEARCH_KEY = "search_key";
    public static final String TYPE = "type";
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_result);
        bundle = getIntent().getExtras();

        initViews();
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (bundle.getString(TicketsResultActivity.TYPE).equals("active"))
            toolbar.setTitle(R.string.title_active_tickets);
        else
            toolbar.setTitle(R.string.title_in_active_tickets);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
    }

    public static void start(Activity activity, Bundle bundle) {

        activity.startActivity(new Intent(activity, TicketsResultActivity.class).putExtras(bundle));
    }
}

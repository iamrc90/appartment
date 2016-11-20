package com.appartment.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.appartment.R;
import com.appartment.adapter.TicketAdapter;
import com.appartment.app.AppConfig;
import com.appartment.app.AppController;
import com.appartment.helpers.EndlessRecyclerViewScrollListener;
import com.appartment.helpers.RecyclerItemClickListener;
import com.appartment.helpers.SessionManager;
import com.appartment.helpers.Utils;
import com.appartment.model.Ticket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ComplaintsActivity extends AppCompatActivity {

    private final String TAG = ComplaintsActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    public final static String serialisedObjKey = "TicketObject";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

        if (!Utils.isNetworkAvailable(ComplaintsActivity.this)) {
            Utils.showDialog(ComplaintsActivity.this, ComplaintsActivity.class);
        }

        recyclerView = (RecyclerView) findViewById(R.id.complaintListView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(this, ticketList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener((GridLayoutManager) mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //feed data
                Log.d(TAG,"Page "+ page);
                Log.d(TAG,"Total "+ totalItemsCount);
                progressBar.setVisibility(View.VISIBLE);
                prepareData();
            }
        });
        recyclerView.setAdapter(adapter);
        // show onscreen loader first time only
        progressDialog = new ProgressDialog(ComplaintsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressDialog.isShowing()) {
                    hideDialog();
                    Toast.makeText(ComplaintsActivity.this,"slow or no internet connection",Toast.LENGTH_LONG).show();
                }
            }
        },5000);
        prepareData();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(ComplaintsActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        //TODO:: move to detail activity
                        Intent intent = new Intent(ComplaintsActivity.this, ComplaintDetails.class);
                        intent.putExtra(serialisedObjKey,ticketList.get(position));
                        startActivity(intent);
                    }
                })
        );
    }

    private void prepareData() {
        String  tag_string_req = "ticket_request";

        String apiUrl = AppConfig.URL_TICKETS;

        StringRequest strReq = new StringRequest(Request.Method.GET,
                apiUrl, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                parseServerData(response.toString());
                hideDialog();
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    // Data Parser
    private void parseServerData(String json) {
        try {
            JSONObject jsonRootObject = new JSONObject(json);

            //Get the instance of JSONArray that contains JSONObjects
            JSONArray jsonArray = jsonRootObject.optJSONArray("data");

            //Iterate the jsonArray and print the info of JSONObjects
            for(int i=0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                String priority     = jsonObject.optString("priority").toString();
                String date         = jsonObject.optString("date").toString();
                String ticketNumber = jsonObject.optString("ticket_number").toString();
                String summary      = jsonObject.optString("summary").toString();
                String address      = jsonObject.optString("address").toString();
                Ticket ticket       = new Ticket(priority,ticketNumber,date,summary,address);
                ticketList.add(ticket);
            }
        } catch (JSONException e) {e.printStackTrace();}
        progressBar.setVisibility(View.GONE);
        hideDialog();
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
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
        Intent intent = new Intent(ComplaintsActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
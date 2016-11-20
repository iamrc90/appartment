package com.appartment.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appartment.R;
import com.appartment.adapter.TicketAdapter;
import com.appartment.app.AppConfig;
import com.appartment.app.AppController;
import com.appartment.helpers.JsonParser;
import com.appartment.helpers.RecyclerItemClickListener;
import com.appartment.helpers.SessionManager;
import com.appartment.model.Ticket;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TicketsResultActivity extends AppCompatActivity {

    private final String TAG = TicketsResultActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private TicketAdapter adapter;
    private List<Ticket> ticketList;
    private ProgressDialog progressDialog;
    public final static String serialisedObjKey = "TicketObject";
    private SessionManager session;

    public static final String SEARCH_KEY = "search_key";
    public static final String TYPE = "type";
    public static final String PRIORITY = "priority";
    public static final String STATUS = "status";
    private Bundle bundle;

    private String type, priority, status, searchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets_result);
        bundle = getIntent().getExtras();

        getData();

        initToolBar();

        initViews();
    }

    private void getData() {

        type = bundle.getString(TYPE);
        priority = bundle.getString(PRIORITY);
        status = bundle.getString(STATUS);
        searchId = bundle.getString(SEARCH_KEY);
    }

    private void initToolBar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

//        if (bundle.getString(TicketsResultActivity.TYPE).equals("active"))
//            toolbar.setTitle(R.string.title_active_tickets);
//        else
//            toolbar.setTitle(R.string.title_in_active_tickets);

        toolbar.setTitle(R.string.title_results);

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

    private void initViews() {

        // start
        recyclerView = (RecyclerView) findViewById(R.id.complaintListView);
        session = new SessionManager(this);
        ticketList = new ArrayList<>();
        adapter = new TicketAdapter(TicketsResultActivity.this, ticketList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(TicketsResultActivity.this, 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(TicketsResultActivity.this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        Intent intent = new Intent(TicketsResultActivity.this, ComplaintDetails.class);
                        intent.putExtra(serialisedObjKey, ticketList.get(position));
                        startActivity(intent);
                    }
                })
        );

        showResults();
    }

    private void showResults() {

        showDialog();
        prepareData();

    }

    public static void start(Activity activity, Bundle bundle) {

        activity.startActivity(new Intent(activity, TicketsResultActivity.class).putExtras(bundle));
    }

    private void prepareData() {
        String tag_string_req = "ticket_request";

        String apiUrl = AppConfig.URL_TICKETS;

        final String userId = Integer.toString(session.getUserId());

        final StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_TICKETS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Tickets: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");

                    // Check for status node in json
                    if (status == 200) {
                        // successful
                        onSuccess(response);
                    } else {
                        // Error Get the error message
                        String errorMsg = jObj.getString("message");

                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
                hideDialog();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                onFailure("something went wrong. please try again");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id", userId);

                if (searchId != null && !searchId.equals(""))
                    params.put("id", searchId);
                else {
                    params.put("status", status);
                    params.put("priority", priority);
                }

                Log.e("params are", params.toString());

                return params;

            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onFailure(String message) {
        hideDialog();
        Toast.makeText(TicketsResultActivity.this, message, Toast.LENGTH_LONG).show();
    }

    private void onSuccess(String response) {
//        parseServerData(response.toString());
        JsonParser.parseTickets(response, ticketList);
        hideDialog();
        adapter.notifyDataSetChanged();
    }

    private void showDialog() {
        // show onscreen loader first time only
        progressDialog = new ProgressDialog(TicketsResultActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

}

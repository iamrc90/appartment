package com.appartment.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.appartment.R;
import com.appartment.adaptor.TicketAdaptor;
import com.appartment.app.AppConfig;
import com.appartment.app.AppController;
import com.appartment.helpers.EndlessRecyclerViewScrollListener;
import com.appartment.helpers.RecyclerItemClickListener;
import com.appartment.helpers.SessionManager;
import com.appartment.model.Ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class InactiveTicketsFragment extends Fragment {

    private final String TAG = ActiveTicketsFragment.class.getSimpleName();
    private RecyclerView recyclerView;
    private TicketAdaptor adapter;
    private List<Ticket> ticketList;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    public final static String serialisedObjKey = "TicketObject";
    private SessionManager session;


    public InactiveTicketsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        session =  new SessionManager(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_active_tickets, container, false);
        // start

        recyclerView = (RecyclerView) rootView.findViewById(R.id.complaintListView);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        ticketList = new ArrayList<>();
        adapter = new TicketAdaptor(getActivity(), ticketList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
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
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading..");
        showDialog();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(progressDialog.isShowing()) {
                    hideDialog();
                    Toast.makeText(getActivity(),"slow or no internet connection",Toast.LENGTH_LONG).show();
                }
            }
        },5000);
        prepareData();

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
//                        Intent intent = new Intent(ComplaintsActivity.this, ComplaintDetails.class);
//                        intent.putExtra(serialisedObjKey,ticketList.get(position));
//                        startActivity(intent);
                    }
                })
        );
        // end
        return rootView;
    }

    private void prepareData() {
        String  tag_string_req = "ticket_request";

        String apiUrl = AppConfig.URL_TICKETS;

        final String userId = Integer.toString(session.getUserId());
        final String status = "closed";

        StringRequest strReq = new StringRequest(Request.Method.POST,
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
                    }
                    else {
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
                params.put("status", status);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void onFailure(String message) {
        hideDialog();
        Toast.makeText(getActivity(), message , Toast.LENGTH_LONG).show();
    }

    private void onSuccess(String response) {
        parseServerData(response.toString());
        hideDialog();
        adapter.notifyDataSetChanged();
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


}

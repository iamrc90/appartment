package com.appartment.helpers;

import com.appartment.model.Ticket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sumit on 11/5/2016.
 */

public class JsonParser {

    public static void parseTickets(String json, List<Ticket> ticketList){

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

    }
}

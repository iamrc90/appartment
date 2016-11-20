package com.appartment.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.appartment.R;
import com.appartment.app.AppConfig;
import com.appartment.model.Ticket;

import java.util.List;

public class TicketAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Ticket> ticketList;
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    public TicketAdapter(Context mContext, List<Ticket> ticketList) {
        this.mContext = mContext;
        this.ticketList = ticketList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView priority, ticketNumber, ticketDate, summary, address;

        public MyViewHolder(View view) {
            super(view);
            priority        = (TextView) view.findViewById(R.id.priority);
            ticketNumber    = (TextView) view.findViewById(R.id.ticketNumber);
            ticketDate      = (TextView) view.findViewById(R.id.ticketDate);
            summary         = (TextView) view.findViewById(R.id.summary);
            address         = (TextView) view.findViewById(R.id.address);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return ticketList.get(position) == null ? VIEW_PROG : VIEW_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_card, parent, false);
        vh = new MyViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder && ticketList.size() > 0) {
            Ticket ticket = ticketList.get(position);
            final MyViewHolder h = (MyViewHolder) holder;
            h.priority.setText(ticket.getPriority());
            h.priority.setBackgroundColor(Color.parseColor(AppConfig.getPriorityBackground(ticket.getPriority())));
            h.priority.setTextColor(Color.parseColor(AppConfig.getPriorityTextColor(ticket.getPriority())));
            h.ticketNumber.setText(ticket.getTicketNumber());
            h.ticketDate.setText(ticket.getTicketDate());
            h.summary.setText(ticket.getSummary());
            h.address.setText(ticket.getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }
}
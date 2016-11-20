package com.appartment.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.appartment.R;
import com.appartment.adapter.FilterAdapter;
import com.appartment.model.Filter;
import com.appartment.model.FilterHeader;
import com.appartment.model.FilterItem;
import com.appartment.model.enums.TicketType;

import java.util.ArrayList;

public class FilterActivity extends AppCompatActivity implements FilterAdapter.MyClickListener {

    public static final String TYPE = "type";
    private Bundle bundle;
    private String type;
    private FilterAdapter bambooSettingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        bundle = getIntent().getExtras();

        initViews();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {

            doFilter();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void doFilter() {

        if (checkFilterSelected()) {

            String priority = getPriority();
            String staus = getStatus();

            Bundle bundle = new Bundle();
            bundle.putString(TicketsResultActivity.TYPE, type);
            bundle.putString(TicketsResultActivity.PRIORITY, priority);
            bundle.putString(TicketsResultActivity.STATUS, staus);
            TicketsResultActivity.start(FilterActivity.this, bundle);

        } else
            Toast.makeText(FilterActivity.this, "Please select any one option", Toast.LENGTH_LONG).show();


    }

    private String getStatus() {
        String SEPARATOR = ",";
        StringBuilder status = new StringBuilder();

        for (int i = 0; i < bambooSettingAdapter.getAll().size(); i++) {
            if (bambooSettingAdapter.getItem(i).objItem instanceof FilterItem) {
                FilterItem filterItem = (FilterItem) bambooSettingAdapter.getItem(i).objItem;
                if (filterItem.getHeaderName().equals("Status") && filterItem.isSelected()) {
                    status.append(filterItem.getId());
                    status.append(SEPARATOR);
                }
            }
        }

        String csv = status.toString();
        //Remove last comma
        if (csv.length() > 0)
            csv = csv.substring(0, csv.length() - SEPARATOR.length());

        return csv;
    }

    private String getPriority() {

        String SEPARATOR = ",";
        StringBuilder priority = new StringBuilder();

        for (int i = 0; i < bambooSettingAdapter.getAll().size(); i++) {
            if (bambooSettingAdapter.getItem(i).objItem instanceof FilterItem) {
                FilterItem filterItem = (FilterItem) bambooSettingAdapter.getItem(i).objItem;
                if (filterItem.getHeaderName().equals("Priority") && filterItem.isSelected()) {
                    priority.append(filterItem.getId());
                    priority.append(SEPARATOR);
                }
            }
        }

        String csv = priority.toString();
        //Remove last comma
        if (csv.length() > 0)
            csv = csv.substring(0, csv.length() - SEPARATOR.length());

        return csv;
    }

    private boolean checkFilterSelected() {

        for (int i = 0; i < bambooSettingAdapter.getAll().size(); i++) {
            if (bambooSettingAdapter.getItem(i).objItem instanceof FilterItem)
                if (((FilterItem) bambooSettingAdapter.getItem(i).objItem).isSelected())
                    return true;

        }
        return false;
    }

    private void initViews() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        type = bundle.getString(TicketsResultActivity.TYPE);

//        if (type.equals("active"))
//            toolbar.setTitle(R.string.title_filter_active_tickets);
//        else
//            toolbar.setTitle(R.string.title_filter_in_active_tickets);

        toolbar.setTitle(R.string.title_filter);

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

        RecyclerView recyclerViewSettings = (RecyclerView) findViewById(R.id.filter_recycler_view);
        recyclerViewSettings.setLayoutManager(new LinearLayoutManager(FilterActivity.this));

        bambooSettingAdapter = new FilterAdapter(FilterActivity.this, this, new ArrayList<Filter>());
        recyclerViewSettings.setAdapter(bambooSettingAdapter);
        bambooSettingAdapter.setOnItemClickListener(this);

        // now get settings here
        bambooSettingAdapter.addAll(getFilter(getFilterItems()));
    }

    public static void start(Activity activity, Bundle bundle) {

        activity.startActivity(new Intent(activity, FilterActivity.class).putExtras(bundle));
    }

    private ArrayList<FilterItem> getFilterItems() {
        ArrayList<FilterItem> filterItems = new ArrayList<>();

        TicketType ticketType = TicketType.active;

        if (type.equals("active"))
            ticketType = TicketType.active;
        else
            ticketType = TicketType.inactive;

        FilterItem filterItem1 = new FilterItem();
        filterItem1.setHeaderName("Priority");
        filterItem1.setName("Critical");
        filterItem1.setId("1");
        filterItem1.setTicketType(ticketType);

        FilterItem filterItem2 = new FilterItem();
        filterItem2.setHeaderName("Priority");
        filterItem2.setName("Major");
        filterItem2.setId("2");
        filterItem2.setTicketType(ticketType);

        FilterItem filterItem3 = new FilterItem();
        filterItem3.setHeaderName("Priority");
        filterItem3.setName("Normal");
        filterItem3.setId("3");
        filterItem3.setTicketType(ticketType);

        FilterItem filterItem4 = new FilterItem();
        filterItem4.setHeaderName("Priority");
        filterItem4.setName("Medium");
        filterItem4.setId("4");
        filterItem4.setTicketType(ticketType);

        FilterItem filterItem5 = new FilterItem();
        filterItem5.setHeaderName("Priority");
        filterItem5.setName("Low");
        filterItem5.setId("5");
        filterItem5.setTicketType(ticketType);

        // now status

        FilterItem filterItem6 = new FilterItem();
        filterItem6.setHeaderName("Status");

        if (ticketType == TicketType.active) {
            filterItem6.setName("New");
            filterItem6.setId("New");
            filterItem6.setTicketType(ticketType);
        } else {
            filterItem6.setName("Closed");
            filterItem6.setId("Closed");
            filterItem6.setTicketType(ticketType);
        }


        FilterItem filterItem7 = new FilterItem();
        filterItem7.setHeaderName("Status");

        if (ticketType == TicketType.active) {
            filterItem7.setName("Inprogress");
            filterItem7.setId("Inprogress");
            filterItem7.setTicketType(ticketType);
        } else {
            filterItem7.setName("Fixed");
            filterItem7.setId("Fixed");
            filterItem7.setTicketType(ticketType);
        }

        // now add all filter item
        filterItems.add(filterItem1);
        filterItems.add(filterItem2);
        filterItems.add(filterItem3);
        filterItems.add(filterItem4);
        filterItems.add(filterItem5);
        filterItems.add(filterItem6);
        filterItems.add(filterItem7);

        return filterItems;
    }

    /**
     * @param filterItemList
     * @return it will return all filter with section and item from event
     */
    private ArrayList<Filter> getFilter(ArrayList<FilterItem> filterItemList) {

        // now convert in to section and items
        ArrayList<Filter> filterItemArrayList = new ArrayList<>();

        //Insert headers into list of items.
        FilterHeader lastHeader = null;
        int sectionManager = -1;
        int headerCount = 0;
        int sectionFirstPosition = 0;

        for (int i = 0; i < filterItemList.size(); i++) {

            FilterHeader header = new FilterHeader();
            header.setEventHeaderTitle(filterItemList.get(i).getHeaderName());

            if (lastHeader == null || !lastHeader.equalsTo(header)) {
                // Insert new header view and update section data.
                sectionFirstPosition = i + headerCount;
                lastHeader = header;
                headerCount += 1;

                filterItemArrayList.add(new Filter(sectionFirstPosition, true, header));
            }
            filterItemArrayList.add(new Filter(sectionFirstPosition, false, filterItemList.get(i)));
        }

        return filterItemArrayList;
    }

    @Override
    public void onItemClick(int position, View v) {

        if (bambooSettingAdapter.getItem(position).objItem instanceof FilterItem)
            ((FilterItem) bambooSettingAdapter.getItem(position).objItem).setSelected(!((FilterItem) bambooSettingAdapter.getItem(position).objItem).isSelected());

    }
}

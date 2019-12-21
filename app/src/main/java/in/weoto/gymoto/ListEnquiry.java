package in.weoto.gymoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.weoto.gymoto.Adapters.ListEnquiryAdapter;
import in.weoto.gymoto.Adapters.MemberAdapter;
import in.weoto.gymoto.GetterSetter.EnquiryList;
import in.weoto.gymoto.GetterSetter.Member;
import in.weoto.gymoto.Helper.GlobalVariables;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

public class ListEnquiry extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token;
    RecyclerView recyclerViewMembers;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<EnquiryList> enquirylist;
    private RecyclerView.Adapter mAdapter;
    ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_enquiry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();

        token = pref.getString("token","");
        recyclerViewMembers = findViewById(R.id.rv_list_enquiry);
        progressBar = findViewById(R.id.progressBarenquiry);
        progressBar.setIndeterminate(true);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefreshenquiry);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        enquirylist = new ArrayList<EnquiryList>();

        recyclerViewMembers.setHasFixedSize(true);
        recyclerViewMembers.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMembers.setLayoutManager(mLayoutManager);

        mAdapter = new ListEnquiryAdapter(enquirylist,getApplicationContext());

        recyclerViewMembers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        recyclerViewMembers.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // shuffle();
                enquirylist.clear();
                mAdapter.notifyDataSetChanged();
                fetchEquiryList();

            }
        });

        fetchEquiryList();




        FabSpeedDial fab = findViewById(R.id.fabListEnquiry);
        fab.addOnStateChangeListener(new FabSpeedDial.OnStateChangeListener() {
            @Override
            public void onStateChange(boolean open) {

            }
        });

        fab.addOnMenuItemClickListener(new FabSpeedDial.OnMenuItemClickListener() {
            @Override
            public void onMenuItemClick(FloatingActionButton fab, TextView textView, int itemId) {

                if(itemId == R.id.one)
                {
                    Toast.makeText(ListEnquiry.this, "CLicked one", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.two)
                {
                    Toast.makeText(ListEnquiry.this, "Clicked two", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.three)
                {
                    Intent EnquiryIntent = new Intent(ListEnquiry.this,AddEnquiry.class);
                    startActivity(EnquiryIntent);
                }
            }
        });
    }

    private void fetchEquiryList()
    {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(1,1);
        asyncHttpClient.addHeader("Authorization",token);


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                /*Toast.makeText(LoginActivity.this, "S1", Toast.LENGTH_SHORT).show();*/

                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                try {
                    String success = response.getString("success");
                    if(success.equals("true"))
                    {

                        SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                        DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);

                        JSONArray membersJSONArray = response.getJSONArray("data");


                        enquirylist.clear();

                        for(int i=0;i< membersJSONArray.length();i++)
                        {
                            String memberName = membersJSONArray.getJSONObject(i).getString("name");
                            String memberID = membersJSONArray.getJSONObject(i).getString("_id");
                            String date = membersJSONArray.getJSONObject(i).getString("date");

                            String phone =  membersJSONArray.getJSONObject(i).getString("phone");

                            EnquiryList enquiryListmodel = new EnquiryList();

                            enquiryListmodel.setName(GlobalVariables.toTitleCase(memberName));
                            enquiryListmodel.setPhone(phone);
                            enquiryListmodel.setId(memberID);

                            myFormat = new SimpleDateFormat("yyyy-MM-dd");
                            Date fDate = null;
                            try {
                                fDate = myFormat.parse(date.split("T")[0]);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            displayFormat = new SimpleDateFormat("E, MMMM d, yyyy", Locale.ENGLISH);
                            String humanReadableDate = displayFormat.format(fDate);

                            enquiryListmodel.setDate(humanReadableDate);

                            enquirylist.add(enquiryListmodel);
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(ListEnquiry.this, "Failed to Fetch data. Please try again.", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                progressBar.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
                enquirylist.clear();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(ListEnquiry.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
            }


        };

        JSONObject jsonParams = new JSONObject();
        try {
            /*jsonParams.put("start_date",  "2010-11-09T18:30:00.000Z");
            jsonParams.put("end_date", "2021-09-19T18:30:00.000Z");*/
            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"get-enquiry", entity, "application/json",
                    responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

}
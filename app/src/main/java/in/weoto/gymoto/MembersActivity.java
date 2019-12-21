package in.weoto.gymoto;

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
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.weoto.gymoto.Adapters.MemberAdapter;
import in.weoto.gymoto.GetterSetter.Member;
import in.weoto.gymoto.Helper.GlobalVariables;

public class MembersActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token;
    RecyclerView recyclerViewMembers;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<Member> membersList;
    private RecyclerView.Adapter mAdapter;
    ProgressBar progressBar;
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();

        token = pref.getString("token","");
        recyclerViewMembers = (RecyclerView) findViewById(R.id.rv_members);
        progressBar =(ProgressBar)findViewById(R.id.progressBar);
        progressBar.setIndeterminate(true);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);



        membersList = new ArrayList<Member>();

        recyclerViewMembers.setHasFixedSize(true);
        recyclerViewMembers.setItemAnimator(new DefaultItemAnimator());

        mLayoutManager = new LinearLayoutManager(this);
        recyclerViewMembers.setLayoutManager(mLayoutManager);

        mAdapter = new MemberAdapter(membersList,getApplicationContext());
        recyclerViewMembers.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        recyclerViewMembers.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // shuffle();
                membersList.clear();
                mAdapter.notifyDataSetChanged();
                fetchMemberData();

            }
        });

        fetchMemberData();


    }


    private void fetchMemberData()
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


                        JSONArray membersJSONArray = response.getJSONArray("data");


                        membersList.clear();

                        for(int i=0;i< membersJSONArray.length();i++)
                        {
                            String memberName = membersJSONArray.getJSONObject(i).getString("name");
                            String memberID = membersJSONArray.getJSONObject(i).getString("_id");

                            String phone =  membersJSONArray.getJSONObject(i).getString("phone");

                            Member member = new Member();
                            member.setName(GlobalVariables.toTitleCase(memberName));
                            member.setPhone(phone);
                            member.setMemID(memberID);



                            JSONArray plansJSONArray = membersJSONArray.getJSONObject(i).getJSONArray("plans");
                            if(plansJSONArray != null)
                            {
                                JSONObject lastPlan = plansJSONArray.getJSONObject(plansJSONArray.length()-1);
                                JSONArray paymentsJSONArray = lastPlan.getJSONArray("payments");
                                JSONObject lastPaymentObject = paymentsJSONArray.getJSONObject(paymentsJSONArray.length() - 1);
                                String remainingAmt = lastPaymentObject.getString("remaining");
                                Log.e("remainingAmt",memberName +" "+remainingAmt);
                                member.setRemainingAmt("Rs. "+remainingAmt);
                            }

                            membersList.add(member);
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(MembersActivity.this, "Failed to Fetch data. Please try again.", Toast.LENGTH_SHORT).show();
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
                membersList.clear();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(MembersActivity.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
            }


        };

        JSONObject jsonParams = new JSONObject();
        try {
            /*jsonParams.put("start_date",  "2010-11-09T18:30:00.000Z");
            jsonParams.put("end_date", "2021-09-19T18:30:00.000Z");*/
            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"get-member", entity, "application/json",
                    responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}

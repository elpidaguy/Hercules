package in.weoto.gymoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import in.weoto.gymoto.Helper.GlobalVariables;
import io.github.kobakei.materialfabspeeddial.FabSpeedDial;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token;
    Double totalRevenueAmt = 0.0;
    TextView textViewRevenueAmt, textViewPendingAmt,textViewNoMem, textViewNoEnq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        textViewRevenueAmt = (TextView)findViewById(R.id.revenueamt);
        textViewPendingAmt = (TextView)findViewById(R.id.pendingamt);
        textViewNoMem = (TextView)findViewById(R.id.nomembers);
        textViewNoEnq = (TextView)findViewById(R.id.noenquiry);
        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();
        token = pref.getString("token","");


        FabSpeedDial fab = findViewById(R.id.mainFab);
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
                    Toast.makeText(MainActivity.this, "CLicked one", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.two)
                {
                    Toast.makeText(MainActivity.this, "Clicked two", Toast.LENGTH_SHORT).show();
                }
                else if (itemId == R.id.three)
                {
                    Intent EnquiryIntent = new Intent(MainActivity.this,AddEnquiry.class);
                    startActivity(EnquiryIntent);
                }
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fetchData();


    }


    private void fetchData() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(1,1);
        asyncHttpClient.addHeader("Authorization",token);


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("data",response.toString());


                try {
                    JSONArray membersArray = response.getJSONObject("data").getJSONArray("members");
                    JSONArray enqArray = response.getJSONObject("data").getJSONArray("enquiry");
                    Double remaining=0.0;
                    for(int i=0;i<membersArray.length();i++)
                    {

                        JSONObject memberObj = membersArray.getJSONObject(i);
                        JSONArray plansArray = memberObj.getJSONArray("plans");

                        for(int j=0;j<plansArray.length();j++)
                        {
                            JSONArray paymentsArray = plansArray.getJSONObject(j).getJSONArray("payments");

                            for(int k=0;k<paymentsArray.length();k++)
                            {

                                JSONObject paymentObj = paymentsArray.getJSONObject(k);

                                String paid = paymentObj.getString("paid");

                                totalRevenueAmt += Double.parseDouble(paid);

                            }



                        }

                        JSONArray lastpayArray = plansArray.getJSONObject(plansArray.length() - 1).getJSONArray("payments");
                        String rem = lastpayArray.getJSONObject(lastpayArray.length()-1).getString("remaining");
                        remaining +=Double.parseDouble(rem);


                    }



                    Toast.makeText(MainActivity.this, " Reve"+totalRevenueAmt+" rema"+remaining, Toast.LENGTH_SHORT).show();

                    textViewRevenueAmt.setText("₹ "+GlobalVariables.toIndianCurr(totalRevenueAmt)  );
                    textViewPendingAmt.setText("₹ "+GlobalVariables.toIndianCurr(remaining));
                    textViewNoMem.setText(""+membersArray.length());
                    textViewNoEnq.setText(""+enqArray.length());


                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(MainActivity.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
            }


        };

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("start_date", "2010-11-09T18:30:00.000Z");
            jsonParams.put("end_date", "2021-09-19T18:30:00.000Z");



            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"gym-dashboard", entity, "application/json",
                    responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_enquiry) {
            Intent ListEnquirytintent = new Intent(MainActivity.this,ListEnquiry.class);
            startActivity(ListEnquirytintent);

        } else if (id == R.id.nav_members) {

            Intent MemberActivityintent = new Intent(MainActivity.this,MembersActivity.class);
            startActivity(MemberActivityintent);

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {


        }
        else if(id == R.id.logout)
        {
            pref = getApplicationContext().getSharedPreferences("gymoto", 0);
            editor = pref.edit();
            editor.clear();
            editor.commit();
            Intent intent = new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

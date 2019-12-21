package in.weoto.gymoto;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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
import java.util.Date;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.weoto.gymoto.Helper.GlobalVariables;
import in.weoto.gymoto.databinding.ActivitySingleEnquiryBinding;

public class SingleEnquiryActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String token;
    String memberID;
    ActivitySingleEnquiryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_enquiry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();

        token = pref.getString("token","");


        memberID = getIntent().getStringExtra("ID");



        fetchMemberData();
    }


    private void fetchMemberData() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(1,1);
        asyncHttpClient.addHeader("Authorization",token);


        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                Log.e("data",response.toString());
                try {
                    JSONObject memberObject = response.getJSONObject("data");
                    String date = memberObject.getString("date");
                    String name = memberObject.getString("name");
                    String gender = memberObject.getString("gender");
                    String email = memberObject.getString("email");
                    String phone = memberObject.getString("phone");
                    String type = memberObject.getString("type");

                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                    DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);

                    //binding.singleenq.tvEnqDate.setText(date);

                    if (gender.equals("Male"))
                    {
                        binding.singleenq.tvEnqName.setText("Mr."+name);
                    }
                    else
                    {
                        binding.singleenq.tvEnqName.setText("Mrs."+name);
                    }

                    binding.singleenq.tvEnqEmail.setText(email);
                    binding.singleenq.tvEnqMobile.setText(phone);
                    binding.singleenq.tvEnqType.setText(type);

                    myFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fDate = myFormat.parse(date.split("T")[0]);

                    displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);
                    String humanReadableDate = displayFormat.format(fDate);

                    binding.singleenq.tvEnqDate.setText("Enquiry Date: "+humanReadableDate );


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(SingleEnquiryActivity.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
            }


        };

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("_id",  memberID);

            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"get-specific-member", entity, "application/json",
                    responseHandler);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

}

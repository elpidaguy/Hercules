package in.weoto.gymoto;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import in.weoto.gymoto.databinding.ActivitySingleMemberBinding;

public class SingleMemberActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private String token;
    String memberID;
    ActivitySingleMemberBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_single_member);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_single_member);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();

        token = pref.getString("token","");


        memberID = getIntent().getStringExtra("memberID");



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
                    String name = memberObject.getString("name");
                    String phone = memberObject.getString("phone");
                    String email = memberObject.getString("email");
                    String address = memberObject.getString("address");

                    JSONArray planArray =  memberObject.getJSONArray("plans");

                    JSONObject currentPlanObject = planArray.getJSONObject(planArray.length() - 1);

                    String final_amt = currentPlanObject.getString("final_amt");
                    String discount = currentPlanObject.getString("discount");
                    String gst = currentPlanObject.getString("gst");
                    String activation_date = currentPlanObject.getString("activation_date");

                    JSONArray paymentArray = currentPlanObject.getJSONArray("payments");
                    String payments = "";

                    SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd");

                    DateFormat displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);


                    for(int i =0;i<paymentArray.length();i++)
                    {
                        String paid = paymentArray.getJSONObject(i).getString("paid");
                        String date = paymentArray.getJSONObject(i).getString("date");
                        String already_paid = paymentArray.getJSONObject(i).getString("already_paid");
                        String remaining = paymentArray.getJSONObject(i).getString("remaining");

                        Date fDate = myFormat.parse(date.split("T")[0]);

                        String humanReadableDate = displayFormat.format(fDate);

                        payments+= "Paid: "+paid;
                        payments+= "\t\tDate:" + humanReadableDate;
                        payments+= "\nRemaining:  "+remaining;
                        payments+= "\t\tPreviously Paid: "+already_paid;

                        payments+= "\n\n\n";


                    }
                    binding.singlemem.textViewPayments.setText(payments);

                    binding.singlemem.textViewUserName.setText(name);
                    binding.singlemem.textViewUserAddress.setText(address);
                    binding.singlemem.textViewUserEmail.setText(email);
                    binding.singlemem.textViewUserPhone.setText(phone);
                    binding.singlemem.textViewUserTotalAmt.setText("Plan Amt: "+final_amt);
                    binding.singlemem.textViewGST.setText("Discount: "+discount);
                    binding.singlemem.textViewDiscount.setText("GST: "+gst);

                     myFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date fDate = myFormat.parse(activation_date.split("T")[0]);

                     displayFormat = new SimpleDateFormat("E MMMM d, yyyy", Locale.ENGLISH);
                    String humanReadableDate = displayFormat.format(fDate);

                    binding.singlemem.textViewActivationDate.setText("Activation Date: "+humanReadableDate  );


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }


            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                Toast.makeText(SingleMemberActivity.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
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

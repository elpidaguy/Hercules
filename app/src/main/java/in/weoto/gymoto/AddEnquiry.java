package in.weoto.gymoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.weoto.gymoto.Helper.GlobalVariables;

public class AddEnquiry extends AppCompatActivity {

    EditText etEnquiryDate,etEnquiryName,etEnquiryMobile,etEnquiryEmail,etEnquiryCom;
    RadioGroup radioGroup;
    RadioButton gender;
    String date,gendertext,name,mobile,email,comment;
    AwesomeValidation mAwesomeValidation;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();
        token = pref.getString("token","");

        //mAwesomeValidation = new AwesomeValidation(BASIC);
        mAwesomeValidation =  new AwesomeValidation(ValidationStyle.BASIC);

        addValidationToViews();

        etEnquiryDate = findViewById(R.id.input_date);
        etEnquiryName = findViewById(R.id.input_name);
        etEnquiryEmail = findViewById(R.id.input_email);
        etEnquiryMobile = findViewById(R.id.input_phone);
        etEnquiryCom = findViewById(R.id.input_comment);

        RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                gender =  findViewById(checkedId);
                gendertext = gender.getText().toString();
                if (gendertext.equals("Mr."))
                {
                    gendertext = "Male";
                }
                else {
                    gendertext = "Female";
                }
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mAwesomeValidation.validate())
                {
                    date = etEnquiryDate.getText().toString();
                    name = etEnquiryName.getText().toString().trim();
                    email = etEnquiryEmail.getText().toString().trim();
                    mobile = etEnquiryMobile.getText().toString().trim();
                    comment = etEnquiryCom.getText().toString().trim();

                    enquireMember();
                }
            }
        });
    }

    private void addValidationToViews()
    {
        mAwesomeValidation.addValidation(this, R.id.input_name, RegexTemplate.NOT_EMPTY, R.string.invalid_name);
        mAwesomeValidation.addValidation(this, R.id.input_date, RegexTemplate.NOT_EMPTY, R.string.Empty);
        mAwesomeValidation.addValidation(this, R.id.input_phone, "^[+]?[0-9]{10,13}$", R.string.invalid_Mobile);
        mAwesomeValidation.addValidation(this, R.id.input_email, android.util.Patterns.EMAIL_ADDRESS, R.string.invalid_Email);
    }


    private void enquireMember() {

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(1,1);
        asyncHttpClient.addHeader("Authorization",token);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                /*Toast.makeText(LoginActivity.this, "S1", Toast.LENGTH_SHORT).show();*/
                try {
                    String success = response.getString("success");
                    if(success.equals("true"))
                    {
                        Intent mainactivityIntent = new Intent(AddEnquiry.this,MainActivity.class);
                        startActivity(mainactivityIntent);
                        Toast.makeText(AddEnquiry.this, "Enquired Successfully!", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //Toast.makeText(AddEnquiry.this, "Invalid Email or Password. Please correct and try again", Toast.LENGTH_SHORT).show();
                        Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection!", Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Toast.makeText(AddEnquiry.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
                Snackbar.make(getWindow().getDecorView().getRootView(), "No Internet Connection!", Snackbar.LENGTH_LONG).show();
            }


        };

        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("date", date);
            jsonParams.put("name", name);
            jsonParams.put("gender", gendertext);
            jsonParams.put("phone", mobile);
            jsonParams.put("email", email);
            jsonParams.put("type", comment);
            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"add-enquiry", entity, "application/json",
                    responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}

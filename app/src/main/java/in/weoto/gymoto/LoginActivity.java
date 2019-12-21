package in.weoto.gymoto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import in.weoto.gymoto.Helper.GlobalVariables;

public class LoginActivity extends AppCompatActivity {

    AppCompatButton btnLogin;
    EditText editTextEmail, editTextPassword;
    String email,pwd;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        btnLogin = findViewById(R.id.btnLogin);
        editTextEmail = (EditText)findViewById(R.id.input_email);
        editTextPassword = (EditText)findViewById(R.id.input_password);
        pref = getApplicationContext().getSharedPreferences("gymoto", 0);
        editor = pref.edit();

        String name = pref.getString("name","");
        if(!name.equals(""))
        {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);

        }



        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString().trim();
                pwd = editTextPassword.getText().toString();


                if(email.equals("") || !isEmailValid(email))
                {
                    Snackbar.make(v, "Invalid Email!", Snackbar.LENGTH_LONG).show();
                }
                else if(pwd.equals("") )
                {
                    Snackbar.make(v, "Incorrect Password!", Snackbar.LENGTH_LONG).show();

                }
                else
                {
                    tryToLogin();

                }





            }
        });
    }

    private void tryToLogin() {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setMaxRetriesAndTimeout(1,1);

        JsonHttpResponseHandler responseHandler = new JsonHttpResponseHandler(){


            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                /*Toast.makeText(LoginActivity.this, "S1", Toast.LENGTH_SHORT).show();*/
                try {
                    String success = response.getString("success");
                    if(success.equals("true"))
                    {
                        String role = response.getString("role");
                        String token = response.getString("token");
                        String name = response.getString("name");
                        String address = response.getString("address");
                        String validity = response.getString("validity");
                        editor.putString("role",role);
                        editor.putString("token",token);
                        editor.putString("name",name);
                        editor.putString("address",address);
                        editor.putString("validity",validity);
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);









                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this, "Invalid Email or Password. Please correct and try again", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }





            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(LoginActivity.this, "Please check connection and try again", Toast.LENGTH_SHORT).show();
            }


        };




        JSONObject jsonParams = new JSONObject();
        try {
            jsonParams.put("email", email);
            jsonParams.put("password", pwd);
            StringEntity entity = new StringEntity(jsonParams.toString());
            asyncHttpClient.post(getApplicationContext(), GlobalVariables.url+"login", entity, "application/json",
                    responseHandler);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }



    }

    public boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

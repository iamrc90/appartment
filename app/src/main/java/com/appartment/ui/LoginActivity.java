package com.appartment.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.appartment.R;
import com.appartment.app.AppConfig;
import com.appartment.app.AppController;
import com.appartment.helpers.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText mLoginId, mLoginPassword;
    private Button mLoginButton;
    private SessionManager session;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // bind UI elements
        mLoginId = (EditText) findViewById(R.id.input_username);
        mLoginPassword = (EditText) findViewById(R.id.input_password);
        mLoginButton = (Button) findViewById(R.id.btn_login);
        // session manager
        session = new SessionManager(getApplicationContext());
        // check if logged in , redirect direct to main page(skip login)
        if(session.isLoggedIn()) {
            // Launching the login activity
            Intent intent = new Intent(LoginActivity.this, ListTickets.class);
            startActivity(intent);
            finish();
        }
        // set listener for login btn
        mLoginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            return;
        }
        createLoginRequest();
    }

    private void createLoginRequest() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        mLoginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        showDialog();

        final String username = mLoginId.getText().toString();
        final String password = mLoginPassword.getText().toString();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int status = jObj.getInt("status");

                    // Check for error node in json
                    if (status == 200) {
                        int userId = jObj.getInt("user_id");
                        // successful
                        onLoginSuccess(userId);
                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("data_message");
                        onLoginFailed(errorMsg);
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                onLoginFailed("something went wrong. please try again");
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hideDialog() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        // disable going back
        moveTaskToBack(true);
    }

    public void onLoginSuccess(int userId) {
        mLoginButton.setEnabled(true);
        session.setLogin(true,userId);
        Intent intent = new Intent(LoginActivity.this, ListTickets.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(String message) {
        Toast.makeText(getBaseContext(), message , Toast.LENGTH_LONG).show();
        mLoginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String username = mLoginId.getText().toString();
        String password = mLoginPassword.getText().toString();

        if (username.isEmpty()) {
            mLoginId.setError("Please enter username");
            valid = false;
        } else {
            mLoginId.setError(null);
        }

        if (password.isEmpty()) {
            mLoginPassword.setError("Please enter password");
            valid = false;
        } else {
            mLoginPassword.setError(null);
        }
        return valid;
    }

}

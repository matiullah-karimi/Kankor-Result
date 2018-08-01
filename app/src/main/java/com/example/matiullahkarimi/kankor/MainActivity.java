package com.example.matiullahkarimi.kankor;

import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private Button searchButton;
    private EditText idEditText;
    private TextView notFoundTextView, nameView, fNameView, gfNameView, schoolView, scoreView, universityView;
    private ProgressBar progressBar;
    private AlertDialog.Builder builder;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.btnSearch);
        idEditText = findViewById(R.id.editTextId);
        notFoundTextView = findViewById(R.id.notFoundResult);
        nameView = findViewById(R.id.nameView);
        fNameView = findViewById(R.id.fNameView);
        gfNameView = findViewById(R.id.gfNameView);
        schoolView = findViewById(R.id.schoolView);
        scoreView = findViewById(R.id.scoreView);
        universityView = findViewById(R.id.university);

        builder = new AlertDialog.Builder(MainActivity.this);
        builder.setView(R.layout.loader);
        dialog = builder.create();

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkConnected()) {
                    getResult(idEditText.getText().toString());
                }
                else {
                    Toast.makeText(MainActivity.this,"موبایل دیتای تان خاموش است", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getResult(String id) {
        String url = "http://kankor.edu.af/results/" + id;
        RequestQueue queue = Volley.newRequestQueue(this);
        setDialog(true);


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        notFoundTextView.setText(response);
                        clearValues();
                        if (response.contains("{")) {
                            try {
                                notFoundTextView.setText("");
                                JSONObject obj = new JSONObject(response);
                                setValues(obj);
                            }
                            catch (Exception ex) {

                            }
                        }
                        setDialog(false);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        setDialog(false);
                        Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_LONG).show();
                        Log.d("============", error.toString());
                    }
                });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
        30000,
        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(stringRequest);
    }

    private void getResultAsync(String id) {
        setDialog(true);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get("http://kankor.edu.af/results/" + id, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                Log.d("1------", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("2------", response.toString());
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("3------", response.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                notFoundTextView.setText(responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("4------", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.d("5------", errorResponse.toString());
            }
        });
    }

    private void setDialog(boolean show){
        if (show)dialog.show();
        else dialog.dismiss();
    }

    private void setValues(JSONObject result) {
        try {
            nameView.setText("اسم: " + result.getString("name"));
            fNameView.setText("ولد/بنت: " + result.getString("father_name"));
            gfNameView.setText("ولدیت: " + result.getString("grand_father_name"));
            schoolView.setText("مکتب: " + result.getString("school"));
            scoreView.setText("نمبر: " + result.getString("score"));
            universityView.setText("نتیجه: " + result.getString("result"));
        }
        catch (Exception ex) {

        }

    }

    private void clearValues() {
        nameView.setText("");
        fNameView.setText("");
        gfNameView.setText("");
        schoolView.setText("");
        scoreView.setText("");
        universityView.setText("");
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}

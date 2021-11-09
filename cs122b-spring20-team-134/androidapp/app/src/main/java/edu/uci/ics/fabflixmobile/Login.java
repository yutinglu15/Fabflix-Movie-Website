package edu.uci.ics.fabflixmobile;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends ActionBarActivity {

    private EditText username;
    private EditText password;
    private TextView message;
    private Button loginButton;
    private String url;

    private String status;
    private String status_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // upon creation, inflate and initialize the layout
        Log.d("start ",">>>>");
        setContentView(R.layout.login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        message = findViewById(R.id.message);
        loginButton = findViewById(R.id.login);
        /**
         * In Android, localhost is the address of the device or the emulator.
         * To connect to your machine, you need to use the below IP address
         * **/
//        url = "https://3.132.212.197:8443/cs122b_spring20_project1_star_example_war/api/";
//        url = "http://10.0.2.2:8080/cs122b_spring20_project1_star_example_war/api/";
        url = "https://3.16.213.213:8443/cs122b-spring20-project1-star-example/api/";
        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
    }

    public void login() {

        message.setText("Trying to login");
        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//        Log.d("cur params ", params);
        //request type is POST
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, url + "login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("login status", response);

                JSONObject obj = null;
                try {
                    obj = new JSONObject(response);
                    status = obj.getString("status");
                    status_message = obj.getString("message");
                    System.out.println("status : " + status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(status.equals("success")){
//                    //initialize the activity(page)/destination
//                    Intent listPage = new Intent(Login.this, ListViewActivity.class);
//                    //without starting the activity/page, nothing would happen
//                    startActivity(listPage);
                    Intent mainPage = new Intent(Login.this, SearchPage.class);
                    startActivity(mainPage);
                }
                else{
                    message.setText(status_message);
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("login.error", error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // Post request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(loginRequest);

    }
}
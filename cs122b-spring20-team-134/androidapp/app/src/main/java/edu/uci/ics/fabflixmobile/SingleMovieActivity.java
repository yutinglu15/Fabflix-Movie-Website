package edu.uci.ics.fabflixmobile;

//import android.support.v7.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SingleMovieActivity extends Activity {
    private String url;
    private Movie m;
    private TextView content;
//    private String mid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        content = findViewById(R.id.movieContent);
        m = new Movie();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_movie);
        Intent i = getIntent();
        String mid = i.getStringExtra("movieId");
        getResponse(mid);
        String text = formText(m);
        Log.d("GENERATE TEXT: ",text);
        content.setText(text);

    }

    public void getResponse(String mid) {
        Log.d("CALLING", "get response");
//        url = "https://3.132.212.197:8443/cs122b-spring20-project1-star-example/";
//        url = "http://10.0.2.2:8080/cs122b_spring20_project1_star_example_war/";
        url = "https://3.16.213.213:8443/cs122b-spring20-project1-star-example/";

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//        Log.d("cur params ", params);
        String search_query = url + "single-movie?id="+mid;
        Log.d("URL", search_query);
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, search_query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("get response success ", "success");
//                Log.d("login status", response);

                try {
                    Movie newMovie = parseResponse(response);
                    refreshMovie(newMovie);
//                    content.setText(text);
                    String text = formText(newMovie);
                    setText(text);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(m.getTitle()==""){
                    Log.d("no movies found", "error");
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
                params.put("id", mid);
//                params.put("q", query);
//                params.put("password", password.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }

    private Movie parseResponse(String response) throws JSONException {
        JSONArray ms = new JSONArray(response);
        JSONObject m = ms.getJSONObject(0);

        String id = m.getString("id");
        String title = m.getString("title");
        int year = m.getInt("year");
        String director = m.getString("director");

        Movie movie = new Movie(id, title, year, director, 0);

        JSONArray sts = (JSONArray) m.get("stars");
        int stsLen = sts.length();
        ArrayList<String> stars = new ArrayList<String>();
        for(int j = 0; j < stsLen; j++){
            JSONObject curstar = sts.getJSONObject(j);
            stars.add(curstar.getString("name"));
        }

        JSONArray gs = (JSONArray) m.get("genres");
        int gsLen = gs.length();
        ArrayList<String> genres = new ArrayList<String>();
        for(int k = 0; k < gsLen; k++){
            genres.add(gs.getString(k));
        }

        movie.setStars(stars);
        movie.setGenres(genres);

        return movie;
    }

    private String formText(Movie m){
        String content = "";
        content += "Title: "+ m.getTitle() + "\n\n";
        content +=  m.getYear() + "     " + m.getDirector() + "\n\n";

        content += "genres: ";
        for(String g:m.getGenres()){
            content += g + ",";
        }

        content += "\n\nstars: ";
        for (String s: m.getStars()){
            content += s +",";
        }
        return content;
    }

    public void refreshMovie(Movie oldMovie){
        this.m = oldMovie;
    }

    public void setText(String text){
        TextView newContent = findViewById(R.id.movieContent);
        newContent.setText(text);
    }
}

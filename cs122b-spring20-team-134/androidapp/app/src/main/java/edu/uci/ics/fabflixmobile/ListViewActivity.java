package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ListViewActivity extends Activity {
    private ArrayList<Movie> movies;
    private String url;
    private String query;
    private int pageNumber = 0;
    public MovieListViewAdapter adapter;

    private Button nextPage;
    private Button prePage;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        nextPage = findViewById(R.id.nextBtn);
        prePage = findViewById(R.id.prevBtn);
        // get intent query
        Intent i = getIntent();
        String raw_query = i.getStringExtra("query");

        String[] tokens = raw_query.trim().split(" ");
        query = String.join("+", tokens);

        Log.d("QUERY", query);

        getResponse();
        //this should be retrieved from the database and the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));

        movies = new ArrayList<Movie>();
        adapter = new MovieListViewAdapter(movies, this);

        ListView listView = findViewById(R.id.list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Movie movie = movies.get(position);
//                String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getTitle(), movie.getYear());
//                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                Intent toSingle = new Intent(ListViewActivity.this, SingleMovieActivity.class);
                toSingle.putExtra("movieId", movie.getId());
                startActivity(toSingle);
            }
        });

        nextPage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getNextResponse(1);
                //this should be retrieved from the database and the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));
                movies = new ArrayList<Movie>();
                adapter = new MovieListViewAdapter(movies,ListViewActivity.this);
//
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
            }
        });
        prePage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                getNextResponse(0);
                //this should be retrieved from the database and the backend server
//        final ArrayList<Movie> movies = new ArrayList<>();
//        movies.add(new Movie("The Terminal", (short) 2004));
//        movies.add(new Movie("The Final Season", (short) 2007));
                movies = new ArrayList<Movie>();
                adapter = new MovieListViewAdapter(movies,ListViewActivity.this);
//
                ListView listView = findViewById(R.id.list);
                listView.setAdapter(adapter);
            }
        });

    }

    public void getNextResponse(int pageControl) {
        Log.d("CALLING", "get response");
        //todo use aws
        url = "https://3.16.213.213:8443/cs122b-spring20-project1-star-example/";
//        url = "http://10.0.2.2:8080/cs122b_spring20_project1_star_example_war/";

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//        Log.d("cur params ", params);
        //request type is POST
        String search_query = "";
        if(pageControl == 1){
            pageNumber = pageNumber+1;
            search_query = url + "aglr-movie-list?fullSearch=True&page="+pageNumber+"&limit=20&q="+query;
        }
        else if(pageControl == 0){
            pageNumber = pageNumber-1;
            search_query = url + "aglr-movie-list?fullSearch=True&page="+pageNumber+"&limit=20&q="+query;
        }

        Log.d("URL", search_query);
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, search_query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("get response", "success");
//                Log.d("login status", response);

                try {
                    ArrayList<Movie> updateMovies = parseResponse(response);
                    refreshMovie(updateMovies);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(movies.size()==0){
                    Log.d("no movies found", "error");
//                    //initialize the activity(page)/destination
//                    Intent listPage = new Intent(Login.this, ListViewActivity.class);
//                    //without starting the activity/page, nothing would happen
//                    startActivity(listPage);
//                    Intent mainPage = new Intent(ListViewActivity.this, SearchPage.class);
//                    startActivity(mainPage);
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
                params.put("fullSearch", "True");
                params.put("q", query);
//                params.put("password", password.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }

    public void getResponse() {
        Log.d("CALLING", "get response");
//        url = "https://3.132.212.197:8443/cs122b-spring20-project1-star-example/";
//        url = "http://10.0.2.2:8080/cs122b_spring20_project1_star_example_war/";
        // Use the same network queue across our application
        url = "https://3.16.213.213:8443/cs122b-spring20-project1-star-example/";
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
//        Log.d("cur params ", params);
        //request type is POST
        String search_query = url + "aglr-movie-list?fullSearch=True&q="+query;
        Log.d("URL", search_query);
        final StringRequest searchRequest = new StringRequest(Request.Method.GET, search_query, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //TODO should parse the json response to redirect to appropriate functions.
                Log.d("get response", "success");
//                Log.d("login status", response);

                try {
                    ArrayList<Movie> updateMovies = parseResponse(response);
                    refreshMovie(updateMovies);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(movies.size()==0){
                    Log.d("no movies found", "error");
//                    //initialize the activity(page)/destination
//                    Intent listPage = new Intent(Login.this, ListViewActivity.class);
//                    //without starting the activity/page, nothing would happen
//                    startActivity(listPage);
//                    Intent mainPage = new Intent(ListViewActivity.this, SearchPage.class);
//                    startActivity(mainPage);
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
                params.put("fullSearch", "True");
                params.put("q", query);
//                params.put("password", password.getText().toString());

                return params;
            }
        };

        // !important: queue.add is where the login request is actually sent
        queue.add(searchRequest);

    }

    public ArrayList<Movie> parseResponse(String response) throws JSONException {
        ArrayList<Movie> movies = new ArrayList<Movie>();
        JSONArray ms = new JSONArray(response);
        int msLen = ms.length();
        for(int i = 0; i < msLen; i++){

            JSONObject m = ms.getJSONObject(i);

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

            movies.add(movie);

        }
        return movies;
    }

    public void refreshMovie(ArrayList<Movie> old_movies){
        for (Movie m:old_movies) {
            this.movies.add(m);
        }    }
}
package edu.uci.ics.fabflixmobile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.android.volley.RequestQueue;

import java.util.ArrayList;

public class SearchPage extends Activity{

    private EditText search_key;
    private Button searchButton;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainpage);

        search_key = findViewById(R.id.search_keyword);
        searchButton = findViewById(R.id.search_button);
//        url = "https://3.132.212.197:8443/cs122b_spring20_project1_star_example_war/api/";
//        url = "http://10.0.2.2:8080/cs122b_spring20_project1_star_example_war/api/";
        url = "https://3.16.213.213:8443/cs122b-spring20-project1-star-example/";

        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){search();}
        });

        search_key.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_ENTER){
                    search();
                    return true;
                }
                return false;
            }
        });

    }

    public void search(){
//        final RequestQueue queue = NetworkManager.sharedManager(this).queue;
        Intent listPage = new Intent(SearchPage.this, ListViewActivity.class);
        listPage.putExtra("query", search_key.getText().toString());
        startActivity(listPage);

    }

}

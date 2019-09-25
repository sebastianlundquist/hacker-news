package com.sebastianlundquist.hackernews;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> titles = new ArrayList<>();
    ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DownloadTask task = new DownloadTask();
        try {
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        ListView listView = findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        listView.setAdapter(arrayAdapter);
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;

            try {
                url = new URL(strings[0]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                String inputLine;
                while ((inputLine = bufferedReader.readLine()) != null) {
                    result += inputLine;
                }

                JSONArray jsonArray = new JSONArray(result);
                int numberOfArticles = 10;
                if (jsonArray.length() < numberOfArticles) {
                    numberOfArticles = jsonArray.length();
                }
                for (int i = 0; i < numberOfArticles; i++) {
                    String articleId = jsonArray.getString(i);
                    url = new URL("https://hacker-news.firebaseio.com/v0/item/" + articleId + ".json");

                    bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                    String articleInfo = "";
                    while ((inputLine = bufferedReader.readLine()) != null) {
                        articleInfo += inputLine;
                    }

                    JSONObject jsonObject = new JSONObject(articleInfo);
                    if (!jsonObject.isNull("title") && !jsonObject.isNull("url")) {
                        String articleTitle = jsonObject.getString("title");
                        String articleUrl = jsonObject.getString("url");
                        url = new URL(articleUrl);
                        bufferedReader = new BufferedReader(new InputStreamReader(url.openStream()));
                        while ((inputLine = bufferedReader.readLine()) != null) {
                            System.out.println(inputLine);
                        }
                    }
                }
                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

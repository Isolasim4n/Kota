package com.example.pras.city;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    // URL to get contacts JSON
    private static String url = "https://innodev.vnetcloud.com/ngc/datacity.json";
    ArrayList<HashMap<String, String>> cityList;
    private String TAG = MainActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityList = new ArrayList<>();

        lv = findViewById(R.id.ListView);

        new GetCity().execute();
    }

    private class GetCity extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray city = jsonObj.getJSONArray("allcity");

                    // looping through All City
                    for (int i = 0; i < city.length(); i++) {
                        JSONObject c = city.getJSONObject(i);

                        String id = c.getString("cityId");
                        String name = c.getString("cityName");
                        String desc = c.getString("cityDescription");
                        String img = c.getString("cityImage");

                        // tmp hash map for single contact
                        HashMap<String, String> cities = new HashMap<>();

                        // adding each child node to HashMap key => value
                        cities.put("cityId", id);
                        cities.put("cityName", name);
                        cities.put("cityDescription", desc);
                        cities.put("cityImage", img);

                        // adding contact to contact list
                        cityList.add(cities);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });

                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, cityList,
                    R.layout.list_item, new String[]{"cityName", "cityDescription",
            }, new int[]{R.id.city,
                    R.id.desc,});

            lv.setAdapter(adapter);
        }
    }
}

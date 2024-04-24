package com.cam.exam;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private UserAdapter adapter;
    private List<UserData> userList;

    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private static final int PAGE_SIZE = 10;
    private boolean isRefreshing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.recycler_view);
        userList = new ArrayList<>();
        adapter = new UserAdapter(userList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fetchUsers_firstLoad();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh data
                Log.e("refresh", "on");
                refreshData();
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                Log.e("visibleItemCount", String.valueOf(visibleItemCount));
                Log.e("totalItemCount", String.valueOf(totalItemCount));
                Log.e("firstVisibleItemPosition", String.valueOf(firstVisibleItemPosition));
                Log.e("isLoading", String.valueOf(isLoading));
                Log.e("isLastPage", String.valueOf(isLastPage));
                // Check if not loading and not on last page
                if (!isLoading && !isLastPage) {
                     if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= PAGE_SIZE) {
                        // Load more items
                        loadMoreItems(visibleItemCount, totalItemCount, firstVisibleItemPosition);
                    }
                }
            }
        });



    }
    private void refreshData() {
        // Fetch fresh data from the API
        fetchUsers_firstLoad();
        handleDataRetrieval(userList);
    }

    private void handleDataRetrieval(List<UserData> users) {
        userList.clear();
        userList.addAll(users);
        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }
    @SuppressLint("StaticFieldLeak")
    private void fetchUsers_firstLoad() {

        new AsyncTask<Void, Void, List<UserData>>() {
            @Override
            protected List<UserData> doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://randomuser.me/api/?results=10");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String jsonString = stringBuilder.toString();

                    List<UserData> users = parseUserResponse(jsonString);
                    return users;
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<UserData> users) {
                if (users != null) {
                    userList.addAll(users);
                    adapter.notifyDataSetChanged();
                    storeUserDataInSharedPreferences(users);
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void fetchUsers(int page_size, int firstVisibleItemPosition) {
        Log.e("fetchUsers", "open");
        Log.e("page_size", String.valueOf(page_size));
        Log.e("currentPage", String.valueOf(firstVisibleItemPosition));
        String url_ = "https://randomuser.me/api/?page=" + firstVisibleItemPosition+"&results=10";
        new AsyncTask<Void, Void, List<UserData>>() {
            @Override
            protected List<UserData> doInBackground(Void... voids) {
                try {
                    URL url = new URL(url_);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        stringBuilder.append(line);
                    }
                    String jsonString = stringBuilder.toString();

                    List<UserData> users = parseUserResponse(jsonString);

                    return users;
                } catch (IOException| JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<UserData> users) {
                if (users != null) {
                    userList.addAll(users);
                    adapter.notifyDataSetChanged();
                    storeUserDataInSharedPreferences(users);
                    isLoading = false;
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch users", Toast.LENGTH_SHORT).show();
                    if (isRefreshing) {
                        swipeRefreshLayout.setRefreshing(false);
                        isRefreshing = false; // Reset the flag
                    }
                }
            }

        }.execute();
    }

    private List<UserData> parseUserResponse(String response) throws JSONException{
        List<UserData> users = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject userObject = resultsArray.getJSONObject(i);
                Log.e("UserObject",userObject.toString());
                String firstName = userObject.getJSONObject("name").getString("first");
                String lastName = userObject.getJSONObject("name").getString("last");
                String fullName = firstName + " " + lastName;
                String imageUrl = userObject.getJSONObject("picture").getString("medium");
                String birthdate = userObject.getJSONObject("dob").getString("date");
                String age = userObject.getJSONObject("dob").getString("age");
                String email = userObject.getString("email");
                String mobile = userObject.getString("cell");
                JSONObject address = userObject.getJSONObject("location");
                String completeAddress = getCompleteAddress(address);
                String contactMobile = userObject.getString("phone");

                users.add(new UserData(firstName, lastName, imageUrl, birthdate, age, email, mobile, completeAddress, fullName, contactMobile));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return users;
    }

    private static String getCompleteAddress(JSONObject addressObject) throws JSONException {
        int streetNumber = addressObject.getJSONObject("street").getInt("number");
        String streetName = addressObject.getJSONObject("street").getString("name");
        String city = addressObject.getString("city");
        String state = addressObject.getString("state");
        String country = addressObject.getString("country");
        String postcode = addressObject.getString("postcode");

        return streetNumber + " " + streetName + ", " + city + ", " + state + ", " + country + ", " + postcode;
    }

    private void storeUserDataInSharedPreferences(List<UserData> users) {
        SharedPreferences sharedPreferences = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String usersJson = gson.toJson(users);
        editor.putString("users", usersJson);
        editor.apply();
    }

    private void loadMoreItems(int visibleItemCount, int totalItemCount, int firstVisibleItemPosition) {
        Log.e("visibleItemCount", String.valueOf(visibleItemCount));
        Log.e("totalItemCount", String.valueOf(totalItemCount));
        Log.e("firstVisibleItemPosition", String.valueOf(firstVisibleItemPosition));
        Log.e("isLoading", String.valueOf(isLoading));
        Log.e("isLastPage", String.valueOf(isLastPage));
        Log.e("PAGE_SIZE", String.valueOf(PAGE_SIZE));

        Log.e("loadMoreItems", "Yes");
        isLoading = true;
        currentPage++;
        Log.e("currentPage", String.valueOf(currentPage++));
        fetchUsers(++visibleItemCount, ++firstVisibleItemPosition);
    }

}
package com.amakenapp.website.amakenapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import com.amakenapp.website.amakenapp.R;
import com.amakenapp.website.amakenapp.helper.BusinessProfilePlaceOrEventAdapter;
import com.amakenapp.website.amakenapp.helper.BusinessProfilePlaceOrEventListItem;
import com.amakenapp.website.amakenapp.helper.Constants;
import com.amakenapp.website.amakenapp.helper.HomeAdapter;
import com.amakenapp.website.amakenapp.helper.HomeListItem;
import com.amakenapp.website.amakenapp.helper.MySingleton;
import com.amakenapp.website.amakenapp.helper.SharedPrefManager;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BusinessProfilePlaces extends AppCompatActivity{

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<BusinessProfilePlaceOrEventListItem> listItems;
    private LinearLayout loading, no_places;
    private TextView addPlace;

    SharedPrefManager sharedPrefManager;
    private static int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_profile_places);
        Toolbar toolbar = (Toolbar) findViewById(R.id.businessProfilePlaces_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPrefManager = SharedPrefManager.getInstance(getApplicationContext());
        userId = sharedPrefManager.getUserId();

        loading = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        loading.setVisibility(View.VISIBLE);

        no_places = (LinearLayout) findViewById(R.id.no_places);
        addPlace =(TextView) findViewById(R.id.add_place);
        addPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BusinessProfilePlaces.this, AddPlace.class));
            }
        });


        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

            /* every item of  recycler view has a fixed size*/
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
            /* we put data we want to store inside the list item*/
        listItems = new ArrayList<>();

        

        getAllPlaces(userId);



    }


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    //// TODO: 3/9/2017  get previous fragment (business profile activity) activity not NavDrw
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            //onBackPressed();
            return true;
        }
        return true;
    }


    public void getAllPlaces(int userId) {
        final int userID = userId;

        StringRequest send = new StringRequest(Request.Method.GET,
                Constants.URL_GET_USER_PLACES + userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                JSONArray arr = obj.getJSONArray("places");
                                for (int i = 0; i < arr.length(); i++) {
                                    JSONObject placeDetails = arr.getJSONObject(i);
                                    BusinessProfilePlaceOrEventListItem listItem = new BusinessProfilePlaceOrEventListItem();

                                    listItem.setPlaceOrEventId(placeDetails.getInt("place_id"));
                                    listItem.setType(placeDetails.getString("type"));
                                    listItem.setPlaceOrEventName(placeDetails.getString("place_name"));
                                    listItem.setPlaceOrEventCategory(placeDetails.getString("place_category"));
                                    listItem.setPlaceOrEventPic(placeDetails.getString("place_photo"));


                                    int bookmarksNumber = placeDetails.getInt("place_bookmarks_number");
                                    String bookmarksNumber2 = Integer.toString(bookmarksNumber);
                                    listItem.setStatBookmark(bookmarksNumber2);

                                    int likesNumber = placeDetails.getInt("place_likes_number");
                                    String likesNumber2 = Integer.toString(likesNumber);
                                    listItem.setStatLikes(likesNumber2);


                                    Double rate = placeDetails.getDouble("place_rating");
                                    String rate2 = Double.toString(rate);
                                    Float rate3 = Float.parseFloat(rate2);
                                    listItem.setPlaceOrEventRatingbar(rate3);

                                    int reviewsnumber = placeDetails.getInt("place_reviews_number");
                                    String reviewsnumbe2 = Integer.toString(reviewsnumber);
                                    listItem.setStatRatings(reviewsnumbe2);

                                    listItems.add(listItem);
                                }
                                adapter = new BusinessProfilePlaceOrEventAdapter(listItems, getApplicationContext());
                                recyclerView.setAdapter(adapter);
                                loading.setVisibility(View.GONE);
                            } else {
                                loading.setVisibility(View.GONE);
                                no_places.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) ;
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(send);
    }




}


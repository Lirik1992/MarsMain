package com.diploma.likharev.android.mymap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class DirectionsScreen extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {


    private static final String LOG_TAG = "Autocomplete Activity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayAdapter;
    private PlaceArrayAdapterSecond mPlaceArrayAdapterSecond;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(56.220773, 43.380105), new LatLng(56.329727, 44.152181));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directions_screen);

        mGoogleApiClient = new GoogleApiClient.Builder(DirectionsScreen.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        final AutoCompleteTextView autocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        final AutoCompleteTextView autoCompleteTextViewSecond = (AutoCompleteTextView) findViewById(
                R.id.autoCompleteTextViewSecond);

        autocompleteTextView.setThreshold(3);
        autoCompleteTextViewSecond.setThreshold(3);

        autocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceArrayAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        autocompleteTextView.setAdapter(mPlaceArrayAdapter);

        autoCompleteTextViewSecond.setOnItemClickListener(mAutocompleteClickListenerSecond);
        mPlaceArrayAdapterSecond = new PlaceArrayAdapterSecond(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        autoCompleteTextViewSecond.setAdapter(mPlaceArrayAdapterSecond);


        ImageButton btnChange = (ImageButton) findViewById(R.id.change_places);

        btnChange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!autocompleteTextView.toString().isEmpty()
                        && !autoCompleteTextViewSecond.toString().isEmpty()) {
                    Editable middle;
                    Editable one = autocompleteTextView.getText();
                    Editable two = autoCompleteTextViewSecond.getText();
                    middle = one;
                    one = two;
                    two = middle;
                    autocompleteTextView.setText(one);
                    autoCompleteTextViewSecond.setText(two);
                }
            }
        });

        ImageButton btnRoute = (ImageButton) findViewById(R.id.build_route);

        btnRoute.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!autocompleteTextView.toString().isEmpty()
                        && !autoCompleteTextViewSecond.toString().isEmpty()) {
                    Intent intent2 = new Intent(DirectionsScreen.this, MainScreen.class);
                    intent2.putExtra("origin", autocompleteTextView.toString());
                    intent2.putExtra("destination", autoCompleteTextViewSecond.toString());
                    startActivity(intent2);
                }
            }
        });
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item != null ? item.placeId : null);
            Log.i(LOG_TAG, "Selected: " + (item != null ? item.description : null));
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + (item != null ? item.placeId : null));
        }
    };

    private AdapterView.OnItemClickListener mAutocompleteClickListenerSecond
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceArrayAdapterSecond.PlaceAutocomplete item = mPlaceArrayAdapterSecond.getItem(position);
            final String placeId = String.valueOf(item != null ? item.placeId : null);
            Log.i(LOG_TAG, "Selected: " + (item != null ? item.description : null));
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + (item != null ? item.placeId : null));
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

        mPlaceArrayAdapterSecond.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");

        mPlaceArrayAdapterSecond.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }
}

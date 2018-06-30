package com.hosea.ochieng.kscps;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;

    //Car parks arrayLists
    public static ArrayList<CarPark> carParks;

    //widgets
    private EditText searchText;


    //map activity variables
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap map;
    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        carParks = new ArrayList<>();
        loadCarParks();

        searchText = (EditText) findViewById(R.id.input_search);

        getLocationPermission();
    }

    private void init(){
        /**
         * Inittializing the app
         */

        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        keyEvent.getAction() == KeyEvent.ACTION_DOWN ||
                        keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute Geolocate method
                    geolocateCarpark();
                }
                return false;
            }
        });
    }

    public void loadCarParks(){
        carParks.add(new CarPark("Mawelle Car Park", -1.322512, 36.800659,"Secure and safer"));
        carParks.add(new CarPark("Orkille Car Park", -1.322748, 36.801002,"Secure and safer"));
        carParks.add(new CarPark("Trevors Car Park", -1.323349, 36.799371,"Secure and safer"));
        carParks.add(new CarPark("2Park parking", -1.32159, 36.798191,"Secure and safer"));
        carParks.add(new CarPark("Logal park", -1.323477, 36.797333,"Secure and safer"));
        carParks.add(new CarPark("Herbit Close", -1.322598, 36.802804,"Secure and safer"));
    }

    public void geolocateCarpark(){
        /**
         * Geolocates car parks available;
         */
        String searchString = searchText.getText().toString();
        for(CarPark c : carParks){
            if(c.getName().equals(searchString)){
                //make a marker of the car park and add to map
                moveCamera(new LatLng(c.getX(), c.getY()), DEFAULT_ZOOM, c.getName());
                return;
            }
        }
        /*Geocoder geocoder = new Geocoder(MapActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(list.size() > 0){
            Address address = list.get(0);
            showToastMessage(address.toString());
        }*/

    }


    private void addCarParkToMap(LatLng latLng, String name){
        Marker marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(name));
        /*MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(name);
        map.addMarker(options);*/
        marker.showInfoWindow();
    }



    private void getDeviceLocation() {
        /**
         * Gets the device location
         */
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        try {
            if (mLocationPermissionsGranted) {
                Task location = fusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Found Location");
                            Location curentLocation = (Location) task.getResult();
                            //TOTO check here, app breaks
                            moveCamera(new LatLng(curentLocation.getLatitude(), curentLocation.getLongitude()), DEFAULT_ZOOM, "My Location");
                            map.setPadding(0,200,0,0);

                            for(CarPark cp : carParks){
                                addCarParkToMap(new LatLng(cp.getX(), cp.getY()), cp.getName());
                            }

                            /*Calculate distace from current position to 1st CarPark*/
                            float[] results = new float[1];

                            Location.distanceBetween(curentLocation.getLatitude(), curentLocation.getLongitude(),
                                    carParks.get(0).getX(), carParks.get(0).getY(),results);

                            float dist = results[0];
                            showToastMessage(String.valueOf(dist) + "M from here to" + carParks.get(0).getName());

                        } else {
                            showToastMessage("Couldn't get device location!");
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void moveCamera(LatLng latLng, float zoom, String title) {
        /**
         * Moves the camera to specified latitude longitude
         */
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        map.addMarker(options);
    }

    public void showToastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapActivity.this);
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermissions: getting permissions");
        String[] permissions = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions, LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissions: called");
        mLocationPermissionsGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d(TAG, "onRequestPermissionsResult: permission success");
                    //initialize map
                    initMap();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        Toast.makeText(this, "Car parks maps ready!", Toast.LENGTH_SHORT).show();
        map = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
            map.getUiSettings().setZoomControlsEnabled(true);
            init();
        }
    }
}

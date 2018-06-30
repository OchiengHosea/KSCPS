package com.hosea.ochieng.kscps;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class HomeFragment extends Fragment {
    public static final String TAG ="Home Fragment Activity";
    RelativeLayout topRatedRelativeLayout;
    RelativeLayout nearbyLocationsRelativeLayout;
    LocationManager lm;
    ConnectivityManager connectivityManager;
    NetworkInfo networkInfo;

    private static final int ERROR_DIALOG_REQUEST = 9001;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nearbyLocationsRelativeLayout = (RelativeLayout)view.findViewById(R.id.nearest_carpark_locations);
        lm = (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        networkInfo = connectivityManager.getActiveNetworkInfo();

        if(isServiceOk()){
            init();
        }

        return view;
    }

    View.OnClickListener showMapCardClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean gps_is_ebnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if(gps_is_ebnabled && networkEnabled()){
                Intent intent = new Intent(getActivity(),MapActivity.class);
                startActivity(intent);
            }else if(!networkEnabled()){
                showToastMessage("Turn on Data Connection or connect to wifi");
            }else if(!gps_is_ebnabled){
                requestGPSTObeEnabled();
            }
        }
    };

    private void init(){
        //add map listeners to the fragment
        nearbyLocationsRelativeLayout.setOnClickListener(showMapCardClickListener);
    }

    public boolean isServiceOk(){
        Log.d(TAG,"isServiceOk: checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());
        if(available == ConnectionResult.SUCCESS){
            //App is ready to make map requests
            Log.d(TAG, "isServiceOk, Google play service is working");
            return true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //trying to fix missing google play services
            Log.d(TAG, "isServiceOk, trying to resolve the error");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available,
            ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getContext(), "You cannot make map requests",Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean networkEnabled(){
        return networkInfo != null && networkInfo.isConnected();
    }

    private void requestGPSTObeEnabled(){
            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
            dialog.setMessage(R.string.enable_gps_network_message);
            dialog.setPositiveButton("TURN ON", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent enableGPSintent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    getContext().startActivity(enableGPSintent);
                }
            });

            dialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    showToastMessage("You cant continue to map");
                }
            });
            dialog.create().show();
    }

    private void showToastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}


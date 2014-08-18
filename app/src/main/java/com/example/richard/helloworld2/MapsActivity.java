package com.example.richard.helloworld2;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private StreetViewPanorama mSvp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }


    private void setupStreetViewIfNeeded(LatLng currentLatLng)
    {
        if (mSvp == null) {
            mSvp = ((SupportStreetViewPanoramaFragment)
                    getSupportFragmentManager().findFragmentById(R.id.street)).getStreetViewPanorama();

        }

        if (mSvp != null) {
            mSvp.setPosition(currentLatLng);
        }
    }
    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }

            @Override
            public void onLocationChanged(Location location) {

                LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition =
                        new CameraPosition.Builder()
                        .target(currentLatLng)
                        .zoom(14)
                        .build();

                mMap.setPadding(0, 0, 50, 50);
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                (new AddressLookupAsyncTask(getApplicationContext(), currentLatLng)).execute();

                setupStreetViewIfNeeded(currentLatLng);

            }
        };

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private class AddressLookupAsyncTask extends AsyncTask<Void, Void, String>
    {
        Context mContext;
        LatLng mLatLng;

        public AddressLookupAsyncTask(Context context, LatLng latLng) {
            mContext = context;
            mLatLng = latLng;
        }

        @Override
        protected String doInBackground(Void... params) {
            Geocoder geoCoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            String address = "home";
            try {
                List<Address> addresses = geoCoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
                address = addresses.get(0).getAddressLine(0);
            } catch (Exception e) {

            }
            return address;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mMap.addMarker(new MarkerOptions().position(mLatLng).title(s));
        }
    }
}

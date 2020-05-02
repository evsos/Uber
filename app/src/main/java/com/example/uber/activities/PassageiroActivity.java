package com.example.uber.activities;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.uber.R;
import com.example.uber.config.FirebaseConfig;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

public
class PassageiroActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private LocationListener locationListener;

    @Override
    protected
    void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_passageiro);
        Toolbar toolbar = findViewById (R.id.toolbar);
        toolbar.setTitle ("Iniciar uma viagem");
        setSupportActionBar (toolbar);

        auth = FirebaseConfig.getFirebaseAuth ();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ().findFragmentById (R.id.map);
        mapFragment.getMapAsync (this);

    }

    @Override
    public
    void onMapReady (GoogleMap googleMap) {

        mMap = googleMap;

        getUserLocation ();


    }

    private
    void getUserLocation () {

        locationManager = (LocationManager) this.getSystemService (Context.LOCATION_SERVICE);

        locationListener = new LocationListener () {
            @Override
            public
            void onLocationChanged (Location location) {

                double latitude = location.getLatitude ();
                double longitude = location.getLongitude ();

                LatLng myPlace = new LatLng ( latitude, longitude);
                mMap.clear ();
                mMap.addMarker (new MarkerOptions ().position (myPlace).title ("my place").icon (BitmapDescriptorFactory.fromResource (R.drawable.usuario)));
                mMap.moveCamera (CameraUpdateFactory.newLatLngZoom (myPlace, 19));
            }

            @Override
            public
            void onStatusChanged (String provider, int status, Bundle extras) {

            }

            @Override
            public
            void onProviderEnabled (String provider) {

            }

            @Override
            public
            void onProviderDisabled (String provider) {

            }
        };

        if (ActivityCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates (LocationManager.GPS_PROVIDER, 10000, 10, locationListener);
        }


    }



    @Override
    public
    boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_main,menu);
        return true;
    }

    @Override
    public
    boolean onOptionsItemSelected (MenuItem item) {
        switch(item.getItemId ()){
            case R.id.menuSair:
                auth.signOut ();
                finish ();
                break;
        }
        return super.onOptionsItemSelected (item);
    }
}

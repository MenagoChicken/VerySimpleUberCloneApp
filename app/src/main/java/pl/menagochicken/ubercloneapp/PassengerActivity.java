package pl.menagochicken.ubercloneapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

public class PassengerActivity extends FragmentActivity implements OnMapReadyCallback {

    //stworzenie zmiennych
    private GoogleMap mMap;

    LocationManager locationManager;

    LocationListener locationListener;

    Button callUberButton;

    Boolean requestActive = false;

    public void logOut(View view){

        ParseUser.logOut();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);

    }

    //metoda przycisku call uber
    public void callUber(View view) {

        Log.i("Info", "Call Uber");

        //jeżeli jest prawda to pobierz z serwera request
        if (requestActive) {
            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("request");
            query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {
                    if (e == null) {
                        if (objects.size() > 0) {
                            //usunięcie wszystkich request
                            for (ParseObject object : objects) {
                                object.deleteInBackground();
                            }
                            requestActive = false;
                            callUberButton.setText("Call an Uber");
                            Log.i("Info", "Call Uber - delete request");

                        }
                    }
                }
            });

            //jeżeli fałsz zapisz nowy request
        } else {

            //pytanie o lokalizacje
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                Location lastKnownUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                //jeśli jest lokacja
                if (lastKnownUserLocation != null) {
                    //zapisanie requesta w postaci username i lokacja
                    ParseObject request = new ParseObject("request");
                    request.put("username", ParseUser.getCurrentUser().getUsername());

                    ParseGeoPoint parseGeoPoint = new ParseGeoPoint(lastKnownUserLocation.getLatitude(), lastKnownUserLocation.getLongitude());
                    request.put("location", parseGeoPoint);

                    request.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                //zmiana przycisku
                                callUberButton.setText("Cancel Uber");
                                requestActive = true;
                                Log.i("Info", "Call Uber - saved");

                            }
                        }
                    });
                } else {
                    Toast.makeText(this, "Could not find location", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    Location lastKnownUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    updateMap(lastKnownUserLocation);
                }
            }
        }
    }

    public void updateMap(Location location) {
        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your location"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        callUberButton = findViewById(R.id.callUberButton);

        // pobranie requestów na start i ustawienie odpowiedniego napisu na buttonie
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("request");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() > 0) {
                        requestActive = true;
                        callUberButton.setText("Cancel Uber");
                    }
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateMap(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownUserLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownUserLocation != null) {
                updateMap(lastKnownUserLocation);
            }
        }

    }
}

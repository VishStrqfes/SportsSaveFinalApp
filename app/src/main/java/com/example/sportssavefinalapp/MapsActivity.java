package com.example.sportssavefinalapp;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private String lat, lon;
    LatLng userLocation;
    FirebaseFirestore db;
    FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        callPermissions();
        getLocationFromFirebase();
    }
    public void requestLocationUpdates(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)== PermissionChecker.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PermissionChecker.PERMISSION_GRANTED) {

            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();

            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setFastestInterval(300000);
            locationRequest.setInterval(600000);

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    userLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("User's Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(userLocation));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation,8f));
                    String authID = mAuth.getCurrentUser().getUid();
                    DocumentReference reference = db.collection("Location").document(authID);
                    String latitude = String.valueOf(locationResult.getLastLocation().getLatitude());
                    String longtitude = String.valueOf(locationResult.getLastLocation().getLongitude());
                    reference.update("lat", latitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MapsActivity.this, "Lat sent to firebase", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    reference.update("long", longtitude).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MapsActivity.this, "Long sent to Firebase", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


                    Toast.makeText(MapsActivity.this, "Lat: " + locationResult.getLastLocation().getLatitude()
                            + "\nLong: " + locationResult.getLastLocation().getLongitude(), Toast.LENGTH_SHORT).show();

                    //LatLng current = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                    //mMap.addMarker(new MarkerOptions().position(current).title("whatup"));
                    //mMap.moveCamera(CameraUpdateFactory.newLatLng(current));


                }
            }, getMainLooper());
        }else callPermissions();
    }

    public void callPermissions(){
        String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        Permissions.check(this, permissions,
                "location permissions are required",
                new Permissions.Options().setSettingsDialogTitle("Warning!").setRationaleDialogTitle("Location permissions"),
                new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        requestLocationUpdates();
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        super.onDenied(context, deniedPermissions);
                        callPermissions();
                    }

                });
    }
    void getLocationFromFirebase(){
        String authID = mAuth.getCurrentUser().getUid();
        DocumentReference reference = db.collection("Location").document(authID);
        reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()) {
                    String latFirebase = documentSnapshot.getString("lat");
                    String longFirebase = documentSnapshot.getString("long");
                    Double latFinal = Double.valueOf(latFirebase);
                    Double longFinal = Double.valueOf(longFirebase);
                    LatLng person = new LatLng(latFinal, longFinal);
                    mMap.addMarker(new MarkerOptions().position(person).title("People's Location"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(person));

                    Toast.makeText(MapsActivity.this, latFirebase, Toast.LENGTH_SHORT).show();
                    Toast.makeText(MapsActivity.this, longFirebase, Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MapsActivity.this, "not working", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MapsActivity.this, "error", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("User's Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}

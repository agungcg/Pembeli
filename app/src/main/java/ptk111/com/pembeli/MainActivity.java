package ptk111.com.pembeli;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import ptk111.com.pembeli.Database.LokasiUser;

public class MainActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    private static final String SP = "ptk11.com.pembeli";
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST = 99;
    private final static int INTERVAL2 = 1000; //2 minutes

    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private Marker locationMarker;
    ArrayList<Marker> locationPedagang = new ArrayList<Marker>();

    LokasiUser lokasiUser = new LokasiUser();
    Handler mHandler = new Handler();
    TextView textLat, textLong;
    GoogleMap map;
    JSONArray arrayUser;

    int iJumlah = 0;
    private int[] listId = new int[100];
    private String[] listNama = new String[100];
    private double[] listLatitude = new double[100];
    private double[] listLongitude = new double[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id. toolbar );
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(null);

        textLat = (TextView) findViewById(R.id.lat);
        textLong = (TextView) findViewById(R.id.lon);

        if(Build.VERSION.SDK_INT > 9){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initGMaps();
        createGoogleApi();
        startRepeatingTask();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu. main , menu);
        return true ;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.logout:
                Toast. makeText (getApplicationContext(), "Logout berhasil" ,
                        Toast. LENGTH_LONG ).show();

                SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
                SharedPreferences.Editor ed = sp.edit();
                ed.clear();
                ed.commit();

                Intent intent = new Intent(getApplicationContext(), login.class);
                startActivity(intent);
                finish();

                return true ;
            default :
                return super.onOptionsItemSelected(item);
        }
    }


    /* ----------------------------------------------------------------------------------- */
    // Initial Fragment Google Maps
    private void initGMaps(){
        MapFragment mapFragment;
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.fragment);
        mapFragment.getMapAsync(this);
    }

    // Create Google Api
    private void createGoogleApi() {
        Log.d(TAG, "createGoogleApi()");
        if ( googleApiClient == null ) {
            googleApiClient = new GoogleApiClient.Builder( this )
                    .addConnectionCallbacks( this )
                    .addOnConnectionFailedListener( this )
                    .addApi( LocationServices.API )
                    .build();
        }
    }
    /* ----------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------- */
    // Check for permission to access Location
    private boolean checkPermission() {
        Log.d(TAG, "checkPermission()");
        // Ask for permission if it wasn't granted yet
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED );
    }

    // Asks for permission
    private void askPermission() {
        Log.d(TAG, "askPermission()");
        ActivityCompat.requestPermissions(this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION },
                MY_PERMISSIONS_REQUEST);
    }

    // Permission Danied
    private void permissionsDenied() {
        Log.w(TAG, "permissionsDenied()");
    }

    // Verify user's response of the permission requested
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult()");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch ( requestCode ) {
            case MY_PERMISSIONS_REQUEST: {
                if ( grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED ){
                    // Permission granted
                    getLastKnownLocation();

                } else {
                    // Permission denied
                    permissionsDenied();
                }
                break;
            }
        }
    }
    /* ----------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------- */
    // Get last known location
    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnownLocation()");
        if ( checkPermission() ) {
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if ( lastLocation != null ) {
                Log.i(TAG, "LasKnown location. " +
                        "Long: " + lastLocation.getLongitude() +
                        " | Lat: " + lastLocation.getLatitude());
                writeLastLocation();
                startLocationUpdates();
            } else {
                Log.w(TAG, "No location retrieved yet");
                startLocationUpdates();
            }
        }
        else askPermission();
    }

    // Start location Updates
    private void startLocationUpdates(){
        int UPDATE_INTERVAL =  1000;
        int FASTEST_INTERVAL = 900;
        LocationRequest locationRequest;
        Log.i(TAG, "startLocationUpdates()");
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

        if ( checkPermission() )
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    // Write location coordinates on UI
    private void writeActualLocation(Location location) {
        SharedPreferences sp = getSharedPreferences(SP, MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        int id;
        String nama;
        String username;
        double latitudePembeli;
        double longitudePembeli;

        id = sp.getInt("id",0);
        nama = sp.getString("nm","");

        textLat.setText( "Lat: " + location.getLatitude() );
        textLong.setText( "Long: " + location.getLongitude() );

        latitudePembeli = location.getLatitude();
        longitudePembeli = location.getLongitude();

        lokasiUser.updateLokasiPembeliById(id, latitudePembeli, longitudePembeli);
        markerLocation(nama ,new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void writeLastLocation() {
        writeActualLocation(lastLocation);
    }
    /* ----------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------- */
    // Create a Location Marker
    private void markerLocation(String nama,LatLng latLng) {
        Log.i(TAG, "markerLocation("+latLng+")");
        String title = nama + ", " + latLng.latitude + ", " + latLng.longitude;
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title(title);

        if ( map!=null ) {
            // Remove the anterior marker
            if ( locationMarker != null )
                locationMarker.remove();
            locationMarker = map.addMarker(markerOptions);
            float zoom = 16f;
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            map.animateCamera(cameraUpdate);
        }
    }

    private void markerPedagang(String nama, LatLng latLng) {
        Log.i(TAG, "markerPembeli("+latLng+")");
        String title = nama + "," + latLng.latitude + ", " + latLng.longitude;
        // Define marker options
        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.pedagang))
                //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title(title);

        //locationPedagang = map.addMarker(markerOptions);
        locationPedagang.add(map.addMarker(markerOptions));
    }

    private void updatePedagang(LatLng latLng, int i) {
        Log.i(TAG, "updatePembeli("+latLng+")");
        String title = latLng.latitude + ", " + latLng.longitude;
        //locationPedagang.setPosition(latLng);
        locationPedagang.get(i).setPosition(latLng);
    }
    /* ----------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------- */
    Runnable mHandlerTask = new Runnable()
    {
        @Override
        public void run() {
            new AmbilData().execute( "http://agungcahya.esy.es/server.php?operasi=viewPedagang" );
            //stopRepeatingTask();
            startRepeatingTask2();
            //mHandler.postDelayed(mHandlerTask, INTERVAL2);
        }
    };

    void startRepeatingTask()
    {
        mHandlerTask.run();
    }

    void stopRepeatingTask()
    {
        mHandler.removeCallbacks(mHandlerTask);
    }



    Runnable mHandlerTask2 = new Runnable()
    {
        @Override
        public void run() {
            new updatePedagang().execute( "http://agungcahya.esy.es/server.php?operasi=viewPedagang" );
            mHandler.postDelayed(mHandlerTask2, INTERVAL2);
        }
    };

    void startRepeatingTask2()
    {
        mHandlerTask2.run();
    }

    void stopRepeatingTask2()
    {
        mHandler.removeCallbacks(mHandlerTask2);
    }
    /* ----------------------------------------------------------------------------------- */



    /* ----------------------------------------------------------------------------------- */
    // All implement method

    @Override
    protected void onStart() {
        super.onStart();
        // Call GoogleApiClient connection when starting the Activity
        googleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Disconnect GoogleApiClient when stopping Activity
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "onConnected()");
        getLastKnownLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "onConnectionSuspended()");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged ["+location+"]");
        lastLocation = location;
        writeActualLocation(location);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.d(TAG, "onMarkerClickListener: " + marker.getPosition() );
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady()");
        map = googleMap;
        map.setOnMarkerClickListener(this);
    }




    private class AmbilData extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strUrl) {
            Log. v ( "yw" , "mulai ambil data" );
            String hasil= "" ;
            //ambil data dari internet
            InputStream inStream = null ;
            int len = 500 ; //buffer
            try {
                URL url = new URL(strUrl[ 0 ]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout( 10000 /* milliseconds */ );
                conn.setConnectTimeout( 15000 /* milliseconds */ );
                conn.setRequestMethod( "GET" );
                conn.connect();
                int response = conn.getResponseCode();
                inStream = conn.getInputStream(); //ambil stream data
                //konversi stream ke string
                Reader r = null ;
                r = new InputStreamReader(inStream, "UTF-8" );
                char [] buffer = new char [len];
                r.read(buffer);
                hasil = new String(buffer);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null ) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return hasil;
        }

        protected void onPostExecute(String result) {
            parseLokasiPedagang(result);
        }

    }

    private class updatePedagang extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... strUrl) {
            Log. v ( "yw" , "mulai ambil data" );
            String hasil= "" ;
            //ambil data dari internet
            InputStream inStream = null ;
            int len = 500 ; //buffer
            try {
                URL url = new URL(strUrl[ 0 ]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //timeout
                conn.setReadTimeout( 10000 );
                conn.setConnectTimeout( 15000 );
                conn.setRequestMethod( "GET" );
                conn.connect();
                int response = conn.getResponseCode();
                inStream = conn.getInputStream(); //ambil stream data
                //konversi stream ke string
                Reader r = null ;
                r = new InputStreamReader(inStream, "UTF-8" );
                char [] buffer = new char [len];
                r.read(buffer);
                hasil = new String(buffer);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inStream != null ) {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return hasil;
        }

        protected void onPostExecute(String result) {
            updateLokasiPedagang(result);
        }

    }

    private void parseLokasiPedagang(String hasil){
        int id;
        double latitude, longitude;
        String nama;
        iJumlah = 0;
        try {

            arrayUser = new JSONArray(hasil);

            for (int i = 0; i < arrayUser.length(); i++) {
                JSONObject jsonChildNode = arrayUser.getJSONObject(i);
                id = jsonChildNode.optInt("id");
                nama = jsonChildNode.optString("nama");
                latitude = jsonChildNode.optDouble("latitude");
                longitude = jsonChildNode.optDouble("longitude");

                iJumlah = iJumlah + i;
                listId[i]= id;
                listNama[i] = nama;
                listLatitude[i]= latitude;
                listLongitude[i]= longitude;


                System.out.println("ID :" + listId[i]);
                System.out.println("latitude :" + listLatitude[i]);
                System.out.println("longitude :" + listLongitude[i]);


                markerPedagang(listNama[i], new LatLng(listLatitude[i], listLongitude[i]));
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateLokasiPedagang(String hasil){
        int id;
        double latitude, longitude;
        String nama;
        iJumlah = 0;
        try {

            arrayUser = new JSONArray(hasil);

            for (int i = 0; i < arrayUser.length(); i++) {
                JSONObject jsonChildNode = arrayUser.getJSONObject(i);
                id = jsonChildNode.optInt("id");
                nama = jsonChildNode.optString("nama");
                latitude = jsonChildNode.optDouble("latitude");
                longitude = jsonChildNode.optDouble("longitude");

                iJumlah = iJumlah + i;
                listId[i]= id;
                listNama[i] = nama;
                listLatitude[i]= latitude;
                listLongitude[i]= longitude;



                System.out.println("ID :" + listId[i]);
                System.out.println("latitude :" + listLatitude[i]);
                System.out.println("longitude :" + listLongitude[i]);


                updatePedagang(new LatLng(listLatitude[i], listLongitude[i]), i);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

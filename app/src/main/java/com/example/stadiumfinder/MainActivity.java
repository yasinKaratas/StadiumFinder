package com.example.stadiumfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapViewDemoActivity";
    private HuaweiMap hMap;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapView mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey("CgB6e3x9DQJTbN5mb9RcmDVRsCvQCxL/2r1YbIum/0Sg1z85AL0MX9XBwc+zzQBOEvSThQP3ZUNvLw8E/SFck+4t");
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this);
        applyPermission();
    }

    public void applyPermission() {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            Log.i(TAG, "sdk <= 28 Q");
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
                hMap.setMyLocationEnabled(true);
                hMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
            }
        }
    }

    private static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addMarker(double lat, double lng, String title, String snip) {
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(title)
                .snippet(snip)
                .clusterable(true);
        hMap.addMarker(options);
    }

    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!((ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    ||
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))) {
                hMap.setMyLocationEnabled(true);
                hMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
        }
//        double lat = 40.64445071288113;
//        double lng = 29.264300941046443;
//        addMarker(lat, lng, "Yalova Üniversitesi Safran Yerleşkesi", "Yalova MYO Bilgisayar Programcılığı");

        hMap.setMarkersClustering(true);
        hMap.setOnMarkerClickListener(marker -> {
            if ((marker.getTitle() == null) || (marker.getTitle() == "null")) return false;
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("stad", marker.getTitle());
            intent.putExtra("city", marker.getSnippet());
            intent.putExtra("lat", marker.getPosition().latitude);
            intent.putExtra("lng", marker.getPosition().longitude);
            startActivity(intent);
            return true;
        });
        GetStadiums();
    }

    private void GetStadiums() {
        String URL = "http://www.yasinkaratas.com.tr/tr.json";
        RequestQueue queue;
        queue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest getYaziRequest = new JsonObjectRequest(Request.Method.GET, URL, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray arrYazilar = response.getJSONArray("stadiums");
                    for (int i = 0; i < arrYazilar.length(); i++) {
                        JSONObject yazi = arrYazilar.getJSONObject(i);
                        String city = yazi.getString("admin_name");
                        String stad = yazi.getString("city");
                        Double lat = yazi.getDouble("lat");
                        Double lng = yazi.getDouble("lng");
                        Double population = yazi.getDouble("population");
                        addMarker(lat, lng, stad, city);
                    }
                } catch (JSONException e) {
                    Log.w(">>>> HATA 1:", e.getMessage());
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.w(">>>> HATA 2: ", error.getMessage());
                    }
                }
        );
        queue.add(getYaziRequest);
    }
}
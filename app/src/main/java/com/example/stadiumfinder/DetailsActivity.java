package com.example.stadiumfinder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.MarkerOptions;

public class DetailsActivity extends AppCompatActivity {
    private static final String TAG = "MapViewDemoActivity";
    private HuaweiMap hMap;

    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";

    private TextView tvStad;
    private TextView tvCity;
    private Button btnBack;
    Double lat = 0D;
    Double lng = 0D;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        MapView mMapView = findViewById(R.id.mapView);
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey("CgB6e3x9DQJTbN5mb9RcmDVRsCvQCxL/2r1YbIum/0Sg1z85AL0MX9XBwc+zzQBOEvSThQP3ZUNvLw8E/SFck+4t");
        mMapView.onCreate(mapViewBundle);
        mMapView.getMapAsync(this::onMapReady);

        tvStad = (TextView) findViewById(R.id.tvStad);
        tvCity = (TextView) findViewById(R.id.tvCity);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            tvStad.setText(bundle.getString("stad") + " Stadı");
            tvCity.setText(bundle.getString("city"));
            lat = bundle.getDouble("lat");
            lng = bundle.getDouble("lng");

        }

    }


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
        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .title(tvStad.getText().toString())
                .snippet(tvCity.getText().toString())
                .clusterable(false);
        hMap.addMarker(options);

        hMap.setMarkersClustering(true);
        hMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 10.0f));

    }

}

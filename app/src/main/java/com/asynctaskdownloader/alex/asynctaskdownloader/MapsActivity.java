package com.asynctaskdownloader.alex.asynctaskdownloader;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MapsActivity extends FragmentActivity {

    GoogleMap mMap; // Might be null if Google Play services APK is not available.
    LocationManager mLocationManager;
    Criteria criteria;
    Location myLocation;
    Location last_location;
    Location before_location;
    String best_provider;
    int cnt;
    SharedPreferences _GPSrecords;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.removeUpdates(myLocationLis);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        _GPSrecords = getSharedPreferences("GPS_RESULT", MODE_PRIVATE);

        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        cnt = 0;

        //取得GPS狀態, 如果沒有開啟就跳到設定頁面
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Intent it = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            Toast.makeText(this,"GPS is not open", Toast.LENGTH_SHORT).show();
            startActivity(it);
        }

        //設定精準度
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        best_provider = mLocationManager.getBestProvider(criteria,true);
        //取得上一次的位置
        last_location = mLocationManager.getLastKnownLocation(best_provider);
        mLocationManager.requestLocationUpdates(best_provider,3000,10, myLocationLis);

        if(last_location==null)
        {
            Toast.makeText(this, "Lasr location is null..", Toast.LENGTH_SHORT).show();
        }else
        {
            this.myLocation = last_location;
        }

        updateMyLocation(myLocation);
    }

    LocationListener myLocationLis = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if(cnt!=0) {
                //上一次位置
                LatLng before_LatLng = new LatLng(before_location.getLatitude(), before_location.getLongitude());
                //更新現在的位置
                myLocation = location;
                updateMyLocation(myLocation);
                LatLng now_LatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                //畫線
                mMap.addPolyline(new PolylineOptions().add(before_LatLng, now_LatLng).width(5).color(Color.GREEN));
                before_location = myLocation;
                cnt++;
            }else
            {
                before_location = myLocation;
                cnt++;
            }


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


    void updateMyLocation(Location myloc)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String timeStamp = formatter.format(curDate);

        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(myloc.getLatitude(),myloc.getLongitude())));
        mMap.addMarker(new MarkerOptions().position(new LatLng(myloc.getLatitude(),myloc.getLongitude())).title(timeStamp));

        //儲存GPS紀錄資料
        _GPSrecords.edit()
                .putInt("ROUND", cnt)
                .putString("LAT_LNG", String.valueOf(myloc.getLatitude()) + "," + String.valueOf(myloc.getLongitude()))
                .putString("TIMESTAMP", timeStamp)
                .commit();

        //Toast.makeText(this,myloc.getLatitude()+","+myloc.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

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

            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));  //預設zoom層級為17
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    } else {
                        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    }
                }
            });
        }
    }

    private void setUpMap() {
        LatLng l1 = new LatLng(25.058215, 121.517123);
//        LatLng l2 = new LatLng(25.043243, 121.505463);
//        LatLng l3 = new LatLng(25.043123, 121.537933);
//        LatLng l4 = new LatLng(25.0080110,121.4675084);
        //加一個Marker
        //mMap.addMarker(new MarkerOptions().position(l1).title("Marker"));
        //mMap.addMarker(new MarkerOptions().position(l2).title("Marker1"));
        //mMap.addMarker(new MarkerOptions().position(l3).title("Marker2"));
        //mMap.addMarker(new MarkerOptions().position(l4).title("嘉福電器行"));

        //mMap.addPolyline(new PolylineOptions().add(l1,l2,l3).width(5).color(Color.MAGENTA).zIndex(1));
        //調整地圖一開始顯示的位置
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l1, 17.0f));
    }
}

package com.asynctaskdownloader.alex.asynctaskdownloader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.InputStream;
import java.net.URL;


public class MainActivity extends ActionBarActivity {
    Button Download;
    ImageView imgView;
    Boolean isConnectedInternet;
    Switch myswitcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Download = (Button)findViewById(R.id.download);
        imgView = (ImageView)findViewById(R.id.imgView);
        myswitcher = (Switch)findViewById(R.id.switch_on_off);


        Download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String img_url = "http://img1.wikia.nocookie.net/__cb20130703150452/disney/images/c/c1/Walt_disney_pictures.jpg";
                ConnectivityManager mConnMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mNetworkInfo = mConnMgr.getActiveNetworkInfo();
                if(mNetworkInfo == null || mConnMgr.getActiveNetworkInfo()==null)
                {
                    Toast.makeText(MainActivity.this,"No network service...", Toast.LENGTH_SHORT).show();

                }else {
                    DownloadTask mytask = new DownloadTask();
                    mytask.execute(img_url);
                }


            }
        });

        myswitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(myswitcher.isChecked())
                {
                    Toast.makeText(MainActivity.this,"Switcher on", Toast.LENGTH_SHORT).show();
                    //Download.performClick();
                    Intent it = new Intent(MainActivity.this,MapsActivity.class);
                    startActivity(it);
                }else
                {
                    Toast.makeText(MainActivity.this,"Switcher off", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public Bitmap downloadBitmapfromURL(String url)
    {
        Bitmap bmp;
        try{
            bmp = BitmapFactory.decodeStream((InputStream)new URL(url).getContent());
        }catch(Exception e)
        {
            e.printStackTrace();
            bmp = null;
        }
        return bmp;
    }

    public class DownloadTask extends AsyncTask<String,Void,Bitmap>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(),"Start to download...", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            return downloadBitmapfromURL(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imgView.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //回到主畫面,把switcher變回關閉狀態
    @Override
    protected void onResume() {
        super.onResume();
        myswitcher.setChecked(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.sundeepchilukur.maps;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();
    private String STATIC_MAP_API_ENDPOINT = "https://maps.googleapis.com/maps/api/staticmap?zoom=13&size=600x300&maptype=roadmap&key=AIzaSyCfDC7Ns9LcgoKuEjyOsPDm_CMM0VQACRo&markers=color:red%7Clabel:C%7C";
    private String laLo;
    ImageView iv;
    Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv = (ImageView) findViewById(R.id.imageView);

        iv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = intentBuilder.build(activity);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                laLo = latitude + "," + longitude;
                try {
                    AsyncTask<Void, Void, Bitmap> setImageFromUrl = new AsyncTask<Void, Void, Bitmap>() {
                        @Override
                        protected Bitmap doInBackground(Void... params) {
                            Bitmap bmp = null;
                            try {
                                String path = STATIC_MAP_API_ENDPOINT + laLo;
                                Log.v("Static Maps URl ", path);
                                InputStream in = makeHttpRequest(createUrl(path));
                                bmp = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            return bmp;
                        }

                        protected void onPostExecute(Bitmap bmp) {
                            if (bmp != null) {

                                iv.setImageBitmap(bmp);
                                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                                laLo = "";
                            }

                        }
                    };
                    setImageFromUrl.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static InputStream makeHttpRequest(URL url) throws IOException {
        InputStream inputStream = null;

        // If the URL is null, then return early.
        if (url == null) {
            return null;
        }


        try {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setDoInput(true);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }


}
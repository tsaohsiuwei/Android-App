package com.example.usanthan.myapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.StrictMode;
import java.net.*;
import java.io.*;

import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

public class PostRequest extends AppCompatActivity{

    private static EditText title;
    private static EditText description;
    private static EditText location;

    private String titleinput;
    private String descriptioninput;
    private String locationinput;
    String token;

    private static TextView errormessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postrequest);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        location = (EditText) findViewById(R.id.location);

        final Button postit = (Button) findViewById(R.id.postit);
        errormessage = (TextView) findViewById(R.id.errorinfo);
        Bundle data = getIntent().getExtras();
        token = data.getString("token");

        postit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postitClicked(v);
            }
        });

    }

   public String  performPostCall(String requestURL, String param) {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(param);

            writer.flush();
            writer.close();
            os.close();
            // int responseCode=conn.getResponseCode();

            // if (responseCode == HttpURLConnection.HTTP_OK) {
            String line;
            BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line=br.readLine()) != null) {
                response+=line;
            }
            //  }
            //  else {
            //  response= null;

            //  }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return response;
    }

    public void postitClicked(View view){


        titleinput = new String (title.getText().toString());
        descriptioninput = new String (description.getText().toString());
        locationinput = new String (location.getText().toString());

        if (titleinput.isEmpty() || descriptioninput.isEmpty() || locationinput.isEmpty()){

            errormessage.setText("Error: Incomplete info");
            return;
        }

        LatLng address = MapsActivity.getLocationFromAddress(getApplicationContext(), locationinput);
        if(address == null){
            errormessage.setText("Error: Incorrect address");
            return;
        }
        String dataUrlParameters = "token="+ token + "&title=" + titleinput + "&description=" + descriptioninput + "&latitude=" + address.latitude + "&longitude=" + address.longitude;
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/";
        String response = performPostCall(dataUrl, dataUrlParameters);
         //work on the response
        System.out.println("Response from server: "+response);
        if (response.isEmpty())
        {
            errormessage.setText("Error: Something wrong with server");
        }


        else{
            Intent in = getIntent();
            Bundle b = new Bundle();
            b.putString("response",response);
            in.putExtras(b);
            setResult(RESULT_OK,in);
            this.finish();

        }



    }
}

package com.example.usanthan.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.StrictMode;
import java.net.*;
import java.io.*;

import android.content.Intent;


public class Signup extends AppCompatActivity {

    private static EditText username;
    private static EditText password;
    private static EditText email;

    private String usernameinput;
    private String passwordinput;
    private String emailinput;

    private static TextView errormessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        username = (EditText) findViewById(R.id.usernamesignup);
        password = (EditText) findViewById(R.id.passwordsignup);
        email =  (EditText) findViewById(R.id.emailsignup);

        final Button signup = (Button) findViewById(R.id.signupbutton);
        errormessage = (TextView) findViewById(R.id.errormessage);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupClicked(v);
            }
        });

    }


    public String HTTPget (String dataUrl)
    {

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        String readstream = null;





        URL url;

        try {





            // System.out.println(dataUrl);
            url = new URL(dataUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            readstream = readStream(con.getInputStream());
            // Give output for the command line
            //  System.out.println(readstream);
        } catch (Exception e) {
            // e.printStackTrace();
        }


        return readstream;
    }


    public String readStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));) {

            String nextLine = "";
            while ((nextLine = reader.readLine()) != null) {
                sb.append(nextLine);
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
        return sb.toString();
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



    public void signupClicked(View view){
        //open of sign up page

        usernameinput = new String (username.getText().toString());
        passwordinput = new String (password.getText().toString());
        emailinput = new String (email.getText().toString());

        if (usernameinput.isEmpty() || passwordinput.isEmpty() || emailinput.isEmpty()){

            errormessage.setText("Error: Incorrect sign up credentials");
            return;
        }

        /*
        String dataUrlParameters = "username="+usernameinput+"&password="+passwordinput+"&email="+emailinput;
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/users/create?" + dataUrlParameters;
        String response = HTTPget (dataUrl);
        */

        String dataUrlParameters = "username="+usernameinput+"&password="+passwordinput+"&email="+emailinput;
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/users";
        String response = performPostCall (dataUrl, dataUrlParameters);

        if (response.isEmpty())
        {
            errormessage.setText("Error: Incorrect sign up credentials");
        }

        else
        {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

        //System.out.println ("signup clicked " + response );
    }

}

package com.example.usanthan.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import android.os.StrictMode;
import java.net.*;
import java.io.*;

import android.content.Intent;



public class MainActivity extends AppCompatActivity {



    private static EditText username;
    private static EditText password;

    private String usernameinput;
    private String passwordinput;

    private static TextView errormessage;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        final Button login = (Button) findViewById(R.id.login);
        final Button signup = (Button) findViewById(R.id.signup);
        errormessage = (TextView) findViewById(R.id.errormessagetext);



        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked(v);
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupClicked(v);
            }
        });

    }

    public void loginClicked(View view){
        //send to server code here


        usernameinput = new String (username.getText().toString());
        passwordinput = new String (password.getText().toString());

        if (usernameinput.isEmpty() || passwordinput.isEmpty()){

            errormessage.setText("Error: Incorrect log in credentials");
            return;
        }

    /*
        String dataUrlParameters = "username="+usernameinput+"&password="+passwordinput;
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/action/login?" + dataUrlParameters ;
        String response = HTTPget (dataUrl);
*/
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/action/login";
        String dataUrlParameters = "username="+usernameinput+"&password="+passwordinput;
        String response = performPostCall (dataUrl,dataUrlParameters );

        errormessage.setText("");
        if (response.isEmpty())
        {
            errormessage.setText("Error: Incorrect log in credentials");
        }

        else
        {

            Intent i = new Intent(this, MapsActivity.class);
            i.putExtra("token", response);
            i.putExtra("username",usernameinput);
            startActivity(i);

            System.out.println("login clicked" + usernameinput + " " + passwordinput + " " + response);


        }

    }

    public void signupClicked(View view){
        //open of sign up page
        /*
        usernameinput = new String (username.getText().toString());
        passwordinput = new String (password.getText().toString());
        String dataUrlParameters = "username="+usernameinput+"&password="+passwordinput;
        String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/users/create?" + dataUrlParameters +"&email=test@test.com";
        String response = HTTPget (dataUrl);
        System.out.println ("signup clicked " + response );*/
        Intent i = new Intent(this, Signup.class);
        startActivity(i);

    }


    public static String HTTPget (String dataUrl)
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


    public static String readStream(InputStream in) {
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
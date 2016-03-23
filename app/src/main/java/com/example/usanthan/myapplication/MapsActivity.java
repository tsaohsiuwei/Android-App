package com.example.usanthan.myapplication;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import android.location.Address;

import static com.example.usanthan.myapplication.R.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    String token;
    String user;
    private static EditText title;
    private static EditText message;
    private static EditText contact;
    private String titleinput;
    private String messageinput;
    private String contactinput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_maps);

        final Button post = (Button) findViewById(id.post);
        final Button refresh = (Button) findViewById(id.refresh);
        final Button complete = (Button) findViewById(id.complete);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postClicked(v);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshClicked(v);
            }
        });

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeClicked(v);
            }
        });

        //assigning token string (token must be used with all requests to backend)
        Bundle logindata = getIntent().getExtras();
        token = logindata.getString("token");
        user = logindata.getString("username");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    public void postClicked(View view){
    Intent i = new Intent(this, PostRequest.class);
    i.putExtra("token", token);
    startActivityForResult(i, 0);
}

    public void completeClicked (View view){
        String response;
        JSONArray responses;
        final JSONResponseObject[] jarray;
        response = MainActivity.HTTPget("http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/?latitude=xxx&longitude=yyy&search=zzz");

        try {
            JSONObject obj;
            responses = new JSONArray(response);
            jarray = new JSONResponseObject[responses.length()];
            //System.out.println(responses.length());
            for (int i = 0; i < responses.length(); i++) {
                obj = responses.getJSONObject(i);
                String JS = obj.toString();
                // if (i == 9)
                // System.out.println(JS);
                jarray[i] = new JSONResponseObject(JS);

            }

            final JSONResponseObject[] jarray2;
            jarray2 = new JSONResponseObject[responses.length()];
            int count = 0;
            for(int i =0; i<responses.length();i++){
                if(jarray[i].OwneruserName.equals(user) &&  jarray[i].status.equals("INPROGRESS") ){

                    jarray2[count] = jarray[i];
                    count++;

                }


            }


            final AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
            final Dialog dialog2 = new Dialog(this);

            LayoutInflater inflater = this.getLayoutInflater();
            final View layout = inflater.inflate(R.layout.dialog_response3, null);
            dialog2.setContentView(layout);


            ArrayList<String> array = new ArrayList<String>();



            for ( int i = 0; i < count; i++){


                array.add(jarray2[i].title + "...");
                // add request id as well in this string

            }


            String[] s = new String[array.size()];
            //array s should hold title and request id that are inprogress that poster wishes to complete
            s = array.toArray(s);


            dialog1.setTitle("Your In Progress Requests: \"");
            dialog1.setItems(s, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                    final int finalI = which;

                    // final int finalWhich = which;
                    int temp = 0;
                    while (true) {
                        if(jarray2[finalI].array[temp].status == null){

                            temp++;
                            continue;
                        }

                        else if (!jarray2[finalI].array[temp].status.equals("ACCEPTED"))
                            temp++;
                        else
                            break;
                    }
                    final int temp3 = temp;
                    String[] listArray = {"Title: " + jarray2[finalI].title, "Description: " + jarray2[finalI].description ,"Contact of responder: " + jarray2[finalI].array[temp].contact};
                    ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, listArray);
                    ListView listView = (ListView) layout.findViewById(id.listView);
                    listView.setAdapter(adapter);
                    TextView title = (TextView) layout.findViewById(id.response_title);
                    title.setText(jarray2[finalI].title);


                    //fix 2 button click functions and add new layout file similar to dialog_response2

                    Button back = (Button) layout.findViewById(id.button_back);
                    back.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog2.dismiss();
                            dialog1.show();

                        }
                    });

                    Button complete = (Button) layout.findViewById(id.button_complete);
                    complete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            int ID = jarray2[finalI].id;
                            int temp2 = jarray2[finalI].array[temp3].id;
                            String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID + "/responses/" + temp2;
                            System.out.println(dataUrl);
                            String dataUrlParameters = "token=" + token + "&action=Review";
                            String response = performPostCall(dataUrl, dataUrlParameters);
                            System.out.println("Sent Accept, and Response from server: " + response);

                            dialog2.dismiss();


                        }
                    });

                    dialog2.show();
                }
            });

            dialog1.create();
            dialog1.show();


        } catch (org.json.JSONException e) {
            System.out.println("There is an error with JSONArray!");
        }

    }
    public void refreshClicked(View view){

        String response;
        JSONArray responses;
        final JSONResponseObject[] jarray;
        response = MainActivity.HTTPget("http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/?latitude=xxx&longitude=yyy&search=zzz");
        System.out.println("Refresh: "+response);
        mMap.clear();
        try {
            JSONObject obj;
            responses = new JSONArray(response);
            jarray = new JSONResponseObject[responses.length()];
            //System.out.println(responses.length());
            for (int i = 0; i < responses.length(); i++) {
                obj = responses.getJSONObject(i);
                String JS = obj.toString();
               // if (i == 9)
                // System.out.println(JS);
                jarray[i] = new JSONResponseObject(JS);
                LatLng point = new LatLng(jarray[i].latitude, jarray[i].longitude);
                if(!jarray[i].status.equals("INPROGRESS") && !jarray[i].status.equals("CLOSED") && !jarray[i].status.equals("COMPLETE") ) {
                    mMap.addMarker(new MarkerOptions().position(point).title(jarray[i].title).snippet("ID: " + jarray[i].id + "\n" + jarray[i].description + "\nPosted On: " + jarray[i].datePosted + "\nPosted By: "+jarray[i].OwneruserName +"\n"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(point));
                }
            }

            for(int i =0; i<responses.length();i++){
               // System.out.print(jarray[i].id + " " + jarray[i].numberofresponses + " " + jarray[i].OwneruserName + " " + user  + " " + jarray[i].status);
                if(jarray[i].OwneruserName.equals(user) && jarray[i].numberofresponses >0 && !jarray[i].status.equals("INPROGRESS") && !jarray[i].status.equals("CLOSED")  && !jarray[i].status.equals("COMPLETE") ) {

                    final AlertDialog.Builder dialog1 = new AlertDialog.Builder(this);
                    final Dialog dialog2 = new Dialog(this);

                    LayoutInflater inflater = this.getLayoutInflater();
                    final View layout = inflater.inflate(R.layout.dialog_response2, null);
                    dialog2.setContentView(layout);


                    ArrayList<String> array = new ArrayList<String>();
                    int count =0;
                    int numreject = 0;
                    for ( count = 0; count < jarray[i].numberofresponses; count++){
                        if (jarray[i].array[count].status == null) {

                            array.add(jarray[i].array[count].title + "...");
                        }
                        else
                            numreject ++;
                    }
                    System.out.println ("numreject " + numreject);
                    if (numreject == jarray[i].numberofresponses )
                        continue;

                    String[] s = new String[array.size()];
                    s = array.toArray(s);

                    final int finalI = i;
                    dialog1.setTitle("Someone responded your request: \"" + jarray[i].title + "\"");
                    dialog1.setItems(s, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            int count = 0;
                            int not = 0;
                            while (count < jarray[finalI].numberofresponses){

                                if (jarray[finalI].array[count].status == null){

                                    System.out.println("in here");
                                        not++;


                                }

                                if (not > which)
                                    break;

                                count++;
                                System.out.println (count + " " + jarray[finalI].numberofresponses + " " + not);
                            }
                            which = count;
                            final int finalWhich = which;
                            String[] listArray = {"Name: "+jarray[finalI].array[which].OriginuserName,"Messsage: " + jarray[finalI].array[which].description, "Contact: " + jarray[finalI].array[which].contact};
                            ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, listArray);
                            ListView listView = (ListView) layout.findViewById(id.listView);
                            listView.setAdapter(adapter);
                            TextView title = (TextView) layout.findViewById(id.response_title);
                            title.setText(jarray[finalI].array[which].title);

                            System.out.println ("num " + which);
                            Button decline = (Button) layout.findViewById(id.button_decline);
                            decline.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int ID = jarray[finalI].id;
                                    int temp = jarray[finalI].array[finalWhich].id;
                                    String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID + "/responses/" + temp;
                                    String dataUrlParameters = "token=" + token + "&action=Reject";
                                    String response = performPostCall(dataUrl, dataUrlParameters);
                                    System.out.println(dataUrl);
                                    System.out.println("Sent Accept, and Response from server: " + response);

                                    dialog2.dismiss();
                                   // dialog1.show();

                                }
                            });


                            Button back = (Button) layout.findViewById(id.button_back);
                            back.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog2.dismiss();
                                    dialog1.show();

                                }
                            });

                            Button accept = (Button) layout.findViewById(id.button_accept);
                            accept.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    int ID = jarray[finalI].id;
                                    int temp = jarray[finalI].array[finalWhich].id;
                                    String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID + "/responses/" + temp;
                                    String dataUrlParameters = "token=" + token + "&action=Accept";
                                    String response = performPostCall(dataUrl, dataUrlParameters);
                                    System.out.println("Sent Accept, and Response from server: " + response);

                                    dialog2.dismiss();

                                    CharSequence text = "You just chose " +jarray[finalI].array[finalWhich].OriginuserName+" to help you!";
                                    int duration = Toast.LENGTH_LONG;
                                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                                    toast.show();



                                }
                            });

                            dialog2.show();
                        }
                    });

                    dialog1.create();
                    dialog1.show();
                }

                if(jarray[i].assignee != null){

                    if(jarray[i].assignee.equals(user) && !jarray[i].status.equals("COMPLETE") && !jarray[i].status.equals("CLOSED")) {
                        CharSequence text = "You are selected to help " + jarray[i].OwneruserName + " for \"" + jarray[i].title + "\"";
                        int duration = Toast.LENGTH_LONG;
                        Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                        toast.show();
                    }
                }

            }

        } catch (org.json.JSONException e) {
            System.out.println("There is an error with JSONArray!");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(requestCode == 0 && resultCode == RESULT_OK){
            String result = data.getExtras().getString("response");
            JSONResponseObject obj = new JSONResponseObject(result);

            LatLng point = new LatLng(obj.latitude, obj.longitude);

            mMap.addMarker(new MarkerOptions().position(point).title(obj.title).snippet("ID: "+obj.id+"\n"+"Message: "+obj.description+"\nPosted On: "+obj.datePosted));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(point));

        }
        else
            System.out.println("Post Cancelled");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        // LatLng sydney = new LatLng(43.67023, -79.38676);
        //String address = "#505 - 11 Liszt Gate Toronto Ontario M2H 1G6";
        String address = "261 Strathmore Blvd. Toronto Ontario M4J 1P7";
        LatLng sydney = getLocationFromAddress(getApplicationContext(), address);

        mMap.addMarker(new MarkerOptions().position(sydney).title("Home").draggable(true).snippet("Some info"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnInfoWindowClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);

        } else {
            // Show rationale and request permission.
            Toast.makeText(this, "Permission Denied",
                    Toast.LENGTH_SHORT).show();
        }

    }


    public void onInfoWindowClick(final Marker marker) {

        final Dialog dialog1 = new Dialog(this);
        final AlertDialog.Builder dialog2 = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();

        final View layout1 = inflater.inflate(R.layout.dialog_response, null);
        final View layout2 = inflater.inflate(R.layout.dialog2, null);
        dialog1.setContentView(layout1);
        dialog2.setView(layout2);

        TextView tasktitle = (TextView)layout1.findViewById(id.response_title);
        tasktitle.setText("Task: "+ marker.getTitle());
        String[] listArray = {marker.getSnippet()};
        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.activity_listview, listArray);
        ListView listView = (ListView) layout1.findViewById(id.listView);
        listView.setAdapter(adapter);

        Button back = (Button)layout1.findViewById(id.button_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });


        Button delete = (Button)layout1.findViewById(id.button_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String snippet = marker.getSnippet();
                String temp[] = snippet.split("\n", 2);
                String id[] = temp[0].split(" ", 2);
                int ID = Integer.parseInt(id[1]);

                String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID;
                String dataUrlParameters = "token=" + token + "&action=Delete";
                String response = performPostCall(dataUrl, dataUrlParameters);

                dialog1.dismiss();
            }
        });

        Button edit = (Button)layout1.findViewById(id.button_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String snippet = marker.getSnippet();
                String temp[] = snippet.split("\n", 2);
                String id[] = temp[0].split(" ", 2);
                int ID = Integer.parseInt(id[1]);



                String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID;
                String dataUrlParameters = "token=" + token + "&action=Delete";
                String response = performPostCall(dataUrl, dataUrlParameters);
                postClicked(v);
                dialog1.dismiss();
            }
        });

        Button accept = (Button)layout1.findViewById(id.button_accept);
        accept.setText("Respond");
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
                dialog2.show();
                title = (EditText) layout2.findViewById(id.dialog_title);
                message = (EditText) layout2.findViewById(id.dialog_message);
                contact = (EditText) layout2.findViewById(id.dialog_contact);
            }
        });


        dialog1.show();

        dialog2.setPositiveButton(string.submit, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogg, int which) {


                titleinput = new String(title.getText().toString());
                messageinput = new String(message.getText().toString());
                contactinput = new String(contact.getText().toString());


                if (titleinput.isEmpty() || messageinput.isEmpty() || contactinput.isEmpty()) {

                    Context context = getApplicationContext();
                    CharSequence text = "Error: Incomplete info";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();


                } else {
                    String snippet = marker.getSnippet();
                    String temp[] = snippet.split("\n", 2);
                    String id[] = temp[0].split(" ", 2);
                    int ID = Integer.parseInt(id[1]);

                    String dataUrl = "http://ec2-52-4-229-5.compute-1.amazonaws.com:8081/requests/" + ID + "/responses";
                    String dataUrlParameters = "token=" + token + "&title=" + titleinput + "&description=" + messageinput + "&contact=" + contactinput;
                    String response = performPostCall(dataUrl, dataUrlParameters);
                    CharSequence text = "You just offered to help!";
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(getApplicationContext(), text, duration);
                    toast.show();
                    System.out.println("Response from server: " + response);
                }

            }
        }).setNegativeButton(string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog2, int which) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.usanthan.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.usanthan.myapplication/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //function you must call to perform HTTP POST, param = payload
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


    public static LatLng getLocationFromAddress(Context context,String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

        return p1;
    }
}

class JSONResponseObject
{

    int id;
    String title;
    String description;
    String datePosted;
    String status;
    double longitude;
    double latitude;

    //owner object
    int OwneruserId;
    String OwneruserName;
    String OwnerjoinDate;

    //implement ratings array here in the future

    String assignee;

    int numberofresponses;

    ResponseArrayObject array [];

    JSONResponseObject (String response)
    {

       try {
           JSONObject json = new JSONObject(response);
           id = json.getInt("id");
           title = json.getString("title");
           description = json.getString("description");
           datePosted = json.getString("datePosted");

          long date = Long.parseLong(datePosted);
           datePosted = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new java.util.Date(date));
           status = json.getString("status");

           longitude = json.getDouble("longitude");
           latitude = json.getDouble("latitude");


           JSONObject json2 = json.getJSONObject("owner");
           OwneruserId = json2.getInt("userId");
           OwneruserName = json2.getString("userName");
           OwnerjoinDate = json2.getString("joinDate");


           JSONObject jsonassignee;
            boolean test  = json.isNull("assignee");
           if (test == false) {
               jsonassignee = json.optJSONObject("assignee");
               assignee = jsonassignee.getString("userName");
           }
           else
               assignee = null;



           JSONArray responses;
           boolean test2  = json.isNull("responses");

           if (test2 == false){
               responses = json.getJSONArray("responses");
               array = new ResponseArrayObject[responses.length()];
               numberofresponses = responses.length();
               for (int i = 0; i < responses.length(); i++) {

                   JSONObject json3 = responses.getJSONObject(i);
                   array[i] = new ResponseArrayObject(json3);
               }
           }
           else
             numberofresponses = 0;
           System.out.println("JSONReponseObject4 " + " " + this.id + " " + this.numberofresponses);

       }

        catch (JSONException e) {

            System.out.println("error in JSONReponseObject" + " " + this.id + " " + this.numberofresponses);
        }
    }

}

class ResponseArrayObject
{
    int id;
    String title;
    String description;
    String contact;
    String datePosted;
    String status;

    //origin object
    int OriginuserID;
    String OriginuserName;
    String OriginjoinDate;
    //implement ratings array here in the future

    //target object
    int TargetuserId;
    String TargetuserName;
    String TargetjoinDate;

    //implement ratings array here in the future

    ResponseArrayObject (JSONObject json)
    {
        try{
            id = json.getInt("id");
            title = json.getString("title");
            description= json.getString("description");
            contact = json.getString("contact");
            datePosted = json.getString("datePosted");

            boolean is_null = json.isNull("status");


            if (is_null == false)
                status = json.getString("status");

            else
                 status = null;

            JSONObject json2 = json.getJSONObject("origin");
            OriginuserID = json2.getInt("userId");
            OriginuserName = json2.getString("userName");
            OriginjoinDate = json2.getString("joinDate");

            JSONObject json3 = json.getJSONObject("target");
            TargetuserId = json3.getInt("userId");
            TargetuserName = json3.getString("userName");
            TargetjoinDate = json3.getString("joinDate");

        }

        catch (JSONException e) {
            System.out.println("error in responseArrayObject");
        }


    }

}
package com.example.rafal.mapa;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.text.Text;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.location.Location.distanceBetween;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        AdapterView.OnItemSelectedListener{

    private GoogleMap mMap;
    private static final String LOG_TAG = "moja apka";
    private List<Marker> markers = new ArrayList<>();
    private List<Polyline> polylines = new ArrayList<>();
    private List<String> markersTitle = new ArrayList<>();
    ArrayAdapter<String> markersAdapter;
    List<Float> markersIndex = new ArrayList<>();
    public static int index =0;
    private Switch linieSwitch, deleteSwitch;
    private boolean isSwitchClicked=false;
    private boolean isDeleteClicked = false;
    private Marker[] clickedMarkers = new Marker[2];
    private TextView distanceET;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /////spinnner

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        markersAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, markersTitle);

        // Drop down layout style - list view with radio button
        markersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(markersAdapter);
        distanceET = (TextView)findViewById(R.id.distance);
        linieSwitch = (Switch)findViewById(R.id.linieSwitch);
        linieSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isSwitchClicked==true)
                {
                    isSwitchClicked=false;
                    clickedMarkers[0]=null;
                    clickedMarkers[1]=null;
                }
                else {
                    isSwitchClicked = true;
                    deleteSwitch.setChecked(false);
                }

            }
        });
        deleteSwitch = (Switch)findViewById(R.id.deleteSwitch);
        deleteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isDeleteClicked==true)
                {
                    isDeleteClicked=false;
                }
                else{
                    isDeleteClicked=true;
                    linieSwitch.setChecked(false);
                    clickedMarkers[0]=null;
                    clickedMarkers[1]=null;
                    System.out.println("isDeleteClicked "+isDeleteClicked);
                }
            }
        });
        setUpMap();
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
        System.out.println("isDeleteClicked "+isDeleteClicked);
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener(){

            @Override
            public void onPolylineClick(Polyline polyline) {System.out.println("helol");
                if(isDeleteClicked){
                    polyline.remove();
                }else{
                    System.out.println("bbbb "+polyline.getPoints().size());
                    double startLatitude = polyline.getPoints().get(0).latitude;
                    double startLongitude = polyline.getPoints().get(0).longitude;
                    double endLatitude = polyline.getPoints().get(1).latitude;
                    double endLongitude = polyline.getPoints().get(1).longitude;
                    float[] results = new float[1];
                    distanceBetween(startLatitude,startLongitude,endLatitude,endLongitude,results);
                    distanceET.setText(Float.toString(results[0]/1000)+" km");
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
///////////////////////////////////////////////////////////////////////////
            @Override
            public void onMapClick(LatLng latLng) {

                // TODO Auto-generated method stub
                System.out.println("AAAAAAAAAAAA");
                MarkerOptions marker = new MarkerOptions()
                        .title("wlasny"+Integer.toString(index))
                        .position(latLng)
                        .snippet("0");
                index++;
                //mMap.addMarker(marker);
                //markersTitle.add("wlasny");
                updateMarkersInformation(marker);

            }
        });
//////////////////////////////////////////////////////////////////////////////

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker) {
                int i;
                for(i=0;i<markers.size();i++){
                    if(markers.get(i).equals(marker)){
                        break;
                    }
                }
                if(isSwitchClicked==false && isDeleteClicked==false){
                Intent intent = new Intent(getBaseContext(), ShowMarker.class);
                System.out.println("index"+i);
                intent.putExtra("population",marker.getSnippet().toString());
                intent.putExtra("title",marker.getTitle());
                intent.putExtra("position",marker.getPosition().toString());
                intent.putExtra("index",i);
                startActivityForResult(intent,1);
                }else if (isSwitchClicked== true && isDeleteClicked == false){
                    if(clickedMarkers[0]==null){
                        clickedMarkers[0]=marker;System.out.println("jestem1");
                    }else if(clickedMarkers[1]==null){
                        clickedMarkers[1]=marker;System.out.println("jestem2");

                        polylines.add(mMap.addPolyline(new PolylineOptions()
                                .add(clickedMarkers[0].getPosition(),clickedMarkers[1].getPosition())));
                        Polyline polyline = polylines.get(polylines.size()-1);
                        polyline.setClickable(true);



                        clickedMarkers[0]=null;
                        clickedMarkers[1]=null;
                    }

                }else if(isDeleteClicked == true && isSwitchClicked == false){

                    markers.remove(marker);
                    markersTitle.clear();
                    for(int k=0;k<markers.size();k++){
                        markersTitle.add(markers.get(k).getTitle());
                    }
                    markersAdapter.notifyDataSetChanged();
                    marker.remove();
                }
                return false;
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                int indexOfMarker = data.getIntExtra("index",-1);
                String population = data.getStringExtra("population");
                String title = data.getStringExtra("title");
                double positionX = data.getDoubleExtra("positionX",-1);
                double positionY = data.getDoubleExtra("positionY",-1);
                System.out.println(positionX+" "+positionY);
                System.out.println(indexOfMarker);
                Marker marker = markers.get(indexOfMarker);
                marker.setSnippet(population);
                for(String s : markersTitle){
                    if(s.equals(title)){
                        title=title+Integer.toString(index);
                        index++;
                        break;
                    }
                }
                marker.setTitle(title);
                marker.setPosition(new LatLng(positionX,positionY));
                markersTitle.clear();
                for(int i=0;i<markers.size();i++){
                    markersTitle.add(markers.get(i).getTitle());
                }
                //System.out.println(marker.getPosition()+"position");
                markersAdapter.notifyDataSetChanged();
                //System.out.println(markers.get(indexOfMarker-1).getPosition());
                //mMap.
                //marker.position(position)

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

    private void setUpMap() {
        Log.e(LOG_TAG,"WYSTARTOWALEM");
        // Retrieve the city data from the web service
        // In a worker thread since it's a network operation.
        new Thread(new Runnable() {
            public void run() {
                try {
                    retrieveAndAddCities();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Cannot retrive cities", e);
                    return;
                }
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            SupportMapFragment mapFrag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFrag.getMapAsync(this);
        }
    }



    protected void retrieveAndAddCities() throws IOException {

        InputStream is = getResources().openRawResource(R.raw.miasta);
        //Writer writer = new StringWriter();
        final StringBuilder jsonCities = new StringBuilder();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                jsonCities.append(buffer, 0, n);
            }
        } finally {
            is.close();
        }

       // String jsonString = writer.toString();

        //System.out.println(jsonString.);;
        System.out.println(jsonCities.toString());
        // Create markers for the city data.
        // Must run this on the UI thread since it's a UI operation.
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    createMarkersFromJson(jsonCities.toString());
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error processing JSON", e);
                }
            }
        });
    }

    void createMarkersFromJson(String json) throws JSONException {
        // De-serialize the JSON string into an array of city objects
        JSONArray jsonArray = new JSONArray(json);
        for (int i = 0; i < jsonArray.length(); i++) {
            // Create a marker for each city in the JSON data.
            JSONObject jsonObj = jsonArray.getJSONObject(i);
            MarkerOptions marker = new MarkerOptions()
                    .title(jsonObj.getString("name"))
                    .snippet(Integer.toString(jsonObj.getInt("population")))
                    .position(new LatLng(
                            jsonObj.getJSONArray("latlng").getDouble(0),
                            jsonObj.getJSONArray("latlng").getDouble(1)
                    ));
            //mMap.addMarker(marker);
            //markers.add(marker);
            updateMarkersInformation(marker);
            System.out.println(markers.toString()+"aaaaa");
        }

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String title = (String)parent.getItemAtPosition(position);
        Marker clickedMarker=null;
        for(Marker marker : markers){
            if(marker.getTitle().equals(title)){
                clickedMarker=marker;
                break;
            }

        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(clickedMarker.getPosition()));
        // Showing selected spinner item

    }
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }

    public void updateMarkersInformation(MarkerOptions marker){
        markers.add(mMap.addMarker(marker));
        //markers.add(marker);
        markersTitle.add(marker.getTitle());
        markersIndex.add(marker.getZIndex());
        markersAdapter.notifyDataSetChanged();
    }

}

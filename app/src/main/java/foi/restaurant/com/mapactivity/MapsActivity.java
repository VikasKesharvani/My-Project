package foi.restaurant.com.mapactivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity /*implements OnMapReadyCallback */ {

    GoogleMap map;
    ArrayList<LatLng> markerPoints;
    double radiusInMeters = 100.0;
    private Marker mMarker;
    //red outline
    MarkerOptions options;
    private Circle mCircle;
    int strokeColor = 0xffff0000;
    //opaque red fill
    int shadeColor = 0x44ff0000;
    private Button mButtonGetDetails;
    private String sourseLat="27.48974";
    private String sourseLang="72.649745";
    private String destLat="23.45444744";
    private String destLong="70.25467855";
    private String distance="400 km";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initializing
        markerPoints = new ArrayList<LatLng>();
        mButtonGetDetails=(Button)findViewById(R.id.button_details) ;
        mButtonGetDetails.setVisibility(View.GONE);
        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        map = fm.getMap();

        if (map != null) {

            // Enable MyLocation Button in the Map
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            map.setMyLocationEnabled(true);
//            map.getUiSettings().setZoomControlsEnabled(false);
//            map.getUiSettings().setMyLocationButtonEnabled(true);
//            map.getUiSettings().setRotateGesturesEnabled(true);
//            map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
//                @Override
//                public void onMyLocationChange(Location location) {
//                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//                    if(mCircle == null || mMarker == null){
//                        drawMarkerWithCircle(latLng);
//                    }else{
//                        updateMarkerWithCircle(latLng);
//                    }
//                }
//            });
//            CircleOptions circleOptions = new CircleOptions().center(point).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(2);
//            mCircle = map.addCircle(circleOptions);
          //  map.getUiSettings().setZoomGesturesEnabled(false);
            // Setting onclick event listener for the map
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                @Override
                public void onMapClick(LatLng point) {

                    // Already two locations
                    if(markerPoints.size()>1){
                        markerPoints.clear();
                        map.clear();
                    }

                    // Adding new item to the ArrayList
                    markerPoints.add(point);

                    // Creating MarkerOptions
                  /*  MarkerOptions*/ options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(point);



                    /**
                     * For the start location, the color of marker is GREEN and
                     * for the end location, the color of marker is RED.
                     */
                    if(markerPoints.size()==1){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    }else if(markerPoints.size()==2){
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    }


                    // Add new marker to the Google Map Android API V2
                    map.addMarker(options);

                    // Checks, whether start and end locations are captured
                    if(markerPoints.size() >= 2){
                        LatLng origin = markerPoints.get(0);
                        LatLng dest = markerPoints.get(1);
                       Log.e("Origin",""+origin.longitude+""+origin.latitude);
                        Log.e("dest",""+dest.longitude+""+dest.latitude);
                        // Getting URL to the Google Directions API
                        String url = getDirectionsUrl(origin, dest);

                        DownloadTask downloadTask = new DownloadTask();

                        // Start downloading json data from Google Directions API
                        downloadTask.execute(url);
                    }
//                    77.328242808580428.565400696639852
//                    04-26 11:48:32.942 5558-5558/foi.restaurant.com.mapactivity E/dest: 78.0064199119806327.36874580089759
                }
            });
        }
    }
    private void updateMarkerWithCircle(LatLng position) {
        mCircle.setCenter(position);
        mMarker.setPosition(position);
    }

    private void drawMarkerWithCircle(LatLng position){
        double radiusInMeters = 100.0;
        int strokeColor = 0xffff0000; //red outline
        int shadeColor = 0x44ff0000; //opaque red fill

        CircleOptions circleOptions = new CircleOptions().center(position).radius(radiusInMeters).fillColor(shadeColor).strokeColor(strokeColor).strokeWidth(8);
        mCircle = map.addCircle(circleOptions);

        MarkerOptions markerOptions = new MarkerOptions().position(position);
        mMarker = map.addMarker(markerOptions);
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);
            Log.e("Url",url.toString());
            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
           // Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
//                Log.e("Json",jObject.toString());
//                JSONArray jsonArray=jObject.getJSONArray("routes");
//                for(int i=0;i<jsonArray.length();i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    JSONObject jsonObjectRoots=jsonObject.getJSONObject("bounds");
//                    JSONObject jsonObjectSource=jsonObjectRoots.getJSONObject("northeast");
//                    JSONObject jsonObjectDest=jsonObjectRoots.getJSONObject("southwest");
//                    sourseLat=jsonObjectSource.getString("lat");
//                    sourseLang=jsonObjectSource.getString("lng");
//                    destLat=jsonObjectDest.getString("lat");
//                    destLong=jsonObjectDest.getString("lng");
//                    JSONArray legs=jsonObjectRoots.getJSONArray("legs");
//                    JSONObject jobj=legs.getJSONObject(1);
//                    distance=jobj.getString("text");
//                }
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);

            }

            // Drawing polyline in the Google Map for the i-th route
            map.addPolyline(lineOptions);
            mButtonGetDetails.setVisibility(View.VISIBLE);
            mButtonGetDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewMessageNotification.notify(MapsActivity.this,"Hello", 1);
                    //showCustomAlertDialog(MapsActivity.this,"Agara","Delhi",sourseLat,sourseLang,destLat,destLong,distance);
                }
            });
        }
    }

    public static void showCustomAlertDialog(Context context, String sourcename,
                                             String destname, String sourcelat, String sourcelong,String destinatonlat, String destlong,String distance) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_details, null);
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setCancelable(false);
        final AlertDialog dialog = alertDialogBuilder.create();
        dialog.show();
        TextView surcename=(TextView) view.findViewById(R.id.source_name);
        TextView destinationname=(TextView) view.findViewById(R.id.source_name_2);
        TextView sorceLoc=(TextView) view.findViewById(R.id.latlong);
        TextView destLoc=(TextView) view.findViewById(R.id.latlong_2);
        TextView distancetxt=(TextView) view.findViewById(R.id.distance);
        ImageView cancel=(ImageView) view.findViewById(R.id.cancel_icon);

        surcename.setText(sourcename);
        destinationname.setText(destname);
        sorceLoc.setText(sourcelat+" "+sourcelong);
        destLoc.setText(destinatonlat+" "+destlong);
        distancetxt.setText(distance);


        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

            }
        });

    }

}

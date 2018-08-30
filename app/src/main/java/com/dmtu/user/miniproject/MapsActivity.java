package com.dmtu.user.miniproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PlaceSelectionListener, GetDirectionListener {

    private GoogleMap mMap;
    private Integer index=0;
    private FusedLocationProviderClient mFusedLocationClient;
    private Integer radius=1000;
    List<Place> placesList = new ArrayList<Place>();
    PlaceAutocompleteFragment placeAutocompleteFragment;
    View view ;
    double lat;
    double lng;
    String urlText="";
    String nameText="";
    String locationText="";
    String phoneNumber="";
    MapWrapperLayout mapWrapperLayout;
    LatLng origin;
    LatLng destination;
    ProgressDialog progressDialog;
    List<Polyline> polylinePaths ;
    List<Marker> originMarkers;

    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //search bar
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        placeAutocompleteFragment.setOnPlaceSelectedListener(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mapWrapperLayout=(MapWrapperLayout) findViewById(R.id.map_wrapper_layout);
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
        mapWrapperLayout.init(mMap, this);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        mMap.setMyLocationEnabled(true);
        mFusedLocationClient=LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location!=null){
                    lat =location.getLatitude();
                    lng =location.getLongitude();
                    LatLng latLng=new LatLng(lat,lng);
                    //move to current location
                    origin=latLng;
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng)
                            .zoom(17)
                            .build();
                    // do animation to move to this location
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
            }
        });
        Toast.makeText(MapsActivity.this,"Your Current Location", Toast.LENGTH_LONG).show();

        view = LayoutInflater.from(this).inflate(R.layout.content,null);

        //find nearby school
        Button schoolButton=(Button) findViewById(R.id.nbSchool);
        schoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="school";
                mMap.clear();
                String url=generateUrl(lat,lng,type);
                Object DataTransfer[]=new Object[2];
                DataTransfer[0]=mMap;
                DataTransfer[1]=url;
                GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(MapsActivity.this,"Nearby School", Toast.LENGTH_LONG).show();
            }
        });

        //find nearby bank
        Button bankButton=(Button) findViewById(R.id.nbBank);
        bankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="bank";
                mMap.clear();
                String url=generateUrl(lat,lng,type);
                Object DataTransfer[]=new Object[2];
                DataTransfer[0]=mMap;
                DataTransfer[1]=url;
                GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(MapsActivity.this,"Nearby Bank", Toast.LENGTH_LONG).show();
            }
        });

        //find nearby school
        Button restaurantButton=(Button) findViewById(R.id.nbRestaurant);
        restaurantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type="restaurant";
                mMap.clear();
                String url=generateUrl(lat,lng,type);
                Object DataTransfer[]=new Object[2];
                DataTransfer[0]=mMap;
                DataTransfer[1]=url;
                GetNearbyPlacesData getNearbyPlacesData=new GetNearbyPlacesData();
                getNearbyPlacesData.execute(DataTransfer);
                Toast.makeText(MapsActivity.this,"Nearby Restaurant", Toast.LENGTH_LONG).show();
            }

        });


    }

    @Override
    public void onPlaceSelected(final Place place) {
        mMap.clear();
        placesList.add(place);
        LatLng latLng=place.getLatLng();

        mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(index.toString())
        );
        index++;
        // create an animation for map while moving to this location
        mMap.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
        // set some feature of map
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)      // Sets the center of the map to HCMUS
                .zoom(17)                   // Sets the zoom (1<= zoom <= 20)
                .build();
        // do animation to move to this location
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        view = LayoutInflater.from(this).inflate(R.layout.content,null);
        nameText= place.getName().toString();
        if(place.getAddress()!=null) {
            locationText = place.getAddress().toString();
        }
        if(place.getPhoneNumber()!=null) {
            phoneNumber=place.getPhoneNumber().toString();}
        if(place.getWebsiteUri()!=null){
        urlText=place.getWebsiteUri().toString();}

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public View getInfoContents(Marker marker) {
                Integer i=-1;
                try {
                i=Integer.valueOf(marker.getTitle());}
                catch (Throwable e){

                }
                if (i>=index || i <0) {
                    destination= marker.getPosition();
                    nameText= marker.getTitle();
                    TextView name =(TextView) view.findViewById(R.id.placeName);
                    TextView location =(TextView) view.findViewById(R.id.placeLocation);
                    TextView phone =(TextView) view.findViewById(R.id.placePhone);
                    TextView website =(TextView) view.findViewById(R.id.placeWebsite);

                    phone.setText("");
                    location.setText("");
                    website.setText("");
                    name.setText(nameText);

                    ImageButton callButton = (ImageButton) view.findViewById(R.id.callPlaceButton);
                    OnInterInfoWindowTouchListener callClick = new OnInterInfoWindowTouchListener(callButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {//do nothing
                        }};
                    callButton.setOnTouchListener(callClick);

                    ImageButton smsButton = (ImageButton) view.findViewById(R.id.smsPlaceButton);
                    OnInterInfoWindowTouchListener smsClick = new OnInterInfoWindowTouchListener(smsButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {//do nothing
                        }};
                    smsButton.setOnTouchListener(smsClick);

                    ImageButton browserButton = (ImageButton) view.findViewById(R.id.browserPlaceButton);
                    OnInterInfoWindowTouchListener browserClick = new OnInterInfoWindowTouchListener(browserButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {//do nothing
                        }};
                    browserButton.setOnTouchListener(browserClick);

                    //direction
                    ImageButton directionButton = (ImageButton) view.findViewById(R.id.directionPlaceButton);
                    OnInterInfoWindowTouchListener directionClick =
                            new OnInterInfoWindowTouchListener(directionButton) {
                                @Override
                                protected void onClickConfirmed(View v, Marker marker) {
                                    requestDirection();
                                }
                            };
                    directionButton.setOnTouchListener(directionClick);

                    mapWrapperLayout.setMarkerWithInfoWindow(marker,view);
                    return view;
                }
                destination= marker.getPosition();
                Place places=placesList.get(i);
                nameText= places.getName().toString();
                if(places.getAddress()!=null) {
                    locationText=places.getAddress().toString();}
                    else locationText="";
                if(places.getPhoneNumber()!=null) {
                    phoneNumber=places.getPhoneNumber().toString();}
                    else phoneNumber="";
                if(places.getWebsiteUri()!=null){
                    urlText=places.getWebsiteUri().toString();}
                    else urlText="";


                TextView name =(TextView) view.findViewById(R.id.placeName);
                TextView location =(TextView) view.findViewById(R.id.placeLocation);
                TextView phone =(TextView) view.findViewById(R.id.placePhone);
                TextView website =(TextView) view.findViewById(R.id.placeWebsite);

                phone.setText("Phone: "+phoneNumber);
                name.setText(nameText);
                location.setText("Address: "+locationText);
                website.setText("Website: "+urlText);

                if(phoneNumber!="") {
                    ImageButton callButton = (ImageButton) view.findViewById(R.id.callPlaceButton);
                    OnInterInfoWindowTouchListener callClick = new OnInterInfoWindowTouchListener(callButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {
                            Uri number = Uri.parse("tel:" + phoneNumber);
                            Intent dialIntent = new Intent(Intent.ACTION_DIAL, number);
                            startActivity(dialIntent);
                        }
                    };
                    callButton.setOnTouchListener(callClick);

                    //sms place
                    ImageButton smsButton = (ImageButton) view.findViewById(R.id.smsPlaceButton);
                    OnInterInfoWindowTouchListener smsClick = new OnInterInfoWindowTouchListener(smsButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.setData(Uri.parse("sms:"));
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("address", phoneNumber);
                            smsIntent.putExtra("sms_body", "Hi, I want to ...");
                            startActivity(smsIntent);
                        }
                    };
                    smsButton.setOnTouchListener(smsClick);


                }

                //browser place
                if (urlText!="") {
                    ImageButton browserButton = (ImageButton) view.findViewById(R.id.browserPlaceButton);
                    OnInterInfoWindowTouchListener browserClick =
                            new OnInterInfoWindowTouchListener(browserButton) {
                        @Override
                        protected void onClickConfirmed(View v, Marker marker) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse(urlText));
                            startActivity(browserIntent);
                        }
                    };
                    browserButton.setOnTouchListener(browserClick);
                }


                //direction
                ImageButton directionButton = (ImageButton) view.findViewById(R.id.directionPlaceButton);
                OnInterInfoWindowTouchListener directionClick =
                        new OnInterInfoWindowTouchListener(directionButton) {
                    @Override
                    protected void onClickConfirmed(View v, Marker marker) {
                        requestDirection();
                    }
                };
                directionButton.setOnTouchListener(directionClick);

                mapWrapperLayout.setMarkerWithInfoWindow(marker,view);
                return view;
            }
        });

    }

    private void requestDirection() {
        GetDirection getDirection;
        getDirection=new GetDirection(this, origin,destination);
        getDirection.excute();
    }

    @Override
    public void onGetDirectionStart() {
        if(polylinePaths!=null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }

        }
        if(originMarkers!=null) {
            for (Marker markers : originMarkers) {
                markers.remove();
            }
        }
        progressDialog = ProgressDialog.show(this, "Please wait", "Finding direction...", true);

    }

    @Override
    public void onGetDirectionSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            ((TextView)findViewById(R.id.distanceText)).setText(route.distance);
            ((TextView)findViewById(R.id.timeText)).setText(route.duration);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_marker))
                    .title(route.startAddress)
                    .position(route.startLocation)));

            PolylineOptions polylineOptions = new PolylineOptions()
                    .geodesic(true)
                    .color(Color.BLUE)
                    .width(10);

            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }

    @Override
    public void onError(Status status) {
        Log.d("Maps", "An error occurred: " + status);
    }

    private String generateUrl(double lat, double lng, String type) {
        //this fuction generation according to
        // https://developers.google.com/places/web-service/search

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + lat + "," + lng);
        googlePlacesUrl.append("&radius=" + radius);
        googlePlacesUrl.append("&type=" + type);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        return (googlePlacesUrl.toString());
    }


}

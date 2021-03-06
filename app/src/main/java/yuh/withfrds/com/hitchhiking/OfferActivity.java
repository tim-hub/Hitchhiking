package yuh.withfrds.com.hitchhiking;


import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;


import com.google.firebase.firestore.GeoPoint;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class OfferActivity extends BaseActivity {
    /*
    Author:
    tim
    github.com/tim-hub
     */
    public static String TIMEFORMAT = "HH:mm dd/MM/yyyy";

    private FirebaseFirestore db;
    private EditText textStart;
    private EditText textDest;
    private EditText textPass;
    private EditText textTimeStart;
    private EditText textTimeEnd;
    private EditText textSeats;


    private static Location depLoc;
    private static Location destLoc;
    private static String depAddress;
    private static String destAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer);
        initTexts();
        initFirestore();
        EventBus.getDefault().register(this);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        Button offerButton = findViewById(R.id.buttonPost);
        offerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postOffer();
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();

//        textStart.setText(depAddress);
//        textDest.setText(destAddress);

    }

    private void initFirestore(){

        // the fire storage codes starts here

//        FirebaseApp.initializeApp(this); // this is not required

        // please remember connect firebase firstly
        // get instance
        db = FirebaseFirestore.getInstance();
    }

    private void initTexts(){

        textStart = (EditText) findViewById(R.id.textStartAddress);
        textDest = (EditText) findViewById(R.id.textDestAddress);
        textPass = (EditText) findViewById(R.id.textPassAddress);
        textTimeStart = (EditText) findViewById(R.id.offerTimeDeparture);
        textTimeEnd = findViewById(R.id.offerTimeRangeTo);
        textSeats = findViewById(R.id.textSeats);

    }

    private void getBackToDashboard(){

        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
    private void openMapActivity(){

        Intent intent = new Intent(this, Maps_Activity.class);
        startActivity(intent);
//        finish();
    }


    private void postOffer(){

        String startPlace = textStart.getText().toString();
        String destination = textDest.getText().toString();
        String passPlaces = textPass.getText().toString();

        GeoPoint userLocation = new GeoPoint(OurLocation.location.getLatitude(), OurLocation.location.getLongitude());

        int seats = Integer.parseInt(textSeats.getText().toString());

        Date timeStart = new Date();
        Date timeEnd = new Date();

        if (textTimeStart.getText().toString()!="" && textTimeEnd.getText().toString() != "") {
            try {

                timeStart = new SimpleDateFormat(TIMEFORMAT).parse(textTimeStart.getText().toString());
                timeEnd = new SimpleDateFormat(TIMEFORMAT).parse(textTimeEnd.getText().toString());

            } catch (Exception e) {
                Log.e("Error", "postOffer: " + e + "");
            }
        }
        OurStore.postAnOffer(userLocation ,startPlace, destination, passPlaces, timeStart, timeEnd, seats, depLoc, destLoc);

        getBackToDashboard();
    }

    /*
    get fields and id of user
     */
    public void postOffer(View view){
        postOffer();
    }

    public void openMap(View view){
        openMapActivity();
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void getAddresses(Msg mMsg) {
         textStart.setText(mMsg.getDep());
         textDest.setText(mMsg.getDest());
         textPass.setText(mMsg.getPath().get(mMsg.getPath().size() -1).getS1());
//        depAddress =  mMsg.getDep();
//        destAddress= mMsg.getDest();
        depLoc = mMsg.getDepLocation();
        destLoc = mMsg.getDestLocation();
    }

//    @Subscribe(threadMode = ThreadMode.ASYNC, sticky = true)
//    public void getAddresses(Msg mMsg) {
//        depAddress =  mMsg.getDep();
//        textStart.setText(depAddress);
//        destAddress= mMsg.getDest();
//        textDest.setText(destAddress);
//        depLoc = mMsg.getDepLocation();
//        destLoc = mMsg.getDestLocation();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}

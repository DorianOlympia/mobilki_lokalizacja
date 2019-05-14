package com.example.mobilki;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.model.Room;
import com.example.model.Router;
import com.example.navigation.NavigationView;
import com.example.service.RouterCallerService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnRouterDataUpdatedListener {

    NavigationView navigationView;
    public static final long NOTIFY_INTERVAL = 1 * 1000;
    Room room;
    WifiManager wifiManager;
    private Context context;
    private WifiRttManager mgr;
    private RangingRequest req;
    private ArrayList<Router> routers;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT)?"RTT supported":"RTT not supported",
                Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);

        room = new Room(10, 10);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setRoom(room);

        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        }


        final Context context = getApplicationContext();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mgr = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);

        for (ScanResult scanResult : wifiManager.getScanResults()) {
            if (scanResult.SSID.contains("Mobilne")) {

                RangingRequest.Builder builder = new RangingRequest.Builder();
                builder.addAccessPoint(scanResult);

                req = builder.build();
            }
        }
        Timer mTimer = new Timer();

        mTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(new Runnable() {

                    @SuppressLint("MissingPermission")
                    @Override
                    public void run() {
                        // display toast
                        mgr.startRanging(req, context.getMainExecutor(), new RangingResultCallback() {

                            @Override
                            public void onRangingFailure(int code) {
//                                Toast.makeText(context, "Ranging Failed!",
//                                        Toast.LENGTH_SHORT).show();
                                Log.d("Ranging","fail");
                            }

                            @Override
                            public void onRangingResults(List<RangingResult> results) {

                                for (RangingResult result : results){
                                    if(result.getStatus() == RangingResult.STATUS_SUCCESS){
//

                                        Log.d("dupa",Float.toString(result.getDistanceMm()));
                                        room.getRouterList().get(0).setDistance(results.get(0).getDistanceMm()/1000.0f);
                                        onRouterUpdated();
                                    }
                                }


                            }
                        });
                    }
                });
            }
        }
        , 0, NOTIFY_INTERVAL );

        //navigationView.updatePosition();
//        startService(new Intent(this, RouterCallerService.class));



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 0x12345) {
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            //Start();
        }
    }

    void Start(){

        Intent intent = new Intent(this, RouterCallerService.class);
        Bundle extras = new Bundle();

        extras.putSerializable("routers", new ArrayList(room.getRouterList()));
        intent.putExtra("bundle", extras);
        startService(intent);
    }

    @Override
    public void onRouterUpdated() {
        navigationView.updatePosition();
    }

//    private void RunTri() {
//        double[][] positions = new double[][]{{0,0}, {4, 4}, {4, 0}};
//        double[] distances = new double[]{2.8, 2.8, 2.8};
//
//        NonLinearLeastSquaresSolver solver = new NonLinearLeastSquaresSolver(new TrilaterationFunction(positions, distances), new LevenbergMarquardtOptimizer());
//        LeastSquaresOptimizer.Optimum optimum = solver.solve();
//
//// the answer
//        double[] centroid = optimum.getPoint().toArray();
//
//// error and geometry information; may throw SingularMatrixException depending the threshold argument provided
//        RealVector standardDeviation = optimum.getSigma(0);
//        RealMatrix covarianceMatrix = optimum.getCovariances(0);
//    }
}

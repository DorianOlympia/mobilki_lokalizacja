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
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.model.Room;
import com.example.model.Router;
import com.example.navigation.NavigationView;
import com.example.service.RouterCallerService;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.Predicate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private Map<String, String> macToNetwork = new HashMap<>();
    private TextView tvLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(getApplicationContext(), getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_WIFI_RTT) ? "RTT supported" : "RTT not supported",
                Toast.LENGTH_SHORT).show();

        setContentView(R.layout.activity_main);

        room = new Room(5, 5);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setRoom(room);
        tvLog = findViewById(R.id.tvLog);

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 0x12345);
        }


        final Context context = getApplicationContext();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mgr = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);


        RangingRequest.Builder builder = new RangingRequest.Builder();
        for (ScanResult scanResult : wifiManager.getScanResults()) {
            if (scanResult.SSID.contains("Mobilne")) {
                builder.addAccessPoint(scanResult);
                macToNetwork.put(scanResult.BSSID, scanResult.SSID);
            }
        }
        req = builder.build();
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
                                                           Log.d("Ranging", "fail");
                                                       }

                                                       @Override
                                                       public void onRangingResults(List<RangingResult> results) {
                                                           for (final Router router : room.getRouterList()) {
                                                               RangingResult rangingResult = IterableUtils.find(results, new Predicate<RangingResult>() {
                                                                   @Override
                                                                   public boolean evaluate(RangingResult object) {
                                                                       return macToNetwork.get(object.getMacAddress().toString()).equals(router.getMac()) && object.getStatus() == RangingResult.STATUS_SUCCESS;
                                                                   }
                                                               });

                                                               if (rangingResult != null) {
                                                                   router.setDistance(rangingResult.getDistanceMm() > 0 ? rangingResult.getDistanceMm() / 1000f : 0);

                                                                   double txPower = -50.0;
                                                                   double rssi = rangingResult.getRssi();
                                                                   float distanceRssi = (float) Math.pow(10, ((double) txPower - rssi) / (10.0 * 2.0));
                                                                   //distanceRssi /= 10.0;
                                                                   router.setRssiDistance(distanceRssi);
                                                               }
                                                           }
                                                           onRouterUpdated();
                                                       }
                                                   });
                                               }
                                           });
                                       }
                                   }
                , 0, NOTIFY_INTERVAL);

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

    public void onShowLogClicked(View v){
        StringBuilder logMsg = new StringBuilder();
        for(Router router : room.getRouterList()){
            logMsg.append(router.getMac() + " (" + router.getxPosMeters() + ", " + router.getyPosMeters() + ") " +" RTT:"+ router.getDistance() +" RSSI:"+ router.getRssiDistance() +"\n");
        }
        tvLog.setText(logMsg.toString());
    }

    void Start() {

        Intent intent = new Intent(this, RouterCallerService.class);
        Bundle extras = new Bundle();

        extras.putSerializable("routers", new ArrayList(room.getRouterList()));
        intent.putExtra("bundle", extras);
        startService(intent);
    }

    @Override
    public void onRouterUpdated() {
        for(Router router : room.getRouterList()){
            Log.d(router.getMac(), Float.toString(router.getDistance()));
        }
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

package com.example.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.net.wifi.rtt.RangingRequest;
import android.net.wifi.rtt.RangingResult;
import android.net.wifi.rtt.RangingResultCallback;
import android.net.wifi.rtt.WifiRttManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.example.model.Room;
import com.example.model.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RouterCallerService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 1 * 1000; // 10 seconds
    WifiManager wifiManager;

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;

    private Context context;
    private WifiRttManager mgr;
    private RangingRequest req;
    private ArrayList<Router> routers;

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getBundleExtra("bundle");
        routers = (ArrayList)bundle.getSerializable("routers");

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        context = getApplicationContext();

        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mgr = (WifiRttManager) context.getSystemService(Context.WIFI_RTT_RANGING_SERVICE);
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        for (ScanResult scanResult : wifiManager.getScanResults()) {
            if (scanResult.SSID.contains("Mobilne")) {
                RangingRequest.Builder builder = new RangingRequest.Builder();
                builder.addAccessPoint(scanResult);

                req = builder.build();
            }
        }
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        int timer = 0;
//        public TimeDisplayTimerTask(Room room){
//            this.room = room;
//
//        }
        int avgDist = 0;
        private Room room;

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @SuppressLint("MissingPermission")
                @Override
                public void run() {
                    // display toast
                    mgr.startRanging(req, context.getMainExecutor(), new RangingResultCallback() {

                        @Override
                        public void onRangingFailure(int code) {
                            Toast.makeText(context, "Ranging Failed!",
                                    Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRangingResults(List<RangingResult> results) {

                            for (RangingResult result : results){
                                if(result.getStatus() == RangingResult.STATUS_SUCCESS){
//                                    room.getRouterList().get(0).setDistance(results.get(0).getDistanceMm()/1000.0f);

                                    timer+=1;
                                    avgDist += results.get(0).getDistanceMm();
                                    if(timer >= 180){
                                        Toast.makeText(context, Float.toString(avgDist/timer),
                                                Toast.LENGTH_SHORT).show();
                                        timer = 0;
                                        avgDist = 0;
                                    }
                                }
                            }


                        }
                    });
                }
            });
        }
    }
}

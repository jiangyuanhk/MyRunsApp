package com.dartmouth.yuanjiang.myruns_2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import meapsoft.FFT;

public class TrackingService extends Service implements SensorEventListener {
    private NotificationManager mNotificationManager;
    private LocationManager locationManager;
    private SensorManager sensorManager;
    public List<Location> locationList;
    private static boolean isRunning;
    private Context mContext;
    public static final String LOCATION_CHANGED = "location changed";
    public static final String LABEL_AVAILABLE = "label available";
    public static final  String Action = "TrackingService_Action";
    private static final String LOGTAG = "TrackingService";
    private Intent locationChangedIntent;
    private Intent labelIntent;
    private IBinder ibinder;
    private BlockingQueue<Double> blockQueue;
    private List<Double> fectures;
    private ClassificationAsyncTask classificationAsyncTask;

    @Override
    public void onCreate() {

        Log.i(LOGTAG, "onCreate");
        mContext = this;
        locationList = new ArrayList<Location>();
        locationChangedIntent = new Intent();
        locationChangedIntent.setAction(LOCATION_CHANGED);

        labelIntent = new Intent();
        labelIntent.setAction(LABEL_AVAILABLE);

        ibinder = new MyBinder();

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        isRunning = true;

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        blockQueue = new LinkedBlockingQueue<Double>();
        fectures = new ArrayList<Double>();

        Log.i(LOGTAG, "ClassificationAsyncTask()" );
        classificationAsyncTask = new ClassificationAsyncTask();
        Log.i(LOGTAG, "ClassificationAsyncTask().execute()" );
        classificationAsyncTask.execute();

        super.onCreate();
    }

    public static boolean isRunning (){
        return isRunning;
    }

    public class MyBinder extends Binder {
        public List<Location> getLocationList(){
            return locationList;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOGTAG, "onBind");
        return ibinder;
        //return mMessenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOGTAG, "onStartCommand" );
        locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 500, 0,
                locationListener);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
                SensorManager.SENSOR_DELAY_FASTEST);


        showNotification();
        return START_STICKY; // Run until explicitly stopped.
    }

    private class ClassificationAsyncTask extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            Log.i(LOGTAG, "doInBackground" );
            while (true){
                if (isCancelled() == true)
                {
                    Log.i(LOGTAG,"isCancelled");
                    return null;
                }
                if(blockQueue.size() <Globals.BLOCK_QUEUE_CAPACITY){
                    continue;
                }
                Log.i(LOGTAG, "doInBackground >= 64" );
                double[] re = new double[Globals.BLOCK_QUEUE_CAPACITY ];
                double[] im = new double[Globals.BLOCK_QUEUE_CAPACITY ];

                double max = Double.MIN_VALUE;
                for(int i=0;i<Globals.BLOCK_QUEUE_CAPACITY;i++){

                    try{
                        double matitude = blockQueue.take();
                        re[i] = matitude;
                        if(matitude > max){
                            max = matitude;
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                FFT fft = new FFT(Globals.BLOCK_QUEUE_CAPACITY);
                fft.fft(re, im);

                for (int i = 0; i < re.length; i++) {
                    // Compute each coefficient
                    double mag = Math.sqrt(re[i] * re[i] + im[i] * im[i]);
                    fectures.add(Double.valueOf(mag));
                    im[i] = .0;
                }

                fectures.add(Double.valueOf(max));
                try {
                    double ret = WekaClassifier.classify(fectures.toArray());
                    fectures.clear();
                    labelIntent.putExtra("label_result", ret);
                    mContext.sendBroadcast(labelIntent);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            Log.i(LOGTAG, "SensorChanged");
            double x = event.values[0], y = event.values[1], z = event.values[2];

            double matitude = Math.sqrt(x * x + y * y + z * z);
            try {
                blockQueue.put(matitude);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            if(location != null){
                Log.i(LOGTAG, "onLocationChanged");
                locationList.add(location);
                mContext.sendBroadcast(locationChangedIntent);
            }
        }

        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status,
                                    Bundle extras) {}
    };

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    /**
     * Display a notification in the notification bar.
     */
    private void showNotification() {
        Log.i(LOGTAG, "showNotification" );
        Intent myIntent = new Intent(this, MapsActivity.class);
        myIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent
                = PendingIntent.getActivity(this,
                0, myIntent,
                Intent.FLAG_ACTIVITY_NEW_TASK);

        Notification notification = new Notification.Builder(this)
                .setContentTitle(this.getString(R.string.service_content_label))
                .setContentText(getResources().getString(R.string.service_content)).setSmallIcon(R.drawable.service_pic)
                .setContentIntent(pendingIntent).build();
        mNotificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.flags = notification.flags
                | Notification.FLAG_ONGOING_EVENT;

        mNotificationManager.notify(0, notification);

    }

    @Override
    public void onDestroy() {
        Log.i(LOGTAG, "onDestroy");
        if(classificationAsyncTask != null &&
                !classificationAsyncTask.isCancelled()){
            classificationAsyncTask.cancel(true);
        }
        classificationAsyncTask = null;

        mNotificationManager.cancelAll(); // Cancel the persistent notification.
        locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(this);
        locationList = null;
        isRunning = false;
        super.onDestroy();
    }


}

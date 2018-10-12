package absenmobilehr.app.kalbenutritionals.absenmobilehr.Service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;

import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.GpsManager.LocationHelper;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.clsHardCode;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsTrackingData;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsUserLogin;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsmCounterData;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.enumCounterData;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsTrackingDataRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsUserLoginRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.clsMainActivity;

import static com.android.volley.VolleyLog.TAG;

/**
 * Created by Robert on 07/06/2017.
 */

public class MyTrackingLocationService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    List<clsUserLogin> dtLogin;
    private LocationHelper locationHelper;
    boolean mockStatus = false;

    public MyTrackingLocationService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        buildGoogleApiClient();
    }

    private GoogleApiClient mGoogleApiClient;

    private void buildGoogleApiClient() {
        // TODO Auto-generated method stub
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    private Location mLastLocation;
    Handler mHandler = new Handler();
    private final static int INTERVAL = 120000;
    private static long UPDATE_INTERVAL = 1 * 360 * 1000;
    ;  //default
    private static long UPDATE_INTERVAL_TESTING = /*1000 * 60 * 2*/3000; //2 minutes
    private static Timer timer = new Timer();

    Location locationBefore = new Location("point past");
    Location locationLast = new Location("point now");


    private void startService() throws JSONException {
        try {
            doService();
            Date currentTime = Calendar.getInstance().getTime();
            Log.i(TAG, "Tracking Do Service Run time : - " + currentTime.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doService() throws JSONException {
        locationHelper = new LocationHelper(this);
        mLastLocation = getLocation();
        SQLiteDatabase db;
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        clsHardCode clsdthc = new clsHardCode();
//        db = SQLiteDatabase.openOrCreateDatabase(clsdthc.dbName, null); // create file database
        clsUserLogin loginData = new clsUserLoginRepo(getApplicationContext()).getDataLogin(getApplicationContext());

        if (loginData != null) {

            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                clsmCounterData counterData = new clsmCounterData();
                counterData.setIntId(enumCounterData.MonitoringLocation.getidCounterData());
                counterData.setTxtDeskripsi("value menunjukan waktu terakhir menjalankan services");
                counterData.setTxtName("Monitor Service Location");
                counterData.setTxtValue(dateFormat.format(cal.getTime()));
                //new clsmCounterDataRepo(getApplicationContext()).createOrUpdate(counterData);

                startRepeatingTask();
                //new clsInit().PushData(db,versionName);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
                if(e1.getMessage()!=null)
                {
                    Log.e("Error", e1.getMessage());
                }
            }

        } else {
            shutdownService();
            Log.e("Error", "Login Data di Service null");
        }
//        db.close();
    }

    public Location getLocation() {
        locationHelper.getLocation();
        Location location2 = null;
        Location location = null;
        try {
            LocationManager locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean canGetLocation = false;


            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                new clsMainActivity().showCustomToast(this, "no network provider is enabled", false);
            } else {
                canGetLocation = true;

                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            0, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            /*if (mLastLocation != null){
                                mLastLocation.reset();
                            }*/
                            mLastLocation = location;
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {

                    if (location != null) {
//                        if (mLastLocation != null){
//                            mLastLocation.reset();
                        mLastLocation = location;
//                        }
                    } else {
                        if (mLastLocation == null) {
                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    1000,
                                    0, this);
                            Log.d("GPS", "GPS Enabled");
                            if (locationManager != null) {
                                location2 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                                if (location2 != null) {
                                    mLastLocation = location2;
                                } else {
//                                    new clsMainActivity().showCustomToast(this,"Your GPS off", false);
                                }

                            }
                        }
                    }

                } else {
                    new clsMainActivity().showCustomToast(this, "Your GPS off", false);
                }
                if (location == null && location2 == null) {
//                    new clsMainActivity().showCustomToast(this,"No Location, please restart your phone", false);
                    locationHelper.getLocation();
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mLastLocation != null) {
            int intOs = Integer.valueOf(android.os.Build.VERSION.SDK);
            if (intOs >= 18) {
                mockStatus = mLastLocation.isFromMockProvider();
            }
        }
        return mLastLocation;
    }

    public Location trackingLocation() {
//        try {
//            LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
//
//            // getting GPS status
//            boolean isGPSEnabled = locationManager
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//            // getting network status
//            boolean isNetworkEnabled = locationManager
//                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//            boolean canGetLocation = false;
//            Location location = null;
//
//            if (!isGPSEnabled && !isNetworkEnabled) {
//                // no network provider is enabled
//                new clsMainActivity().showCustomToast(getApplicationContext(), "no network provider is enabled", false);
//            } else {
//                canGetLocation = true;
//                if (isNetworkEnabled) {
//                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    }
//                    locationManager.requestLocationUpdates(
//                            LocationManager.NETWORK_PROVIDER,
//                            1000,
//                            0, this);
//                    Log.d("Network", "Network Enabled");
//                    if (locationManager != null) {
//                        mLastLocation = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            double latitude = location.getLatitude();
//                            double longitude = location.getLongitude();
//                        }
//                    }
//                }
//                // if GPS Enabled get lat/long using GPS Services
//                if (isGPSEnabled) {
//                    if (mLastLocation == null) {
//                        locationManager.requestLocationUpdates(
//                                LocationManager.GPS_PROVIDER,
//                                1000,
//                                0, this);
//                        Log.d("GPS", "GPS Enabled");
//                        if (locationManager != null) {
//                            location = locationManager
//                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
//                            if (location != null) {
//                                double latitude = location.getLatitude();
//                                double longitude = location.getLongitude();
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }


        clsTrackingData dataLocation = new clsTrackingData();
        clsUserLogin dataUserActive = new clsUserLoginRepo(getApplicationContext()).getDataLogin(getApplicationContext());
        final int index;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        try {
//            String intSquence = new Intent().getStringExtra("intSequence");
//            if (intSquence != null &&!intSquence.equals("null")){
//                int intSq = Integer.parseInt(intSquence);
//                index = intSq + 1;
//            }else{
            List<clsTrackingData> _datas = (List<clsTrackingData>) new clsTrackingDataRepo(getApplicationContext()).findAll();
            if (_datas != null && _datas.size() > 0) {
                index = new clsTrackingDataRepo(getApplicationContext()).getMaxSequence() + 1;
            } else {
                index = 1;
            }
            if (mLastLocation == null){
                locationHelper = new LocationHelper(this);
            }
//            }

            if (new clsTrackingDataRepo(getApplicationContext()).getContactCount() == 0) {
                if (dataUserActive != null) {
                    if (dataUserActive.getTxtGUI() != null) {
                        try {
                            dataLocation.setGuiId(new clsMainActivity().GenerateGuid());
                            dataLocation.setTxtLongitude(String.valueOf(mLastLocation.getLongitude()));
                            dataLocation.setTxtLatitude(String.valueOf(mLastLocation.getLatitude()));
                            dataLocation.setTxtUserId(dataUserActive.getTxtUserID());
                            dataLocation.setTxtUsername(dataUserActive.getTxtUserName());
                            dataLocation.setTxtDeviceId(dataUserActive.getTxtDeviceId());
                            dataLocation.setTxtBranchCode(dataUserActive.getTxtKodeCabang());
                            dataLocation.setTxtNIK(dataUserActive.getEmployeeId());
                            dataLocation.setGuiIdLogin(dataUserActive.getTxtGUI());
                            dataLocation.setIntSequence(index);
                            dataLocation.setTxtTime(dateFormat.format(cal.getTime()));
                            dataLocation.setIntSubmit("1");
                            dataLocation.setIntSync("0");
                            new clsTrackingDataRepo(getApplicationContext()).create(dataLocation);
                            Log.i(TAG, "Tracking jam : - " + dataLocation.getTxtTime() + " sequence ke " + dataLocation.getIntSequence() + ".. yes ");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            Log.e("Tracking save err :", e.getMessage());
                        }
                    } else {
//                        shutdownService();
                        Log.e("Tracking Error", "No data User Active");
                    }
                } else {
//                    shutdownService();
                    Log.e("Tracking Error", "No data User Active");
                }
            } else {
                int data = 0;
                List<clsTrackingData> datas = new clsTrackingDataRepo(getApplicationContext()).getLastDataByTime();
                data = Integer.valueOf(datas.get(0).getIntSequence());

                if (dataUserActive != null) {
                    dataLocation.setGuiId(new clsMainActivity().GenerateGuid());
                    dataLocation.setTxtLongitude(String.valueOf(mLastLocation.getLongitude()));
                    dataLocation.setTxtLatitude(String.valueOf(mLastLocation.getLatitude()));
                    dataLocation.setTxtUserId(dataUserActive.getTxtUserID());
                    dataLocation.setTxtUsername(dataUserActive.getTxtUserName());
                    dataLocation.setTxtDeviceId(dataUserActive.getTxtDeviceId());
                    dataLocation.setTxtBranchCode(dataUserActive.getTxtKodeCabang());
                    dataLocation.setTxtNIK(dataUserActive.getEmployeeId());
                    dataLocation.setGuiIdLogin(dataUserActive.getTxtGUI());
                    dataLocation.setIntSequence(index);
                    dataLocation.setTxtTime(dateFormat.format(cal.getTime()));
                    dataLocation.setIntSubmit("1");
                    dataLocation.setIntSync("0");
                    if (dataUserActive.getTxtGUI() != null) {
                        try {
                            new clsTrackingDataRepo(getApplicationContext()).create(dataLocation);
                            Log.i(TAG, "Tracking jam : - " + dataLocation.getTxtTime() + " sequence ke " + dataLocation.getIntSequence() + ".. Semangat kk ");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
//                    shutdownService();
                    Log.e("Tracking Error", "No data User Active");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }


        return mLastLocation;
    }

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            trackingLocation();
            mHandler.postDelayed(mHandlerTask, INTERVAL);
        }
    };

    void startRepeatingTask() {
        mHandlerTask.run();
    }

    public void shutdownService() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mHandlerTask);
            Log.i(getClass().getSimpleName(), "Service Tracking stopped...");
        }

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        //Toast.makeText(this, "Welcome Kalbe SPG Mobile", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startService();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 35000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //Toast.makeText(this, " onStartCommand", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    startService();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 35000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        clsUserLogin dataUserActive = new clsUserLoginRepo(getApplicationContext()).getDataLogin(getApplicationContext());
        if (dataUserActive == null) {
            shutdownService();
        } else {
            Intent broadcastIntent = new Intent("absenmobilehr.app.kalbenutritionals.absenmobilehr.Service.RestartSensor");
            sendBroadcast(broadcastIntent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            mLastLocation = location;
            Log.i("latitude,longitude", location.getLatitude() + "," + location.getLongitude());
        }

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}

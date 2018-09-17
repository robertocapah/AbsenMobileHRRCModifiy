package absenmobilehr.app.kalbenutritionals.RCMobile.Service;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import absenmobilehr.app.kalbenutritionals.RCMobile.Data.VolleyResponseListener;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.VolleyUtils;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.clsHardCode;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.clsHelper;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsAbsenData;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsPushData;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsTrackingData;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsmConfig;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsmCounterData;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.enumCounterData;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsAbsenDataRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsTrackingDataRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsUserLoginRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsmConfigRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsmCounterDataRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.enumConfigData;

import static com.android.volley.VolleyLog.TAG;

public class MyServiceNative extends Service {
    public MyServiceNative() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        //Toast.makeText(this, "Welcome Kalbe SPG Mobile", Toast.LENGTH_LONG).show();

    }

    //private static long UPDATE_INTERVAL = 1*36*1000;  //default
    private static long UPDATE_INTERVAL = 1 * 360 * 1000;
    ;  //default
    //private static long UPDATE_INTERVAL_DELAY = 180000;  //default
    private static long UPDATE_INTERVAL_TESTING = 60000;  //2 minutes
    private static Timer timer = new Timer();

    private void _startService() {
        long intInverval = 0;
        /*if(new clsMainBL().getLIVE().equals("1")){
            intInverval=UPDATE_INTERVAL;
		}else{
			intInverval=UPDATE_INTERVAL_TESTING;
		}*/

        intInverval = UPDATE_INTERVAL_TESTING;
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(
                new TimerTask() {
                    public void run() {
                        try {
                            doServiceWork();
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }, 3000, intInverval);
        //Log.i(getClass().getSimpleName(), "FileScannerService Timer started....");
    }

    private void doServiceWork() throws JSONException {
        String versionName = "";
        try {
            versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (NameNotFoundException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        clsPushData dtJson = new clsHelper().pushData(versionName, getApplicationContext());
        if (dtJson == null) {
            _shutdownService();
        } else {
            try {
                clsmConfig configData = null;
                String linkPushData= "";
                try {
                    configData = (clsmConfig) new clsmConfigRepo(getApplicationContext()).findById(enumConfigData.API_EF.getidConfigData());
                    linkPushData = configData.getTxtValue()+new clsHardCode().linkPushData;
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                String strLinkAPI = linkPushData;
                new VolleyUtils().makeJsonObjectRequestPushData(getApplicationContext(), strLinkAPI, dtJson, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                        String error;
                    }

                    @Override
                    public void onResponse(String response, Boolean status, String strErrorMsg) {
                        String res = response;

                        Log.i(TAG, "balikan data push dari server : - " + response +".. Ganteng banget ! ");
                        clsAbsenData absenData = new clsAbsenData();
                        clsTrackingData trackingData = new clsTrackingData();
//                clsUserLogin userLogin = new clsUserLogin();
                        try {
                            JSONArray jsonArray1 = new JSONArray(response);
                            for (int i = 0; i < jsonArray1.length(); i++) {
                                JSONObject method = jsonArray1.getJSONObject(i);
                                String listMethod = method.getString("PstrMethodRequest");
                                if (listMethod.equals(trackingData.Property_ListOftrackingLocation)) {
                                    if (method.getString("pBoolValid").equals("1")) {
                                        new clsTrackingDataRepo(getApplicationContext()).updateAllRowTracking();
                                        Log.i("Tracking Push","success");
                                    }
                                }
                                if (listMethod.equals(absenData.Property_ListOftAbsenUser)) {
                                    if (method.getString("pBoolValid").equals("1")) {
                                        new clsAbsenDataRepo(getApplicationContext()).updateAllRowAbsen();
                                    }
                                }
                            }
                    /*for(Object data : jsonObject1){

                    }*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

//        SQLiteDatabase db;
//    	String versionName="";
//    	try {
//			versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e2) {
//			// TODO Auto-generated catch block
//			e2.printStackTrace();
//		}
        clsHardCode clsdthc = new clsHardCode();
//        db = SQLiteDatabase.openOrCreateDatabase(clsdthc.txtDatabaseName,null); // create file database
//        tUserLoginDA _tUserLoginDA=new tUserLoginDA(db);
        int c = 0;
        try {
            c = new clsUserLoginRepo(getApplicationContext()).getContactsCount(getApplicationContext());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (c > 0) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Calendar cal = Calendar.getInstance();
                clsmCounterData _data =new clsmCounterData();
                _data.setIntId(enumCounterData.MonitorSchedule.getidCounterData());
                _data.setTxtDeskripsi("value menunjukan waktu terakhir menjalankan services");
                _data.setTxtName("Monitor Service");
                _data.setTxtValue(dateFormat.format(cal.getTime()));
                new clsmCounterDataRepo(getApplicationContext()).createOrUpdate(_data);

                //new clsInit().PushData(db,versionName);
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            /*tAbsenUserDA _tAbsenUserDA =new tAbsenUserDA (db);

            List<tAbsenUserData> ListOftAbsenUserData=_tAbsenUserDA.getAllDataToPushData(db);
            List<tActivityData> ListOftActivityData=_tActivityDA.getAllDataToPushData(db);
            dataJson dtPush=new dataJson();
            HashMap<String, String> FileUpload=null;
            FileUpload=new HashMap<String, String>();
            if(ListOftAbsenUserData!= null){
                dtPush.setListOftAbsenUserData(ListOftAbsenUserData);
                for (tAbsenUserData dttAbsenUserData : ListOftAbsenUserData) {
                    if(dttAbsenUserData.get_txtImg1()!=null){
                        FileUpload.put("FUAbsen1"+dttAbsenUserData.get_intId(), dttAbsenUserData.get_txtImg1().toString());
                    }
                    if(dttAbsenUserData.get_txtImg2()!=null){
                        FileUpload.put("FUAbsen2"+dttAbsenUserData.get_intId(), dttAbsenUserData.get_txtImg2().toString());
                    }
                }
            }*/

        } else {
            _shutdownService();
        }

//        db.close();
    }

    private Response.Listener<String> createMyReqSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "Ski data category server - " + response);
                clsAbsenData absenData = new clsAbsenData();
                clsTrackingData trackingData = new clsTrackingData();
//                clsUserLogin userLogin = new clsUserLogin();
                try {
                    JSONArray jsonArray1 = new JSONArray(response);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject method = jsonArray1.getJSONObject(i);
                        String listMethod = method.getString("PstrMethodRequest");
                        if (listMethod.equals(trackingData.Property_ListOftrackingLocation)) {
                            if (method.getString("pBoolValid").equals("1")) {
                                new clsTrackingDataRepo(getApplicationContext()).updateAllRowTracking();
                            }
                        }
                        if (listMethod.equals(absenData.Property_ListOftAbsenUser)) {
                            clsAbsenData databefore = new clsAbsenDataRepo(getApplicationContext()).getDataCheckinActive(getApplicationContext());
                            databefore.setSync("1");
                            if (method.getString("pBoolValid").equals("1")) {
                                try {
                                    new clsAbsenDataRepo(getApplicationContext()).update(databefore);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    /*for(Object data : jsonObject1){

                    }*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Ski error connect - " + error);
            }
        };
    }

    private void _shutdownService() {
        if (timer != null) timer.cancel();
        Log.i(getClass().getSimpleName(), "Timer stopped...");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // For time consuming an long tasks you can launch a new thread here...
        //Toast.makeText(this, "Welcome Kalbe SPG Mobile", Toast.LENGTH_LONG).show();
        _startService();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        //Toast.makeText(this, " onStartCommand", Toast.LENGTH_LONG).show();
        _startService();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        _shutdownService();
    }
}

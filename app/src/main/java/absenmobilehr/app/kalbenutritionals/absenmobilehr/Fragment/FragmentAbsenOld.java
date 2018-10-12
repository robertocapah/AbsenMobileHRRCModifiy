package absenmobilehr.app.kalbenutritionals.absenmobilehr.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.VolleyResponseListener;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.VolleyUtils;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.bl.clsMainBL;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.clsHardCode;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsAbsenData;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsDeviceInfo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsUserLogin;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsmConfig;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsAbsenDataRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsDeviceInfoRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsUserLoginRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.clsmConfigRepo;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.repo.enumConfigData;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.MainMenu;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.R;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.clsMainActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;
import jim.h.common.android.lib.zxing.config.ZXingLibConfig;
import jim.h.common.android.lib.zxing.integrator.IntentIntegrator;
import jim.h.common.android.lib.zxing.integrator.IntentResult;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Robert on 14/09/2017.
 */

public class FragmentAbsenOld extends Fragment implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    private GoogleMap mMap;
    private Location mLastLocation;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = FragmentAbsen.class.getSimpleName();
    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 3000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters
    private Spinner spnOutlet;
    private Spinner spnBranch;
    private Context context = null;
    private List<String> arrData;
    private String Long;
    private String Lat;
    private String Acc;
    private static final int CAMERA_CAPTURE_IMAGE1_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE2_REQUEST_CODE = 130;
    private ImageView imgPrevNoImg1;
    private ImageView imgPrevNoImg2;
    private ImageView imgScaner;
    private static final String IMAGE_DIRECTORY_NAME = "Image Sales";
    private HashMap<String, String> HMbranch = new HashMap<String, String>();
    private HashMap<String, String> HMoutlet = new HashMap<String, String>();
    private HashMap<String, String> HMoutletLang = new HashMap<String, String>();
    private HashMap<String, String> HMoutletLat = new HashMap<String, String>();
    final String finalFile = null;
    private TextView lblLong;
    private TextView lblLang;
    private TextView txtHDId;
    private ArrayAdapter<String> dataAdapterBranch;
    private ArrayAdapter<String> dataAdapterOutlet;
    BitmapFactory.Options options;
    private Button btnRefreshMaps, btnPopupMap;
    private Button btnCheckIn;
    private String MenuID;
    private String[] arrdefaultBranch = new String[]{"Branch"};
    private String[] arrdefaultOutlet = new String[]{"Outlet"};
    private clsAbsenData _clsAbsenData = null;

    private String nameBranch;
    private String nameOutlet;
    private String branchCode;
    private String outletCode;
    private String myClass;
    private Class<?> clazz = null;

    private ZXingLibConfig zxingLibConfig;
    private Uri uriImage;
    private int countActivity;
    List<clsUserLogin> dataLogin = null;
    List<clsDeviceInfo> dataDevice = null;

    clsMainActivity _clsMainActivity = new clsMainActivity();

    View v;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static Bitmap resizeBitMapImage1(String filePath, int targetWidth, int targetHeight) {
        Bitmap bitMapImage = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            double sampleSize = 0;
            Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth
                    - targetWidth);
            if (options.outHeight * options.outWidth * 2 >= 1638) {
                sampleSize = scaleByHeight ? options.outHeight / targetHeight : options.outWidth / targetWidth;
                sampleSize = (int) Math.pow(2d, Math.floor(Math.log(sampleSize) / Math.log(2d)));
            }
            options.inJustDecodeBounds = false;
            options.inTempStorage = new byte[128];
            while (true) {
                try {
                    options.inSampleSize = (int) sampleSize;
                    bitMapImage = BitmapFactory.decodeFile(filePath, options);
                    break;
                } catch (Exception ex) {
                    try {
                        sampleSize = sampleSize * 2;
                    } catch (Exception ex1) {

                    }
                }
            }
        } catch (Exception ex) {

        }
        return bitMapImage;
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Absen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://absenmobilehr.app.kalbenutritionals.absenmobilehr/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    public class MyAdapter extends ArrayAdapter<String> {
        private List<String> arrayDataAdapyter;
        private Context Ctx;

        public List<String> getArrayDataAdapyter() {
            return arrayDataAdapyter;
        }

        public void setArrayDataAdapyter(List<String> arrayDataAdapyter) {
            this.arrayDataAdapyter = arrayDataAdapyter;
        }

        public Context getCtx() {
            return Ctx;
        }

        public void setCtx(Context ctx) {
            Ctx = ctx;
        }

        public MyAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            setCtx(context);
            setArrayDataAdapyter(objects);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View row = inflater.inflate(R.layout.custom_spinner, parent, false);
            if (arrData.size() > 0) {
                TextView label = (TextView) row.findViewById(R.id.tvTitle);
                //label.setText(arrData.get(position));
                label.setText(getArrayDataAdapyter().get(position));
                TextView sub = (TextView) row.findViewById(R.id.tvDesc);
                sub.setVisibility(View.GONE);
                sub.setVisibility(View.GONE);
                label.setTextColor(new Color().parseColor("#000000"));
                row.setBackgroundColor(new Color().parseColor("#FFFFFF"));
            }
            return row;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_absen, container, false);
        final String GuiID = new clsMainBL().GenerateGuid();
        txtHDId = (TextView) v.findViewById(R.id.txtHDId);
        btnRefreshMaps = (Button) v.findViewById(R.id.btnRefreshMaps);
        btnCheckIn = (Button) v.findViewById(R.id.buttonCheckIn);
        btnPopupMap = (Button) v.findViewById(R.id.viewMap);
        imgPrevNoImg1 = (ImageView) v.findViewById(R.id.imageViewCamera1);
        imgPrevNoImg2 = (ImageView) v.findViewById(R.id.imageViewCamera2);
        lblLong = (TextView) v.findViewById(R.id.tvLong);
        lblLang = (TextView) v.findViewById(R.id.tvLat);
        imgScaner = (ImageView) v.findViewById(R.id.imageScanner);
        options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        context = getActivity().getApplicationContext();
        clsUserLoginRepo repoLogin = new clsUserLoginRepo(getActivity().getApplicationContext());
        clsDeviceInfoRepo repoDevice = new clsDeviceInfoRepo(getActivity().getApplicationContext());

        imgScaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator.initiateScan(getActivity(), zxingLibConfig);
            }
        });
        try {
            dataLogin = (List<clsUserLogin>) repoLogin.findAll();
            dataDevice = (List<clsDeviceInfo>) repoDevice.findAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
//            _clsAbsenData = new clsAbsenDataRepo(getActivity().getApplicationContext()).getDataCheckinActive(getActivity().getApplicationContext());

        lblLong.setText("");
        lblLang.setText("");
        MenuID = "mnAbsenKBN";

//        final mMenuData dtmenuData = new mMenuBL().getMenuDataByMenuName(MenuID);
        btnRefreshMaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                displayLocation(mLastLocation);
                getLocation();
                if (mLastLocation != null) {
                    displayLocation(mLastLocation);
                }
                new clsMainActivity().showCustomToast(getContext(), "Location Updated", true);
            }
        });

        getLocation();

        if (mLastLocation != null) {
            displayLocation(mLastLocation);
        }

        btnPopupMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                final View promptView = layoutInflater.inflate(R.layout.popup_map_absen, null);
                btnPopupMap.setEnabled(false);
                GoogleMap mMap = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    mMap = ((MapFragment) (getActivity()).getFragmentManager().findFragmentById(R.id.map)).getMap();

                    if (mMap == null) {
                        mMap = ((MapFragment) (getActivity()).getFragmentManager().findFragmentById(R.id.map)).getMap();
                    }

                    double latitude = Double.parseDouble(String.valueOf(lblLang.getText()));
                    double longitude = Double.parseDouble(String.valueOf(lblLong.getText()));

                    MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Your Location");

                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

                    final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(marker.getPosition());
                    LatLngBounds bounds = builder.build();

                    mMap.clear();
                    mMap.addMarker(marker);
                    //CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude)).zoom(19).build();

                    final GoogleMap finalMMap = mMap;
                    mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

                        @Override
                        public void onCameraChange(CameraPosition arg0) {
                            // Move camera.
                            finalMMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 60));
                            // Remove listener to prevent position reset on camera move.
                            finalMMap.setOnCameraChangeListener(null);
                        }
                    });


                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                    alertDialogBuilder.setView(promptView);
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            btnPopupMap.setEnabled(true);
                                            MapFragment f = null;
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                f = (MapFragment) (getActivity()).getFragmentManager().findFragmentById(R.id.map);
                                            }
                                            if (f != null) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                                                    (getActivity()).getFragmentManager().beginTransaction().remove(f).commit();
                                                }
                                            }

                                            dialog.dismiss();
                                        }
                                    });
                    final AlertDialog alertD = alertDialogBuilder.create();

                    Location locationA = new Location("point A");

                    locationA.setLatitude(latitude);
                    locationA.setLongitude(longitude);


//                    alertD.setTitle(String.valueOf((int) Math.ceil(distance)) + " meters");
                    alertD.show();
                }

            }
        });

//        List<mEmployeeBranchData> listDataBranch = new mEmployeeBranchBL().GetAllData();
//        List<mEmployeeAreaData> listDataArea = new mEmployeeAreaBL().GetAllData();
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }

        // First we need to check availability of play services
        imgPrevNoImg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idUserActive = String.valueOf(dataLogin.get(0).getTxtUserID());
                String deviceInfo = String.valueOf(dataDevice.get(0).getTxtDevice());
                List<clsAbsenData> absenUserDatas = new ArrayList<clsAbsenData>();
                if (_clsAbsenData == null) {
                    _clsAbsenData = new clsAbsenData();
                }
                _clsAbsenData.setGuiId(GuiID);
                _clsAbsenData.setIntSubmit("0");
                _clsAbsenData.setSync("0");
                _clsAbsenData.setTxtAbsen("0");
                _clsAbsenData.setDtCheckout("-");
                _clsAbsenData.setTxtLatitude(lblLang.getText().toString());
                _clsAbsenData.setTxtLongitude(lblLong.getText().toString());
                _clsAbsenData.setTxtDeviceId(deviceInfo);
                _clsAbsenData.setTxtUserId(idUserActive);
                try {
                    new clsAbsenDataRepo(getActivity().getApplicationContext()).createOrUpdate(_clsAbsenData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                captureImage1();
            }
        });

        imgPrevNoImg2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String idUserActive = String.valueOf(dataLogin.get(0).getTxtUserID());
                String deviceInfo = String.valueOf(dataDevice.get(0).getTxtDevice());
                List<clsAbsenData> absenUserDatas = new ArrayList<clsAbsenData>();
                if (_clsAbsenData == null) {
                    _clsAbsenData = new clsAbsenData();
                }
                _clsAbsenData.setGuiId(GuiID);
                _clsAbsenData.setIntSubmit("0");
                _clsAbsenData.setSync("0");
                _clsAbsenData.setTxtAbsen("0");
                _clsAbsenData.setDtCheckout("-");
                _clsAbsenData.setTxtLatitude(lblLang.getText().toString());
                _clsAbsenData.setTxtLongitude(lblLong.getText().toString());
                _clsAbsenData.setTxtDeviceId(deviceInfo);
                _clsAbsenData.setTxtUserId(idUserActive);
                try {
                    new clsAbsenDataRepo(getActivity().getApplicationContext()).createOrUpdate(_clsAbsenData);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                captureImage2();
            }
        });
        /*arrData = new ArrayList<String>();
        if (listDataBranch.size() > 0) {
            for (mEmployeeBranchData dt : listDataBranch) {
                arrData.add(dt.get_txtBranchName());
                HMbranch.put(dt.get_txtBranchName(), dt.get_txtBranchCode());
            }
            dataAdapterBranch = new MyAdapter(getContext(), R.layout.custom_spinner, arrData);
            spnBranch.setAdapter(dataAdapterBranch);
        }
        arrData = new ArrayList<String>();
        if (listDataArea.size() > 0) {
            for (mEmployeeAreaData dt : listDataArea) {
                arrData.add(dt.get_txtOutletName());
                HMoutlet.put(dt.get_txtOutletName(), dt.get_txtOutletCode());
                HMoutletLang.put(dt.get_txtOutletName(), dt.get_txtLongitude());
                HMoutletLat.put(dt.get_txtOutletName(), dt.get_txtLatitude());
            }
            dataAdapterOutlet = new MyAdapter(getContext(), R.layout.custom_spinner, arrData);
            spnOutlet.setAdapter(dataAdapterOutlet);
        }
*/
        if (_clsAbsenData != null) {
            if (_clsAbsenData.getIntSubmit().equals("1")) {
                imgPrevNoImg1.setClickable(false);
                imgPrevNoImg2.setClickable(false);
            }


            txtHDId.setText(_clsAbsenData.getGuiId());
            lblLang.setText(_clsAbsenData.getTxtLatitude());
            lblLong.setText(_clsAbsenData.getTxtLongitude());

            double latitude = Double.valueOf(lblLang.getText().toString());
            double longitude = Double.valueOf(lblLong.getText().toString());
            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("Updating Location!");
            marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
            try {
                initilizeMap();
                // Changing map type
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            } catch (Exception e) {
                e.printStackTrace();
            }
            mMap.clear();
            mMap.addMarker(marker);
        } else {
            int IdAbsen = 0;
            try {
                IdAbsen = new clsAbsenDataRepo(context).getContactsCount(context) + 1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            txtHDId.setText(String.valueOf(IdAbsen));
//            displayLocation();
        }

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(getContext()).addApi(AppIndex.API).build();
        btnCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myClass = "absenmobilehr.app.kalbenutritionals.absenmobilehr.MainMenu";
                ;
                MenuID = "mnCheckinKBN";
                clazz = null;

                myClass = "absenmobilehr.app.kalbenutritionals.absenmobilehr.MainMenu";
                MenuID = "mnCheckinKBN";
                LayoutInflater layoutInflater = LayoutInflater.from(getContext());
                final View promptView = layoutInflater.inflate(R.layout.confirm_data, null);

                final TextView _tvConfirm = (TextView) promptView.findViewById(R.id.tvTitle);
                final TextView _tvDesc = (TextView) promptView.findViewById(R.id.tvDesc);
                _tvDesc.setVisibility(View.INVISIBLE);
                _tvConfirm.setText("Check In Data ?");
                _tvConfirm.setTextSize(18);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setView(promptView);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Boolean pRes = true;
                                        if (_clsAbsenData == null) {

                                            pRes = false;

                                        } else {
                                            if ((_clsAbsenData.getTxtImg1() == null)
                                                    && (_clsAbsenData.getTxtImg2() == null)) {

                                                pRes = false;
                                            }
                                        }
                                        if (pRes) {
                                            double latitude = Double.parseDouble(String.valueOf(lblLang.getText()));
                                            double longitude = Double.parseDouble(String.valueOf(lblLong.getText()));
                                            Location locationA = new Location("point A");

                                            locationA.setLatitude(latitude);
                                            locationA.setLongitude(longitude);


                                            clsUserLogin checkLocation = new clsUserLoginRepo(context).getDataLogin(context);
                                            if (_clsAbsenData == null) {
                                                _clsAbsenData = new clsAbsenData();
                                            }
                                            clsAbsenData datatAbsenUserData = _clsAbsenData;
                                            clsUserLogin dataUserActive = new clsUserLoginRepo(context).getDataLogin(context);
                                            String idUserActive = String.valueOf(dataUserActive.getTxtUserID());
                                            clsDeviceInfo dataDeviceInfoUser = new clsDeviceInfoRepo(context).getDataDevice(context);
                                            clsAbsenData absenUserDatas = new clsAbsenData();
                                            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Calendar cal = Calendar.getInstance();
                                            datatAbsenUserData.setDtCheckin(dateFormat.format(cal.getTime()));
//                                            datatAbsenUserData.setIntId(txtHDId.getText().toString());
                                            datatAbsenUserData.setGuiId(GuiID);
                                            datatAbsenUserData.setIntSubmit("1");
                                            datatAbsenUserData.setSync("0");
                                            datatAbsenUserData.setTxtAbsen("0");
                                            datatAbsenUserData.setTxtLatitude(lblLang.getText().toString());
                                            datatAbsenUserData.setTxtLongitude(lblLong.getText().toString());
                                            datatAbsenUserData.setTxtDeviceId(dataDeviceInfoUser.getTxtDevice());
                                            datatAbsenUserData.setTxtUserId(idUserActive);
                                            datatAbsenUserData.setTxtGuiIdLogin(dataUserActive.getTxtGUI());
                                            datatAbsenUserData.setDtCheckout(null);
                                            try {
                                                new clsAbsenDataRepo(context).createOrUpdate(datatAbsenUserData);
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            imgPrevNoImg1.setClickable(false);
                                            imgPrevNoImg2.setClickable(false);
                                            btnRefreshMaps.setClickable(false);
                                            btnRefreshMaps.setVisibility(View.GONE);


                                            _clsMainActivity.showCustomToast(getContext(), "Saved", true);
                                            try {
                                                clazz = Class.forName(myClass);
                                                Intent myIntent = new Intent(getContext(), MainMenu.class);
//                                                myIntent.putExtra(clsParameterPutExtra.MenuID, MenuID);
//                                                myIntent.putExtra(clsParameterPutExtra.BranchCode, branchCode);
//                                                myIntent.putExtra(clsParameterPutExtra.OutletCode, outletCode);
                                                getActivity().finish();
                                                startActivity(myIntent);
                                            } catch (ClassNotFoundException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                        } else {
                                            _clsMainActivity.showCustomToast(getContext(), "Please Photo at least 1 photo..", false);
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                final AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
//					else{
//						clazz = Class.forName(myClass);
//						Intent myIntent = new Intent(getApplicationContext(), clazz);
//						myIntent.putExtra(clsParameterPutExtra.MenuID, MenuID);
//						myIntent.putExtra(clsParameterPutExtra.BranchCode, branchCode);
//						myIntent.putExtra(clsParameterPutExtra.OutletCode, outletCode);
//						finish();
//						startActivity(myIntent);
//					}

        });

//        displayLocation();

        return v;
    }


    private void getOutlet() {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        clsmConfig configData = null;
        String linkPushData = "";
        try {
            configData = (clsmConfig) new clsmConfigRepo(getActivity().getApplicationContext()).findById(enumConfigData.API_EF.getidConfigData());
            linkPushData = configData.getTxtValue() + new clsHardCode().linkGetOutlet;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String strLinkAPI = linkPushData;
//        String nameRole = selectedRole;
        final JSONObject resJson = new JSONObject();
        clsUserLogin dataLogin = new clsUserLogin();
        dataLogin = new clsUserLoginRepo(getActivity().getApplicationContext()).getDataLogin(getActivity().getApplicationContext());

        /*try {
            resJson.put("TxtNIK", dataLogin.getEmployeeId());
        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        final String mRequestBody = dataLogin.getEmployeeId();
        new VolleyUtils().makeJsonObjectRequest(getActivity(), strLinkAPI, mRequestBody, "Getting Outlet Data", new VolleyResponseListener() {
            @Override
            public void onError(String response) {
//                new clsMainActivity().showCustomToast(getActivity().getApplicationContext(), "Connection Lost, get latest data failed", false);
                final SweetAlertDialog dialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...Connection lost")
                        .setContentText("Please check your internet connection,and try again");
                dialog.show();
                dialog.setCancelable(false);
                dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        dialog.dismiss();
                    }
                });

            }

            @Override
            public void onResponse(String response, Boolean status, String strErrorMsg) {
                String a = response;
//                if(!status){
//                    new clsMainActivity().showCustomToast(getApplicationContext(), strErrorMsg, false);
//                }
            }
        });
    }
    private void gettingLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location != null) {
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 1000, 0, this);
    }

    public Location getLocation() {
        try {
            LocationManager locationManager = (LocationManager) getActivity()
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            boolean canGetLocation = false;
            Location location = null;

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                new clsMainActivity().showCustomToast(getContext(), "no network provider is enabled", false);
            } else {
                canGetLocation = true;
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            1000,
                            0, this);
                    Log.d("Network", "Network Enabled");
                    if (locationManager != null) {
                        mLastLocation = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (mLastLocation == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                1000,
                                0, this);
                        Log.d("GPS", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                double latitude = location.getLatitude();
                                double longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return mLastLocation;
    }

    @SuppressWarnings("deprecation")
    private void displayLocation(Location mLastLocation) {

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            double accurate = mLastLocation.getAccuracy();
            lblLong.setText(longitude + "");
            lblLang.setText(latitude + "");

            Long = String.valueOf(longitude);
            Lat = String.valueOf(latitude);
            Acc = String.valueOf(accurate);
        }

    }

    private void buildGoogleApiClient() {
        // TODO Auto-generated method stub
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

    }

    @SuppressWarnings("deprecation")
    private boolean checkPlayServices() {
        // TODO Auto-generated method stub
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(), PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
            }
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    private void initilizeMap() {
        // TODO Auto-generated method stub
        if (mMap == null) {

            // check if map is created successfully or not
            if (mMap == null) {

            }
        }

    }

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Absen Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://absenmobilehr.app.kalbenutritionals.absenmobilehr/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initilizeMap();
        checkPlayServices();
    }

    private Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private File getOutputMediaFile() {
        // External sdcard location

        File mediaStorageDir = new File(new clsHardCode().txtFolderAbsen + File.separator);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Failed create " + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "tmp_absen" + ".jpg");
        return mediaFile;
    }

    protected void captureImage1() {
        uriImage = getOutputMediaFileUri();
        Intent intentCamera1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera1.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        getActivity().startActivityForResult(intentCamera1, CAMERA_CAPTURE_IMAGE1_REQUEST_CODE);
    }

    protected void captureImage2() {
        uriImage = getOutputMediaFileUri();
        Intent intentCamera2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera2.putExtra(MediaStore.EXTRA_OUTPUT, uriImage);
        getActivity().startActivityForResult(intentCamera2, CAMERA_CAPTURE_IMAGE2_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        // if the result is capturing Image
        if (requestCode == 99){
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult.getContents() == null && scanResult.getFormatName() == null) {
                return;
            }
            final String result = scanResult.getContents();
            Log.d("Data info", "Scanning Success result = "+result);
//            if (result != null) {
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show();
//            }
//            clsQRCodeData qrCodeData = new clsQRCodeData();
//            qrCodeData.setIntQRCodeID(result);
//            qrCodeData.setTxtKontakID(dataMember.get(0).txtKontakId.toString());
//
//            qrCodeRepo = new clsQRCodeRepo(getApplicationContext());
//
//            int h = 0;
//            h = qrCodeRepo.createOrUpdate(qrCodeData);
//            if(h > -1) {
//                Log.d("Data info", "Scanning Success");
////                                    status = true;
//            }
//
//            sendData();
//            statusQRCode.setText("Scan Success");
        }
        if (requestCode == CAMERA_CAPTURE_IMAGE1_REQUEST_CODE) {
            if (resultCode == -1) {
                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    String uri = uriImage.getPath().toString();

                    bitmap = BitmapFactory.decodeFile(uri, bitmapOptions);

                    previewCapturedImage1(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (resultCode == 0) {
                _clsMainActivity.showCustomToast(getContext(), "User canceled photo", false);
            } else {
                _clsMainActivity.showCustomToast(getContext(), "Something error", false);
            }
        } else if (requestCode == CAMERA_CAPTURE_IMAGE2_REQUEST_CODE) {
            if (resultCode == -1) {
                try {

                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    String uri = uriImage.getPath().toString();

                    bitmap = BitmapFactory.decodeFile(uri, bitmapOptions);

                    previewCapturedImage2(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (resultCode == 0) {
                _clsMainActivity.showCustomToast(getContext(), "User canceled photo", false);
            } else {
                _clsMainActivity.showCustomToast(getContext(), "Something error", false);
            }
        }

    }

    private void previewCapturedImage1(Bitmap photo) {
        try {
//            dttAbsenUserData = _tAbsenUserBL.getDataCheckInActive();
            Bitmap bitmap = new clsMainActivity().resizeImageForBlob(photo);
            imgPrevNoImg1.setVisibility(View.VISIBLE);
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, out); // bmp is your Bitmap instance
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Bitmap photo_view = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, blob);
//            Bitmap bitmap1 = Bitmap.createScaledBitmap(photo, 150, 150, false);
//            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, blob);
            byte[] pht = out.toByteArray();
            imgPrevNoImg1.setImageBitmap(photo_view);
            if (_clsAbsenData != null) {
                _clsAbsenData.setTxtImg1(pht);
            } else {
                _clsAbsenData.setTxtImg1(pht);
//                dttAbsenUserData.set_txtImg2(null);
                _clsAbsenData.setGuiId(txtHDId.getText().toString());
            }
            _clsAbsenData.setIntSubmit("0");
            _clsAbsenData.setSync("0");
            _clsAbsenData.setTxtAbsen("0");
            /*try {
                new clsAbsenDataRepo(context).createOrUpdate(_clsAbsenData);
            } catch (SQLException e) {
                e.printStackTrace();
            }*/

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void previewCapturedImage2(Bitmap photo) {
        try {
//            dttAbsenUserData = _tAbsenUserBL.getDataCheckInActive();
            Bitmap bitmap = new clsMainActivity().resizeImageForBlob(photo);
            imgPrevNoImg2.setVisibility(View.VISIBLE);
            ByteArrayOutputStream out = null;
            try {
                out = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, out); // bmp is your Bitmap instance
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Bitmap photo_view = Bitmap.createScaledBitmap(bitmap, 150, 150, true);
            ByteArrayOutputStream blob = new ByteArrayOutputStream();
//            photo.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, blob);
//            Bitmap bitmap1 = Bitmap.createScaledBitmap(photo, 150, 150, false);
//            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, blob);
            byte[] pht = out.toByteArray();
            imgPrevNoImg2.setImageBitmap(photo_view);
            if (_clsAbsenData != null) {
                _clsAbsenData.setTxtImg2(pht);
            } else {
                _clsAbsenData.setTxtImg1(null);
                _clsAbsenData.setTxtImg2(pht);
                _clsAbsenData.setGuiId(txtHDId.getText().toString());
            }
            _clsAbsenData.setIntSubmit("1");
            _clsAbsenData.setSync("0");
            _clsAbsenData.setTxtAbsen("0");//
            /*try {
                new clsAbsenDataRepo(context).createOrUpdate(_clsAbsenData);
            } catch (SQLException e) {
                e.printStackTrace();
            }*/
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // TODO Auto-generated method stub
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

    }

    @Override
    public void onConnected(@Nullable Bundle arg0) {

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        // TODO Auto-generated method stub
        mGoogleApiClient.connect();

    }

    private boolean isDeviceSupportCamera() {
        if (getContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        mLastLocation = location;
        displayLocation(mLastLocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

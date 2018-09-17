package absenmobilehr.app.kalbenutritionals.RCMobile;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import absenmobilehr.app.kalbenutritionals.RCMobile.Data.DatabaseManager;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.bl.clsMainBL;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsStatusMenuStart;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsDeviceInfoRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsUserLoginRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsmConfigRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.enumStatusMenuStart;

public class Splash extends AppCompatActivity {
    long delay = 5000;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;
    private static int result = 0;
    clsDeviceInfoRepo deviceInfoRepo = null;
    clsUserLoginRepo loginRepo = null;
    private TextView version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.anim_layout);
        initDB();
        version = (TextView) findViewById(R.id.tv_version);

        try {
            version.setText(getPackageManager().getPackageInfo(getPackageName(), 0).versionName + " \u00a9 KN-IT");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void initDB()
    {
        DatabaseManager.init(this);
//        deviceInfoRepo = new clsDeviceInfoRepo(this);
//        loginRepo = new clsUserLoginRepo(this);
    }

    private void StartAnimations() {

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView iv = (TextView) findViewById(R.id.iv_anim);
        iv.clearAnimation();
        iv.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        anim.reset();
        ImageView iv2 = (ImageView) findViewById(R.id.imageView2);
//        iv2.setBackgroundResource(R.mipmap.ic_hris_kn_160);
        iv2.clearAnimation();
        iv2.startAnimation(anim);
    }

    private boolean checkPermission() {

        AlertDialog.Builder builder = new AlertDialog.Builder(Splash.this);
        builder.setMessage("You need to allow access. . .");
        builder.setCancelable(false);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && !ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        &&!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        &&!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.CAMERA)
                        &&!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.VIBRATE)
                        &&!ActivityCompat.shouldShowRequestPermissionRationale(Splash.this,
                        Manifest.permission.READ_PHONE_STATE)){
                    ActivityCompat.requestPermissions(Splash.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    dialog.dismiss();

                }else {
                    ActivityCompat.requestPermissions(Splash.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA, Manifest.permission.READ_PHONE_STATE},
                            REQUEST_CODE_ASK_PERMISSIONS);
                    dialog.dismiss();
                }

            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();

            return true;
    }

    private void checkStatusMenu() {

        Timer runProgress = new Timer();
        TimerTask viewTask = new TimerTask() {

            public void run() {

                Intent myIntent = new Intent(Splash.this, Login.class);
//                clsHelper _clsHelper = new clsHelper();
//                _clsHelper.createFolderApp();
//                _clsHelper.createFolderUserData();

                clsStatusMenuStart _clsStatusMenuStart = null;
                try {
                    _clsStatusMenuStart = new clsMainBL().checkUserActive(getApplicationContext());
                    int intOs = Integer.valueOf(android.os.Build.VERSION.SDK);
                    if (intOs >= 16){
                        finishAffinity();
                    }else{
                        ActivityCompat.finishAffinity(Splash.this);
                    }
                    if (_clsStatusMenuStart.get_intStatus() == enumStatusMenuStart.FormLogin) {
                        myIntent = new Intent(Splash.this, Login.class);
                    } else if (_clsStatusMenuStart.get_intStatus() == enumStatusMenuStart.UserActiveLogin) {
                        myIntent = new Intent(Splash.this, MainMenu.class);
                        myIntent.putExtra("key_view", "main_menu");
//                        startService(new Intent(getApplicationContext(), MyServiceNative.class));
//                        startService(new Intent(getApplicationContext(), MyTrackingLocationService.class));
                    }else if (_clsStatusMenuStart.get_intStatus() == enumStatusMenuStart.PushDataAbsenHRMobile) {
                        myIntent = new Intent(Splash.this, PushData.class);
                        myIntent.putExtra("key_view", "push_data");
//                        startService(new Intent(getApplicationContext(), MyServiceNative.class));
//                        startService(new Intent(getApplicationContext(), MyTrackingLocationService.class));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
//String
                try {
                    new clsmConfigRepo(getApplicationContext()).InsertDefaultMconfig();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Log.d("log error",e.getMessage());
                    Toast.makeText(getApplicationContext(),"gagal menyimpan mConfig data",Toast.LENGTH_LONG);
                }
                finish();
                startActivity(myIntent);
            }
        };
        runProgress.schedule(viewTask, delay);
    }

    @Override
    protected void onResume() {
        super.onResume();

        int hasWriteExternalStoragePermission = ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadExternalStoragePermission = ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCameraPermission = ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.CAMERA);
        int hasPhonePermission = ContextCompat.checkSelfPermission(Splash.this,
                Manifest.permission.READ_PHONE_STATE);

        if (Build.VERSION.SDK_INT >= 23){
            if (hasWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED) {
                boolean checkPermission = checkPermission();
            } else if (hasReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED){
                boolean checkPermission = checkPermission();
            } else if (hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED){
                boolean checkPermission = checkPermission();
            } else if (hasCameraPermission != PackageManager.PERMISSION_GRANTED){
                boolean checkPermission = checkPermission();
            } else if (hasPhonePermission != PackageManager.PERMISSION_GRANTED){
                boolean checkPermission = checkPermission();
            }  else {
                StartAnimations();
                checkStatusMenu();
            }
        } else {
            StartAnimations();
            checkStatusMenu();
        }
    }
}
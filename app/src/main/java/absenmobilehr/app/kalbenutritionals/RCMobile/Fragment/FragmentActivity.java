package absenmobilehr.app.kalbenutritionals.RCMobile.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import absenmobilehr.app.kalbenutritionals.RCMobile.Data.VolleyResponseListener;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.VolleyUtils;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.adapter.AdapterListActivity;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.clsHardCode;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.Event;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsActivityUser;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsUserLogin;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.common.clsmConfig;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsUserLoginRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.clsmConfigRepo;
import absenmobilehr.app.kalbenutritionals.RCMobile.Data.repo.enumConfigData;
import absenmobilehr.app.kalbenutritionals.RCMobile.R;
import absenmobilehr.app.kalbenutritionals.RCMobile.clsMainActivity;
import absenmobilehr.app.kalbenutritionals.RCMobile.lib.SpinnerCustom;
import absenmobilehr.app.kalbenutritionals.RCMobile.lib.clsDatePicker;
import absenmobilehr.app.kalbenutritionals.RCMobile.utils.Tools;
import absenmobilehr.app.kalbenutritionals.RCMobile.widget.LineItemDecoration;

import static absenmobilehr.app.kalbenutritionals.RCMobile.lib.clsDatePicker.formatSimpleDate;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Robert on 9/3/2018.
 */

public class FragmentActivity extends Fragment {
    View v;
    Context context;
    private RecyclerView recyclerView;
    private AdapterListActivity mAdapter;
    private List<clsActivityUser> listDeleteData = new ArrayList<>();
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private Toolbar toolbar;
    private Button spn_from_date;
    private static final int TXT_NEW = 0;
    private static final int TXT_EXIST = 1;
    FloatingActionButton fabAdd;
    private static int year, month, day;
    String token = "";
    public static final int DIALOG_QUEST_CODE = 300;
    clsUserLogin dataUserActive = new clsUserLogin();
    List<String> listDataActivity = new ArrayList<String>();
    List<String> listPrograms = new ArrayList<String>();
    private HashMap<String, String> HMProgram = new HashMap<>();
    private HashMap<String, String> HMActivity = new HashMap<>();
    static String TXT_SPN_DEFAULT = "Choose one ..";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
        dataUserActive = new clsUserLoginRepo(context).getDataLogin(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_activity, container, false);
        fabAdd = v.findViewById(R.id.fabAddNew);
        SharedPreferences prefs = getActivity().getSharedPreferences(new clsHardCode().MY_PREFS_NAME, MODE_PRIVATE);
        token = prefs.getString("token", "No token defined");
        initComponent();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showDialogFullscreen();
                clsActivityUser user = null;
                showCustomDialog(user, TXT_NEW);
            }
        });

        return v;

    }

    private void showCustomDialogInfo(clsActivityUser data) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ((AppCompatButton) v).getText().toString() + " Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void initComponent() {
        clsmConfig configData = null;
        try {
            configData = (clsmConfig) new clsmConfigRepo(context).findById(enumConfigData.API_EF.getidConfigData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkGetActivity;
        final JSONObject resJson = new JSONObject();
        String username = dataUserActive.getTxtUserName();
        try {
//            resJson.put("txtUsername", username);
            resJson.put("txtUsername", "muhamad.riza");
            resJson.put("bitActive", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String mRequestBody = resJson.toString();
        new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI, token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
            @Override
            public void onError(String response) {
//                ToastCustom.showToasty(context, response, 2);
            }

            @Override
            public void onResponse(String response, Boolean status, String strErrorMsg) {
                if (response != null) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response);
                        int result = jsonObject.getInt("intResult");
                        String warn = jsonObject.getString("txtMessage");
                        if (result == 1) {
                            if (!jsonObject.getString("ListData").equals("null")) {

                                recyclerView = (RecyclerView) v.findViewById(R.id.recyclerViewA);
                                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                                recyclerView.addItemDecoration(new LineItemDecoration(context, LinearLayout.VERTICAL));
                                recyclerView.setHasFixedSize(true);

                                JSONArray jsn = jsonObject.getJSONArray("ListData");
                                JSONArray array = jsn.getJSONArray(0);
                                List<clsActivityUser> itemsActivity = new ArrayList<>();
                                for (int n = 0; n < array.length(); n++) {
                                    JSONObject object = array.getJSONObject(n);
                                    clsActivityUser activityUser = new clsActivityUser();

                                    String intProgramAuditId = object.getString("intProgramAuditId");
                                    String intProgramAuditDetailId = object.getString("intProgramAuditDetailId");
                                    String category = object.getString("category");
                                    String activity = object.getString("activity");
                                    String dtStart = object.getString("dtStart");
                                    String dtEnd = object.getString("dtEnd");
                                    int intFlag = object.getInt("intFlag");
                                    String txtFlag = object.getString("txtFlag");
                                    int intStatus = object.getInt("intStatus");
                                    String txtStatus = object.getString("txtStatus");
                                    String dtVerified = object.getString("dtVerified");
                                    String txtNotes = object.getString("txtNotes");

                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//                                    "2018-09-06T00:00:00+07:00"
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                    Date datedtStart = null;
                                    Date datedtEnd = null;
                                    Date datedtVerified = null;
                                    try {
                                        datedtStart = inputFormat.parse(dtStart);
                                        datedtEnd = inputFormat.parse(dtEnd);
                                        if (dtVerified.equals("null")) {
                                            datedtVerified = inputFormat.parse(dtVerified);
                                            activityUser.setDtVerified(datedtVerified);
                                        }else{
                                            activityUser.setDtVerified(null);
                                        }
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    activityUser.setIntProgramAuditDetailId(intProgramAuditDetailId);
                                    activityUser.setIntProgramAuditId(intProgramAuditId);
                                    activityUser.setTxtStatus(txtStatus);
                                    activityUser.setActivity(activity);
                                    activityUser.setTxtCategory(category);
                                    activityUser.setDtEnd(datedtEnd);
                                    activityUser.setDtStart(datedtStart);
                                    activityUser.setIntFlag(intFlag);
                                    activityUser.setTxtFlag(txtFlag);
                                    activityUser.setIntStatus(intStatus);
                                    activityUser.setTxtNotes(txtNotes);
                                    itemsActivity.add(activityUser);

                                }
                                mAdapter = new AdapterListActivity(context, itemsActivity);
                                recyclerView.setAdapter(mAdapter);
                                mAdapter.setOnClickListener(new AdapterListActivity.OnClickListener() {
                                    @Override
                                    public void onItemClick(View view, clsActivityUser obj, int pos) {
                                        if (mAdapter.getSelectedItemCount() > 0) {
                                            enableActionMode(pos, obj);
                                        } else {
                                            // read the inbox which removes bold category the row
                                            clsActivityUser activityUser = mAdapter.getItem(pos);
//                                            Toast.makeText(context, "Read: " + activityUser.getTxtCategory(), Toast.LENGTH_SHORT).show();
                                            showCustomDialog(obj, TXT_EXIST);
                                        }
                                    }

                                    @Override
                                    public void onItemLongClick(View view, clsActivityUser obj, int pos) {
                                        enableActionMode(pos, obj);
                                    }
                                });

                                actionModeCallback = new ActionModeCallback();
                            }
                        } else {
//                                ToastCustom.showToasty(context,warn, 2);

                        }
                    } catch (JSONException ex) {
                        String x = ex.getMessage();
                        new clsMainActivity().showCustomToast(context,x,false);
                    }
                }
            }
        });
    }

    private void enableActionMode(int position, clsActivityUser obj) {
        if (obj.getIntFlag() == 2 && obj.getIntStatus() ==1) {
            listDeleteData.add(obj);
            if (actionMode == null) {
                AppCompatActivity activity = (AppCompatActivity) getActivity();
                actionMode = activity.startSupportActionMode(actionModeCallback);
//            actionMode = getActivity().startActionMode(actionModeCallback);
            }
            toggleSelection(position);
        }

    }

    private void toggleSelection(int position) {
        boolean bolToggle = mAdapter.toggleSelection(position);
        if (bolToggle && listDeleteData != null){
            listDeleteData.remove(position);
        }
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();


        }
    }
    public void refreshPage(){
        Fragment frg = null;
        frg = getFragmentManager().findFragmentByTag("Fragment_activity");
        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(frg);
        ft.attach(frg);
        ft.commit();
    }

    private void showCustomDialog(clsActivityUser user, int a) {
        final Dialog dialog = new Dialog(getActivity());
        Calendar cal = Calendar.getInstance();
        long d = cal.getTimeInMillis();

        if (a == TXT_NEW) {
            String userName = dataUserActive.getTxtUserName();
            dialog.setContentView(R.layout.dialog_add_event);
            dialog.setCancelable(true);

            final Spinner spnProgram = dialog.findViewById(R.id.spnProgram);
            final Spinner spnActivity = dialog.findViewById(R.id.spnActivity);
            final EditText et_notes = dialog.findViewById(R.id.et_notes);
            final TextView tv_name_user = dialog.findViewById(R.id.tv_name_user);
            spn_from_date = (Button) dialog.findViewById(R.id.spn_from_date);
//            spn_from_date.setText(Tools.getFormattedDateEvent(d));
            Calendar calNew = Calendar.getInstance();
            calNew.add(Calendar.DATE,1);
            year = calNew.get(Calendar.YEAR);
            month = calNew.get(Calendar.MONTH);
            day = calNew.get(Calendar.DATE);
            displayDate(spn_from_date, clsDatePicker.format.standard1);
            spn_from_date.setClickable(false);
            spn_from_date.setFocusable(false);
            tv_name_user.setText(dataUserActive.getTxtName());
            clsmConfig configData = null;
            try {
                configData = (clsmConfig) new clsmConfigRepo(context).findById(enumConfigData.API_EF.getidConfigData());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkGetProgram;
            final JSONObject resJson = new JSONObject();
            try {
                resJson.put("userName", userName);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            final String mRequestBody = resJson.toString();
            new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI,  token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
                @Override
                public void onError(String response) {

                }

                @Override
                public void onResponse(String response, Boolean status, String strErrorMsg) {
                    if (response != null) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int result = jsonObject.getInt("intResult");
                            String warn = jsonObject.getString("txtMessage");
                            if (result == 1) {
                                if (!jsonObject.getString("ListData").equals("null")) {
                                    JSONArray jsn = jsonObject.getJSONArray("ListData");
                                    listPrograms = new ArrayList<String>();
                                    HMProgram = new HashMap<>();
                                    listPrograms.add(TXT_SPN_DEFAULT);
                                    HMProgram.put(TXT_SPN_DEFAULT,"0");
                                    JSONArray array = jsn.getJSONArray(0);

                                    for (int n = 0; n < array.length(); n++) {
                                        JSONObject object = array.getJSONObject(n);
                                        String IntProgramAuditSubActivityId = object.getString("IntProgramAuditSubActivityId");
                                        String IntSubDetailActivityId = object.getString("IntSubDetailActivityId");
                                        String TxtDescription = object.getString("TxtDescription");
                                        listPrograms.add(TxtDescription);
                                        HMProgram.put(TxtDescription,IntProgramAuditSubActivityId);
                                    }
                                    SpinnerCustom.setAdapterSpinner(spnProgram, context, R.layout.custom_spinner, listPrograms);
//                                    SpinnerCustom.setAdapterSpinner(spnActivity, context, R.layout.custom_spinner, listDefault);
                                }
                            }else{


                            }
                        }catch (JSONException ex){
                            String x = ex.getMessage();
                        }
                        spnProgram.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                String programName = spnProgram.getSelectedItem().toString();
                                String id = HMProgram.get(programName);
                                clsmConfig configData = null;
                                try {
                                    configData = (clsmConfig) new clsmConfigRepo(context).findById(enumConfigData.API_EF.getidConfigData());
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                                final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkGetActivitySpn;
                                final JSONObject resJson = new JSONObject();
                                try {
                                    resJson.put("id", id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final String mRequestBody = resJson.toString();
                                if(!id.equals("0")){
                                    new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI, token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
                                        @Override
                                        public void onError(String response) {
//                                        ToastCustom.showToasty(context, response, 2);
                                        }

                                        @Override
                                        public void onResponse(String response, Boolean status, String strErrorMsg) {
                                            if (response != null) {
                                                JSONObject jsonObject = null;
                                                try {
                                                    jsonObject = new JSONObject(response);
                                                    int result = jsonObject.getInt("intResult");
                                                    String warn = jsonObject.getString("txtMessage");
                                                    if (result == 1) {
                                                        if (!jsonObject.getString("ListData").equals("null")) {
                                                            JSONArray jsn = jsonObject.getJSONArray("ListData");
                                                            JSONArray array = jsn.getJSONArray(0);
                                                            listDataActivity = new ArrayList<String>();
                                                            listDataActivity.add(TXT_SPN_DEFAULT);
                                                            HMActivity = new HashMap<>();
                                                            HMActivity.put(TXT_SPN_DEFAULT,"0");
                                                            for (int n = 0; n < array.length(); n++) {
                                                                JSONObject object = array.getJSONObject(n);
                                                                String IntSubDetailActivityId = object.getString("IntSubDetailActivityId");
                                                                String IntProgramAuditSubActivityId = object.getString("IntProgramAuditSubActivityId");
                                                                String TxtDescription = object.getString("TxtDescription");
                                                                listDataActivity.add(TxtDescription);
                                                                HMActivity.put(TxtDescription,IntProgramAuditSubActivityId);
                                                            }
                                                            SpinnerCustom.setAdapterSpinner(spnActivity, context, R.layout.custom_spinner, listDataActivity);
                                                        }
                                                    }else{
//                                                    ToastCustom.showToasty(context,warn, 2);

                                                    }
                                                }catch (JSONException ex){
                                                    String x = ex.getMessage();
                                                }
                                            }
                                        }
                                    });
                                }else{
                                    List<String> listDefault = new ArrayList<>();
                                    listDefault.add(TXT_SPN_DEFAULT);
                                    HMActivity = new HashMap<>();
                                    HMActivity.put(TXT_SPN_DEFAULT,"0");
                                    SpinnerCustom.setAdapterSpinner(spnActivity, context, R.layout.custom_spinner, listDefault);
                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

                        final AppCompatButton bt_submit = (AppCompatButton) dialog.findViewById(R.id.bt_submit);
                        ((EditText) dialog.findViewById(R.id.et_notes)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                bt_submit.setEnabled(!s.toString().trim().isEmpty());
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        spn_from_date.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ///dialogDatePickerLight((Button) v);
                                dialogDateMaterial((Button) v);
                            }
                        });
                        bt_submit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                                alertDialog.setTitle("Alert");
                                alertDialog.setMessage("Are you sure ?");
                                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {


                                            }
                                        });

                                alertDialog.show();
                                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String program = spnProgram.getSelectedItem().toString();
                                        String activity = spnActivity.getSelectedItem().toString();
                                        String idProgram = HMProgram.get(program);
                                        String idActivity = HMActivity.get(activity);
                                        String dates = spn_from_date.getText().toString();

                                        boolean valid = true;
                                        String txtMessage = "";

                                        if (idActivity.equals("0") || idProgram.equals("0")){
                                            valid = false;
                                            txtMessage = "choose Program and Activity";
                                        }
                                        String notes = et_notes.getText().toString().replace(" ","");
                                        if (notes.equals("")){
                                            valid = false;
                                            if(txtMessage.equals("")){
                                                txtMessage = "Notes empty !";
                                            }

                                        }
                                        if (!valid){
                                            new clsMainActivity().showCustomToast(context,txtMessage,false);
                                        }else{
                                            clsmConfig configData = null;
                                            try {
                                                configData = (clsmConfig) new clsmConfigRepo(context).findById(enumConfigData.API_EF.getidConfigData());
                                            } catch (SQLException e) {
                                                e.printStackTrace();
                                            }
                                            final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkSaveNewUnplanActivity;
                                            final JSONObject resJson = new JSONObject();
                                            try {
                                                resJson.put("userName", dataUserActive.getTxtUserName());
                                                resJson.put("nik", dataUserActive.getTxtUserID());
                                                resJson.put("program", program);
                                                resJson.put("activity", activity);
                                                resJson.put("idProgram", idProgram);
                                                resJson.put("idActivity", idActivity);
                                                resJson.put("notes", et_notes.getText().toString());
                                                resJson.put("dates", dates);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            final String mRequestBody = resJson.toString();
                                            new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI, token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
                                                @Override
                                                public void onError(String response) {
                                                    new clsMainActivity().showCustomToast(context,response,false);
                                                }

                                                @Override
                                                public void onResponse(String response, Boolean status, String strErrorMsg) {
                                                    if (response != null) {
                                                        JSONObject jsonObject = null;
                                                        try {
                                                            jsonObject = new JSONObject(response);
                                                            int result = jsonObject.getInt("intResult");
                                                            String warn = jsonObject.getString("txtMessage");
                                                            if (result == 1) {
                                                                dialog.dismiss();
                                                                alertDialog.dismiss();
                                                                refreshPage();
                                                            }else{
                                                                new clsMainActivity().showCustomToast(context,warn,false);
                                                                alertDialog.dismiss();
                                                            }
                                                        }catch (JSONException ex){
                                                            String x = ex.getMessage();
                                                        }
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        });

                        dialog.show();
                        dialog.getWindow().setAttributes(lp);
                    }
                }
            });

        } else {
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
            dialog.setContentView(R.layout.dialog_exist_event);
            dialog.setCancelable(true);
            EditText et_date = (EditText) dialog.findViewById(R.id.et_date_exist);
            EditText et_category = (EditText) dialog.findViewById(R.id.et_category);
            EditText et_activity = (EditText) dialog.findViewById(R.id.et_activity);
            EditText et_status = (EditText) dialog.findViewById(R.id.et_status);
            EditText et_notes = (EditText) dialog.findViewById(R.id.et_notes);
            TextView tv_name_user = dialog.findViewById(R.id.tv_name_user);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
            String category = user.getTxtCategory();
            et_category.setText(category);
            String activity = user.getActivity();
            et_activity.setText(activity);
            tv_name_user.setText(dataUserActive.getTxtName());
            String strDtStart = outputFormat.format(user.getDtStart());
            String strDtEnd = outputFormat.format(user.getDtEnd());
            et_date.setText(strDtStart +" / "+strDtEnd);
            String txtnt = "";
            if (!user.getTxtNotes().equals("null")){
                txtnt = user.getTxtNotes();
            }
            et_notes.setText(txtnt);
            int intStatus = user.getIntStatus();
            String dtVerified = "";
            if (intStatus != 1){
                dtVerified = outputFormat.format(user.getDtVerified());
            }
            et_status.setText(user.getTxtStatus()+" "+dtVerified);

            final AppCompatButton bt_submit = (AppCompatButton) dialog.findViewById(R.id.bt_submit);

            bt_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);
        }

    }
    private static void displayDate(Button btn, int format) {
        GregorianCalendar c = new GregorianCalendar(year, month, day);
        String date = formatSimpleDate(c.getTime(), format);
        btn.setText(date);
    }
    private void dialogDateMaterial(final Button bt){

        OnSelectDateListener ondate = new OnSelectDateListener() {
            @Override
            public void onSelect(List<Calendar> calendar) {
//            int a = 10;
                Calendar calSelected = calendar.get(0);
                year = calSelected.get(Calendar.YEAR);
                month = calSelected.get(Calendar.MONTH);
                day = calSelected.get(Calendar.DATE);
                displayDate(bt, clsDatePicker.format.standard1);
                bt.setEnabled(true);
            }

        };

        DatePickerBuilder oneDayBuilder = new DatePickerBuilder(getActivity(), ondate)
                .pickerType(CalendarView.ONE_DAY_PICKER)
                .date(Calendar.getInstance())
                .headerColor(R.color.colorPrimaryDark)
                .headerLabelColor(R.color.currentMonthDayColor)
                .selectionColor(R.color.daysLabelColor)
//                .todayLabelColor(R.color.colorAccent)
                .dialogButtonsColor(android.R.color.holo_green_dark)
//                .disabledDaysLabelsColor(android.R.color.holo_red_light)
//                .disabledDays(dts)
                .disabledDaysLabelsColor(android.R.color.holo_red_light);
//                .previousButtonSrc(R.drawable.ic_chevron_left_black_24dp)
//                .forwardButtonSrc(R.drawable.ic_chevron_right_black_24dp)


        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        a = Calendar.getInstance();
        b.add(Calendar.DATE, 3);
        oneDayBuilder.minimumDate(a);
        oneDayBuilder.maximumDate(b);
        String DATE_MAX = "date_max";
        String DATE_MIN = "date_min";
        DatePicker oneDayPicker = oneDayBuilder.build();
        oneDayPicker.show();
    }

    private void dialogDatePickerLight(final Button bt) {
        Calendar cur_calender = Calendar.getInstance();
        DatePickerDialog datePicker = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        long date_ship_millis = calendar.getTimeInMillis();
                        bt.setText(Tools.getFormattedDateEvent(date_ship_millis));
                    }
                },
                cur_calender.get(Calendar.YEAR),
                cur_calender.get(Calendar.MONTH),
                cur_calender.get(Calendar.DAY_OF_MONTH)
        );
        //set dark light
        datePicker.setThemeDark(false);
        datePicker.setAccentColor(getResources().getColor(R.color.colorPrimary));
        datePicker.setMinDate(cur_calender);
        datePicker.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void showDialogFullscreen() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        DialogFullscreenFragment newFragment = new DialogFullscreenFragment();
        newFragment.setRequestCode(DIALOG_QUEST_CODE);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
        newFragment.setOnCallbackResult(new DialogFullscreenFragment.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Object obj) {
                if (requestCode == DIALOG_QUEST_CODE) {
                    displayDataResult((Event) obj);
                }
            }
        });
    }

    private void displayDataResult(Event event) {
//        ((TextView) v.findViewById(R.id.tv_email)).setText(event.activityDesc);
//        ((TextView) v.findViewById(R.id.tv_name)).setText(event.name);
//        ((TextView) v.findViewById(R.id.tv_location)).setText(event.location);
//        ((TextView) v.findViewById(R.id.tv_from)).setText(event.category);
//        ((TextView) v.findViewById(R.id.tv_to)).setText(event.to);
//        ((TextView) v.findViewById(R.id.tv_allday)).setText(event.is_allday.toString());
//        ((TextView) v.findViewById(R.id.tv_timezone)).setText(event.timezone);
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(getActivity(), R.color.blue_grey_700);
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteList();
                mode.finish();
                return true;
            }
            return false;
        }

        private void deleteList() {
            /*List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
            for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {

                mAdapter.removeData(selectedItemPositions.get(i));
            }
            mAdapter.notifyDataSetChanged();*/

            clsmConfig configData = null;
            List<JSONObject> listJsonDeleted = new ArrayList<>();
            Collection<JSONObject> itemsListJquey = new ArrayList<JSONObject>();
            for (clsActivityUser data : listDeleteData){
                JSONObject resJsonListDeleted = new JSONObject();
                try {
                    resJsonListDeleted.put("id",data.getIntProgramAuditDetailId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                itemsListJquey.add(resJsonListDeleted);
            }

            try {
                configData = (clsmConfig) new clsmConfigRepo(context).findById(enumConfigData.API_EF.getidConfigData());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkDeleteListOutstanding;
            final JSONObject resJson = new JSONObject();

            try {
                resJson.put("data", new JSONArray(itemsListJquey));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String json = resJson.toString();
//            json = json.replaceAll("\\\\","");
            final String mRequestBody = resJson.toString();
            new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI, token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
                @Override
                public void onError(String response) {
                    new clsMainActivity().showCustomToast(context,response,false);
                }

                @Override
                public void onResponse(String response, Boolean status, String strErrorMsg) {
                    if (response != null) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            int result = jsonObject.getInt("intResult");
                            String warn = jsonObject.getString("txtMessage");
                            if (result == 1) {
                                refreshPage();
                                /*if (!jsonObject.getString("ListData").equals("null")) {
                                    JSONArray jsn = jsonObject.getJSONArray("ListData");
                                    JSONArray array = jsn.getJSONArray(0);
                                    for (int n = 0; n < array.length(); n++) {
                                        JSONObject object = array.getJSONObject(n);
                                        String txtDiscount = object.getString("decDiscount");
                                    }
                                }*/
                            }else{
                                new clsMainActivity().showCustomToast(context,response,false);

                            }
                        }catch (JSONException ex){
                            String x = ex.getMessage();
                        }
                    }
                }
            });


        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            listDeleteData = new ArrayList<>();
            actionMode = null;
            Tools.setSystemBarColor(getActivity(), R.color.red_600);
        }
    }
}

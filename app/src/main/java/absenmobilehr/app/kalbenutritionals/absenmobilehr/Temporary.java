package absenmobilehr.app.kalbenutritionals.absenmobilehr;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by Robert on 28/02/2018.
 */

public class Temporary extends AppCompatActivity {


    /*Button btnBro;
        clsmConfig configData = null;
        try {
            configData = (clsmConfig) new clsmConfigRepo(getApplicationContext()).findById(enumConfigData.API_EF.getidConfigData());
        } catch (SQLException e) {
            e.printStackTrace();
        }

    final String strLinkAPI = configData.getTxtValue() + new clsHardCode().linkLogin;
    final JSONObject resJson = new JSONObject();
                    try {
                        resJson.put("txtPhone", "");
                        resJson.put("txtMember", keyword);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                final String mRequestBody = resJson.toString();
                new VolleyUtils().makeJsonObjectRequestWithToken(getActivity(), strLinkAPI, token, mRequestBody, "Please Wait...", new VolleyResponseListener() {
                    @Override
                    public void onError(String response) {
                        ToastCustom.showToasty(context, response, 2);
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
                                                                    for (int n = 0; n < array.length(); n++) {
                                                                            JSONObject object = array.getJSONObject(n);
                                                                            String txtDiscount = object.getString("decDiscount");
                                                                    }
                                                                }
                                                            }else{
                                                                ToastCustom.showToasty(context,warn, 2);

                                                            }
                                                        }catch (JSONException ex){
                                                            String x = ex.getMessage();
                                                        }
                        }
                    }
                });
            }
*/

}

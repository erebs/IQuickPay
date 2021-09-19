package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoreActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;

    List<RecentDthRechargeModel> mobileDthModels = new ArrayList<>(1000);
    private RecentDthRechargeAdaptor recentDthRechargeAdaptor;
    RecyclerView mobileDthRecyclerView;
    KProgressHUD Loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        mobileDthRecyclerView = findViewById(R.id.ComRecyclerView);
        recentDthRechargeAdaptor = new RecentDthRechargeAdaptor(getApplication());
        mobileDthRecyclerView.setAdapter(recentDthRechargeAdaptor);
        mobileDthRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mobileDthRecyclerView.setNestedScrollingEnabled(false);
        LoginApi();

    }

    public void LoginApi()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/commission?user_id="+sharedPreferences.getString("id","");
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("VOLLEYES", response);
                        try {
                            Loader.dismiss();
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {

                                String data = Res.getString("plan");
                                JSONArray Results = new JSONArray(data);
                                mobileDthModels.clear();
                                for (int i = 0; i < Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RecentDthRechargeModel recentDthRechargeModel = new RecentDthRechargeModel();
                                    recentDthRechargeModel.setProvider(rst.getString("operatorname"));
                                    recentDthRechargeModel.setDis(rst.getString("opetatortypename"));
                                    recentDthRechargeModel.setStatus(rst.getString("amount")+"%");
                                    mobileDthModels.add(recentDthRechargeModel);
                                }
                                recentDthRechargeAdaptor.renewItems(mobileDthModels);

                            }
                            else
                            Toast.makeText(MoreActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(MoreActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null)
                        {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();
                            Toast.makeText(MoreActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username","");
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);
    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(MoreActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

}
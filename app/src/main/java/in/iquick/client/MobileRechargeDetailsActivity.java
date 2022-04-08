package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
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

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MobileRechargeDetailsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String RechargeID;
    TextView RechID,RechMobile,Operator,Type,Offer,Amt,Date,Status;
    KProgressHUD Loader;

    TextView aSts,aCmts;
    LinearLayout aBox, stsBox;
    EditText cCmts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge_details);

        aSts = findViewById(R.id.a_sts);
        aCmts = findViewById(R.id.a_comments);
        aBox = findViewById(R.id.aBox);
        stsBox = findViewById(R.id.stsBox);
        cCmts  = findViewById(R.id.c_comments);

        RechID = findViewById(R.id.RechargeID_details);
        RechMobile = findViewById(R.id.RechMob_details);
        Operator = findViewById(R.id.RechOp_details);
        Type = findViewById(R.id.RechType_details);
        Offer = findViewById(R.id.RechOffType_details);
        Amt = findViewById(R.id.RechAmount_details);
        Date = findViewById(R.id.RechDT_details);
        Status = findViewById(R.id.RechSts_details);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        try {
            Intent intent = getIntent();
            RechargeID=intent.getStringExtra("RechargeID");
            KsebRechargeApi();
            //Toast.makeText(this, RechargeID, Toast.LENGTH_SHORT).show();
        }catch (Exception e)
        {
        }

        GetDesputeApi();

    }

    public void ShareApp(View view)
    {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Greetings from IQUICK\n Your Mobile Recharge of Rs."+Amt.getText().toString()+" towards the number "+RechMobile.getText().toString()+" was successful.");
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


    public void KsebRechargeApi()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/recharge?rech_id="+RechargeID;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);
                        Loader.dismiss();
                        try {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {

                                String data = Res.getString("rdata");
                                JSONObject rst = new JSONObject(data);
                                RechID.setText("#"+rst.getString("apitransid")+" ("+RechargeID+")");
                                RechMobile.setText(rst.getString("number"));
                                Type.setText(rst.getString("type"));
                                Operator.setText(rst.getString("operatorname"));
                                Offer.setText(rst.getString("circlename"));
                                Amt.setText("₹"+rst.getString("amount"));
                                Date.setText(rst.getString("created_at"));
                                Status.setText(rst.getString("status"));
                            }else
                            {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();
                            Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mreach_id",RechargeID);
                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }

    public void GetDesputeApi()
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"get/dispute?rech_id="+RechargeID;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @SuppressLint("UseCompatLoadingForDrawables")
                    @Override
                    public void onResponse(String response) {
                        findViewById(R.id.dBox).setVisibility(View.VISIBLE);


                        Log.i("VOLLEYES", response);
                        try {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                aBox.setVisibility(View.VISIBLE);
                                stsBox.setVisibility(View.VISIBLE);

                                String data = Res.getString("dispute");
                                if(!data.equalsIgnoreCase("null")) {
                                    JSONObject rst = new JSONObject(data);
                                    String ssssts =rst.getString("status");
                                    
                                    if(ssssts.equalsIgnoreCase("New"))
                                        aSts.setBackground(getDrawable(R.drawable.neww));
                                    else if(ssssts.equalsIgnoreCase("Completed"))
                                        aSts.setBackground(getDrawable(R.drawable.success));
                                    else
                                        aSts.setBackground(getDrawable(R.drawable.danger));

                                    aSts.setText(ssssts);
                                    cCmts.setText(rst.getString("desc"));
                                    String acm = rst.getString("comment");
                                    if (acm.length() > 0)
                                        aBox.setVisibility(View.VISIBLE);
                                    else
                                        aBox.setVisibility(View.GONE);
                                    aCmts.setText(acm);
                                }
                                else
                                {
                                    aBox.setVisibility(View.GONE);
                                    stsBox.setVisibility(View.GONE);
                                }
                            }else
                            {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mreach_id",RechargeID);
                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }


    public void SubmitBtn(View view)
    {
        if (cCmts.length()>0)
            CreateDespute(cCmts.getText().toString());
        else
            Toast.makeText(getApplicationContext(), "Comment Box can't be empty!", Toast.LENGTH_SHORT).show();
    }

    public void CreateDespute(final String cmts)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"create/dispute?user_id="+sharedPreferences.getString("id","")
                +"&rech_id="+RechargeID+"&desc="+cmts+"&number="+RechMobile.getText().toString();
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);
                        Loader.dismiss();
                        try {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                GetDesputeApi();
                            }else
                            {
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

                        }


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if(response != null && response.data != null){
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();
                            Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",sharedPreferences.getString("id",""));
                params.put("rech_id",RechargeID);
                params.put("number",RechMobile.getText().toString());
                params.put("desc",cmts);

                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }

}
package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge_details);

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

    public void goBack(View view)
    {
        super.onBackPressed();
    }


    public void complaintBtn(View view)
    {ChangeStatus("Refresh");}

    public void ChangeStatus(final String ssts)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"mobilereachargestatus";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
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

                                KsebRechargeApi();
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
                params.put("status",ssts);

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
package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
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

import de.mateware.snacky.Snacky;
import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class dthRechargeActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener {

    TextView Operator,PlanText;
    ModalBottomSheetDialog modalBottomSheetDialog;
    EditText cusID,Amount;
    KProgressHUD Loader;
    ScrollView scrollView;
    Button RechargeBtn;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dth_recharge);

        RechargeBtn = findViewById(R.id.RechargeBtn_dth);
        PlanText = findViewById(R.id.PlanText_dth);
        Operator = findViewById(R.id.OperatorText_dth);

        cusID = findViewById(R.id.cusID);
        Amount = findViewById(R.id.Amount_dth);
        scrollView = findViewById(R.id.scrollView_dth);
        RechargeBtn.setEnabled(false);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        cusID.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>4)
                {
                    RechargeBtn.setEnabled(true);
                }
                else
                    RechargeBtn.setEnabled(false);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });


    }


    public void RechargeBtn_dth(View view)
    {
        if (cusID.length()>4 && Operator.length()>0 && Amount.length()>0)
        {
            dthRechargeApi();
        }
        else
        {
            Snacky.builder()
                    .setActivity(dthRechargeActivity.this)
//                .setActionText("ACTION")
                    .setActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do something
                        }
                    })
                    .setText("Some required fields are missing!")
                    .setDuration(Snacky.LENGTH_LONG)
                    .build()
                    .show();
        }
    }

    public void CustInfoBtn(View view)
    {
        if (cusID.length()>3 && Operator.length()>0)
        {
            getCustInfo(cusID.getText().toString(),getProviderCode(Operator.getText().toString()));
        }
        else
        {
            Snacky.builder()
                    .setActivity(dthRechargeActivity.this)
//                .setActionText("ACTION")
                    .setActionClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //do something
                        }
                    })
                    .setText("Some required fields are missing!")
                    .setDuration(Snacky.LENGTH_LONG)
                    .build()
                    .show();
        }

    }

    public void dthOperator(View view)
    {

            modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                    .setHeader("Select Operator")
                    .add(R.menu.dth)
                    .build();

        modalBottomSheetDialog.show(getSupportFragmentManager(), "DTH");

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(String tag, Item item) {


                getCustInfo(cusID.getText().toString(),getProviderCode(item.getTitle().toString()));
                Operator.setText(item.getTitle().toString());
        modalBottomSheetDialog.dismiss();
        //  Toast.makeText(this, CircleText.getText().toString()+"-"+OperatorText.getText().toString(), Toast.LENGTH_SHORT).show();
    }

    public String getProviderCode(String Provider)
    {
        String ProviderCode = "";
        if(Provider.equalsIgnoreCase("TataSky"))
        {
            ProviderCode = "TSK";
        } if(Provider.equalsIgnoreCase("Sun Direct"))
    {
        ProviderCode = "SUN";
    }
    else if(Provider.equalsIgnoreCase("Dish TV"))
    {
        ProviderCode = "DSH";
    }
    else if(Provider.equalsIgnoreCase("VideoCon D2H"))
    {
        ProviderCode = "VDH";
    }
    else if(Provider.equalsIgnoreCase("Airtel DTH"))
    {
        ProviderCode = "ART";
    }
        return ProviderCode;
    }

    public String getProviderCode2(String Provider)
    {
        String ProviderCode = "";
        if(Provider.equalsIgnoreCase("TataSky"))
        {
            ProviderCode = "TSD";
        } if(Provider.equalsIgnoreCase("Sun Direct"))
    {
        ProviderCode = "SD";
    }
    else if(Provider.equalsIgnoreCase("Dish TV"))
    {
        ProviderCode = "DTD";
    }
    else if(Provider.equalsIgnoreCase("VideoCon D2H"))
    {
        ProviderCode = "VDD";
    }
    else if(Provider.equalsIgnoreCase("Airtel DTH"))
    {
        ProviderCode = "ATD";
    }
        return ProviderCode;
    }

    void StartCount()
    {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Loader.dismiss();

                Intent diya = new Intent(dthRechargeActivity.this, SuccessActivity.class);
                diya.putExtra("Type","DTH");
                diya.putExtra("Number",cusID.getText().toString());
                diya.putExtra("Amount",Amount.getText().toString());
                diya.putExtra("Status","Pending");
                startActivity(diya);
                finish();

            }
        }.start();
    }

    public void dthRechargeApi()
    {
        Loader.show();
        StartCount();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/recharge/new?type=DTH&user_id="+sharedPreferences.getString("id","")+"&circle=&mobile="+cusID.getText().toString()+"&operator="+getProviderCode2(Operator.getText().toString())+"&amount="+Amount.getText().toString();
        Log.e("DTH_URL",URL);
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
                params.put("customer",cusID.getText().toString());
                Log.i("loginp ", params.toString());

                return params;
            }

        };


        // Add the realibility on the connection.
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 0, 1.0f));

        // Start the request immediately
        queue.add(request);

    }


    public void getCustInfo( String CustID, String OpCode)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.dthcusinfo)+"&operator_code="+OpCode+"&customer_id="+CustID;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader.dismiss();
                            JSONObject Res=new JSONObject(response);
                            if(Res.getString("status").equalsIgnoreCase("true")) {
                                String data = Res.getString("data");

                                JSONObject rst = new JSONObject(data);
                                PlanText.setText("Customer Name: "+rst.getString("customer_name")+"\n"+"Current Plan: "+ rst.getString("customer_plan_name")+"("+rst.getString("customer_plan_amount")+")\n"+
                                        "Current Balance: "+rst.getString("customer_balance")+"\nNext Recharge Date: "+rst.getString("next_recharge_date"));
                                Amount.setText(rst.getString("customer_plan_amount"));
                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            Toast.makeText(getApplicationContext(), "Network Error: "+e, Toast.LENGTH_SHORT).show();


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
                            Toast.makeText(getApplicationContext(), "Network Error: "+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username","username");
                params.put("password","password");

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
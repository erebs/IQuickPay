package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.LinearLayout;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.mateware.snacky.Snacky;
import dev.jai.genericdialog2.GenericDialog;
import dev.jai.genericdialog2.GenericDialogOnClickListener;
import nl.invissvenska.modalbottomsheetdialog.Item;
import nl.invissvenska.modalbottomsheetdialog.ModalBottomSheetDialog;

public class MobileRechargeActivity extends AppCompatActivity implements ModalBottomSheetDialog.Listener,FruitAdapter1.getAmt{

    String type,proid,provider;
    TextView title,OperatorText,CircleText;
    LinearLayout PlanText;
    ModalBottomSheetDialog modalBottomSheetDialog;
    EditText mobileNumber,Amount;
    KProgressHUD Loader;
    ScrollView scrollView;
    Button RechargeBtn;
    SharedPreferences sharedPreferences;
    String Cat="Top up";
    Boolean isTopup = true;
    
    List<FruitModel1> list1 = new ArrayList<>(1000);
    private FruitAdapter1 PlacesAdapter;
    RecyclerView placesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_recharge);

        RechargeBtn = findViewById(R.id.RechargeBtn);
        PlanText = findViewById(R.id.PlanText);
        title = findViewById(R.id.rechargeType);
        OperatorText = findViewById(R.id.OperatorText);
        CircleText = findViewById(R.id.CircleText);
        mobileNumber = findViewById(R.id.MobileNumber);
        Amount = findViewById(R.id.Amount);
        scrollView = findViewById(R.id.scrollViewnew);
        RechargeBtn.setEnabled(false);
        placesView = (RecyclerView) findViewById(R.id.placesViewFrag);
        placesView.setNestedScrollingEnabled(false);
        this.PlacesAdapter = new FruitAdapter1(this);
        placesView.setAdapter(PlacesAdapter);
        placesView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        try {
            Intent intent = getIntent();
            type=intent.getStringExtra("type");
            if(type.equalsIgnoreCase("Prepaid")) {
                title.setText("Mobile Recharge");
            }
            else
                title.setText("Postpaid Bill Payment");


        }catch (Exception e)
        {
            title.setText("Mobile Recharge");
        }


        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()==10)
                {
                        AutoFetch(s.toString());
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

    @Override
    public void Plan(String amt) {

        Amount.setText(amt);
        scrollView.scrollTo(0,scrollView.getTop());
    }

    void StartCount()
    {
        new CountDownTimer(10000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Loader.dismiss();

    Intent diya = new Intent(MobileRechargeActivity.this, SuccessActivity.class);
                                diya.putExtra("Type",type);
                                diya.putExtra("Number",mobileNumber.getText().toString());
                                diya.putExtra("Amount",Amount.getText().toString());
                                diya.putExtra("Status","Pending");
    startActivity(diya);
    finish();

            }
        }.start();
    }

    public void RechargeBtn(View view)
    {
        if (mobileNumber.length()==10 && OperatorText.length()>0 && CircleText.length()>0 && Amount.length()>0)
        {
            MobileRechargeApi();
            //StartCount();
        }
        else
        {
            Snacky.builder()
                    .setActivity(MobileRechargeActivity.this)
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

    public void Topup(View view)
    {
        Cat="Top up";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Topup).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void FullTalktime(View view)
    {
        Cat="Full Talktime";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.FullTalktime).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void SMS(View view)
    {
        Cat="SMS";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.SMS).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G2Data(View view)
    {
        Cat="2G Data";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.G2Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G3Data(View view)
    {
        Cat="3G Data";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void G4Data(View view)
    {
        Cat="4G%20Data";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.G4Data).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Local(View view)
    {
        Cat="Local";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Local).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void STD(View view)
    {
        Cat="STD";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.STD).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void ISD(View view)
    {
        Cat="ISD";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.ISD).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Roaming(View view)
    {
        Cat="Roaming";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Roaming).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Other(View view)
    {
        Cat="Other";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Other).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Validity(View view)
    {
        Cat="Validity";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Validity).setBackground(getDrawable(R.drawable.ripple_box));
    }
    public void Plan(View view)
    {
        Cat="Plan";
         ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Plan).setBackground(getDrawable(R.drawable.ripple_box));
    }

    public void TopUpBtn(View view)
    {
        Cat="Top up";
        isTopup = true;
        ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.Topup).setBackground(getDrawable(R.drawable.ripple_box));
        findViewById(R.id.TopUpBtn).setBackground(getDrawable(R.drawable.lbtn));
        findViewById(R.id.SpecialBtn).setBackground(getDrawable(R.drawable.rbtnd));
        Cat="Top up";
        findViewById(R.id.plansSlider).setVisibility(View.GONE);
    }

    public void SpecialBtn(View view)
    {
        Cat="SMS";
        isTopup = false;
        ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
        clearBox();
        findViewById(R.id.SMS).setBackground(getDrawable(R.drawable.ripple_box));
        findViewById(R.id.TopUpBtn).setBackground(getDrawable(R.drawable.lbtnd));
        findViewById(R.id.SpecialBtn).setBackground(getDrawable(R.drawable.rbtn));
        Cat="SMS";
        findViewById(R.id.plansSlider).setVisibility(View.VISIBLE);
        findViewById(R.id.Topup).setVisibility(View.GONE);
        findViewById(R.id.FullTalktime).setVisibility(View.GONE);
    }

    void clearBox()
    {
        findViewById(R.id.Topup).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.FullTalktime).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.SMS).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G2Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G3Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.G4Data).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Local).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.STD).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.ISD).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Roaming).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Other).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Validity).setBackground(getDrawable(R.drawable.ripple));
        findViewById(R.id.Plan).setBackground(getDrawable(R.drawable.ripple));
    }

    public void Operator(View view)
    {
        if(type.equalsIgnoreCase("Prepaid")) {
            modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                    .setHeader("Select Operator")
                    .add(R.menu.prepaid)
                    .build();
        }
        else {
            modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                    .setHeader("Select Operator")
                    .add(R.menu.postpaid)
                    .build();
        }
        modalBottomSheetDialog.show(getSupportFragmentManager(), type);

    }

    public void Circle(View view)
    {
        modalBottomSheetDialog = new ModalBottomSheetDialog.Builder()
                .setHeader("Select Circle")
                .add(R.menu.circle)
                .build();
        modalBottomSheetDialog.show(getSupportFragmentManager(), "Circle");
    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(String tag, Item item) {
        if(tag.equalsIgnoreCase("Circle")) {
            CircleText.setText(item.getTitle().toString());
            if(type.equalsIgnoreCase("Prepaid")) {
                findViewById(R.id.plansSlider).setVisibility(View.VISIBLE);
                findViewById(R.id.Topup).setVisibility(View.VISIBLE);
                findViewById(R.id.FullTalktime).setVisibility(View.VISIBLE);
                findViewById(R.id.SpecialBox).setVisibility(View.GONE);
                if (isTopup && OperatorText.getText().toString().contains("BSNL")) {
                    Cat = "Top up";
                    findViewById(R.id.plansSlider).setVisibility(View.GONE);
                    findViewById(R.id.SpecialBox).setVisibility(View.VISIBLE);

                }
                if (!isTopup && OperatorText.getText().toString().contains("BSNL")) {
                    Cat = "SMS";
                    findViewById(R.id.plansSlider).setVisibility(View.VISIBLE);
                    findViewById(R.id.Topup).setVisibility(View.GONE);
                    findViewById(R.id.FullTalktime).setVisibility(View.GONE);
                    findViewById(R.id.SpecialBox).setVisibility(View.VISIBLE);

                }
                ViewPlans(OperatorText.getText().toString(), CircleText.getText().toString());
                PlanText.setVisibility(View.VISIBLE);
            }
        }
        else
            OperatorText.setText(item.getTitle().toString());
        modalBottomSheetDialog.dismiss();
      //  Toast.makeText(this, CircleText.getText().toString()+"-"+OperatorText.getText().toString(), Toast.LENGTH_SHORT).show();
    }
    
    public String getCircleCode(String Circle)
    {
        String CircleCode = "0";
        if(Circle.equalsIgnoreCase("Kerala"))
            CircleCode = "11";
        else if(Circle.equalsIgnoreCase("Tamil Nadu"))
            CircleCode = "20";
        else if(Circle.equalsIgnoreCase("Andhra Pradesh"))
            CircleCode = "1";
        else if(Circle.equalsIgnoreCase("Assam"))
            CircleCode = "2";
        else if(Circle.equalsIgnoreCase("Bihar & Jharkhand"))
            CircleCode = "3";
        else if(Circle.equalsIgnoreCase("Delhi"))
            CircleCode = "5";
        else if(Circle.equalsIgnoreCase("Uttar Pradesh"))
            CircleCode = "34";
        else if(Circle.equalsIgnoreCase("Gujarat"))
            CircleCode = "6";
        else if(Circle.equalsIgnoreCase("Haryana"))
            CircleCode = "7";
        else if(Circle.equalsIgnoreCase("Himachal Pradesh"))
            CircleCode = "8";
        else if(Circle.equalsIgnoreCase("Jammu and Kashmir"))
            CircleCode = "9";
        else if(Circle.equalsIgnoreCase("Karnataka"))
            CircleCode = "10";
        else if(Circle.equalsIgnoreCase("Kolkata"))
            CircleCode = "12";
        else if(Circle.equalsIgnoreCase("Mumbai"))
            CircleCode = "15";
        else if(Circle.equalsIgnoreCase("Rajasthan"))
            CircleCode = "19";
        else if(Circle.equalsIgnoreCase("Punjab"))
            CircleCode = "18";
        else if(Circle.equalsIgnoreCase("Chennai"))
            CircleCode = "4";
        else if(Circle.equalsIgnoreCase("GOA"))
            CircleCode = "28";
        return CircleCode;
    }

    public String getProviderCode(String Provider)
    {
        String ProviderCode = "";
        if(Provider.equalsIgnoreCase("Idea"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"ID":"IDP");
        } if(Provider.toLowerCase().contains("vi"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"VF":"VFP");
        }
        else if(Provider.toLowerCase().contains("airtel"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"AT":"ATP");
        }
        else if(Provider.toLowerCase().contains("jio"))
        {
            ProviderCode = (type.equalsIgnoreCase("Prepaid")?"JIO":"RJC");
        }
        else if(Provider.toLowerCase().contains("bsnl"))
        {
           if(isTopup)
               ProviderCode = (type.equalsIgnoreCase("Prepaid")?"BSNL":"BSNL");
           else
               ProviderCode = (type.equalsIgnoreCase("Prepaid")?"BSS":"BSNL");
        }

        return ProviderCode;
    }

    public String getProviderName(String Provider)
    {
        String ProviderCode = "";
    if(Provider.toLowerCase().contains("vi"))
    {
        ProviderCode = "Vi";
    }
    else if(Provider.toLowerCase().contains("airtel"))
    {
        ProviderCode = "Airtel";
    }
    else if(Provider.toLowerCase().contains("jio"))
    {
        ProviderCode = "Jio";
    }
    else if(Provider.toLowerCase().contains("bsnl"))
    {
        ProviderCode = "BSNL";
    }

        return ProviderCode;
    }


    public void AutoFetch(String mnum)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = "https://cycloneapi.com/v1/mfetch?number="+mnum;
        Log.e("URL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Response",response);
                        Loader.dismiss();
                        try {
                            JSONObject Res=new JSONObject(response);
                            String pro    = Res.getString("provider_name");
                            String crcl    = Res.getString("circle_name");
                            if (pro.toLowerCase().contains("bsnl"))
                            {
                                findViewById(R.id.SpecialBox).setVisibility(View.VISIBLE);
                            }else
                            {
                                findViewById(R.id.SpecialBox).setVisibility(View.GONE);
                            }
                            OperatorText.setText(getProviderName(pro));
                            if(type.equalsIgnoreCase("Prepaid")) {
                                findViewById(R.id.plansSlider).setVisibility(View.VISIBLE);
                                findViewById(R.id.Topup).setVisibility(View.VISIBLE);
                                findViewById(R.id.FullTalktime).setVisibility(View.VISIBLE);
                                if (isTopup && OperatorText.getText().toString().contains("BSNL")) {
                                    Cat = "Top up";
                                    findViewById(R.id.plansSlider).setVisibility(View.GONE);
                                }
                                if (!isTopup && OperatorText.getText().toString().contains("BSNL")) {
                                    Cat = "SMS";
                                    findViewById(R.id.plansSlider).setVisibility(View.VISIBLE);
                                    findViewById(R.id.Topup).setVisibility(View.GONE);
                                    findViewById(R.id.FullTalktime).setVisibility(View.GONE);
                                }
                                ViewPlans(OperatorText.getText().toString(), CircleText.getText().toString());
                                PlanText.setVisibility(View.VISIBLE);
                            }
                            CircleText.setText(crcl);
                            if(type.equalsIgnoreCase("Prepaid"))
                            ViewPlans(OperatorText.getText().toString(),CircleText.getText().toString());
                        }catch (Exception e)
                        {}
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

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                //  params.put("shop_id",sharedPreferences.getString("shop_id",""));
                params.put("limit","50");
                Log.i("loginp ", params.toString());

                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(3000, 0, 1.0f));
        queue.add(request);

    }

    public void ViewPlans(String opid,String circle)
    {

        Loader.show();
        String oprt = (getProviderName(opid)=="Vi"?"Vodafone":getProviderName(opid));
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.tdapi_urlmrplans)+"&operator_id="+oprt+"&circle_id="+circle+"&recharge_type="+Cat;
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response+"--"+URL);
                        list1.clear();
                        try {
                           Loader.dismiss();
                            JSONObject Res=new JSONObject(response);
                            String data    = Res.getString("data");
                            JSONArray Results = new JSONArray(data);


                            for(int i=0; i<Results.length(); i++) {
                                String Result = Results.getString(i);
                                JSONObject rst = new JSONObject(Result);
                                FruitModel1 fruitModel1 = new FruitModel1();
                                fruitModel1.setAmt(rst.getString("recharge_amount"));
                                fruitModel1.setDis(rst.getString("recharge_long_desc"));
                                fruitModel1.setVal("Validity: "+rst.getString("recharge_validity"));
                                list1.add(fruitModel1);}
                            PlacesAdapter.renewItems(list1);


                        }catch (Exception e){
                            Log.e("catcherror",e+"d");
                          //  Toast.makeText(PlansActivity.this, "No Plans available on this category!", Toast.LENGTH_SHORT).show();
                            PlacesAdapter.renewItems(list1);
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
                           // Toast.makeText(PlansActivity.this, "Network Error: "+errorString, Toast.LENGTH_SHORT).show();

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

    public void MobileRechargeApi()
    {
        Loader.show();
        RechargeBtn.setEnabled(false);
        StartCount();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/recharge/new?type="+type+"&user_id="+sharedPreferences.getString("id","")+"&mobile="+mobileNumber.getText().toString()+"&operator="+getProviderCode(OperatorText.getText().toString())+
                "&circle="+getCircleCode(CircleText.getText().toString())+"&amount="+Amount.getText().toString();
        Log.e("URL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,

                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Log.i("VOLLEYES", response);
                        Loader.dismiss();
                        try
                        {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");
                            if(sts.equalsIgnoreCase("01"))
                            {



                            }
                            else
                            {
                                RechargeBtn.setEnabled(true);
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                            }

                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"");
                            Toast.makeText(MobileRechargeActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MobileRechargeActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                Log.i("loginp ", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 0, 1.0f));
        queue.add(request);

    }



}
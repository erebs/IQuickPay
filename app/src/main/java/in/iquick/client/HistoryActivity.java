package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import org.json.JSONArray;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText numberSearchInput;
    List<RecentMobileRechargeModel> mobileRechargeModels = new ArrayList<>(1000);
    private RecentMobileRechargeAdaptor recentMobileRechargeAdaptor;
    RecyclerView mobileRechargeRecyclerView;
    ImageView From,All;
    TextView dateText;
    private int mYear, mMonth, mDay;
    String FromDate="",ToDate="",DateData="";
    Boolean isFromDate=false,isToDate=false,isRefreshOnly=false;
    String Status,fromdate="",todate="",District,Dvalue="";
    LinearLayout dateView;
    KProgressHUD Loader;
    String[] pages = {"10","20","50","100","200","500","1000"};
    BottomSheet.Builder builder;
    String Limit = "10";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        From = findViewById(R.id.from);
        dateText = findViewById(R.id.dateText);
        All = findViewById(R.id.all);
        dateView = findViewById(R.id.dateView);
        numberSearchInput = findViewById(R.id.numberSearchInput);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        mobileRechargeRecyclerView = findViewById(R.id.RechargeHistoryRecyclerView);
        recentMobileRechargeAdaptor = new RecentMobileRechargeAdaptor(getApplication());
        mobileRechargeRecyclerView.setAdapter(recentMobileRechargeAdaptor);
        mobileRechargeRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        mobileRechargeRecyclerView.setNestedScrollingEnabled(false);
        GetStatement();



        numberSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >0)
                {


                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {

                //SearchPlaces(s.toString());
                // filter your list from your input
                filter(s.toString());
                //you can use runnable postDelayed like 500 ms to delay search text
            }
        });

    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    void filter(String text){
        List<RecentMobileRechargeModel> temp = new ArrayList();
        for(RecentMobileRechargeModel recentMobileRechargeModel: mobileRechargeModels){
            //or use .equal(text) with you want equal match
            //use .toLowerCase() for better matches
            if(recentMobileRechargeModel.getMobile().toLowerCase().contains(text.toLowerCase())){
                temp.add(recentMobileRechargeModel);
            }
        }
        //update recyclerview
        recentMobileRechargeAdaptor.updateList(temp);
    }

    void getDate(final String Type)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(HistoryActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        DateData = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        if(Type.equalsIgnoreCase("From"))
                        {dateText.setText("From "+DateData);
                            isFromDate=true;
                            FromDate=DateData;
                            getDate("To");}
                        else
                        {dateText.setText(dateText.getText().toString()+" To "+DateData);
                            isToDate = true;
                            ToDate = DateData;
                            GetStatement();
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void All(View view)
    {
        isFromDate=isToDate=false;
        dateView.setVisibility(View.GONE);
        GetStatement();
    }

    public void From(View view)
    {
        dateText.setText("");
        dateView.setVisibility(View.VISIBLE);
        if (!isFromDate)
            getDate("From");

    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(HistoryActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void GetStatement()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            Status ="&status=";

        if(isFromDate)
        {if(isToDate)
        { fromdate = "&fdate="+FromDate+"&tdate="+ToDate; }
        else
        { fromdate = "&fdate=&tdate="; }
        }else
        { fromdate = "&fdate=&tdate="; }
        String URL = getString(R.string.api_url)+"shop/recharges?type="+Dvalue+"&user_id="+sharedPreferences.getString("id","")+"&limit="+Limit+"&offset=0"+Status+fromdate;
        // Toast.makeText(this, URL, Toast.LENGTH_SHORT).show();
        Log.e("URL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader.dismiss();
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");
                            mobileRechargeModels.clear();
                            if(sts.equalsIgnoreCase("01")) {
                                String data = Res.getString("rdata");
                                JSONArray Results = new JSONArray(data);


                                for (int i = 0; i < Results.length(); i++)
                                {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    RecentMobileRechargeModel mobileRechargeModel = new RecentMobileRechargeModel();
                                    mobileRechargeModel.setMobile(rst.getString("number")+" - ₹"+rst.getString("amount"));
                                    mobileRechargeModel.setProvider(rst.getString("operatorname"));
                                    mobileRechargeModel.setDis("("+rst.getString("type")+" - "+rst.getString("circlename")+") on " + rst.getString("created_at")+"\n#"+rst.getString("apitransid")+" ("+rst.getString("id")+")");
                                    mobileRechargeModel.setStatus(rst.getString("status").toUpperCase());
                                    mobileRechargeModel.setIdRecharge(rst.getString("id"));
                                    mobileRechargeModels.add(mobileRechargeModel);
                                }
                                recentMobileRechargeAdaptor.renewItems(mobileRechargeModels);

                            }
                        }catch (Exception e){
                            Log.e("catcherror",e+"d");


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
                params.put("shop_id",sharedPreferences.getString("shop_id",""));
                if(isFromDate)
                {if(isToDate)
                {
                    params.put("fromdate",FromDate);
                    params.put("todate",ToDate);
                }
                }
                if(isRefreshOnly)
                {
                    params.put("status","Refresh");
                }
                params.put("limit",Limit);
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


    public void NumbersBtn(View view)
    {
        {
            builder = new BottomSheet.Builder(this);
            builder.setTitle("Choose numbers");
            builder.setContentType(BottomSheet.LIST);
            builder.setItems(pages, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Limit = pages[which];
                    GetStatement();

                }
            });
            builder.show();
        }
    }

}
package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WalletActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    List<StatementModel> statementModels = new ArrayList<>(1000);
    private StatementAdaptor statementAdaptor;
    RecyclerView statementRecyclerView;
    private int mYear, mMonth, mDay;
    String FromDate="",ToDate="",DateData="";
    Boolean isFromDate=false,isToDate=false;
    LinearLayout dateView;
    KProgressHUD Loader;
    TextView dateText;
    ImageView From,All;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        From = findViewById(R.id.statementFrom);
        All = findViewById(R.id.statementAll);
        dateText = findViewById(R.id.statementDtxt);
        dateView = findViewById(R.id.statementDview);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        statementRecyclerView = findViewById(R.id.statementRecyclerView);
        statementAdaptor = new StatementAdaptor(getApplication());
        statementRecyclerView.setAdapter(statementAdaptor);
        statementRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        statementRecyclerView.setNestedScrollingEnabled(false);
        GetStatement();


    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    void getDate(final String Type)
    {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(WalletActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        DateData = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        if (Type.equalsIgnoreCase("From"))
                            if (Type.equalsIgnoreCase("From")) {
                                dateText.setText("From " + DateData);
                                isFromDate = true;
                                FromDate = DateData;
                                getDate("To");
                            } else {
                                dateText.setText(dateText.getText().toString() + " To " + DateData);
                                isToDate = true;
                                ToDate = DateData;
                                GetStatement();
                            }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void statementAll(View view)
    {
        isFromDate=isToDate=false;
        dateText.setText("");
        dateView.setVisibility(View.GONE);
        GetStatement();
    }
    public void statementFrom(View view)
    {
        dateText.setText("");
        dateView.setVisibility(View.VISIBLE);
        if (!isFromDate)
            getDate("From");
    }


    public void GetStatement()
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shopamountlist";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
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

                            if(sts.equalsIgnoreCase("01")) {
                                String data = Res.getString("shopamount");
                                JSONArray Results = new JSONArray(data);
                                statementModels.clear();
                                for (int i = 0; i < Results.length(); i++) {
                                    String Result = Results.getString(i);
                                    JSONObject rst = new JSONObject(Result);
                                    StatementModel statementModel = new StatementModel();
                                    statementModel.setAmount(rst.getString("shopamount"));
                                    statementModel.setDate(rst.getString("amountdate"));
                                    statementModel.setType(rst.getString("amount_type"));
                                    statementModels.add(statementModel);
                                }
                                statementAdaptor.renewItems(statementModels);

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
                params.put("shopid",sharedPreferences.getString("shop_id",""));
                if(isFromDate)
                {if(isToDate)
                {
                    params.put("fromdate",FromDate);
                    params.put("todate",ToDate);
                }
                }
                params.put("limit","100");
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
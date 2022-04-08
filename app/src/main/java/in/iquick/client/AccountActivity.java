package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import in.iquick.client.models.OrderModel;

public class AccountActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText EditChangePassword;
    LinearLayout ChangeBox;
    JRequest jRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        EditChangePassword = findViewById(R.id.EditChangePassword);
        ChangeBox = findViewById(R.id.ChangeBox);

    }

    public void Logout(View view)
    {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", "");
        editor.putString("password", "");
        editor.putString("id", "");
        editor.putString("name", "");
        editor.putString("phone","");
        editor.putString("shopname", "");
        editor.putString("comm_plan", "");
        editor.apply();
        Intent intents = new Intent(AccountActivity.this, LoginActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void DashboardBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MainActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(AccountActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
    }

    public void Orders(View view)
    {
        Intent intents = new Intent(AccountActivity.this, OrderActivity.class);
        startActivity(intents);
    }

    public void ChangeOpnBtn(View view)
    {
        ChangeBox.setVisibility(View.VISIBLE);
    }

    public void CancelBtn(View view)
    {
        ChangeBox.setVisibility(View.GONE);
        EditChangePassword.setText("");
    }

    public void ChangeBtn(View view)
    {
        if ((EditChangePassword.length()>2))
            GetChangePassword();
            else
            Toast.makeText(getApplicationContext(), "Please enter your new Password!", Toast.LENGTH_SHORT).show();
    }

    public void GetChangePassword() {

        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/change?user_id="+sharedPreferences.getString("id","")
                +"&password="+EditChangePassword.getText().toString();
        Log.e("URLLLLLL",URL);
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response)
                    {
                        Loader.dismiss();
                        Log.i("VOLLEYES", response);
                        try {

                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {

                                ChangeBox.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Password Updated Successfully...", Toast.LENGTH_SHORT).show();

                            }
                            else
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                        catch (Exception e)
                        {
                            Log.e("catcherror",e+"d");
                            Toast.makeText(AccountActivity.this, "Catch Error :"+e, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(AccountActivity.this, "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        )
        {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_id",sharedPreferences.getString("id",""));
                params.put("password",EditChangePassword.getText().toString());
                Log.i("loginp ", params.toString());
                return params;
            }
        };
        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

}
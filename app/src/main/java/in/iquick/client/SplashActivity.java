package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
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

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView cID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        cID = findViewById(R.id.cID);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        getCountryApi();

    }

    public void getCountryApi()
    {
        TelephonyManager tm = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        findViewById(R.id.splashLoader).setVisibility(View.GONE);
        cID.setText(countryCode);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("countryCode", countryCode);
        editor.apply();
        startLogin();
    }

    public void startLogin()
    {
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                if(sharedPreferences.getString("username","").length()>2 && sharedPreferences.getString("password","").length()>2)
                {
                    Intent diya = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(diya);
                    finish();

                }else
                {
                    Intent diya = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(diya);
                    finish();

                }
            }
        }, 3000);
    }

}
package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.mateware.snacky.Snacky;
import in.iquick.client.models.Categories;
import in.iquick.client.models.Products;

public class MainActivity extends AppCompatActivity {

    ImageCarousel carousel, imageCarousel;
    List<CarouselItem> list = new ArrayList<>();
    List<CarouselItem> carouselImage  = new ArrayList<>();
    TextView mainBalance,Totalsale,clientName;
    ScrollTextView MarqueeText;
    SharedPreferences sharedPreferences;
    KProgressHUD Loader;
    ImageView RefreshBtn;
    ProgressBar progressBar;
    ZoomableImageView AdvertismentsImage;
    String CountryCode;
    JRequest jRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carousel = findViewById(R.id.carousel);
        imageCarousel = findViewById(R.id.imageCarouselMA);

        imageCarousel.setCarouselType(CarouselType.SHOWCASE);
        imageCarousel.setShowNavigationButtons(false);
        imageCarousel.setScaleOnScroll(true);
        imageCarousel.setItemLayout(R.layout.custom_fixed_size_item_layout);
        imageCarousel.setImageViewId(R.id.image_view);

        mainBalance = findViewById(R.id.mainBalance);
        Totalsale = findViewById(R.id.Totalsale);
        clientName = findViewById(R.id.clientName);
        RefreshBtn = findViewById(R.id.RefreshBtn);
        progressBar = findViewById(R.id.progressBar);
        AdvertismentsImage = findViewById(R.id.AdvertismentsImageMA);

        list.add(new CarouselItem("http://ieeesbjcet.org/defaults/bn1.jpg",""));
        list.add(new CarouselItem("http://ieeesbjcet.org/defaults/bn2.jpg",""));
        list.add(new CarouselItem("http://ieeesbjcet.org/defaults/bn3.jpg",""));
        carousel.addData(list);
        MarqueeText = findViewById(R.id.MarqueeText);
        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        HomeAPI(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
        RechargesStatus();
        Marquee();
        SliderApi();

        CountryCode = sharedPreferences.getString("countryCode","");
        if (CountryCode.toLowerCase(Locale.ROOT).equalsIgnoreCase("in"))
        {
            findViewById(R.id.ecomView).setVisibility(View.VISIBLE);
        }else
        {
            findViewById(R.id.ecomView).setVisibility(View.GONE);
        }

    }

    public void RefreshBtn(View view)
    {
        RechargesStatus();
        Marquee();
    }

    @Override
    protected void onResume()
    {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mramt", "");
        editor.apply();
        HomeAPI(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
        RechargesStatus();
        Marquee();
        AdvertismentApi();
        super.onResume();

    }

    public void CM(View view)
    {
        Snacky.builder()
                .setActivity(MainActivity.this)
//                .setActionText("ACTION")
                .setActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //do something
                    }
                })
                .setText("Coming Soon...")
                .setDuration(Snacky.LENGTH_LONG)
                .build()
                .show();
    }

    public void PrepaidBtn(View view)
    {
//        providerPrepaid.setVisibility(View.VISIBLE);
//        providerPostpaid.setVisibility(View.GONE);
//        providerdth.setVisibility(View.GONE);
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Prepaid");
        intents.putExtra("provider","n");
        startActivity(intents);
    }

    public void ShopMainBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, ShopmainActivity.class);
        startActivity(intents);
    }

    public void EcommerceBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, EcommerceActivity.class);
        startActivity(intents);
    }

    public void PostpaidBtn(View view)
    {
//        providerPrepaid.setVisibility(View.GONE);
//        providerPostpaid.setVisibility(View.VISIBLE);
//        providerdth.setVisibility(View.GONE);
        Intent intents = new Intent(MainActivity.this, MobileRechargeActivity.class);
        intents.putExtra("type","Postpaid");
        intents.putExtra("provider","n");
        startActivity(intents);
    }
    public void dthBtn(View view)
    {
        //      Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        Intent intents = new Intent(MainActivity.this, dthRechargeActivity.class);
        intents.putExtra("type","DTH");
        startActivity(intents);
    }

    public void ksebBtn(View view)
    {
        //  Toast.makeText(this, "Coming Soon...", Toast.LENGTH_SHORT).show();
        Intent intents = new Intent(MainActivity.this, KsebRechargeActivity.class);
        intents.putExtra("type","Electricty");
        startActivity(intents);
    }

    public void HistoryBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, HistoryActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void AccountBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, AccountActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }

    public void MoreBtn(View view)
    {
        Intent intents = new Intent(MainActivity.this, MoreActivity.class);
        startActivity(intents);
        overridePendingTransition(R.anim.fadein,R.anim.fadeout);
        finish();
    }



    void Loader(Boolean status)
    {

        if(status)
            Loader.show();
        else
            Loader.dismiss();
    }

    public void HomeAPI(final String username, final String password)
    {
        Loader(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"shop/home?user_id="+sharedPreferences.getString("id","");
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {


                        Log.i("VOLLEYES", response);

                        try {
                            Loader(false);
                            JSONObject Res = new JSONObject(response);
                            String sts     = Res.getString("sts");
                            String msg     = Res.getString("msg");

                            if(sts.equalsIgnoreCase("01"))
                            {
                                mainBalance.setText("₹"+Res.getString("wallet"));
                                Totalsale.setText("₹"+Res.getString("debit"));
                                clientName.setText("Hello "+Res.getString("name")+",");

                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("shopname", Res.getString("name"));
                                editor.apply();

                            }

                        }catch (Exception e){
                            Log.e("catcherror",e+"d");

                            // Toast.makeText(getApplicationContext(), "Catch Error :"+e, Toast.LENGTH_SHORT).show();

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
                            Loader(false);
                            //    Toast.makeText(getApplicationContext(), "Network Error :"+errorString, Toast.LENGTH_SHORT).show();
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
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 1, 1.0f));

        // Start the request immediately
        queue.add(request);

    }

    public void ContactUs(View view)
    {
        Intent i = new Intent(getApplicationContext(), WebActivity.class);
        i.putExtra("u",getString(R.string.site_url)+"contact.php");
        i.putExtra("t","Contact Us");
        startActivity(i);
    }

    public void RechargesStatus()
    {
        RefreshBtn.setVisibility(View.GONE);
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"recharge/corn";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        RefreshBtn.setVisibility(View.VISIBLE);
                        findViewById(R.id.progressBar).setVisibility(View.GONE);
                        if(response.length()>5)
                        {

                            HomeAPI(sharedPreferences.getString("username",""),sharedPreferences.getString("password",""));
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

                            findViewById(R.id.progressBar).setVisibility(View.GONE);

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shop_id",sharedPreferences.getString("shop_id",""));
                params.put("limit","50");
                Log.i("loginp ", params.toString());

                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 1, 1.0f));
        queue.add(request);

    }

    public void Marquee()
    {
        MarqueeText.setVisibility(View.GONE);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.api_url)+"marquee";
        StringRequest request = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        JSONObject Res = null;
                        try {
                            Res = new JSONObject(response);
                            String sts     = Res.getString("sts");
                            String marqueee     = Res.getString("marquee");
                            if(sts.equalsIgnoreCase("01"))
                            {
                                MarqueeText.setVisibility(View.VISIBLE);
                                MarqueeText.setText(marqueee);
                                MarqueeText.startScroll();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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


                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();
                params.put("shop_id",sharedPreferences.getString("shop_id",""));
                params.put("limit","50");
                Log.i("loginp ", params.toString());

                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(1000, 1, 1.0f));
        queue.add(request);

    }


    public void AdvertismentApi()
    {

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("id", "");

            jRequest = new JRequest(RequestJson, "advertisment", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");


                    if(sts.equalsIgnoreCase("01"))
                    {
                        String advertismentsImage="";
                        //Sliders
                        String advertismentsArrays = Res.getString("advertisments");
                        JSONArray advertismentsArray = new JSONArray(advertismentsArrays);
                        String advertismentsObject = advertismentsArray.getString(0);
                        JSONObject AdvertismentsDetails = new JSONObject(advertismentsObject);

                            advertismentsImage = getString(R.string.ecom_assets_url) + AdvertismentsDetails.getString("image");
                            Glide.with(getApplicationContext()).load(advertismentsImage).into(AdvertismentsImage);
                    }
                    else
                        Snacky.builder().setActivity(getParent()).setText(msg).setDuration(Snacky.LENGTH_LONG).build().show();

                }
            });
            jRequest.execute();
        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }


    public void SliderApi()
    {

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("id", "");

            jRequest = new JRequest(RequestJson, "mainbanners", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");


                    if(sts.equalsIgnoreCase("01"))
                    {
                        //Sliders
                        String slidersArrays = Res.getString("banners");
                        JSONArray slidersArray = new JSONArray(slidersArrays);

                        carouselImage.clear();
                        for (int i = 0; i < slidersArray.length(); i++)
                        {
                            String SliderObjectString = slidersArray.getString(i);
                            JSONObject SliderObject = new JSONObject(SliderObjectString);
                            String sliderImageUrl = getString(R.string.ecom_assets_url) + SliderObject.getString("image");
                            carouselImage.add(new CarouselItem(sliderImageUrl));
                        }
                        imageCarousel.addData(carouselImage);
                        imageCarousel.setAutoPlay(true);
                    }
                    else
                        Snacky.builder().setActivity(getParent()).setText(msg).setDuration(Snacky.LENGTH_LONG).build().show();

                }
            });
            jRequest.execute();
        } catch ( JSONException e) {
            e.printStackTrace();
        }
    }

}
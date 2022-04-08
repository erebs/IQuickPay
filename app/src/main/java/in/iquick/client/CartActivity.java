package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

public class CartActivity extends AppCompatActivity implements CartAdapter.myRefresh {

    KProgressHUD Loader;
    SharedPreferences sharedPreferences;
    List<CartModel> cartModels = new ArrayList<>();
    private RecyclerView CartView;
    private CartAdapter cartAdapter;
    String Items, whtsmsg = "", state, dist, latitude, longitude, email;
    TextView itemCount, itemTotal, grandTotal, paymentTotal, stateDist, setCOD, setRZ;
    EditText inputPincode, cartAds, inputArea;
    boolean isCod = true;
    Long tsLong = System.currentTimeMillis() / 1000;
    String ts = tsLong.toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        itemCount = findViewById(R.id.itemCount);
        itemTotal = findViewById(R.id.itemTotal);
        grandTotal = findViewById(R.id.grandTotal);


        CartView = findViewById(R.id.CartView);
        CartView.setNestedScrollingEnabled(false);
        this.cartAdapter = new CartAdapter(this);
        CartView.setAdapter(cartAdapter);
        GridLayoutManager mGrid = new GridLayoutManager(getApplicationContext(), 1);
        CartView.setLayoutManager(mGrid);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        getCart(sharedPreferences.getString("ecom_id", ""));
    }

    public void goBack(View view) {
        super.onBackPressed();
    }

    public void Refresh() {
        getCart(sharedPreferences.getString("ecom_id", ""));
    }

    public void getCart(String userid) {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.ecom_api_url) + "getcart";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Loader.dismiss();
                        Log.e("Respone", response);

                        try {
                            JSONObject Res = new JSONObject(response);
                            String sts = Res.getString("sts");
                            double Total = 0;
                            if (sts.equalsIgnoreCase("01")) {
                                String CartItems = Res.getString("cart");
                                JSONArray CartItemsArray = new JSONArray(CartItems);
                                cartModels.clear();
                                int cnt = 0;
                                Items = "";
                                if (CartItemsArray.length() > 0) {
                                    findViewById(R.id.TotalBoxCart).setVisibility(View.VISIBLE);
                                    findViewById(R.id.emptyCartBox).setVisibility(View.GONE);
                                } else {
                                    findViewById(R.id.TotalBoxCart).setVisibility(View.GONE);
                                    findViewById(R.id.emptyCartBox).setVisibility(View.VISIBLE);
                                }
                                for (int i = 0; i < CartItemsArray.length(); i++) {
                                    String CartItem = CartItemsArray.getString(i);
                                    JSONObject CartItemObject = new JSONObject(CartItem);
                                    CartModel cartModel = new CartModel();
                                    cartModel.setName(CartItemObject.getString("productname"));
                                    //cartModel.setQty(CartItemObject.getString("unitname"));
                                    cartModel.setID(CartItemObject.getString("id"));
                                    cartModel.setPrice(CartItemObject.getString("productprice"));
                                    cartModel.setOffer(CartItemObject.getString("productofferprice"));
                                    cartModel.setItemQty(CartItemObject.getString("quantity"));
                                    Items = Items + cnt + ". " + CartItemObject.getString("productname") +
                                            " X " + CartItemObject.getString("quantity") + "\n";
                                    double cost = Double.parseDouble(CartItemObject.getString("productofferprice"));
                                    int qty = Integer.parseInt(CartItemObject.getString("quantity"));
                                    Total = Total + (cost * qty);
                                    cartModels.add(cartModel);
                                    cnt = cnt + 1;
                                }
                                cartAdapter.renewItems(cartModels);
                                if (cnt == 1)
                                    itemCount.setText("Price (" + cnt + " Item)");
                                else
                                    itemCount.setText("Price (" + cnt + " Items)");
                                itemTotal.setText(Total + "");
                                double gTotal = Total + 0;
                                grandTotal.setText(gTotal + "");
                            } else {

                            }

                        } catch (Exception e) {
                            Log.e("catcherror", e + "d");

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid", userid);
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

    public void placeOrderBtn(View view) {

                placeOrder();

    }



    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }


    public void placeOrder() {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.ecom_api_url) + "placeorder";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Loader.dismiss();
                        Log.e("Respone", response);

                        try {
                            JSONObject Res = new JSONObject(response);
                            String sts = Res.getString("sts");
                            String msg = Res.getString("msg");
                            if (sts.equalsIgnoreCase("01")) {
                                finish();
                                Toast.makeText(CartActivity.this, msg, Toast.LENGTH_SHORT).show();
                            } else {

                                Toast.makeText(CartActivity.this, msg, Toast.LENGTH_SHORT).show();

                            }


                        } catch (Exception e) {
                            Log.e("catcherror", e + "d");

                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        NetworkResponse response = error.networkResponse;
                        String errorMsg = "";
                        if (response != null && response.data != null) {
                            String errorString = new String(response.data);
                            Log.i("log error", errorString);
                            Loader.dismiss();

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("usertype", "Distributer");
                params.put("userid", sharedPreferences.getString("ecom_id", ""));
                    params.put("type", "CoD");
                params.put("number", sharedPreferences.getString("phone", ""));
                params.put("name", sharedPreferences.getString("name", ""));
                params.put("email", sharedPreferences.getString("email", ""));
                params.put("address", sharedPreferences.getString("address", ""));
                params.put("pincode", "123456");
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

}
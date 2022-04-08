package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.ravikoradiya.zoomableimageview.ZoomableImageView;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.mateware.snacky.Snacky;
import in.iquick.client.adapters.Product;
import in.iquick.client.models.Categories;
import in.iquick.client.models.Products;

public class ProductActivity extends AppCompatActivity {

    TextView ProductName, ProductCrumb, ProductPrice, ProductDesc;
    String productId="", productName="";
    JRequest jRequest;
    ZoomableImageView ProductImage;
    ImageView ThumbnailImage1, ThumbnailImage2, ThumbnailImage3, ThumbnailImage4;
    String productImage3, productImage1, productImage2, productImage4;
    CardView ThumbnailView1, ThumbnailView2, ThumbnailView3, ThumbnailView4;
    BottomSheet.Builder builder;
    String[] unitId = new String[0];
    String[] unitName = new String[0];
    String[] unitPrice = new String[0];
    TextView VarientView;
    String uPrice="", uId = "", uName = "";
    SharedPreferences sharedPreferences;

    List<Products> mProductsFP = new ArrayList<>();
    private RecyclerView FPRecycleview;
    private Product productFP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        VarientView = findViewById(R.id.productVarientName);
        ProductName = findViewById(R.id.productNamePA);
        ProductCrumb  = findViewById(R.id.crumbPA);
        ProductPrice  = findViewById(R.id.productPricePA);
        ProductDesc  = findViewById(R.id.productDescPA);
        ProductImage = findViewById(R.id.productMainImagePA);
        FPRecycleview = findViewById(R.id.similarProductsRecycleviewPA);
        ThumbnailImage1 = findViewById(R.id.ProductThumbnailImage1);
        ThumbnailImage2 = findViewById(R.id.ProductThumbnailImage2);
        ThumbnailImage3 = findViewById(R.id.ProductThumbnailImage3);
        ThumbnailImage4 = findViewById(R.id.ProductThumbnailImage4);
        ThumbnailView1 = findViewById(R.id.ProductThumbnailView1);
        ThumbnailView2 = findViewById(R.id.ProductThumbnailView2);
        ThumbnailView3 = findViewById(R.id.ProductThumbnailView3);
        ThumbnailView4 = findViewById(R.id.ProductThumbnailView4);

        //FPRV
        FPRecycleview.setNestedScrollingEnabled(false);
        this.productFP = new Product(this);
        FPRecycleview.setAdapter(productFP);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 2);
        FPRecycleview.setLayoutManager(mGridFP);

        try
        {
            Intent intent = getIntent();
            productId=intent.getStringExtra("productId");
            productName=intent.getStringExtra("productName");
        }
        catch (Exception e)
        {Log.e("getIntent Error",e+"");}

        ProductName.setText(productName);
        ProductApi();
    }

    public void ProductApi()
    {

        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("id", productId);

            jRequest = new JRequest(RequestJson, "product/"+productId, true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");


                    if(sts.equalsIgnoreCase("01"))
                    {
                        productImage1 = productImage2 = productImage3 = productImage4 = "";
                        //Sliders
                        String slidersObject = Res.getString("product");
                        JSONObject ProductDetails = new JSONObject(slidersObject);

                        try {
                            productImage1 = getString(R.string.ecom_assets_url) +"/"+ ProductDetails.getString("image");
                            Glide.with(getApplicationContext()).load(productImage1).into(ProductImage);
                            ProductImage.setBackground(getDrawable(R.color.white));

                            productImage2 = getString(R.string.ecom_assets_url) +"/"+ ProductDetails.getString("image2");
                            productImage3 = getString(R.string.ecom_assets_url) +"/"+ ProductDetails.getString("image3");
                            productImage4 = getString(R.string.ecom_assets_url) +"/"+ ProductDetails.getString("image4");

//                        if(productImage1.length()>5)
//                        {   ThumbnailView1.setVisibility(View.VISIBLE);
//                            Glide.with(getApplicationContext()).load(productImage1).into(ThumbnailImage1);  }
                            if(productImage2.length()>45)
                            {   ThumbnailView2.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(productImage2).into(ThumbnailImage2);  }
                            if(productImage3.length()>45)
                            {   ThumbnailView3.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(productImage3).into(ThumbnailImage3);  }
                            if(productImage4.length()>45)
                            {   ThumbnailView4.setVisibility(View.VISIBLE);
                                Glide.with(getApplicationContext()).load(productImage4).into(ThumbnailImage4);  }
                        }catch (Exception e)
                        {}


                        ProductCrumb.setText(Res.getString("category")+" -> "+Res.getString("sub_category"));

                        ProductDesc.setText(ProductDetails.getString("desc"));
                        ProductPrice.setText(ProductDetails.getString("offerprice"));

                        String ProductsFP = Res.getString("similar_products");
                        JSONArray ProductsArrayFP = new JSONArray(ProductsFP);
                        mProductsFP.clear();
                        String productImageFP = "";
                        for (int i = 0; i < ProductsArrayFP.length(); i++)
                        {
                            String ProductObjectString = ProductsArrayFP.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            Products productsMFP = new Products();
                            productsMFP.setName(ProductObject.getString("name"));
                            productsMFP.setId(ProductObject.getString("id"));
                            productsMFP.setDesc(ProductObject.getString("desc"));
                            productsMFP.setPrice(ProductObject.getString("offerprice"));
                            productsMFP.setBestSeller(ProductObject.getString("best_seller"));
                            productImageFP = getString(R.string.ecom_assets_url)+"/"+ProductObject.getString("image");
                            productsMFP.setImage(productImageFP);
                            mProductsFP.add(productsMFP);
                        }
                        productFP.renewItems(mProductsFP);

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

    public void ProductThumbnailBtn1(View view)
    {
        ThumbnailView1.setVisibility(View.GONE);
        Glide.with(getApplicationContext()).load(productImage1).into(ProductImage);

        if(productImage2.length()>5)
        {   ThumbnailView2.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage2).into(ThumbnailImage2);  }
        if(productImage3.length()>5)
        {   ThumbnailView3.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage3).into(ThumbnailImage3);  }
        if(productImage4.length()>5)
        {   ThumbnailView4.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage4).into(ThumbnailImage4);  }
    }

    public void ProductThumbnailBtn2(View view)
    {
        ThumbnailView2.setVisibility(View.GONE);
        Glide.with(getApplicationContext()).load(productImage2).into(ProductImage);

        if(productImage1.length()>45)
        {   ThumbnailView1.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage1).into(ThumbnailImage1);  }
        if(productImage3.length()>45)
        {   ThumbnailView3.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage3).into(ThumbnailImage3);  }
        if(productImage4.length()>45)
        {   ThumbnailView4.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage4).into(ThumbnailImage4);  }
    }

    public void ProductThumbnailBtn3(View view)
    {
        ThumbnailView3.setVisibility(View.GONE);
        Glide.with(getApplicationContext()).load(productImage3).into(ProductImage);

        if(productImage1.length()>45)
        {   ThumbnailView1.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage1).into(ThumbnailImage1);  }
        if(productImage2.length()>45)
        {   ThumbnailView2.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage2).into(ThumbnailImage2);  }
        if(productImage4.length()>45)
        {   ThumbnailView4.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage4).into(ThumbnailImage4);  }
    }

    public void ProductThumbnailBtn4(View view)
    {
        ThumbnailView4.setVisibility(View.GONE);
        Glide.with(getApplicationContext()).load(productImage4).into(ProductImage);

        if(productImage1.length()>45)
        {   ThumbnailView1.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage1).into(ThumbnailImage1);  }
        if(productImage3.length()>45)
        {   ThumbnailView3.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage3).into(ThumbnailImage3);  }
        if(productImage2.length()>45)
        {   ThumbnailView2.setVisibility(View.VISIBLE);
            Glide.with(getApplicationContext()).load(productImage2).into(ThumbnailImage2);  }
    }

    public void goBack(View view)
    {
        super.onBackPressed();
    }

    public void AddtoCartBtn(View view)
    {
        addToCart(sharedPreferences.getString("ecom_id", ""),productId,uId,"1");
    }

    public void addToCart(String userid,String productid,String unitid, String quantity)
    {
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String URL = getString(R.string.ecom_api_url)+"addtocart";
        StringRequest request = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {

                        Loader.dismiss();
                        Log.e("Respone", response);

                        try {
                            JSONObject Res=new JSONObject(response);
                            String sts    = Res.getString("sts");
                            String msg    = Res.getString("msg");
                            if(sts.equalsIgnoreCase("01"))
                            {
                                Toast.makeText(ProductActivity.this, ProductName.getText().toString()+" added to your cart.", Toast.LENGTH_SHORT).show();
                            }
                            else
                                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

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

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("userid",userid);
                params.put("productid",productid);
                params.put("quantity",quantity);
                params.put("productname",uName);
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

    public void VarientBtn(View view)
    {
        {
            builder = new BottomSheet.Builder(this);
            builder.setTitle("Choose a variant");
            builder.setContentType(BottomSheet.LIST);
            builder.setItems(unitName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    VarientView.setText(unitName[which]);
                    ProductPrice.setText("₹"+unitPrice[which]);
                    uPrice = unitPrice[which];
                }
            });
            builder.show();
        }
    }

}
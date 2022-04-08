package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.imaginativeworld.whynotimagecarousel.CarouselType;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mateware.snacky.Snacky;
import in.iquick.client.adapters.Category;
import in.iquick.client.adapters.Product;
import in.iquick.client.adapters.ProductH;
import in.iquick.client.adapters.SubCategory;
import in.iquick.client.models.Categories;
import in.iquick.client.models.Products;
import in.iquick.client.models.SubCategories;
import ru.nikartm.support.ImageBadgeView;

public class EcommerceActivity extends AppCompatActivity

{
    JRequest jRequest;
    LinearLayout ishopSplash;
    SharedPreferences sharedPreferences;
    List<CarouselItem> carouselImage = new ArrayList<>();
    ImageCarousel carousel;
    TextView CartCountView;

    List<Categories> mCategories = new ArrayList<>();
    private RecyclerView CategoryRecycleview;
    private Category category;

    List<Products> mProductsFP = new ArrayList<>();
    private RecyclerView FPRecycleview;
    private ProductH productFP;

    List<Products> mProductsPP = new ArrayList<>();
    private RecyclerView PPRecycleview;
    private ProductH productPP;

    List<Products> mProductsTP = new ArrayList<>();
    private RecyclerView TPRecycleview;
    private ProductH productTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecommerce);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);

        ishopSplash = findViewById(R.id.ishopSplash);
        carousel  = findViewById(R.id.carouselEM);
        CartCountView = findViewById(R.id.tv_cart_count_ea);
        CategoryRecycleview = findViewById(R.id.CategoryRecycleviewEM);
        PPRecycleview = findViewById(R.id.popularsProductsRecycleviewEM);
        TPRecycleview = findViewById(R.id.trendingProductsRecycleviewEM);
        FPRecycleview = findViewById(R.id.featuredProductsRecycleviewEM);

        //ImageCarousel
        carousel.setCarouselType(CarouselType.SHOWCASE);
        carousel.setShowNavigationButtons(false);
        carousel.setScaleOnScroll(true);
        carousel.setItemLayout(R.layout.custom_fixed_size_item_layout);
        carousel.setImageViewId(R.id.image_view);

        //CategpryRV
        this.category = new Category(this);
        CategoryRecycleview.setAdapter(category);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        CategoryRecycleview.setLayoutManager(HorizontalLayout);

        //FPRV
        this.productFP = new ProductH(this);
        FPRecycleview.setAdapter(productFP);
        //GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 2);
        LinearLayoutManager mGridFP = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        FPRecycleview.setLayoutManager(mGridFP);

        //PPRV
        this.productPP = new ProductH(this);
        PPRecycleview.setAdapter(productPP);
        //GridLayoutManager mGridPP = new GridLayoutManager(getApplicationContext(), 2);
        LinearLayoutManager mGridPP = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        PPRecycleview.setLayoutManager(mGridPP);

        //TPRV
        this.productTP = new ProductH(this);
        TPRecycleview.setAdapter(productTP);
        //GridLayoutManager mGridTP = new GridLayoutManager(getApplicationContext(), 2);
        LinearLayoutManager mGridTP = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        TPRecycleview.setLayoutManager(mGridTP);

        FastIN();

    }


    public void FastIN()
    {
        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("name", sharedPreferences.getString("name",""));
            RequestJson.put("email", sharedPreferences.getString("phone","")+"@iquickpay.in");
            RequestJson.put("password", sharedPreferences.getString("phone",""));
            RequestJson.put("phone", sharedPreferences.getString("phone",""));
            RequestJson.put("address", sharedPreferences.getString("shopname",""));

            ishopSplash.setVisibility(View.VISIBLE);
            jRequest = new JRequest(RequestJson, "autoregister", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Toast.makeText(getApplicationContext(), "Login as "+sharedPreferences.getString("name","")+".", Toast.LENGTH_SHORT).show();
                    ishopSplash.setVisibility(View.GONE);

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");

                    if(sts.equalsIgnoreCase("01"))
                    {
                        String UserObjectString = Res.getString("user");
                        JSONObject UserObject = new JSONObject(UserObjectString);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("ecom_id", UserObject.getString("id"));
                        editor.putString("name", UserObject.getString("name"));
                        editor.apply();
                        HomeApi();
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

    @Override
    protected void onResume() {
        if(sharedPreferences.getString("ecom_id","").length()>0)
            HomeApi();
        super.onResume();
    }

    public void HomeApi()
    {

        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("id", sharedPreferences.getString("ecom_id",""));

            jRequest = new JRequest(RequestJson, "home", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");


                    if(sts.equalsIgnoreCase("01"))
                    {

                        CartCountView.setText(Res.getString("cart_count"));

                        //Sliders
                        String slidersArrays = Res.getString("sliders");
                        JSONArray slidersArray = new JSONArray(slidersArrays);

                        carouselImage.clear();
                        for (int i = 0; i < slidersArray.length(); i++)
                        {
                            String SliderObjectString = slidersArray.getString(i);
                            JSONObject SliderObject = new JSONObject(SliderObjectString);
                            String sliderImageUrl = getString(R.string.ecom_assets_url) + SliderObject.getString("image");
                            carouselImage.add(new CarouselItem(sliderImageUrl));
                        }
                        carousel.addData(carouselImage);
                        carousel.setAutoPlay(true);

                        //Category
                        String Categories = Res.getString("categories");
                        JSONArray CategoriesArray = new JSONArray(Categories);
                        mCategories.clear();
                        String categoryImage="";
                        for (int i = 0; i < CategoriesArray.length(); i++)
                        {
                            String Category = CategoriesArray.getString(i);
                            JSONObject CategoryObject = new JSONObject(Category);
                            Categories categories = new Categories();
                            categories.setName(CategoryObject.getString("name"));
                            categories.setId(CategoryObject.getString("id"));
                            categoryImage = getString(R.string.ecom_assets_url)+CategoryObject.getString("image");
                            Log.e("Params",categoryImage);
                            categories.setImage(categoryImage);
                            mCategories.add(categories);
                        }
                        category.renewItems(mCategories);

                        String ProductsFP = Res.getString("featured_products");
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

                        String ProductsPP = Res.getString("populars_products");
                        JSONArray ProductsArrayPP = new JSONArray(ProductsPP);
                        mProductsPP.clear();
                        String productImagePP = "";
                        for (int i = 0; i < ProductsArrayPP.length(); i++)
                        {
                            String ProductObjectString = ProductsArrayPP.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            Products productsMPP = new Products();
                            productsMPP.setName(ProductObject.getString("name"));
                            productsMPP.setId(ProductObject.getString("id"));
                            productsMPP.setDesc(ProductObject.getString("desc"));
                            productsMPP.setPrice(ProductObject.getString("offerprice"));
                            productsMPP.setBestSeller(ProductObject.getString("best_seller"));
                            productImagePP = getString(R.string.ecom_assets_url)+"/"+ProductObject.getString("image");
                            productsMPP.setImage(productImagePP);
                            mProductsPP.add(productsMPP);
                        }
                        productPP.renewItems(mProductsPP);

                        String ProductsTP = Res.getString("trending_products");
                        JSONArray ProductsArrayTP = new JSONArray(ProductsTP);
                        mProductsTP.clear();
                        String productImageTP = "";
                        for (int i = 0; i < ProductsArrayTP.length(); i++)
                        {
                            String ProductObjectString = ProductsArrayTP.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            Products productsMTP = new Products();
                            productsMTP.setName(ProductObject.getString("name"));
                            productsMTP.setId(ProductObject.getString("id"));
                            productsMTP.setDesc(ProductObject.getString("desc"));
                            productsMTP.setPrice(ProductObject.getString("offerprice"));
                            productsMTP.setBestSeller(ProductObject.getString("best_seller"));
                            productImageTP = getString(R.string.ecom_assets_url)+"/"+ProductObject.getString("image");
                            productsMTP.setImage(productImageTP);
                            mProductsTP.add(productsMTP);
                        }
                        productTP.renewItems(mProductsTP);

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


    public void goBack(View view)
    {
        super.onBackPressed();
    }


    public void gotoSearch(View view)
    {
        Intent i = new Intent(this,SearchActivity.class);
        startActivity(i);
    }

    public void gotoCart(View view)
    {
        Intent i = new Intent(this,CartActivity.class);
        startActivity(i);
    }
}
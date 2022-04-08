package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.imaginativeworld.whynotimagecarousel.CarouselItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mateware.snacky.Snacky;
import in.iquick.client.adapters.Product;
import in.iquick.client.adapters.SubCategory;
import in.iquick.client.models.Categories;
import in.iquick.client.models.Products;
import in.iquick.client.models.SubCategories;

public class SubCategoryActivity extends AppCompatActivity implements SubCategory.GetSubCategory{

    List<SubCategories> mSubCategories = new ArrayList<>();
    private RecyclerView SubCategoryRecycleview;
    private SubCategory subCategory;

    List<Products> mProducts = new ArrayList<>();
    private RecyclerView ProductRecycleview;
    private Product product;

    TextView CategoryName;
    String categoryId, categoryName;
    JRequest jRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        SubCategoryRecycleview = findViewById(R.id.subCategoryRecycleviewCA);
        CategoryName =  findViewById(R.id.categoryNameCA);

        try
        {
            Intent intent = getIntent();
            categoryId=intent.getStringExtra("categoryId");
            categoryName=intent.getStringExtra("categoryName");
        }
        catch (Exception e)
        {Log.e("getIntent Error",e+"");}

        CategoryName.setText(categoryName);
        GetCategory(categoryId,"");

        //SubCategpryRV
        this.subCategory = new SubCategory(this);
        SubCategoryRecycleview.setAdapter(subCategory);
        LinearLayoutManager HorizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        SubCategoryRecycleview.setLayoutManager(HorizontalLayout);

        //ProductRV
        ProductRecycleview = findViewById(R.id.ProductsRecycleviewSC);
        ProductRecycleview.setNestedScrollingEnabled(false);
        this.product = new Product(this);
        ProductRecycleview.setAdapter(product);
        GridLayoutManager mGrid = new GridLayoutManager(getApplicationContext(), 2);
        ProductRecycleview.setLayoutManager(mGrid);
    }

    public void GetCategory(String categoryId,String subcategoryId)
    {

        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("categoryid", categoryId);
            RequestJson.put("subcategoryid", subcategoryId);

            jRequest = new JRequest(RequestJson, "products", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");

                    if(sts.equalsIgnoreCase("01"))
                    {
                        String Categories = Res.getString("subcategories");
                        JSONArray CategoriesArray = new JSONArray(Categories);
                        mSubCategories.clear();
                        SubCategories subCategories = new SubCategories();
                        subCategories.setName("All");
                        subCategories.setId("0");
                        mSubCategories.add(subCategories);

                        for (int i = 0; i < CategoriesArray.length(); i++)
                        {
                            String Category = CategoriesArray.getString(i);
                            JSONObject CategoryObject = new JSONObject(Category);
                            subCategories = new SubCategories();
                            subCategories.setName(CategoryObject.getString("name"));
                            subCategories.setId(CategoryObject.getString("id"));
                            mSubCategories.add(subCategories);
                        }
                        subCategory.renewItems(mSubCategories);

                        String Products = Res.getString("products");
                        JSONArray ProductsArray = new JSONArray(Products);
                        mProducts.clear();
                        Products products = new Products();
                        String productImage = "";
                        for (int i = 0; i < ProductsArray.length(); i++)
                        {
                            String ProductObjectString = ProductsArray.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            Products productsM = new Products();
                            productsM.setName(ProductObject.getString("name"));
                            productsM.setId(ProductObject.getString("id"));
                            productsM.setDesc(ProductObject.getString("desc"));
                            productsM.setPrice(ProductObject.getString("offerprice"));
                            productsM.setBestSeller(ProductObject.getString("best_seller"));
                            productImage = getString(R.string.ecom_assets_url)+"/"+ProductObject.getString("image");
                            productsM.setImage(productImage);
                            mProducts.add(productsM);
                        }
                        product.renewItems(mProducts);
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

    @Override
    public void SubCategory(String subCategoryId)
    {
        GetCategory(categoryId,subCategoryId);
    }
}
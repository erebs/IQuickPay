package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.mateware.snacky.Snacky;
import in.iquick.client.adapters.Product;
import in.iquick.client.adapters.Search;
import in.iquick.client.models.Products;
import in.iquick.client.models.Searches;
import in.iquick.client.models.SubCategories;

public class SearchActivity extends AppCompatActivity {

    List<Searches> mSearches = new ArrayList<>();
    private RecyclerView SearchRecycleview;
    private Search search;
    JRequest jRequest;
    EditText SearchInput;
    RelativeLayout SearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchInput = findViewById(R.id.productSearchEdit);
        SearchBox = findViewById(R.id.searchBox);


        //SearchRV
        SearchRecycleview = findViewById(R.id.searchRecycleviewSA);
        SearchRecycleview.setNestedScrollingEnabled(false);
        this.search = new Search(this);
        SearchRecycleview.setAdapter(search);
        GridLayoutManager mGrid = new GridLayoutManager(getApplicationContext(), 1);
        SearchRecycleview.setLayoutManager(mGrid);

        SearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length() >2)
                {
                    GetSerach(s.toString());
                    SearchBox.setVisibility(View.VISIBLE);
                }
                else
                {
                    SearchBox.setVisibility(View.GONE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void CancelBtn(View view)
    {
        finish();
    }

    public void GetSerach(String term)
    {
        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("search", term);

            jRequest = new JRequest(RequestJson, "products", false, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {

                    JSONObject Res = new JSONObject(result);
                    String sts     = Res.getString("sts");
                    String msg     = Res.getString("msg");

                    if(sts.equalsIgnoreCase("01"))
                    {

                        String Products = Res.getString("products");
                        JSONArray ProductsArray = new JSONArray(Products);
                        mSearches.clear();
                        in.iquick.client.models.Products products = new Products();
                        String productImage = "";
                        for (int i = 0; i < ProductsArray.length(); i++)
                        {
                            String ProductObjectString = ProductsArray.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            Searches searches = new Searches();
                            searches.setName(ProductObject.getString("name"));
                            searches.setId(ProductObject.getString("id"));
                            mSearches.add(searches);
                        }
                        search.renewItems(mSearches);
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
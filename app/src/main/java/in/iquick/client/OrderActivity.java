package in.iquick.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.iquick.client.adapters.OrderAdapter;
import in.iquick.client.models.OrderModel;

public class OrderActivity extends AppCompatActivity {

    JRequest jRequest;
    List<OrderModel> orderModels = new ArrayList<>();
    private RecyclerView OrderView;
    private OrderAdapter orderAdapter;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        sharedPreferences = getSharedPreferences("MYSCBCL", MODE_PRIVATE);
        OrderView = findViewById(R.id.orderview);
        OrderView.setNestedScrollingEnabled(false);
        this.orderAdapter = new OrderAdapter(this);
        OrderView.setAdapter(orderAdapter);
        GridLayoutManager mGridFP = new GridLayoutManager(getApplicationContext(), 1);
        OrderView.setLayoutManager(mGridFP);
        GetOrdersApi();

    }

    public void GetOrdersApi() {

        findViewById(R.id.noorder).setVisibility(View.VISIBLE);
        KProgressHUD Loader = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f).show();

        try {
            JSONObject RequestJson = new JSONObject();
            RequestJson.put("userid", sharedPreferences.getString("ecom_id",""));

            jRequest = new JRequest(RequestJson, "orders", true, this, new JRequest.TaskCompleteListener() {
                @Override
                public void onTaskComplete(String result) throws JSONException {
                    Loader.dismiss();

                    JSONObject Res = new JSONObject(result);
                    String sts = Res.getString("sts");
                    String msg = Res.getString("msg");


                    if (sts.equalsIgnoreCase("01")) {
                        findViewById(R.id.noorder).setVisibility(View.GONE);


                        String ProductsFP = Res.getString("orders");
                        JSONArray ProductsArrayFP = new JSONArray(ProductsFP);
                        orderModels.clear();
                        String productImageFP = "";
                        for (int i = 0; i < ProductsArrayFP.length(); i++) {
                            String ProductObjectString = ProductsArrayFP.getString(i);
                            JSONObject ProductObject = new JSONObject(ProductObjectString);
                            OrderModel orderModel = new OrderModel();
                            orderModel.setGrand(ProductObject.getString("offerprice"));
                            orderModel.setId(ProductObject.getString("id")+" - "+ProductObject.getString("productname"));
                            orderModel.setPlaced(ProductObject.getString("order_on"));
                            orderModel.setPstatus(ProductObject.getString("paystatus")+" ("+ProductObject.getString("paytype")+")");
                            orderModel.setPlaced_desc(ProductObject.getString("status"));
                            orderModels.add(orderModel);
                        }
                        orderAdapter.renewItems(orderModels);

                    } else
                        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    //Snacky.builder().setActivity(getParent()).setText(msg).setDuration(Snacky.LENGTH_LONG).build().show();

                }
            });
            jRequest.execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goBack(View view) {
        super.onBackPressed();
    }


}
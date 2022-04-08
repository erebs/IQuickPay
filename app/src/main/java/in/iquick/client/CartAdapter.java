package in.iquick.client;

import static android.graphics.Paint.STRIKE_THRU_TEXT_FLAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    Context ctx;
    private List<CartModel> mCartModel = new ArrayList<>();
    CartModel cartModel;
    String catallID;
    int row_index=0;
    KProgressHUD Loader;
    private myRefresh myrefresh;


    public CartAdapter(Context ctx){
        this.ctx = ctx;
        Loader = KProgressHUD.create(ctx)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Updating cart")
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
        try {
            this.myrefresh = ((myRefresh) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<CartModel> list) {
        this.mCartModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.cartitem, parent,false);
        return new MyViewHolder(inflate);
    }



    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
            cartModel = mCartModel.get(position);
            holder.Name.setText(cartModel.getName());
            if (cartModel.getPrice().equalsIgnoreCase(cartModel.getOffer()))
                holder.Price.setVisibility(View.GONE);
        holder.Price.setText("₹"+cartModel.getPrice());
        holder.Price.setPaintFlags(STRIKE_THRU_TEXT_FLAG);
        holder.Offer.setText("₹"+cartModel.getOffer());
        double offer = Double.parseDouble(cartModel.getOffer());
        double price = Double.parseDouble(cartModel.getPrice());
        double off = (price-offer)/offer;
        off = off*100;
        String offText = String.format("%.0f", off);
        holder.Off.setText(offText+"% Off");
        holder.ItemQty.setText(cartModel.getItemQty());

            holder.Remove.setOnClickListener(new View.OnClickListener() {
                @Override
            public void onClick(View v) {

                    Remove(mCartModel.get(position).getID());
            }
        });

            if(Integer.parseInt(cartModel.getItemQty())<=1)
            {
                holder.Minus.setImageResource(R.drawable.remove_from_cart);
                holder.Minus.setBackground(ctx.getDrawable(R.drawable.btn_danger_int));
                holder.Minus.setColorFilter(ContextCompat.getColor(ctx, R.color.danger), android.graphics.PorterDuff.Mode.MULTIPLY);
            }
            else {
                holder.Minus.setImageResource(R.drawable.minus);
                holder.Minus.setBackground(ctx.getDrawable(R.drawable.btn_success_int));
                holder.Minus.setColorFilter(ContextCompat.getColor(ctx, R.color.success), android.graphics.PorterDuff.Mode.MULTIPLY);
            }

        if(Integer.parseInt(cartModel.getItemQty())>=10)
        {
            holder.Plus.setBackground(ctx.getDrawable(R.drawable.btn_disabled_int));
            holder.Plus.setColorFilter(ContextCompat.getColor(ctx, R.color.disabled), android.graphics.PorterDuff.Mode.MULTIPLY);
        }
        else
        {
            holder.Plus.setBackground(ctx.getDrawable(R.drawable.btn_success_int));
            holder.Plus.setColorFilter(ContextCompat.getColor(ctx, R.color.success), android.graphics.PorterDuff.Mode.MULTIPLY);
        }

        holder.Minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowQty = mCartModel.get(position).getItemQty();
                int qty = Integer.parseInt(nowQty);
                if(qty>=1)
                {
                    qty = qty-1;
                    Update(mCartModel.get(position).getID(),qty+"");
                }else
                {
                    Remove(mCartModel.get(position).getID());
                }
            }
        });

        holder.Plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nowQty = mCartModel.get(position).getItemQty();
                int qty = Integer.parseInt(nowQty);
                if(qty>=10)
                {
                    Toast.makeText(ctx, "Maximum quantity allowed for purchase is 10.", Toast.LENGTH_SHORT).show();
                }else
                {
                    qty = qty+1;
                    Update(mCartModel.get(position).getID(),qty+"");
                }
            }
        });


    }

    public interface myRefresh{
        void Refresh();
    }

    public void Remove(String cartid)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(ctx.getApplicationContext());
        String URL =ctx.getString(R.string.ecom_api_url)+"removecart";
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
                            if(sts.equalsIgnoreCase("01"))
                            {
                                myrefresh.Refresh();
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

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("cartid",cartid);
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }

    public void Update(String cartid,String Qty)
    {
        Loader.show();
        RequestQueue queue = Volley.newRequestQueue(ctx.getApplicationContext());
        String URL =ctx.getString(R.string.ecom_api_url)+"changequantity";
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
                            if(sts.equalsIgnoreCase("01"))
                            {
                                myrefresh.Refresh();
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

                        }
                    }
                }
        ) {


            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<String, String>();

                params.put("cartid",cartid);
                params.put("quantity",Qty);
                Log.e("Volley Parameters", params.toString());
                return params;
            }

        };

        request.setShouldCache(false);
        request.setRetryPolicy(new DefaultRetryPolicy(10000, 1, 1.0f));
        queue.add(request);

    }




    @Override
    public int getItemCount() {
        return mCartModel.size();
    }

    public void clear() {
        int size = mCartModel.size();
        mCartModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Price,Offer,Off,ItemQty;
        ImageView Remove;
        ImageView Plus,Minus;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.cartName);
            Price = (TextView) itemView.findViewById(R.id.cartPrice);
            Offer = (TextView) itemView.findViewById(R.id.cartOffer);
            Off = (TextView) itemView.findViewById(R.id.cartOff);
            ItemQty = (TextView) itemView.findViewById(R.id.cartQty);
            Plus = (ImageView) itemView.findViewById(R.id.cartPlus);
            Minus = (ImageView) itemView.findViewById(R.id.cartMinus);
            Remove = (ImageView) itemView.findViewById(R.id.cartRemove);

        }

    }
}
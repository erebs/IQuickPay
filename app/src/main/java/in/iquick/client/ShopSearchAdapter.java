package in.iquick.client;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ShopSearchAdapter extends RecyclerView.Adapter<ShopSearchAdapter.MyViewHolder> {

    Context ctx;
    private List<ShopSearchModel> mShopSearchModels = new ArrayList<>(1000);
    ShopSearchModel ShopSearchModel;
    SharedPreferences sharedPreferences;

    public ShopSearchAdapter(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("SECGRO",MODE_PRIVATE);
        if(sharedPreferences.getString("PINCODE", "").length()==6)
        {
        }

    }

    public void renewItems(List<ShopSearchModel> list) {
        this.mShopSearchModels = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_search_list, parent,false);
        return new MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ShopSearchModel = mShopSearchModels.get(position);
        holder.Name.setText(ShopSearchModel.getName());
        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intents = new Intent(ctx.getApplicationContext(), ProductActivity.class);
//                intents.putExtra("productID",mShopSearchModels.get(position).getId());
//                ctx.startActivity(intents);

            }

        });
    }

    @Override
    public int getItemCount() {
        return mShopSearchModels.size();
    }

    public void updateList(List<ShopSearchModel> list){
        mShopSearchModels = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name;
        LinearLayout Layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.shopSearchName);
            Layout = itemView.findViewById(R.id.shopSearchLayout);

        }

    }


}
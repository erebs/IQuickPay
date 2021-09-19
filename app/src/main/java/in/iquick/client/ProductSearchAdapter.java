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
import java.util.Locale;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.MyViewHolder> {

    Context ctx;
    private List<ProductSearchModel> mProductSearchModels = new ArrayList<>(1000);
    ProductSearchModel ProductSearchModel;
    SharedPreferences sharedPreferences;

    public ProductSearchAdapter(Context ctx){
        this.ctx = ctx;
        sharedPreferences = ctx.getSharedPreferences("SECGRO",MODE_PRIVATE);
        if(sharedPreferences.getString("PINCODE", "").length()==6)
        {
        }

    }

    public void renewItems(List<ProductSearchModel> list) {
        this.mProductSearchModels = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_search_list, parent,false);
        return new MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        ProductSearchModel = mProductSearchModels.get(position);
        holder.Name.setText(ProductSearchModel.getName());
        holder.Category.setText(ProductSearchModel.getCategory());
        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Intent intents = new Intent(ctx.getApplicationContext(), ProductActivity.class);
//                intents.putExtra("productID",mProductSearchModels.get(position).getId());
//                ctx.startActivity(intents);
                
            }

        });
    }

    @Override
    public int getItemCount() {
        return mProductSearchModels.size();
    }

    public void updateList(List<ProductSearchModel> list){
        mProductSearchModels = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Name,Category;
        LinearLayout Layout;

        public MyViewHolder(View itemView) {
            super(itemView);

            Name = (TextView) itemView.findViewById(R.id.productSearchName);
            Category = (TextView) itemView.findViewById(R.id.productSearchCategory);
            Layout = itemView.findViewById(R.id.productSearchLayout);
            
        }

    }


}
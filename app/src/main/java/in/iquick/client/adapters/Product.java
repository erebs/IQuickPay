package in.iquick.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import in.iquick.client.ProductActivity;
import in.iquick.client.R;
import in.iquick.client.SubCategoryActivity;
import in.iquick.client.models.Products;
import in.iquick.client.models.Products;

public class Product extends RecyclerView.Adapter<Product.MyViewHolder>
{
    Context ctx;
    private List<Products> mProducts = new ArrayList<>();
    Products Products;

    public Product(Context ctx)
    {
        this.ctx = ctx;
    }

    public void renewItems(List<Products> list)
    {
        this.mProducts = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_product_layout, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
            Products = mProducts.get(position);
            holder.Name.setText(mProducts.get(position).getName());
            holder.Desc.setText(mProducts.get(position).getDesc());
            holder.Price.setText("₹"+mProducts.get(position).getPrice());
            if(mProducts.get(position).getBestSeller().equalsIgnoreCase("1"))
                holder.BestSeller.setVisibility(View.VISIBLE);
            else
                holder.BestSeller.setVisibility(View.GONE);
            Glide.with(holder.itemView).load(mProducts.get(position).getImage()).into(holder.Image);
            holder.Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        Intent intents = new Intent(ctx.getApplicationContext(), ProductActivity.class);
                        intents.putExtra("productId",mProducts.get(position).getId());
                        intents.putExtra("productName",mProducts.get(position).getName());
                        ctx.startActivity(intents);
                    }
            });
    }

    @Override
    public int getItemCount()
    {
        return mProducts.size();
    }

    public void clear()
    {
        int size = mProducts.size();
        mProducts.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name,Price,BestSeller,Desc;
        ImageView Image;
        LinearLayout Layout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.productName);
            Price =  itemView.findViewById(R.id.productPrice);
            BestSeller =  itemView.findViewById(R.id.bestSellerBadge);
            Desc =  itemView.findViewById(R.id.productDesc);
            Image = itemView.findViewById(R.id.productImage);
            Layout = itemView.findViewById(R.id.productLayout);
        }

    }
}
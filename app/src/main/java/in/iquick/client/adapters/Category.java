package in.iquick.client.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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

import in.iquick.client.R;
import in.iquick.client.SubCategoryActivity;
import in.iquick.client.models.Categories;

public class Category extends RecyclerView.Adapter<Category.MyViewHolder>
{
    Context ctx;
    private List<Categories> mCategories = new ArrayList<>();
    Categories Categories;

    public Category(Context ctx)
    {
        this.ctx = ctx;
    }

    public void renewItems(List<Categories> list)
    {
        this.mCategories = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_category_layout, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
            Categories = mCategories.get(position);
            holder.Name.setText(mCategories.get(position).getName());
            Glide.with(holder.itemView).load(mCategories.get(position).getImage()).into(holder.Image);
            holder.Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        Intent intents = new Intent(ctx.getApplicationContext(), SubCategoryActivity.class);
                        intents.putExtra("categoryId",mCategories.get(position).getId());
                        intents.putExtra("categoryName",mCategories.get(position).getName());
                        ctx.startActivity(intents);
                    }
            });
    }

    @Override
    public int getItemCount()
    {
        return mCategories.size();
    }

    public void clear()
    {
        int size = mCategories.size();
        mCategories.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name;
        View BottomLine;
        ImageView Image;
        LinearLayout Layout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.categoryName);
            BottomLine =  itemView.findViewById(R.id.categoryBottomLine);
            Image = itemView.findViewById(R.id.categoryImage);
            Layout = itemView.findViewById(R.id.categoryLayout);
        }

    }
}
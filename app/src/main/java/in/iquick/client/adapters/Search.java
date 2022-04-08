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
import in.iquick.client.models.Searches;

public class Search extends RecyclerView.Adapter<Search.MyViewHolder>
{
    Context ctx;
    private List<Searches> mSearches = new ArrayList<>();
    Searches Searches;

    public Search(Context ctx)
    {
        this.ctx = ctx;
    }

    public void renewItems(List<Searches> list)
    {
        this.mSearches = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.shop_search_list, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
            Searches = mSearches.get(position);
            holder.Name.setText(mSearches.get(position).getName());
            holder.Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        Intent intents = new Intent(ctx.getApplicationContext(), ProductActivity.class);
                        intents.putExtra("productId",mSearches.get(position).getId());
                        intents.putExtra("productName",mSearches.get(position).getName());
                        ctx.startActivity(intents);
                    }
            });
    }

    @Override
    public int getItemCount()
    {
        return mSearches.size();
    }

    public void clear()
    {
        int size = mSearches.size();
        mSearches.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name;
        LinearLayout Layout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.shopSearchName);
            Layout = itemView.findViewById(R.id.shopSearchLayout);
        }

    }
}
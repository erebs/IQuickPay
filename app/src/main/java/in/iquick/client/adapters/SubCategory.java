package in.iquick.client.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import in.iquick.client.R;
import in.iquick.client.models.SubCategories;

public class SubCategory extends RecyclerView.Adapter<in.iquick.client.adapters.SubCategory.MyViewHolder>
{
    Context ctx;
    private List<SubCategories> mSubCategories = new ArrayList<>();
    SubCategories SubCategories;
    int row_index = 0;
    private GetSubCategory getSubCategory;

    public SubCategory(Context ctx)
    {
        this.ctx = ctx;
        try
        {this.getSubCategory = ((GetSubCategory) ctx);}
        catch (ClassCastException e)
        {throw new ClassCastException("Activity must implement AdapterCallback."); }
    }

    public void renewItems(List<SubCategories> list)
    {
        this.mSubCategories = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_sub_category_layout, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
            SubCategories = mSubCategories.get(position);
            holder.Name.setText(SubCategories.getName());
            holder.Name.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {
                        row_index = position;
                        notifyDataSetChanged();
                        getSubCategory.SubCategory(mSubCategories.get(position).getId());
                    }
            });

        if(row_index == position)
        {
            holder.BottomLine.setVisibility(View.VISIBLE);
            holder.Name.setTextColor(ctx.getResources().getColor(R.color.purple_50));
            holder.Name.setTypeface(null, Typeface.BOLD);
        }
        else
        {
            holder.BottomLine.setVisibility(View.GONE);
            holder.Name.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.Name.setTypeface(null, Typeface.NORMAL);
        }
    }

    public interface GetSubCategory
    {
        void SubCategory(String subCategoryId);
    }

    @Override
    public int getItemCount()
    {
        return mSubCategories.size();
    }

    public void clear()
    {
        int size = mSubCategories.size();
        mSubCategories.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView Name;
        View BottomLine;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            Name = itemView.findViewById(R.id.subCatagoryName);
            BottomLine =  itemView.findViewById(R.id.subCatagoryBottomLine);
        }

    }
}
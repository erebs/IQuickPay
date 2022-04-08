package in.iquick.client.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import in.iquick.client.R;
import in.iquick.client.models.OrderModel;
import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder>
{
    Context ctx;
    private List<OrderModel> mOrderModel = new ArrayList<>();
    OrderModel OrderModel;
    int row_index = 0;

    public OrderAdapter(Context ctx)
    {
        this.ctx = ctx;
    }

    public void renewItems(List<OrderModel> list)
    {
        this.mOrderModel = list;
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_order_layout, parent,false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, @SuppressLint("RecyclerView") final int position)
    {
            OrderModel = mOrderModel.get(position);
        holder.ID.setText("#"+OrderModel.getId());
        holder.Grand.setText("₹"+OrderModel.getGrand());
        holder.Psts.setText(OrderModel.getPstatus());
        holder.Placed_disc.setText(OrderModel.getPlaced_desc());
        holder.Placed.setText(OrderModel.getPlaced());

            holder.Layout.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                    {

                    }
            });

        if(row_index == position)
        {

        }
        else
        {

        }
    }

    @Override
    public int getItemCount()
    {
        return mOrderModel.size();
    }

    public void clear()
    {
        int size = mOrderModel.size();
        mOrderModel.clear();
        notifyItemRangeRemoved(0, size);
        Log.e("ClearTS","Done");
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView ID, Psts, Total, Discount, Grand, Placed, Placed_disc;
        LinearLayout Layout;

        public MyViewHolder(View itemView)
        {
            super(itemView);
            ID = itemView.findViewById(R.id.rv_order_id);
            Psts = itemView.findViewById(R.id.rv_order_status);
            Total = itemView.findViewById(R.id.rv_order_total);
            Discount = itemView.findViewById(R.id.rv_order_discount);
            Grand = itemView.findViewById(R.id.rv_order_grand);
            Placed = itemView.findViewById(R.id.rv_order_placed);
            Placed_disc = itemView.findViewById(R.id.rv_order_placed_desc);
            Layout = itemView.findViewById(R.id.rv_order_layout);
        }

    }
}
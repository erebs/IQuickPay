package in.iquick.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class FruitAdapter1 extends RecyclerView.Adapter<FruitAdapter1.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<FruitModel1> mFruitModel1s = new ArrayList<>();
    FruitModel1 fruitModel1;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;
    getAmt getamt;

    public FruitAdapter1(Context ctx){
        this.ctx = ctx;
        try {
            this.getamt = ((getAmt) ctx);
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement AdapterCallback.");
        }
    }

    public void renewItems(List<FruitModel1> list) {
        this.mFruitModel1s = list;
        notifyDataSetChanged();
    }

    @Override
    public FruitAdapter1.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.plans_layout, parent,false);
        return new FruitAdapter1.MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(FruitAdapter1.MyViewHolder holder, final int position) {
        fruitModel1 = mFruitModel1s.get(position);
        holder.amt.setText("₹"+fruitModel1.getAmt());
        holder.dis.setText(fruitModel1.getDis());
        holder.val.setText(fruitModel1.getVal());

        holder.cardvidparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getamt.Plan(mFruitModel1s.get(position).getAmt());

            }

        });
    }

    @Override
    public int getItemCount() {
        return mFruitModel1s.size();
    }

    public void updateList(List<FruitModel1> list){
        mFruitModel1s = list;
        notifyDataSetChanged();
    }

    public interface getAmt{
        void Plan(String amt);
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView amt,dis,val;

        LinearLayout cardvidparent;
        public MyViewHolder(View itemView) {
            super(itemView);

            amt = (TextView) itemView.findViewById(R.id.amt);
            dis = (TextView) itemView.findViewById(R.id.dis);
            val = (TextView) itemView.findViewById(R.id.val);

            cardvidparent = itemView.findViewById(R.id.scard);
        }

    }


}
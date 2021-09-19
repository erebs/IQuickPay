package in.iquick.client;

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

public class StatementAdaptor extends RecyclerView.Adapter<StatementAdaptor.MyViewHolder> {

    private LayoutInflater inflater;
    Context ctx;
    private List<StatementModel> mStatementModels = new ArrayList<>();
    StatementModel StatementModel;
    String vidID;
    SharedPreferences sharedPreferences;
    Context context;
    Fragment LocationFrag;
    FragmentTransaction ft;

    public StatementAdaptor(Context ctx){
        this.ctx = ctx;

    }

    public void renewItems(List<StatementModel> list) {
        this.mStatementModels = list;
        notifyDataSetChanged();
    }

    @Override
    public StatementAdaptor.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.statement_layout, parent,false);
        return new StatementAdaptor.MyViewHolder(inflate);


    }

    @Override
    public void onBindViewHolder(StatementAdaptor.MyViewHolder holder, final int position) {

        StatementModel = mStatementModels.get(position);
        holder.Amount.setText(StatementModel.getAmount());
        holder.Details.setText(StatementModel.getDate());
        holder.Type.setText(StatementModel.getType());

        if(StatementModel.getType().equalsIgnoreCase("Credit"))
            holder.Type.setBackground(ctx.getDrawable(R.drawable.success));
        else
            holder.Type.setBackground(ctx.getDrawable(R.drawable.danger));

    }

    @Override
    public int getItemCount() {
        return mStatementModels.size();
    }

    public void updateList(List<StatementModel> list){
        mStatementModels = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView Amount,Type,Details;

        LinearLayout cardvidparent;
        public MyViewHolder(View itemView) {
            super(itemView);

            Amount = (TextView) itemView.findViewById(R.id.statement_amount);
            Details = (TextView) itemView.findViewById(R.id.statement_details);
            Type = (TextView) itemView.findViewById(R.id.statement_type);

        }

    }


}
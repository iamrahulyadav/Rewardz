package in.dthoughts.innolabs.adzapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.ColorChooser;
import in.dthoughts.innolabs.adzapp.modal.Transactions;

import java.util.List;
import java.util.Random;

public class TransactionsListAdapter extends RecyclerView.Adapter<TransactionsListAdapter.ViewHolder> {

    private Context context;
    private List<Transactions> TransactionsList;


    public TransactionsListAdapter(Context context, List<Transactions> transactionsList) {
        this.context = context;
        this.TransactionsList = transactionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_transactions, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionsListAdapter.ViewHolder holder, int position) {

        holder.parentLayout.setBackground(ColorChooser.ColorChooser());//settings a random color as background dynamically

        holder.publisherName.setText(context.getResources().getString(R.string.offer_redeemed) +" " +TransactionsList.get(position).getPublisher_name());
        holder.timestamp.setText(TransactionsList.get(position).getTime_stamp());

    }

    @Override
    public int getItemCount() {
        if(TransactionsList.size() >0){
            return TransactionsList.size();
        }
        else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView timestamp, publisherName;
        LinearLayout parentLayout;

        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            //TODO:et all the find vew by id here for this layout
            parentLayout = mView.findViewById(R.id.parentLayout);
            publisherName = mView.findViewById(R.id.publisherName);
            timestamp = mView.findViewById(R.id.timestamp);
        }
    }
}

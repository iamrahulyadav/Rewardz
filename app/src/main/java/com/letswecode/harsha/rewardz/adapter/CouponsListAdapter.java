package com.letswecode.harsha.rewardz.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.helper.ColorChooser;
import com.letswecode.harsha.rewardz.modal.Transactions;

import java.util.List;
import java.util.Random;

public class CouponsListAdapter extends RecyclerView.Adapter<CouponsListAdapter.ViewHolder> {

    private Context context;
    private List<Transactions> TransactionsList;

    public CouponsListAdapter(Context context, List<Transactions> transactionsList) {
        this.context = context;
        this.TransactionsList = transactionsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_coupons, parent, false);
        return new CouponsListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponsListAdapter.ViewHolder holder, int position) {

        holder.parentLayout.setBackground(ColorChooser.ColorChooser());//settings a random color as background dynamically

        holder.publisherName.setText(context.getResources().getString(R.string.offer_redeemed) +" " +TransactionsList.get(position).getPublisher_name());
        holder.expiresOn.setText(context.getResources().getString(R.string.offer_expires_on)  +" " +TransactionsList.get(position).getExpires_on()); //TODO:chnge hard coded strings to strings.xml
        holder.couponCode.setText(TransactionsList.get(position).getCoupon_code());
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
        TextView expiresOn, publisherName,couponCode;
        LinearLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            //TODO:et all the find vew by id here for this layout
            parentLayout = mView.findViewById(R.id.parentLayout);
            publisherName = mView.findViewById(R.id.publisherName);
            expiresOn = mView.findViewById(R.id.expireson);
            couponCode = mView.findViewById(R.id.couponCode);


        }
    }

}

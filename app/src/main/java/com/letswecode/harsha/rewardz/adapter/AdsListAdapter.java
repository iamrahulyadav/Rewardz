package com.letswecode.harsha.rewardz.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.modal.Ads;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdsListAdapter extends RecyclerView.Adapter<AdsListAdapter.ViewHolder> {

    public List<Ads> AdsList;

    public AdsListAdapter(List<Ads> AdsList) {
        this.AdsList = AdsList;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(AdsList.get(position).getPublisher_image()).into(holder.profile_pic);
        holder.publisher_name.setText(AdsList.get(position).getPublisher_name());
        holder.expires_on.setText(AdsList.get(position).getExpires_on());
        Picasso.get().load(AdsList.get(position).getAd_banner()).into(holder.ad_banner);
        holder.ad_description.setText(AdsList.get(position).getAd_description());
        holder.ad_url.setText(AdsList.get(position).getAd_url());

    }

    @Override
    public int getItemCount() {
        if(AdsList.size() >0){
            return AdsList.size();
        }
        else{
                return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public TextView publisher_name, city, expires_on, ad_description, ad_url;
        public ImageView profile_pic, ad_banner;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            profile_pic = mView.findViewById(R.id.profilePic);
            publisher_name = mView.findViewById(R.id.name);
            expires_on = mView.findViewById(R.id.expireson);
            ad_description = mView.findViewById(R.id.adDescription);
            ad_url = mView.findViewById(R.id.txtUrl);
            ad_banner = mView.findViewById(R.id.adBanner);


        }
    }
}

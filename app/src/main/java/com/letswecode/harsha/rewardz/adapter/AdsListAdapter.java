package com.letswecode.harsha.rewardz.adapter;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.letswecode.harsha.rewardz.MainActivity;
import com.letswecode.harsha.rewardz.R;
import com.letswecode.harsha.rewardz.modal.Ads;
import com.letswecode.harsha.rewardz.ui.DetailAdActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdsListAdapter extends RecyclerView.Adapter<AdsListAdapter.ViewHolder> {

    Context context;
    public List<Ads> AdsList;
    public AdsListAdapter(Context context, List<Ads> adsList) {
        this.context = context;
        this.AdsList = adsList;
    }



//    public AdsListAdapter(List<Ads> AdsList) {
//        this.AdsList = AdsList;
//
//    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Picasso.get().load(AdsList.get(position).getPublisher_image()).into(holder.profile_pic);
        holder.publisher_name.setText(AdsList.get(position).getPublisher_name());
        holder.expires_on.setText(AdsList.get(position).getExpires_on());
        Picasso.get().load(AdsList.get(position).getAd_banner()).into(holder.ad_banner);
        holder.ad_description.setText(AdsList.get(position).getAd_description());
        holder.ad_url.setText(AdsList.get(position).getAd_url());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Toast.makeText(context,  AdsList.get(position).getPublisher_name(),Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, DetailAdActivity.class);
                    intent.putExtra("adPublisherPic", AdsList.get(position).getPublisher_image());
                    intent.putExtra("adPublisherName", AdsList.get(position).getPublisher_name());
                    intent.putExtra("adExpiresOn", AdsList.get(position).getExpires_on());
                    intent.putExtra("adBanner", AdsList.get(position).getAd_banner());
                    intent.putExtra("adDescription", AdsList.get(position).getAd_description());
                    intent.putExtra("adUrl", AdsList.get(position).getAd_url());
                    intent.putExtra("adType", AdsList.get(position).getAd_type());
                    intent.putExtra("adVideoUrl", AdsList.get(position).getVideo_url());
                    intent.putExtra("adPoints", AdsList.get(position).getPoints());
                    context.startActivity(intent);
            }
        });

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
        public LinearLayout parentLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            parentLayout = mView.findViewById(R.id.parentLayout);
            profile_pic = mView.findViewById(R.id.profilePic);
            publisher_name = mView.findViewById(R.id.name);
            expires_on = mView.findViewById(R.id.expireson);
            ad_description = mView.findViewById(R.id.adDescription);
            ad_url = mView.findViewById(R.id.txtUrl);
            ad_banner = mView.findViewById(R.id.adBanner);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(context, DetailAdActivity.class);
//                    intent.putExtra("adPublisherPic", AdsList.get(getAdapterPosition()).getPublisher_image());
//                    intent.putExtra("adPublisherName", AdsList.get(getAdapterPosition()).getPublisher_name());
//                    intent.putExtra("adExpiresOn", AdsList.get(getAdapterPosition()).getExpires_on());
//                    intent.putExtra("adBanner", AdsList.get(getAdapterPosition()).getAd_banner());
//                    intent.putExtra("adDescription", AdsList.get(getAdapterPosition()).getAd_description());
//                    intent.putExtra("adUrl", AdsList.get(getAdapterPosition()).getAd_url());
//                    intent.putExtra("adType", AdsList.get(getAdapterPosition()).getAd_type());
//                    intent.putExtra("adVideoUrl", AdsList.get(getAdapterPosition()).getVideo_url());
//                    intent.putExtra("adPoints", AdsList.get(getAdapterPosition()).getPoints());
//                }
//            });

        }
    }
}

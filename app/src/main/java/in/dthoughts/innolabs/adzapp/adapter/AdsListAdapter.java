package in.dthoughts.innolabs.adzapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.helper.ColorChooser;
import in.dthoughts.innolabs.adzapp.modal.Ads;
import in.dthoughts.innolabs.adzapp.ui.DetailAdActivity;
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

        holder.parentLayout.setBackground(ColorChooser.ColorChooser());//settings a random color as background dynamically

        //Picasso.get().load(AdsList.get(position).getPublisher_image()).placeholder(R.drawable.ic_sort_white_24dp).error(R.drawable.ic_error_black_24dp).into(holder.profile_pic);
        Picasso.with(context).load(AdsList.get(position).getPublisher_image()).placeholder(R.drawable.ic_sort_white_24dp).error(R.drawable.ic_error_black_24dp).into(holder.profile_pic);
        Log.d("doc",AdsList.get(position).getPublisher_image());
        holder.publisher_name.setText(AdsList.get(position).getPublisher_name());
        holder.expires_on.setText(AdsList.get(position).getExpires_on());
        //Picasso.get().load(AdsList.get(position).getAd_banner()).into(holder.ad_banner);
        Picasso.with(context).load(AdsList.get(position).getAd_banner()).into(holder.ad_banner);
        Log.d("doc",AdsList.get(position).getAd_banner());
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
                    intent.putExtra("adCouponCode", AdsList.get(position).getCoupon_code());
                    intent.putExtra("adID", AdsList.get(position).getKey());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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


        }
    }
}

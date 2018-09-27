package in.dthoughts.innolabs.adzapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

import in.dthoughts.innolabs.adzapp.R;
import in.dthoughts.innolabs.adzapp.ui.PublisherDetailActivity;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

    Context context;
    ArrayList<String> publisherNameList;
    ArrayList<String> profilePicList;
    String SearchedString;

    class SearchViewHolder extends RecyclerView.ViewHolder {

        ImageView ProfilePic;
        TextView PublisherName;
        LinearLayout parentLayout;

        public SearchViewHolder(View itemView) {
            super(itemView);

            ProfilePic = itemView.findViewById(R.id.profile_Pic);
            PublisherName = itemView.findViewById(R.id.publisher_Name);
            parentLayout = itemView.findViewById(R.id.parentLayout);
        }
    }

    public SearchAdapter(Context context, ArrayList<String> publisherNameList, ArrayList<String> profilePicList, String SearchedString) {
        this.context = context;
        this.publisherNameList = publisherNameList;
        this.profilePicList = profilePicList;
        this.SearchedString = SearchedString;
        Log.d("docc5", SearchedString);

    }

    @NonNull
    @Override
    public SearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.listview_search, parent, false);
        ;
        return new SearchAdapter.SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {
        String text = publisherNameList.get(position).toString();
        Spannable spanText = Spannable.Factory.getInstance().newSpannable(text);
        int i = text.toLowerCase(Locale.getDefault()).indexOf(SearchedString);
        if (i != -1) {
            spanText.setSpan(new ForegroundColorSpan(Color.GREEN), i,
                    i + SearchedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanText.setSpan(new StyleSpan(Typeface.BOLD), i,
                    i + SearchedString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.PublisherName.setText(spanText, TextView.BufferType.SPANNABLE);
        } else {
            holder.PublisherName.setText(text, TextView.BufferType.NORMAL);
        }

        //holder.PublisherName.setText(/*ssBuilder*/publisherNameList.get(position).toString());
        Picasso.with(context).load(profilePicList.get(position)).placeholder(R.drawable.ic_sort_black_24dp).error(R.drawable.ic_error_black_24dp).into(holder.ProfilePic);

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PublisherDetailActivity.class);
                intent.putExtra("Publisher_Name", publisherNameList.get(position).toString());
                intent.putExtra("Publisher_Image", profilePicList.get(position).toString());
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return publisherNameList.size();
    }


}

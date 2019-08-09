package com.wisemindsolution.travelmantics;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
//import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;


public class DealAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "DealsRecyclerAdapter";
    private ArrayList<TravelDealModel> mDeals = new ArrayList<>();
    DealAdapter(Activity activity) {
        Log.i(TAG, "create...................");
        FirebaseUtil.openFbReference("traveldeals", activity);
        FirebaseDatabase mFirebaseDatabase = FirebaseUtil.firebaseDatabase;
        DatabaseReference mDatabaseReference = FirebaseUtil.databaseReference;
        ChildEventListener mChildEventListener = setListener();
        mDatabaseReference.addChildEventListener(mChildEventListener);
    }
    private ChildEventListener setListener() {
        Log.i(TAG, "listener...................");
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, dataSnapshot.toString());
                TravelDealModel deal = null;
                try{
                deal = dataSnapshot.getValue(TravelDealModel.class);
                }catch (Exception e){
                Log.i(TAG, e.toString());
                }
                if (deal != null){ Log.i(TAG, deal.getTitle());
                mDeals.add(deal);
                }
               notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "listener...................childchanged");

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Log.i(TAG, dataSnapshot.getValue().toString());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.i(TAG, "listener...................childmoved");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_card, parent, false);
        return new ContentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
                ContentViewHolder dealItemHolder = (ContentViewHolder) holder;
                dealItemHolder.deal = mDeals.get(position);
                dealItemHolder.mTitleView.setText(Html.fromHtml(dealItemHolder.deal.getTitle()));
                dealItemHolder.mDescriptionView.setText(dealItemHolder.deal.getDescription());
                dealItemHolder.mPrice.setText(dealItemHolder.deal.getPrice());
                showImage(dealItemHolder.deal.getImageUrl(), dealItemHolder.mImageView);
    }

    @Override
    public int getItemCount() {
        return mDeals.size();
    }
    private void showImage(String url, ImageView image){
        if(url != null && !url.isEmpty()){
            Picasso.get()
                    .load(url)
                    .resize(160, 160)
                    .centerCrop()
                    .into(image);
        }
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final View mView;
        private final TextView mTitleView;
        private final TextView mDescriptionView;
        private final TextView mPrice;
        private final ImageView mImageView;
        private TravelDealModel deal;

        ContentViewHolder(View view) {
            super(view);
            mView = view;
            mTitleView = view.findViewById(R.id.list_title_view);
            mDescriptionView = view.findViewById(R.id.list_description_view);
            mImageView = view.findViewById(R.id.list_image_view);
            mPrice = view.findViewById(R.id.list_price_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            TravelDealModel deal = mDeals.get(position);
            Intent intent = new Intent(v.getContext(), DealActivity.class);
            intent.putExtra("deal", deal);
            v.getContext().startActivity(intent);
        }
    }
}

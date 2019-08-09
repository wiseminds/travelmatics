package com.wisemindsolution.travelmantics;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.res.Resources;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class DealActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "DEAL_ACTIVITY";
    private static final int REQUEST_CODE = 10;
EditText titleTxtView;
EditText descriptionTxtView;
EditText priceTxtView;
TravelDealModel mDeal ;
ImageView imageView;
Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseUtil.openFbReference("traveldeals", this);
        titleTxtView = findViewById(R.id.titleTextView);
        descriptionTxtView = findViewById(R.id.descriptionTxtView);
        priceTxtView = findViewById(R.id.priceTextView);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        TravelDealModel deal = intent.getParcelableExtra("deal");
        if (deal == null) deal = new TravelDealModel(null,null, null, null, null, null);
        mDeal = deal;
        imageView = findViewById(R.id.imageView);
//        Log.i(TAG, deal.getImageUrl());
        if (deal.getImageUrl() != null) showImage(deal.getImageUrl());
        titleTxtView.setText(deal.getTitle());
        descriptionTxtView.setText(deal.getDescription());
        priceTxtView.setText(deal.getPrice());

        button = findViewById(R.id.selectImageButton);
        button.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater =  getMenuInflater();
        inflater.inflate(R.menu.deal_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_menu:
                saveDeal();
                Toast.makeText(this, "Deal Saved", Toast.LENGTH_LONG).show();
                clean();
                finish();
                return true;
            case R.id.delete_deal:
                deleteDeal(mDeal);
                finish();
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void clean() {
        titleTxtView.setText("");
        descriptionTxtView.setText("");
        priceTxtView.setText("");
        titleTxtView.requestFocus();
    }

    private void saveDeal() {
        mDeal.setTitle(titleTxtView.getText().toString());
        mDeal.setDescription(descriptionTxtView.getText().toString());
        mDeal.setPrice(priceTxtView.getText().toString());

        if(mDeal.getId() == null )  {
            mDeal.setId("ghsgfhsfgh");
            FirebaseUtil.databaseReference.push().setValue(mDeal);
        }
        else  FirebaseUtil.databaseReference.child(mDeal.getId()).setValue(mDeal);
    }
    private void deleteDeal(TravelDealModel deal ){
        if(deal.getId() == null) Toast.makeText(this, "Please save a deal to delete it", Toast.LENGTH_LONG).show();
        else {
            FirebaseUtil.databaseReference.child(deal.getId()).removeValue();
        }
        Log.i(TAG, deal.getImageName());
        if(deal.getImageName() != null && !deal.getImageName().isEmpty()){
         StorageReference ref = FirebaseUtil.mStorage.getReference().child(deal.getImageName());
         ref.delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
             @Override
             public void onSuccess(Void aVoid) {
                 Log.i(TAG, "Image deleted successfully");
             }
         }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Log.i(TAG, "Failure deleting Image");

             }
         });
        }
    }
    private void backToList(){
    Intent intent = new Intent(this, TravelListActivity.class);
    startActivity(intent);
    }
    private void showImage(String url){
        Log.i(TAG, "cxcnxcxcn.......................................nnnnnnnn...");
        if(url != null && !url.isEmpty()){                    Log.i(TAG, url);

            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Picasso.get()
                    .load(url)
                    .resize(width, width *2/3)
                    .centerCrop()
                    .error(R.drawable.ic_launcher_background)
                    .into(imageView);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == REQUEST_CODE && resultCode == RESULT_OK ){
            Uri imageUri = data.getData();
            final StorageReference ref = FirebaseUtil.mStorageRef.child(imageUri.getLastPathSegment());
            Log.i(TAG, "cxcnxcxcn..........................................");


            ref.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    String pictureName = taskSnapshot.getStorage().getPath();
                    mDeal.setImageName(pictureName);

                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            mDeal.setImageUrl(uri.toString());
                            Log.i(TAG, mDeal.getImageName());

                            showImage(uri.toString());

                        }
                    });

                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
        intent2.setType("image/jpeg");
        intent2.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent2, "Insert Image"), REQUEST_CODE);
    }
}

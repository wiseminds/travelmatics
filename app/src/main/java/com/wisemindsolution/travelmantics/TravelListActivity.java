package com.wisemindsolution.travelmantics;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.squareup.picasso.Picasso;

public class TravelListActivity extends AppCompatActivity {
    private final String TAG = "TravelListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Picasso.get().setLoggingEnabled(true);
        setContentView(R.layout.activity_travel_list);
        FirebaseUtil.openFbReference("traveldeals", this);
        RecyclerView recyclerView = findViewById(R.id.dealsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DealAdapter dealAdapter = new DealAdapter(this);
        recyclerView.setAdapter(dealAdapter);



    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtil.detatchListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtil.attachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_travel_list, menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.insert_menu:
                Intent intent = new Intent(this, DealActivity.class);
                startActivity(intent);
                return true;
            case R.id.log_out_menu:
                AuthUI.getInstance()
                        .signOut(this)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                FirebaseUtil.attachListener();
                            }
                        });
                FirebaseUtil.detatchListener();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }
}



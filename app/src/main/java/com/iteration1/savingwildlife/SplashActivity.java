package com.iteration1.savingwildlife;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;

import com.google.firebase.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SplashActivity extends AppCompatActivity {
    private ArrayList<Beach> beachList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent();
        intent.setClass(SplashActivity.this, MainActivity.class);
        String imei = getImei(getApplicationContext());
        readUser(imei);
        startActivity(intent);
        finish();
    }

    // The get imei method
    public String getImei(Context context) {
        TelephonyManager telephonyMgr = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
        String imei = "";
        if (ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_PHONE_STATE)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_PHONE_STATE},
                        1);
            }
        }
        imei = telephonyMgr.getDeviceId();
        return imei ;
    }

    private void readUser(String i) {
        // Get the reference of firebase instance
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");
        databaseReference.orderByChild("ID").equalTo(i);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean add = true;
                for(DataSnapshot data: dataSnapshot.getChildren()){
                    // Check imei exist
                    if (data.child("imei").getValue().toString().equals(i)) {
                        add = false;
                    }
                }
                if (add){
                    String id = databaseReference.push().getKey();
                    databaseReference.child(id).child("imei").setValue(i);
                    Random random = new Random();
                    int length = random.nextInt(15);
                    databaseReference.child(id).child("name").setValue(UIUtils.getRandomString(length));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return;
            }

        }
    }

}


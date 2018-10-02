package com.iteration1.savingwildlife;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.iteration1.savingwildlife.entities.Beach;
import com.iteration1.savingwildlife.entities.Report;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class MakeReport extends AppCompatActivity {
    private Toolbar toolbar;
    private ArrayList<Beach> beachList1;
    private ArrayList<Beach> beachList;
    private Spinner beachName;
    private Spinner eventType;
    private TextView toolbar_title;
    private ImageView imageUploaded;
    private DatePicker eventDate;
    private Button btnSave;
    private Button btnUpload;
    private Beach option;
    private String beachname;
    private long startDate;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;
    //    private String typeForEmail;
    private String event_date;
    private String address;
    //    private Image image;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    StorageReference imageForEmail;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page);
        covertToLongType();
        initUI();
        beachList1 = new ArrayList<>();
        new LoadTask().execute();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveInfo();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Attention")
                .setMessage("This report functionality is used to help you stop some inappropriate behaviors " +
                        "which may possibly threat to the local marine life, it will enable you to " +
                        "send an Email to council or wildlife organization, please use it carefully!")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void chooseImage() {
        Intent newIntent = new Intent();
        newIntent.setType("image/*");
        newIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(newIntent, "Select Picture"),PICK_IMAGE_REQUEST);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageUploaded.setImageBitmap(bitmap);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void saveInfo() {
        if(filePath != null && !eventType.getSelectedItem().toString().equals("Select one") && !beachName.getSelectedItem().toString().equals("Select one"))
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("event images/"+ UUID.randomUUID().toString());
//            imageForEmail = ref;
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(MakeReport.this, "the report has been uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(MakeReport.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
            beachname = beachName.getSelectedItem().toString();
            String type = eventType.getSelectedItem().toString();
            String typeForEmail = type;
            Integer year = eventDate.getYear();
            Integer month = eventDate.getMonth() + 1;
            Integer Day = eventDate.getDayOfMonth();
            event_date = Day.toString() + "-" + month.toString() + "-" + year.toString();
            // String dateForEmail = event_date;
            String id = databaseReference.push().getKey();
            Report event = new Report(type,event_date);
            databaseReference.child(id).child("beach_name").setValue(beachname);
            databaseReference.child(id).child("event_type").setValue(type);
            databaseReference.child(id).child("event_date").setValue(event_date);
            databaseReference.child(id).child("reference").setValue(ref.toString());

            Log.i("Send email", "");
            int a = beachList1.size();
            for(int i = 0;i < beachList.size();i ++){
                if (beachList.get(i).getName().equals(beachname)) {
                    address = beachList.get(i).getEmail();
                }
            }
            String text = "Dear Sir/Madam,\nI am at " + beachname + ". At the date " + event_date + ", I found someone is doing "
                    + typeForEmail + " which in my concern may do harm to our local marine animals or damage environment. " +
                    "Please come to stop these behaviours ASAP. Thanks.\n " +
                    "Please find the attached image that I took of this behaviour.\n Regards";
            String[] TO = {address};
            String[] CC = {""};
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setType("application/image");
            emailIntent.setPackage("com.google.android.gm");
            emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, TO);
            emailIntent.putExtra(android.content.Intent.EXTRA_CC, CC);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Incident report");
            emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, text);
            emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            emailIntent.putExtra(Intent.EXTRA_STREAM, filePath);

            try {
                startActivity(Intent.createChooser(emailIntent, "send email..."));
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(MakeReport.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }
//            Intent intent = new Intent();
//            intent.setClass(getApplicationContext(), InfoPage.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("beach", option);
//            intent.putExtras(bundle);
//            startActivity(intent);
            eventType.setSelection(0);
            imageUploaded.setImageDrawable(getResources().getDrawable(R.drawable.ic_image_black_24dp));
        }
        else{
            Toast.makeText(MakeReport.this, "You miss type or image!", Toast.LENGTH_SHORT).show();
        }
    }


    private void initUI(){
        toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageUploaded = (ImageView) findViewById(R.id.imageView1);
        beachName = (Spinner) findViewById(R.id.spinnerForBeachName);
        eventType = (Spinner) findViewById(R.id.spinner1);
        eventDate = (DatePicker) findViewById(R.id.datePicker1);
        eventDate.setMaxDate(new Date().getTime());
        eventDate.setMinDate(startDate);
        btnUpload = (Button) findViewById(R.id.uploadbtn);
        btnSave = (Button) findViewById(R.id.savebtn);
        databaseReference = FirebaseDatabase.getInstance().getReference("report");
//        Intent intent1 = getIntent();
//        Bundle bundle = intent1.getExtras();
//        assert bundle != null;
//        option = (Beach) bundle.getSerializable("selected");
//        assert option != null;
//        beachname = option.getName();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        String titlePlus = "Report";
//        toolbar_title.setText(titlePlus + beachname);
        // Back to former page
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void covertToLongType(){
        try {
            String dateString = "01/01/2018";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date date = sdf.parse(dateString);

            startDate = date.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private class LoadTask extends AsyncTask<String, Integer, String> {

        @MainThread
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            ss.show(2000);
            Log.d("pre", "onPreExecute() called");
        }

        @WorkerThread
        @Override
        protected String doInBackground(String... params) {

            // Get the reference of firebase instance
            DatabaseReference mReference = FirebaseDatabase.getInstance().getReference("beaches");

            mReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        Beach b = child.getValue(Beach.class);
                        beachList1.add(b);
                    }
                    beachList = beachList1;
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getDetails());
                }
            });

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progresses) {
            Log.d("mprogress", "onProgressUpdate(Progress... progresses) called");
        }

        @MainThread
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute("finish");
            Log.d("post", "onPostExecute(Result result) called");
        }

        @Override
        protected void onCancelled() {
            Log.d("cancelled", "onCancelled() called");
        }

    }

}
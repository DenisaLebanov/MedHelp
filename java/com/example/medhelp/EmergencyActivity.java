package com.example.medhelp;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class EmergencyActivity extends AppCompatActivity {

    private Button sendButton;
    private Button uploadImageButton;
    private Button getLocationButton;

    private ImageView photo;

    private TextView addressText;

    private EditText editText;

    private String username;
    private String longitude;
    private String latitude;
    private String text;
    private String downloadUrl = "";
    private String completeAddress;
    private String time;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Geocoder geocoder;

    private Uri photoUri;

    private boolean locationButtonIsPressed = false;
    private boolean uploadButtonIsPressed = false;
    private boolean popupWasDisplayed = false;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReference1;

    private int number = 1;

    private View popupView;

    private Button okButton;

    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emergency_activity);

        sendButton = findViewById(R.id.SendEmergencyButton);
        uploadImageButton = findViewById(R.id.TakeAPhotoButton);
        getLocationButton = findViewById(R.id.GetLocationButton);

        photo = findViewById(R.id.Photo);

        addressText = findViewById(R.id.AddressTextView);

        editText = findViewById(R.id.MultilineText);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
        databaseReference = firebaseDatabase.getReference("Emergencies");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                while (snapshot.hasChild("Emergency" + number))
                    number++;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        handleSendButton();
        handleUploadButton();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitude = location.getLatitude() + "";
                longitude = location.getLongitude() + "";

                geocoder = new Geocoder(EmergencyActivity.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    addressText.setText(addresses.get(0).getAddressLine(0) + " ");
                    getLocationButton.setBackgroundColor(Color.GREEN);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Intent intent1 = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent1);
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.INTERNET
                }, 22);
                return;
            }
        } else
            configureButton();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 22:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configureButton();
                return;
        }
    }

    private void configureButton() {
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EmergencyActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                locationManager.requestLocationUpdates("gps", 1000, 1, locationListener);
                locationButtonIsPressed = true;
            }
        });
    }

    private void handleUploadButton() {
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent();
                intent3.setType("image/*");
                intent3.setAction(Intent.ACTION_GET_CONTENT);
                
                launchActivity.launch(intent3);
                uploadButtonIsPressed = true;
                }
        });
    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void uploadImage(){

        if (photoUri != null){
            time = System.currentTimeMillis() + "";
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Emergency").child(username + "-" + time + "." + getFileExtension(photoUri));

            storageReference.putFile(photoUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            Log.d("Denisa", downloadUrl);
                            Log.d("Denisa", "Imagine incarcata cu succes!");
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Log.d("Denisa", "Astepata...");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Denisa", "Eroare upload imagine!");
                }
            });
        }
    }
    
    ActivityResultLauncher<Intent> launchActivity = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK){
            Intent data = result.getData();
            
            if (data != null && data.getData() != null){
                photoUri = data.getData();
                Bitmap photoBitmap = null;
                try {
                    photoBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                }
                catch (IOException e){
                    e.printStackTrace();
                }
                photo.setImageBitmap(photoBitmap);
                uploadImageButton.setBackgroundColor(Color.GREEN);
                uploadImage();
            }
        }
    });

    private void handleSendButton() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (editText.getText().toString().equals(""))
                    editText.setError("Please eneter a short description of the problem.");

                else if (!locationButtonIsPressed)
                    Toast.makeText(EmergencyActivity.this, "Please press the location button.", Toast.LENGTH_SHORT).show();

                else if (!uploadButtonIsPressed)
                    Toast.makeText(EmergencyActivity.this, "Please add a photo.", Toast.LENGTH_SHORT).show();

                else {
                    text = editText.getText().toString();
                    completeAddress = addressText.getText().toString();

                    firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                    databaseReference1 = firebaseDatabase.getReference("Emergencies");

                    databaseReference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Log.d("Denisa", downloadUrl + "+++++");
                            Emergency emergency = new Emergency(latitude, longitude, completeAddress, downloadUrl, text, username);
                            databaseReference1.child("Emergency" + number).setValue(emergency);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d("Denisa", "Eroare la scriere urgenta EmergencyActivity!");
                        }
                    });

                    if (!popupWasDisplayed) {

                        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
                        popupView = layoutInflater.inflate(R.layout.popup_emergency, null);

                        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                        int height = LinearLayout.LayoutParams.WRAP_CONTENT;

                        popupWindow = new PopupWindow(popupView, width, height, true);

                        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                        View background = (View) popupWindow.getContentView().getParent();
                        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) background.getLayoutParams();
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                        layoutParams.dimAmount = 0.75f;
                        windowManager.updateViewLayout(background, layoutParams);

                        okButton = popupView.findViewById(R.id.OkButton);

                        okButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                popupWasDisplayed = true;
                                popupWindow.dismiss();
                            }
                        });
                    }
                    else{
                        Intent intent2 = new Intent(EmergencyActivity.this, MainActivityPatients.class);
                        intent2.putExtra("username", username);
                        startActivity(intent2);
                    }
                }
            }
        });
    }
}
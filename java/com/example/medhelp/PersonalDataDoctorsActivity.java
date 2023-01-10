package com.example.medhelp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PersonalDataDoctorsActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;

    private Button saveButton;
    private Button uploadButton;

    private TextInputLayout firstNameLayout;
    private TextInputLayout secondNameLayout;
    private TextInputLayout phoneLayout;
    private TextInputLayout specializationLayout;

    private TextInputEditText firstNameText;
    private TextInputEditText secondNameText;
    private TextInputEditText phoneText;
    private TextInputEditText specializationText;

    private String firstName;
    private String secondName;
    private String phone;
    private String specialization;
    private String username = "";
    private String downloadUrl = "";

    private boolean isPressed = false;

    private Uri uriImage;

    private static final int IMAGE_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_data_doctors_activity);

        firstNameLayout = findViewById(R.id.FirstNamePersonalDataDoctorsLayout);
        secondNameLayout = findViewById(R.id.SecondNamePersonalDataDoctorsLayout);
        phoneLayout = findViewById(R.id.PhonePersonalDataDoctorsLayout);
        specializationLayout = findViewById(R.id.SpecializationPersonalDataDoctorsLayout);

        firstNameText = findViewById(R.id.FirstNamePersonalDataDoctorsText);
        secondNameText = findViewById(R.id.SecondNamePersonalDataDoctorsText);
        phoneText = findViewById(R.id.PhonePersonalDataDoctorsText);
        specializationText = findViewById(R.id.SpecializationPersonalDataDoctorsText);

        saveButton = findViewById(R.id.SaveButtonDoctors);
        uploadButton = findViewById(R.id.UploadButtonDoctors);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");

        isPressed = false;

        handleSaveButton();
        handleUploadButton();
    }

    public void handleSaveButton(){
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseDatabase = FirebaseDatabase.getInstance("https://medhelp-79ef0-default-rtdb.europe-west1.firebasedatabase.app/");
                databaseReference = firebaseDatabase.getReference("PersonalDataDoctors");

                firstName = firstNameText.getText().toString().trim();
                secondName = secondNameText.getText().toString().trim();
                phone = phoneText.getText().toString().trim();
                specialization = specializationText.getText().toString().trim();

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        DPersonalData data = new DPersonalData(firstName, secondName, phone, specialization, downloadUrl);
                        if(filterFirstName() && filterSecondName() &&  filterPhone() && filterSpecialization() && isPressed) {
                            databaseReference.child(username).setValue(data);
                            Intent intent = new Intent(PersonalDataDoctorsActivity.this, AddServicesActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("specialization", specialization);
                            intent.putExtra("name", firstName + " " + secondName);
                            startActivity(intent);
                        }
                        else
                            Log.d("Denisa", "Eroare scriere informatii aditionale medic in baza de date!");
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d("Denisa", "Eroare scriere informatii aditionale medic in baza de date!");
                    }
                });
            }
        });
    }

    public boolean filterFirstName(){
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(firstName);
        boolean isValid = matcher.matches();

        firstNameLayout.setError(null);

        if (firstName.isEmpty()) {
            firstNameLayout.setError("This field must be completed!");
            return false;
        }
        else if (!isValid){
            firstNameLayout.setError("Invalid characters!");
            return false;
        }
        else
            firstNameLayout.setError(null);
        return true;
    }

    public boolean filterSecondName(){
        Pattern pattern = Pattern.compile("[A-Za-z]+");
        Matcher matcher = pattern.matcher(secondName);
        boolean isValid = matcher.matches();

        secondNameLayout.setError(null);

        if (secondName.isEmpty()) {
            secondNameLayout.setError("This field must be completed!");
            return false;
        }
        else if (!isValid){
            secondNameLayout.setError("Invalid characters!");
            return false;
        }
        else
            secondNameLayout.setError(null);
        return true;
    }

    public boolean filterPhone(){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher matcher = pattern.matcher(phone);
        boolean isValid = matcher.matches();

        phoneLayout.setError(null);

        if (phone.isEmpty()){
            phoneLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            phoneLayout.setError("Invalid characters!");
            return false;
        }

        else
            phoneLayout.setError(null);
        return true;
    }

    public boolean filterSpecialization(){
        Pattern pattern = Pattern.compile("[A-Za-z ]+");
        Matcher matcher = pattern.matcher(specialization);
        boolean isValid = matcher.matches();

        specializationLayout.setError(null);

        if(specialization.isEmpty()){
            specializationLayout.setError("This field must be completed!");
            return false;
        }

        if (!isValid){
            specializationLayout.setError("Invalid characters!");
            return false;
        }

        else
            specializationLayout.setError(null);
        return true;
    }

    public void handleUploadButton(){
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isPressed = true;
                openImage();
            }
        });
    }

    public void openImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage = data.getData();

            uploadImage();
        }
    }

    public String getFileExtension(Uri imageUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(imageUri));
    }

    public void uploadImage(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (uriImage != null){
            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Doctor's photos").child(username + "." + getFileExtension(uriImage));

            storageReference.putFile(uriImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUrl = uri.toString();
                            Log.d("Denisa", downloadUrl);
                            progressDialog.dismiss();
                            Log.d("Denisa", "Imagine incarcata cu succes!");
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    Double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    Log.d("Denisa", progress + "");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("Denisa", "Eroara upload imagine!");
                }
            });
        }
    }
}
package com.gp.exorra.friendzonecomplete;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//deze klasse dient om aanpassingen te maken aan de account van de gebruiker
public class SettingsActivity extends AppCompatActivity {
    //alle variabelen worden aangemaakt.
    private EditText EdtNameField, EdtSurname, EdtTown;
    private Button BtnConfirm, BtnBack;
    private ImageView ImgProfileImage;

    private String userID, name, surname, town, phone, profileImageURL;

    private Uri resultUri;

    //alle Firebase variabelen worden aangemaakt
    private FirebaseAuth FBAuth;
    private DatabaseReference DBreference;

    //deze methode wordt aangeroepen bij het aanmaken van de activiteit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //alle variabelen krijgen een object uit de activity_settings.xml toegewezen.=
        EdtNameField = findViewById(R.id.name);
        EdtSurname = findViewById(R.id.surname);
        EdtTown = findViewById(R.id.town);
        ImgProfileImage = findViewById(R.id.profileImage);
        BtnBack = findViewById(R.id.back);
        BtnConfirm = findViewById(R.id.confirm);

        //de Firebase variabelen krijgen hun waardes toegewezen
        FBAuth = FirebaseAuth.getInstance();
        userID = FBAuth.getCurrentUser().getUid();
        //er wordt een referentie gelegd naar de gegevens van de gebruiker uit de database
        DBreference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);

        //alle informatie van de gebruiker wordt uit de database gehaald
        getUserInfo();

        ImgProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, 1);
            }
        });

        //als er op de "Confirm" knop wordt gedrukt, word alle info opgeslagen
        BtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInfo();
            }
        });

        //als er op de "Back" knop wordt gedrukt, word alle verandere info verworpen
        BtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });
    }

    //deze methode slaagt alle info op
    private void saveUserInfo() {
        name = EdtNameField.getText().toString();
        surname = EdtSurname.getText().toString();
        town = EdtTown.getText().toString();

        //alle info wordt in een HashMap opgeslagen
        Map userInfo = new HashMap();
        userInfo.put("Name", name);
        userInfo.put("surName", surname);
        userInfo.put("town", town);
        userInfo.put("phone", phone);
        DBreference.updateChildren(userInfo);
        //deze if structuur gaat kijken of er reeds een foto van een gebruiker is opgeslagen
        //indien ja, wordt deze getoond aan de gebruiker op het settings scherm
        if (resultUri != null){
            StorageReference filePath = FirebaseStorage.getInstance().getReference().child("ProfileImages").child(userID);
            Bitmap bitmap = null;

            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), resultUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //hier wordt de geuploade foto gecompresseerd, zodat deze zo min mogelijk plaats in beslag zullen nemen
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] data = baos.toByteArray();

            //hier wordt de foto geupload in de databank
            UploadTask uploadTask = filePath.putBytes(data);
            //als het uploaden mislukt, wordt de methode beeïndigd
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    finish();
                }
            });
            //als het uploaden wel lukt, kan de afbeelding wel worden opgeslagen en wordt de URL opgeslagen in de databank.
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                    Map userInfo = new HashMap();
                    userInfo.put("profileImageUrl", downloadUrl.toString());
                    DBreference.updateChildren(userInfo);

                    finish();
                }
            });
        }
        //als er nog geen afbeelding geupload is, wordt de methode beeïndigt
        else {
            finish();
        }
    }

    //alle informatie van de gebruiker wordt opgehaald en in de settings gezet
    private void getUserInfo() {
        DBreference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    if (map.get("Name") != null){
                       name = map.get("Name").toString();
                        EdtNameField.setText(name);
                    }
                    if (map.get("surName") != null){
                        surname = map.get("surName").toString();
                        EdtSurname.setText(surname);
                    }
                    if (map.get("town") != null){
                        town = map.get("town").toString();
                        EdtTown.setText(town);
                    }
                    Glide.with(ImgProfileImage).clear(ImgProfileImage);
                    if (map.get("profileImageUrl") != null){
                        profileImageURL = map.get("profileImageUrl").toString();
                        switch (profileImageURL){
                            case "default":
                                Glide.with(getApplication()).load(R.drawable.def_user_icon).into(ImgProfileImage);
                                break;
                            default:
                                Glide.with(getApplication()).load(profileImageURL).into(ImgProfileImage);
                                break;
                        }
                    }
                }
            }
            //wordt niet gebruikt
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //de profielfoto in de activiteit krijgt hier de resultUri als bron
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK){
            final Uri imageUri = data.getData();
            resultUri = imageUri;
            ImgProfileImage.setImageURI(resultUri);
        }
    }
}

package com.gp.exorra.friendzonecomplete.Authentication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.gp.exorra.friendzonecomplete.MainActivity;
import com.gp.exorra.friendzonecomplete.R;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    //De nodige variabelen worden aangemaakt
    private Button BtnRegister, BtnDate;
    private EditText ETxEmail, EtxPassword, EtxName, EtxSurName, EtxTown;
    private RadioGroup RdgGender;
    int mYear, mMonth, mDay;

    //De nodige Firebase variabelen worden aangemaakt
    private FirebaseAuth FBAuth;
    private FirebaseAuth.AuthStateListener FBAuthStateListener;

    //methode die wordt opgeroepen bij het aanmaken van de activiteit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //de Firebase Authenticator wordt geinitialiseerd, deze gaat kijken of er nog een gebruiker was ingelogd van de vorige keer dat de app gebruikt werd
        // Indien ja, dan wordt deze gebruiker uitgelogd
        FBAuth = FirebaseAuth.getInstance();
        FBAuthStateListener = new FirebaseAuth.AuthStateListener() {
            final FirebaseUser FBUser = FirebaseAuth.getInstance().getCurrentUser();
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (FBUser != null){
                    FBAuth.signOut();
                }
            }
        };

        //de objecten uit de Register.xml worden aan de variabelen toegewezen
        BtnRegister = findViewById(R.id.register);
        ETxEmail = findViewById(R.id.email);
        EtxPassword = findViewById(R.id.password);
        EtxName = findViewById(R.id.name);
        EtxSurName = findViewById(R.id.surname);
        EtxTown = findViewById(R.id.town);
        RdgGender = findViewById(R.id.radioGroup);
        BtnDate = findViewById(R.id.datebutton);
        Calendar c=Calendar.getInstance();
        mYear=c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);

        //deze methode wordt gebruikt als de gebruiker op de knop voor hun geboortedatum drukt
        //deze opent een dialoogvenster met een kalender, waar de gebruiker zijn/haar geboortedatum kan aanduiden.
        BtnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0);
            }
        });

        //deze methode wordt gebruikt als de gebruiker op Register drukt.
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //de final variabelen worden aangemaakt.
                final String email = ETxEmail.getText().toString();
                final String password = EtxPassword.getText().toString();
                final String name = EtxName.getText().toString();
                final String surname = EtxSurName.getText().toString();
                final String town = EtxTown.getText().toString();
                final String date = BtnDate.getText().toString();
                if (date == ""){
                    return;
                }

                //hier wordt nagekeken wat de ID is van de aangeduide RadioButton.
                //deze wordt doorgegeven aan de final variabele, waar men bepaalt of de geregistreerde gebruiker een man of een vrouw is.
                int selectedID = RdgGender.getCheckedRadioButtonId();
                final RadioButton radioButton = findViewById(selectedID);
                if (radioButton.getText() == null) {
                    //door return zal de methode stopgezet worden en wordt er aan de gebruiker medegedeeld dat ze een geslacht moeten kiezen.
                    Toast.makeText(Register.this,"Please choose a gender", Toast.LENGTH_SHORT);
                    return;
                }

                //deze methode zal een nieuwe gebruiker aanmaken in Firebase met de gebruikte variabelen.
                //de manier waarop gebruikers inloggen is via email en wachtwoord
                //bij het aanmaken van de account wordt de data van de gebruiker automatisch gelinkt aan deze gegevens.
                FBAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()){
                            Toast.makeText(Register.this, "Sign up error", Toast.LENGTH_SHORT);
                        }
                        else{
                            String userId = FBAuth.getCurrentUser().getUid();
                            //er wordt een nieuwe referentie aangemaakt op de locatie "users" met het unieke ID "userID"
                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                            //met een HashMap kan men in één keer meerdere variabelen meegeven om te registreren, in plaats van dit variabele per variabele te doen.
                            Map userInfo = new HashMap<>();
                            userInfo.put("Name", name);
                            userInfo.put("surName", surname);
                            userInfo.put("sex", radioButton.getText().toString());
                            userInfo.put("town", town);
                            userInfo.put("profileImageUrl", "default");
                            userInfo.put("birthDate", date);
                            //Na het toevoegen van de data wordt de data in Firebase geupdatet en wordt de MainActivity gestart.
                            currentUserDb.updateChildren(userInfo);
                            Intent intent = new Intent(Register.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                            return;
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        FBAuth.addAuthStateListener(FBAuthStateListener);
    }

    @Override
    protected  void onStop(){
        super.onStop();
        FBAuth.removeAuthStateListener(FBAuthStateListener);
    }

    //wordt niet gebruikt, maar is wel nodig om een datePicker aan te maken.
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {

    }

    int year, month, day;

    @Override
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, mYear, mMonth, mDay);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;

            BtnDate.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
        }
    };
}

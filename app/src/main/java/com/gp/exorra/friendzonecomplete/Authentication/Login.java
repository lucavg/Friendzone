package com.gp.exorra.friendzonecomplete.Authentication;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.gp.exorra.friendzonecomplete.MainActivity;
import com.gp.exorra.friendzonecomplete.R;

public class Login extends AppCompatActivity {
    //aanmaken van alle benodigde variabelen
    private Button BtnLogin;
    private EditText ETxEmail, EtxPassword;

    //de nodige Firebase variabelen worden aangemaakt
    private FirebaseAuth FBAuth;
    private FirebaseAuth.AuthStateListener FBAuthStateListener;

    //methode die wordt aangeroepen bij het aanmaken van de activiteit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Hier wordt nagekeken of de gebruiker waar men de vorige keer mee inlogde nog steeds ingelogd is. indien ja, dan sluit de activiteit en gaat het over naar de MainActivity.
        FBAuth = FirebaseAuth.getInstance();
        FBAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser FBUser = FirebaseAuth.getInstance().getCurrentUser();
                if (FBUser != null){
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        //De variabelen worden toegewezen aan de objecten die zich in de activity_login.xml bevinden.
        BtnLogin = findViewById(R.id.login);
        ETxEmail = findViewById(R.id.email);
        EtxPassword = findViewById(R.id.password);

        //Deze methode wordt aangeroepen als de gebruiker op de knop Login drukt. het email adres en het wachtwoord worden in een final variabele gezet, wat wilt zeggen dat de
        //waarde van deze variabelen doorheen heel de methode niet meer van waarde zullen of mogen veranderen. De Firebase authenticator wordt aangeroepen en deze gaat met de methode
        //signInWithEmailAndPassword proberen in te loggen op de firebase server. indien dit lukt, gaat men door naar de volgende activiteit doordat de FBAuthStateListener wordt aangeroepen.
        //Indien het inloggen mislukt zal de gebruiker een melding krijgen die zegt dat ze het nog eens moeten proberen.
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ETxEmail.getText().toString() != "" && EtxPassword.getText().toString() != ""){
                    final String email = ETxEmail.getText().toString();
                    final String password = EtxPassword.getText().toString();
                    FBAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                Toast.makeText(Login.this, "login error, please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    //Deze methode wordt aangeroepen bij het opstarten van de activiteit. Deze zal een StateListener toevoegen aan de FBAuthenticator, die zal luisteren of de gebruiker inlogt.
    @Override
    protected void onStart(){
        super.onStart();
        FBAuth.addAuthStateListener(FBAuthStateListener);
    }

    //Het tegengestelde van de onStart, deze methode zal de StateListener stopzetten.
    @Override
    protected  void onStop(){
        super.onStop();
        FBAuth.removeAuthStateListener(FBAuthStateListener);
    }
}

package com.gp.exorra.friendzonecomplete.Authentication;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.gp.exorra.friendzonecomplete.R;

public class ChooseLoginOrRegister extends AppCompatActivity {

    private Button BtnLogin, BtnRegister;

    //methode die wordt aangeroepen bij het maken van de activiteit:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_or_register);

        //declareren van de nodige variabelen:
        BtnLogin = findViewById(R.id.login);
        BtnRegister = findViewById(R.id.register);

        //methode die wordt aangeroepen bij het aanklikken van de login-knop
        //deze methode zal de activiteit "ChooseLoginOrRegister" afsluiten en zal de "login" activiteit aanmaken
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginOrRegister.this, Login.class);
                startActivity(intent);
                finish();
                return;
            }
        });

        //methode die wordt aangeroepen bij het aanklikken van de register-knop
        //deze methode zal de activiteit "ChooseLoginOrRegister" afsluiten en zal de "register" activiteit aanmaken
        BtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChooseLoginOrRegister.this, Register.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}

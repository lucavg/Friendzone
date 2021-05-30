package com.gp.exorra.friendzonecomplete;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gp.exorra.friendzonecomplete.Authentication.ChooseLoginOrRegister;
import com.gp.exorra.friendzonecomplete.Cards.arrayAdapter;
import com.gp.exorra.friendzonecomplete.Cards.cards;
import com.gp.exorra.friendzonecomplete.Matches.MatchesActivity;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Alle variabelen worden hier gedeclareerd
    private com.gp.exorra.friendzonecomplete.Cards.arrayAdapter arrayAdapter;
    private String currentUID;
    private List<cards> rowItems;
    boolean status = false;

    //hier worden alle Firebase variabelen gedeclareerd
    private FirebaseAuth FBAuth;
    private DatabaseReference usersDB;

    //deze methode wordt aangeroepen bij het aanmaken van de MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //de FirebaseAuthenticator wordt aangemaakt en de databank word gevuld met gebruikers
        FBAuth = FirebaseAuth.getInstance();
        usersDB = FirebaseDatabase.getInstance().getReference().child("Users");
        currentUID = FBAuth.getCurrentUser().getUid();

        //deze methode wordt aangeroepen om het geslacht van de huidige gebruiker te weten te komen
        checkUserSex();

        //er wordt een lijst voor de gebruikers aangemaakt en een arrayAdapter die we vullen met de gebruikers
        rowItems = new ArrayList<>();
        arrayAdapter = new arrayAdapter(this, R.layout.item, rowItems);

        //er wordt een SwipeFlingAdapterView aangemaakt, die we vullen met de items uit de arrayAdapter.
        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);

        flingContainer.setAdapter(arrayAdapter);
        //de listener gaat luisteren naar de acties die de gebruiker onderneemt
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            //wordt niet gebruikt
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            //wordt gebruikt als de gebruiker naar links veegt
            @Override
            public void onLeftCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userID = obj.getuID();
                //de gebruiker op de kaart wordt toegevoegd aan de connecties van de gebruiker als een "nee"
                //de gebruiker zal deze kaart niet meer zien, tenzij deze connectie wordt verwijderd in de databank
                usersDB.child(userID).child("connections").child("no").child(currentUID).setValue(true);
                Toast.makeText(MainActivity.this, "Nope!", Toast.LENGTH_SHORT).show();
            }

            //wordt gebruikt als de gebruiker naar rechts veegt
            @Override
            public void onRightCardExit(Object dataObject) {
                cards obj = (cards) dataObject;
                String userID = obj.getuID();
                //de gebruiker op de kaart wordt toegevoegd aan de connecties van de gebruiker als een "ja"
                //de gebruiker zal deze kaart niet meer zien, tenzij deze connectie wordt verwijderd in de databank
                usersDB.child(userID).child("connections").child("yes").child(currentUID).setValue(true);
                isConnectionMatch(userID);
                if (status == false){ }
                else{
                    Toast.makeText(MainActivity.this, "Yes!", Toast.LENGTH_SHORT).show();
                }
            }

            //wordt niet gebruikt
            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
            }

            //wordt niet gebruikt
            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });
    }

    //deze methode gaat kijken of een gebruiker die naar rechts wordt geveegt ook zelf de gebruiker naar rechts heeft geveegt
    //indien ja, dan wordt de gebruiker toegevoegd aan de matches van de gebruiker.
    private void isConnectionMatch(final String userID) {
        DatabaseReference currentUserConnectionsDB = usersDB.child(currentUID).child("connections").child("yes").child(userID);
        currentUserConnectionsDB.addListenerForSingleValueEvent(new ValueEventListener() {
            //deze methode kijkt na of de twee gebruikers een match zijn
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //als de gebruikers een match zijn, krijgt de gebruiker een melding op zijn/haar scherm
                    Toast.makeText(MainActivity.this,"A new connection has been made!", Toast.LENGTH_LONG).show();
                    status = true;

                    //de unieke sleutel voor de chat tussen de gebruikers wordt opgeslagen als de chatID
                    String key = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();

                    //beide gebruikers krijgen de chatID bij hun respectievelijke match
                    usersDB.child(dataSnapshot.getKey()).child("connections").child("matches").child(currentUID).child("chatID").setValue(key);
                    usersDB.child(currentUID).child("connections").child("matches").child(dataSnapshot.getKey()).child("chatID").setValue(key);
                }
            }

            //wordt niet gebruikt
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //deze methode gaat kijken wat het geslacht van de gebruiker is en zal deze aan de variabelen toekennen.
    //enkel nodig als u wilt dat de gebruiker enkel gebruikers van het andere geslacht te zien krijgt
    private String userSex;
    private String oppositeUserSex;
    public void checkUserSex(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userDB = usersDB.child(user.getUid());
        userDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    if (dataSnapshot.child("sex").getValue() != null){
                        userSex = dataSnapshot.child("sex").getValue().toString();
                        switch (userSex){
                            case "male":
                                oppositeUserSex = "female";
                                break;
                            case "female":
                                oppositeUserSex = "male";
                                break;
                        }
                        getOppositeSexUsers();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //deze variabele gaat alle gebruikers van het tegenovergestelde geslacht van de gebruiker uit de databank halen
    public void getOppositeSexUsers(){
        usersDB.addChildEventListener(new ChildEventListener() {
            //deze methode vult de adapter met de gebruikers van het andere geslacht
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //indien u wilt dat de gebruiker enkel gebruikers van het tegenovergestelde geslacht te zien krijgt, gebruik dan de gecommenteerde if structuur in plaats van de huidige:
//                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("no").hasChild(currentUID) && !dataSnapshot.child("connections").child("yes").hasChild(currentUID) && dataSnapshot.child("sex").getValue().toString().equals(oppositeUserSex))
                if (dataSnapshot.exists() && !dataSnapshot.child("connections").child("no").hasChild(currentUID) && !dataSnapshot.child("connections").child("yes").hasChild(currentUID) && !dataSnapshot.getKey().equals(currentUID)){
                    String profileImageUrl = "default";
                    if (!dataSnapshot.child("profileImageUrl").getValue().equals("default")){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }
                    cards item = new cards(dataSnapshot.getKey(), dataSnapshot.child("Name").getValue().toString(), dataSnapshot.child("surName").getValue().toString(), profileImageUrl, dataSnapshot.child("birthDate").getValue().toString(), dataSnapshot.child("town").getValue().toString());
                    rowItems.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            }
            //wordt niet gebruikt
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }
            //wordt niet gebruikt
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }
            //wordt niet gebruikt
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }
            //wordt niet gebruikt
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //deze methode zal de huidige gebruiker uitloggen
    //hierna kan men opnieuw inloggen of een nieuwe gebruiker registreren
    public void logoutUser(View view) {
        FBAuth.signOut();
        Intent intent = new Intent(MainActivity.this, ChooseLoginOrRegister.class);
        startActivity(intent);
        finish();
        return;
    }

    //deze methode opent de activity_settings.xml
    public void toSettings(View view) {
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }

    //deze methode opent de activity_matches.xml
    public void toMatches(View view) {
        Intent intent = new Intent(MainActivity.this, MatchesActivity.class);
        intent.putExtra("userSex", userSex);
        startActivity(intent);
        return;
    }
}
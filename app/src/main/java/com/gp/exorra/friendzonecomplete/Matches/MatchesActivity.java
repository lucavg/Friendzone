package com.gp.exorra.friendzonecomplete.Matches;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gp.exorra.friendzonecomplete.R;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    //alle variabelen worden aangemaakt
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mMatchesAdapter;
    private RecyclerView.LayoutManager mRecycleLayoutManager;
    private String currentUserID;

    //deze methode wordt aangeroepen bij het aanmaken van de MatchesActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);

        //de ID van de huidige gebruiker wordt in een lokale variabele opgeslagen.
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(true);
        mRecycleLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        mRecyclerView.setLayoutManager(mRecycleLayoutManager);
        mMatchesAdapter = new MatchesAdapter(getDataSetMatches(), MatchesActivity.this);
        mRecyclerView.setAdapter(mMatchesAdapter);

        //deze methode gaat de ID van de match oproepen en daarmee de informatie van de match uit de databank halen.
        getUserMatchID();
    }

    //deze methode maakt een referentie aan naar de matches van de gebruiker en gaat dan met een Listener zoeken naar de informatie van de match
    private void getUserMatchID() {
        DatabaseReference matchDb = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches");
        matchDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot match : dataSnapshot.getChildren()){
                        //indien de match van de gebruiker wordt gevonden in de databank, zal de methode worden aangeroepen om de informatie op te halen.
                        FetchMatchInformation(match.getKey());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //deze methode gaat met behulp van de MatchID alle informatie van de match uit de databank halen en deze op het scherm van de gebruiker tonen
    private void FetchMatchInformation(String key) {
        //er wordt een referentie gelegd in de databank naar de match
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
        usersDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //alle informatie wordt in variabelen gestopt
                if (dataSnapshot.exists()){
                    String userID = dataSnapshot.getKey();
                    String name = "";
                    String surName = "";
                    String town = "";
                    String profileImageUrl = "";
                    if (dataSnapshot.child("Name").getValue() != null){
                        name = dataSnapshot.child("Name").getValue().toString();
                    }
                    if (dataSnapshot.child("surName").getValue() != null){
                        surName = dataSnapshot.child("surName").getValue().toString();
                    }
                    if (dataSnapshot.child("town").getValue() != null){
                        town = dataSnapshot.child("town").getValue().toString();
                    }
                    if (dataSnapshot.child("profileImageUrl").getValue() != null){
                        profileImageUrl = dataSnapshot.child("profileImageUrl").getValue().toString();
                    }

                    //de informatie wordt aan een lijst toegevoegd en de MatchesAdapter wordt op de hoogte gebracht van de veranderingen.
                    MatchesObject obj = new MatchesObject(userID, name, surName, town, profileImageUrl);
                    resultMatches.add(obj);
                    mMatchesAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ArrayList<MatchesObject> resultMatches = new ArrayList<>();
    private List<MatchesObject> getDataSetMatches() {
        return resultMatches;
    }
}

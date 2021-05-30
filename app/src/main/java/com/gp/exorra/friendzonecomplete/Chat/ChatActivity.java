package com.gp.exorra.friendzonecomplete.Chat;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gp.exorra.friendzonecomplete.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    //De nodige variabelen worden aangemaakt
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;

    private EditText TxtSendMessage;
    private Button BtnSendMessage;

    private String currentUserID, matchID, chatID;

    private ArrayList<ChatObject> resultChat = new ArrayList<>();
    private List<ChatObject> getDataSetMatches() { return resultChat; }

    //de 2 Firebase variabelen worden aangemaakt
    //deze variabelen zorgen voor de databank met alle gebruikers en de databank met alle chatberichten
    private DatabaseReference databaseUser, databaseChat;

    //deze methode wordt aangeroepen bij het aanmaken van de activiteit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //getIntent wordt aangeroepen via de Bundle die als parameter werd meegegeven vanuit de MatchesViewHolders.
        matchID = getIntent().getExtras().getString("matchID");

        //de huidige UserID wordt toegewezen
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //de 2 nodige databanken worden toegewezen
        databaseUser = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID).child("connections").child("matches").child(matchID);
        databaseChat = FirebaseDatabase.getInstance().getReference().child("chat");
        getChatID();

        //de RecyclerView wordt aangemaakt en krijgt enkele eigenschappen toegewezen.
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(ChatActivity.this);
        mRecyclerView.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new ChatAdapter(getDataSetMatches(), ChatActivity.this);
        mRecyclerView.setAdapter(mChatAdapter);

        TxtSendMessage = findViewById(R.id.message);
        BtnSendMessage = findViewById(R.id.sendButton);

        //deze methode wordt aangeroepen bij het klikken op de "send" knop.
        BtnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        //deze methode zal alle berichten tussen de UserID en de MatchID uit de databank halen en tonen aan de gebruiker.
        getChatMessages();
    }

    //deze methode wordt gebruikt om een bericht te versturen
    private void sendMessage() {
        //het bericht wordt opgehaald uit de EditText
        String sendMessageText = TxtSendMessage.getText().toString();
        //er zal enkel iets verstuurd worden als er daadwerkelijk een bericht in de EditText staat
        if (!sendMessageText.isEmpty()){
            //de .push() methode zal de databank dwingen een nieuw veld aan te maken in de Firebase databank met een uniek ID
            DatabaseReference newMessageDb = databaseChat.push();

            //een HashMap wordt gebruikt om het bericht en de versturende gebruiker in op te slaan
            Map newMessage = new HashMap();
            newMessage.put("createdByUser", currentUserID);
            newMessage.put("text", sendMessageText);

            //de HashMap newMessage zal worden toegevoegd aan de push van newMessageDB
            newMessageDb.setValue(newMessage);
        }
        //de text in het EditText vak wordt weggehaald
        TxtSendMessage.setText(null);
    }

    //Deze methode zal het unieke ID voor de chat uit de databank halen en deze opslaan in een lokale variabele.
    private void getChatID(){
        //met een AddListenerForSingleValueEvent kunnen we een waarde uit de Firebase databank halen
        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
            //de datasnapshot stelt ons in staat om de data uit de databank te halen
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    //de huidige chatID wordt opgehaald
                    chatID = dataSnapshot.child("chatID").getValue().toString();
                    //de Chat database wordt gewijzigd zodat we niet heel de database moeten oproepen maar enkel het gedeelte met de chat tussen de UserID en de MatchID
                    databaseChat = databaseChat.child(chatID);
                    //de vorige chatberichten tussen de 2 gebruikers worden opgehaald
                    getChatMessages();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }

    //deze methode wordt aangeroepen als we de vorige chatberichten tussen de 2 gebruikers willen oproepen
    private void getChatMessages() {
        //deze methode zal meerdere waarden uit de databank kunnen halen
        databaseChat.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot.exists()){
                    String message = null;
                    String createdByUser = null;
                    //deze if structuur gaat nakijken of de velden in de databank gevuld zijn en stopt ze dan in de variabelen.
                        if (dataSnapshot.child("text").getValue()!= null){
                            message = dataSnapshot.child("text").getValue().toString();
                        }
                        if (dataSnapshot.child("createdByUser").getValue() != null){
                            createdByUser = dataSnapshot.child("createdByUser").getValue().toString();
                        }
                        //deze if structuur gaat dubbel checken of de 2 variabelen wel degelijk gevuld zijn
                        if (message != null && createdByUser != null){
                            //deze variabele is nodig in de ChatAdapter om het uiterlijk van de berichten aan te passen. standaard is deze false
                            boolean currentUserBool = false;
                            if (createdByUser.equals(currentUserID)){
                                currentUserBool = true;
                            }
                            ChatObject newMessage = new ChatObject(message, currentUserBool);
                            //de resultChat (die alle berichten bijhoud) wordt gevuld met de berichten en de ChatAdapter wordt op de hoogte gebracht van de veranderingen die zijn gemaakt.
                            resultChat.add(newMessage);
                            mChatAdapter.notifyDataSetChanged();
                    }
                }
            }
            //deze methoden worden niet gebruikt
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}

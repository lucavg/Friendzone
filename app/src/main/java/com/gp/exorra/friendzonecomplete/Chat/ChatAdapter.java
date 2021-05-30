package com.gp.exorra.friendzonecomplete.Chat;

import android.content.Context;
import android.graphics.Color;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gp.exorra.friendzonecomplete.R;

import java.util.List;

//deze klasse is een aanvulling van de ChatViewHolders
//de taak van deze klasse is de layout van de berichten controleren.
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolders>{
    private List<ChatObject> chatList;
    private Context context;

    //deze constructor maakt een chatList aan en een context
    public ChatAdapter(List<ChatObject> matchesList, Context context){
        this.chatList = matchesList;
        this.context = context;
    }

    //bij het aanmaken van een nieuwe ChatViewHolder wordt deze methode aangeroepen
    //de layout van de ChatViewHolders wordt hier bepaald en teruggegeven
    @Override
    public ChatViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolders rcv = new ChatViewHolders(layoutView);
        return rcv;
    }

    //bij het vastzetten van de ChatViewHolders, oftewel het plaatsen van de berichten in de activiteit, wordt deze methode aangeroepen
    //hier krijgt elk bericht een persoonlijke layout die bepaalt of het bericht van de UserID is of van de MatchID
    @Override
    public void onBindViewHolder(ChatViewHolders holder, int position) {
        holder.message.setText(chatList.get(position).getMessage());
        //met de CurrentUserBool die we van de ChatActivity meekregen, kunnen we zien van welke gebruiker het bericht kwam
        //als het bericht van de UserID is, krijgt het een bijna zwarte tekstkleur op een grijze achtergrond en zal de tekst aan de rechterkant van het scherm staan
        if (chatList.get(position).getCurrentUserBool()){
            holder.message.setGravity(Gravity.END);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.container.setBackgroundColor(Color.parseColor("#207178"));
        }
        //als het bericht van de MatchID is, krijgt het een witte tekstkleur op een blauwe achtergrond en zal de tekst aan de linkerkant van het scherm staan
        else{
            holder.message.setGravity(Gravity.START);
            holder.message.setTextColor(Color.parseColor("#404040"));
            holder.container.setBackgroundColor(Color.parseColor("#437d7f"));
        }
    }

    //deze methode geeft het aantal items in de chatList terug
    @Override
    public int getItemCount() {
        return this.chatList.size();
    }
}

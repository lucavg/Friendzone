package com.gp.exorra.friendzonecomplete.Cards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gp.exorra.friendzonecomplete.Cards.cards;
import com.gp.exorra.friendzonecomplete.R;

import java.util.List;

public class arrayAdapter extends ArrayAdapter<cards>{
    Context context;

    //deze constructor gaat een lijst met items voorzien die we kunnen gebruiken om de gebruiker naar het profiel van andere gebruikers te kunnen laten kijken.
    public arrayAdapter(Context context, int resourceID, List<cards> items){
        super(context, resourceID, items);
    }

    //deze constructor gaat elke kaart voorzien van de nodige data zoals naam, dorp en profielfoto
    public View getView(int position, View convertView, ViewGroup parent){
        cards card_item = getItem(position);

        //de convertView gaat gevuld worden met de Item.xml
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        //de profielfoto en naam van de gebruikers worden toegewezen.
        TextView name = convertView.findViewById(R.id.name);
        TextView surName = convertView.findViewById(R.id.surname);
        TextView age = convertView.findViewById(R.id.age);
        TextView town = convertView.findViewById(R.id.town);
        ImageView image = convertView.findViewById(R.id.image);

        //de naam en profielfoto worden op de kaart geplakt
        name.setText(card_item.getName());
        surName.setText(card_item.getSurName());
        age.setText(card_item.getAge());
        town.setText(card_item.getTown());
        switch (card_item.getProfileImageUrl()){
            //Glide is de library die we gaan gebruiken om de profielfoto's van de kaarten te injecteren met een foto
            case "default":
                //als default wordt deze het "default_user_icon"
                Glide.with(image).clear(image);
                Glide.with(convertView.getContext()).load(R.drawable.icon).into(image);
                break;
            default:
                //indien de gebruiker reeds een profielfoto heeft, wordt deze opgehaald via de cards.java klasse.
                //de profielfoto wordt in het programma opgeslagen als een String met een URL die leidt naar de opslagplaats in Firebase
                Glide.with(image).clear(image);
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }
        //met "return convertView" wordt er een volledige kaart teruggegeven aan de gebruiker
        return convertView;
    }
}

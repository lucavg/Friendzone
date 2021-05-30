package com.gp.exorra.friendzonecomplete.Matches;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.gp.exorra.friendzonecomplete.R;

import java.util.List;

//deze klasse is een verlenging van de MatchesViewHolder
//de layout van de MatchesViewHolders wordt hier bepaald en teruggegeven
public class MatchesAdapter extends RecyclerView.Adapter<MatchesViewHolders>{
    private List<MatchesObject> matchesList;
    private Context context;

    public MatchesAdapter(List<MatchesObject> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    //bij het aanmaken van de MatchesViewHolders word deze methode aangeroepen en zullen de ViewHolders deze parameters hebben
    @Override
    public MatchesViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_matches, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        MatchesViewHolders rcv = new MatchesViewHolders(layoutView);
        return rcv;
    }

    //als men een waarde aan de MatchesViewHolders wilt geven wordt deze methode aangeroepen.
    @Override
    public void onBindViewHolder(MatchesViewHolders holder, int position) {
        holder.TxtMatchId.setText(matchesList.get(position).getUserId());
        holder.TxtMatchSurName.setText(matchesList.get(position).getSurName() + " ");
        holder.TxtMatchName.setText(matchesList.get(position).getName());
        try {
            if (!matchesList.get(position).getProfileImageUrl().equals("default")) {
                Glide.with(context).load(matchesList.get(position).getProfileImageUrl()).into(holder.ImgProfilePic);
            }
        }
        finally {

        }

    }

    //deze methode geeft het aantal items in de matchesList terug
    @Override
    public int getItemCount() {
        return this.matchesList.size();
    }
}

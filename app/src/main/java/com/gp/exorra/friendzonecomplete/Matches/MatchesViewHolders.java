package com.gp.exorra.friendzonecomplete.Matches;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gp.exorra.friendzonecomplete.Chat.ChatActivity;
import com.gp.exorra.friendzonecomplete.R;

//deze klasse is de "container" waarin we een match kunnen zetten
public class MatchesViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView TxtMatchId, TxtMatchName, TxtMatchSurName;
    public ImageView ImgProfilePic;

    public MatchesViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);

        TxtMatchId = itemView.findViewById(R.id.MatchID);
        TxtMatchName = itemView.findViewById(R.id.MatchName);
        TxtMatchSurName = itemView.findViewById(R.id.MatchSurName);
        ImgProfilePic = itemView.findViewById(R.id.ImageID);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("matchID", TxtMatchId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}

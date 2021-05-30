package com.gp.exorra.friendzonecomplete.Chat;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gp.exorra.friendzonecomplete.R;

//deze klasse is de "container" waarin we elk bericht zullen zetten
public class ChatViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView message;
    public LinearLayout container;

    public ChatViewHolders(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        message = itemView.findViewById(R.id.message);
        container = itemView.findViewById(R.id.container);
    }

    @Override
    public void onClick(View view) {
    }
}

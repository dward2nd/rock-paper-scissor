package com.rockpaperscissor.components;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.GameplayActivity;
import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.SelectPlayer;

public class SelectPlayerAdapter extends RecyclerView.Adapter<SelectPlayerAdapter.ViewHolder> {

   private RPSPlayer clientPlayer;
   private RPSPlayer[] players;
   private Context context;

   public SelectPlayerAdapter(Context context, RPSPlayer[] players, RPSPlayer clientPlayer) {
      this.context = context;
      this.players = players;
      this.clientPlayer = clientPlayer;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(context);
      View view = inflater.inflate(R.layout.component_playerlist, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.getPlayerNameLabel().setText(players[position].getDisplayName());
      holder.getPlayerStatLabel().setText(String.format("%d played\n%d won",
            players[position].getTotalGamePlayed(),
            players[position].getTotalGameWon()));

      holder.getSelectPlayerCard().setOnClickListener((View view) -> {
         Intent intent = new Intent(context, GameplayActivity.class);
         intent.putExtra(SelectPlayer.INTENT_CLIENT, clientPlayer);
         intent.putExtra(SelectPlayer.INTENT_OPPONENT, players[position]);
         context.startActivity(intent);
      });
   }

   @Override
   public int getItemCount() {
      return this.players.length;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      // components
      private CardView selectPlayerCard;
      private TextView playerNameLabel;
      private TextView playerStatLabel;

      public ViewHolder(View itemView) {
         super(itemView);
         selectPlayerCard = itemView.findViewById(R.id.selectPlayerCard);
         playerNameLabel = itemView.findViewById(R.id.playerNameLabel);
         playerStatLabel = itemView.findViewById(R.id.playerStatLabel);
      }

      public CardView getSelectPlayerCard() {
         return selectPlayerCard;
      }

      public TextView getPlayerNameLabel() {
         return playerNameLabel;
      }

      public TextView getPlayerStatLabel() {
         return playerStatLabel;
      }
   }
}

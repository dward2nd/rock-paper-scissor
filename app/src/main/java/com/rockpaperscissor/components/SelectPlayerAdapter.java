package com.rockpaperscissor.components;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
      private static final int[] SHAPE_ICON = {
            R.drawable.rock_small,
            R.drawable.paper_small,
            R.drawable.scissor_small
      };
      private TextView playerNameLabel;
      private TextView playerStatLabel;
      private ImageView playerAvatar;

      public ViewHolder(View itemView) {
         super(itemView);
         selectPlayerCard = itemView.findViewById(R.id.selectPlayerCard);
         playerAvatar = itemView.findViewById(R.id.selectPlayerAvatar);
         playerNameLabel = itemView.findViewById(R.id.playerNameLabel);
         playerStatLabel = itemView.findViewById(R.id.playerStatLabel);

         int randomShape = 3;
         while (randomShape == 3)
            randomShape = (int) Math.floor(Math.random() * 3);
         playerAvatar.setImageResource(SHAPE_ICON[randomShape]);
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

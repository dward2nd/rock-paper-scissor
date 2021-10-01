package com.rockpaperscissor.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;

import java.util.ArrayList;

public class SelectPlayerAdapter extends RecyclerView.Adapter<SelectPlayerAdapter.ViewHolder> {

   private final ArrayList<RPSPlayer> players;
   private final Context context;

   public SelectPlayerAdapter(Context context, ArrayList<RPSPlayer> players) {
      this.context = context;
      this.players = players;
   }

   @NonNull
   @Override
   public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      LayoutInflater inflater = LayoutInflater.from(context);
      View view = inflater.inflate(R.layout.viewholder_playerlist, parent, false);
      return new ViewHolder(view);
   }

   @Override
   public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
      holder.getPlayerNameLabel().setText(players.get(position).getDisplayName());
      holder.getPlayerStatLabel().setText(String.format("%d won\n%d played",
            players.get(position).getTotalGameWon(),
            players.get(position).getTotalGamePlayed()));
   }

   @Override
   public int getItemCount() {
      return this.players.size();
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      private static final int[] SHAPE_ICON = {
            R.drawable.rock_small,
            R.drawable.paper_small,
            R.drawable.scissor_small
      };
      private final TextView playerNameLabel;
      private final TextView playerStatLabel;

      public ViewHolder(View itemView) {
         super(itemView);
         // components
         ImageView playerAvatar = itemView.findViewById(R.id.selectPlayerAvatar);
         playerNameLabel = itemView.findViewById(R.id.playerNameLabel);
         playerStatLabel = itemView.findViewById(R.id.playerStatLabel);

         int randomShape = 3;
         while (randomShape == 3)
            randomShape = (int) Math.floor(Math.random() * 3);
         playerAvatar.setImageResource(SHAPE_ICON[randomShape]);
      }

      public TextView getPlayerNameLabel() {
         return playerNameLabel;
      }

      public TextView getPlayerStatLabel() {
         return playerStatLabel;
      }
   }
}

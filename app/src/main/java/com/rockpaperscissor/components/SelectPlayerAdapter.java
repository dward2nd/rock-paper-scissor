package com.rockpaperscissor.components;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;

public class SelectPlayerAdapter extends RecyclerView.Adapter<SelectPlayerAdapter.ViewHolder> {

   RPSPlayer[] players;
   Context context;

   public SelectPlayerAdapter(Context context, RPSPlayer[] players) {
      this.context = context;
      this.players = players;
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
      holder.playerNameLabel.setText(players[position].getDisplayName());
      holder.playerStatLabel.setText(String.format("%d played\n%d won",
            players[position].getTotalGamePlayed(),
            players[position].getTotalGameWon()));
   }

   @Override
   public int getItemCount() {
      return this.players.length;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      TextView playerNameLabel;
      TextView playerStatLabel;

      public ViewHolder(View itemView) {
         super(itemView);
         playerNameLabel = itemView.findViewById(R.id.playerNameLabel);
         playerStatLabel = itemView.findViewById(R.id.playerStatLabel);
      }
   }
}

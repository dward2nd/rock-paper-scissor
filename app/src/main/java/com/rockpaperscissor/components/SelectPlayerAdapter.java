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

public class SelectPlayerAdapter extends RecyclerView.Adapter<SelectPlayerAdapter.ViewHolder> {

   String[] playerNames;
   Context context;

   public SelectPlayerAdapter(Context context, String[] playerNames) {
      this.context = context;
      this.playerNames = playerNames;
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
      holder.playerNameLabel.setText(playerNames[position]);
   }

   @Override
   public int getItemCount() {
      return this.playerNames.length;
   }

   public static class ViewHolder extends RecyclerView.ViewHolder {
      TextView playerNameLabel;

      public ViewHolder(View itemView) {
         super(itemView);
         playerNameLabel = itemView.findViewById(R.id.playerNameLabel);
      }
   }
}

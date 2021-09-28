package com.rockpaperscissor.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;

import java.util.ArrayList;

public class ScoreboardFragment extends Fragment {
   private static ScoreboardFragment myself = new ScoreboardFragment();
   private ArrayList<RPSPlayer> players;
   private TextView loadingLabel;
   private RecyclerView playerList;
   private SelectPlayerAdapter selectPlayerAdapter;
   private boolean playerSet = false;
   private boolean viewCreated = false;

   public static ScoreboardFragment getInstance() {
      return myself;
   }

   public ScoreboardFragment() {
      super(R.layout.fragment_scoreboard);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      playerList = view.findViewById(R.id.scoreBoardPlayerList);
      loadingLabel = view.findViewById(R.id.scoreboardLoadingLabel);

      if (playerSet)
         loadScoreboard();

      viewCreated = true;
   }

   public void setPlayers(ArrayList<RPSPlayer> players) {
      if (!playerSet) {
         this.players = players;
         playerSet = true;
      } else {
         this.players.clear();
         this.players.addAll(players);
      }

      if (viewCreated)
         loadScoreboard();
   }

   private void loadScoreboard() {
      selectPlayerAdapter = new SelectPlayerAdapter(getActivity(), players);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(getActivity()));

      loadingLabel.setVisibility(TextView.INVISIBLE);
      playerList.setVisibility(RecyclerView.VISIBLE);
   }
}

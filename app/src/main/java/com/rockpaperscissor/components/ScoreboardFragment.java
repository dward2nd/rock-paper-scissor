package com.rockpaperscissor.components;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;

public class ScoreboardFragment extends Fragment {
   private static ScoreboardFragment myself = new ScoreboardFragment();
   private final int playerLength = 25;
   private RPSPlayer[] samplePlayer;
   private RecyclerView playerList;

   public ScoreboardFragment() {
      super(R.layout.scoreboard_fragment);
   }

   public static ScoreboardFragment getInstance() {
      return myself;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      this.initPlayers();

      playerList = view.findViewById(R.id.scoreBoardPlayerList);
      SelectPlayerAdapter selectPlayerAdapter
            = new SelectPlayerAdapter(getActivity(), samplePlayer);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(getActivity()));
   }

   private void initPlayers() {
      this.samplePlayer = new RPSPlayer[this.playerLength];
      for (int i = 0; i < this.playerLength; ++i) {
         this.samplePlayer[i] = new RPSPlayer(String.format("%09d", i), "Player " + (i + 1), "Session " + (i + 1));
      }
   }
}

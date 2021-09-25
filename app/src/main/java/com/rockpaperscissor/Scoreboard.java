package com.rockpaperscissor;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rockpaperscissor.components.SelectPlayerAdapter;

public class Scoreboard extends AppCompatActivity {
   private final int playerLength = 25;
   private RPSPlayer[] samplePlayer;

   private RecyclerView playerList;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.scoreboard);
      getSupportActionBar().hide();

      Bundle intentExtras = getIntent().getExtras();

      this.initPlayers();

      playerList = findViewById(R.id.scoreBoardPlayerList);
      SelectPlayerAdapter selectPlayerAdapter
            = new SelectPlayerAdapter(this, samplePlayer);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(this));
   }

   private void initPlayers() {
      this.samplePlayer = new RPSPlayer[this.playerLength];
      for (int i = 0; i < this.playerLength; ++i) {
         this.samplePlayer[i] = new RPSPlayer(String.format("%09d", i), "Player " + (i + 1), "Session " + (i + 1));
      }
   }
}

package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.rockpaperscissor.components.SelectPlayerAdapter;

public class SelectPlayer extends AppCompatActivity {

   final int playerLength = 25;
   RecyclerView playerList;
   String[] playerName;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_select_player);

      // receive a message from LoginActivity and display here.
      //Intent intent = getIntent();
      //String message = intent.getStringExtra(LoginActivity.EXTRA_LOGIN);
      TextView textView = findViewById(R.id.displayNameLabel);
      textView.setText(RPSServer.getClientPlayer().getDisplayName());

      // connect to the recycler view for playerList
      this.initPlayerName();
      playerList = findViewById(R.id.playerList);
      SelectPlayerAdapter selectPlayerAdapter = new SelectPlayerAdapter(this, playerName);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(this));
   }

   private void initPlayerName() {
      this.playerName = new String[this.playerLength];
      for (int i = 0; i < this.playerLength; ++i) {
         this.playerName[i] = "Player " + (i + 1);
      }
   }
}
package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.components.SelectPlayerAdapter;

public class SelectPlayer extends AppCompatActivity {
   // activity global constant
   public static final String INTENT_CLIENT = "com.rockpaperscissor.CLIENT_PLAYER";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.OPPONENT_PLAYER";

   // game variable
   private final int playerLength = 25;
   // activity variable
   private long pressedTime;
   private RPSPlayer[] samplePlayer;
   private RPSPlayer clientPlayer;

   // ui components
   private RecyclerView playerList;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.player_menu);
      getSupportActionBar().hide();

      playerList = findViewById(R.id.playerList);

      // receive a message from LoginActivity and display here.
      Intent activityIntent = getIntent();
      clientPlayer = activityIntent.getParcelableExtra(LoginActivity.INTENT_LOGIN);

      Toast.makeText(getApplicationContext(),
            "Current User: " + clientPlayer.getDisplayName(),
            Toast.LENGTH_LONG)
            .show();

      // connect to the recycler view for playerList
      this.initPlayers();
      SelectPlayerAdapter selectPlayerAdapter
            = new SelectPlayerAdapter(this, samplePlayer, clientPlayer);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(this));
   }

   private void initPlayers() {
      this.samplePlayer = new RPSPlayer[this.playerLength];
      for (int i = 0; i < this.playerLength; ++i) {
         this.samplePlayer[i] = new RPSPlayer(String.format("%09d", i), "Player " + (i + 1));
      }
   }

   @Override
   public void onBackPressed() {
      if (pressedTime + 2000 > System.currentTimeMillis()) {
         Intent homeIntent = new Intent(Intent.ACTION_MAIN);
         homeIntent.addCategory(Intent.CATEGORY_HOME);
         homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(homeIntent);
      } else {
         Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
      }
      pressedTime = System.currentTimeMillis();
   }

   public void onSettingClicked(View view) {
      Intent settingIntent = new Intent(this, SettingActivity.class);
      startActivity(settingIntent);
   }
}
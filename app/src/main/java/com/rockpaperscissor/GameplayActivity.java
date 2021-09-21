package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameplayActivity extends AppCompatActivity {
   private RPSPlayer clientPlayer;
   private RPSPlayer opponentPlayer;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.gameplay);
      getSupportActionBar().hide();

      Intent gameplayIntent = getIntent();
      this.clientPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_CLIENT);
      this.opponentPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_OPPONENT);
   }
}

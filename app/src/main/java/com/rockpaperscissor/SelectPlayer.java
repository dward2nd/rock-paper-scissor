package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockpaperscissor.components.ConfirmDialog;
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
   private ImageButton exitBtn;
   private RecyclerView playerList;
   private TextView clientInfoLabel;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.player_menu);
      getSupportActionBar().hide();

      playerList = findViewById(R.id.playerList);

      // receive a message from LoginActivity and display here.
      Bundle intentExtras = getIntent().getExtras();
      if (intentExtras != null) {
         if (intentExtras.containsKey(LoginActivity.INTENT_LOGIN))
            this.clientPlayer = intentExtras.getParcelable(LoginActivity.INTENT_LOGIN);
         else if (intentExtras.containsKey(SummarizeActivity.INTENT_CLIENT))
            this.clientPlayer = intentExtras.getParcelable(SummarizeActivity.INTENT_CLIENT);
      }

      clientInfoLabel = findViewById(R.id.selectPlayerClientInfo);
      clientInfoLabel.setText(String.format("Hello %s!\nPlayed %d   |   Won %d",
            clientPlayer.getDisplayName(), clientPlayer.getTotalGamePlayed(),
            clientPlayer.getTotalGameWon()));

      // connect to the recycler view for playerList
      this.initPlayers();
      SelectPlayerAdapter selectPlayerAdapter
            = new SelectPlayerAdapter(this, samplePlayer, clientPlayer);
      playerList.setAdapter(selectPlayerAdapter);
      playerList.setLayoutManager(new LinearLayoutManager(this));

      this.exitBtn = findViewById(R.id.selectPlayerExitBtn);
      this.exitBtn.setOnClickListener((View view) -> {
         onBackPressed();
      });
   }

   private void initPlayers() {
      this.samplePlayer = new RPSPlayer[this.playerLength];
      for (int i = 0; i < this.playerLength; ++i) {
         this.samplePlayer[i] = new RPSPlayer(String.format("%09d", i), "Player " + (i + 1));
      }
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuConfirmDialog);

      ConfirmDialog logoutDialog = ConfirmDialog.getInstance();

      if (currentFragment == null) {
         logoutDialog.setDialogTitle("Log Out");
         logoutDialog.setDialogDescription("You are about to logout and lose all the stat. Proceed?");
         logoutDialog.setOnCancel((View view) -> {
            fragmentManager.beginTransaction()
                  .remove(logoutDialog)
                  .commit();
         });
         logoutDialog.setOnConfirm((View view) -> {
            Intent intent = new Intent(SelectPlayer.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAndRemoveTask();
         });

         fragmentManager.beginTransaction()
               .add(R.id.playerMenuConfirmDialog, logoutDialog)
               .commit();
      } else {
         fragmentManager.beginTransaction()
               .remove(logoutDialog)
               .commit();
      }
   }

   public void onSettingClicked(View view) {
      Intent settingIntent = new Intent(this, SettingActivity.class);
      startActivity(settingIntent);
   }
}
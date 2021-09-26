package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.components.AlertDialog;
import com.rockpaperscissor.components.ConfirmDialog;
import com.rockpaperscissor.components.ScoreboardFragment;
import com.rockpaperscissor.components.SelectPlayerSessionManager;
import com.rockpaperscissor.components.SettingDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.data.PlayerTemplate;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

public class SelectPlayer extends AppCompatActivity {
   // activity global constant
   public static final String INTENT_CLIENT = "com.rockpaperscissor.CLIENT_PLAYER";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.OPPONENT_PLAYER";
   public static final String INTENT_SESSION = "com.rockpaperscissor.SESSION";

   private RPSPlayer clientPlayer;
   private RPSPlayer pseudoOpponentPlayer;
   private final Handler selectPlayerHandler = new Handler(Looper.getMainLooper());
   private ArrayList<RPSPlayer> scoreboardPlayers;

   // ui components
   private Button sessionBtn;
   private Button scoreboardBtn;
   private ImageButton exitBtn;
   private TextView clientInfoLabel;

   // fragments
   private SelectPlayerSessionManager sessionFragment;
   private ScoreboardFragment scoreboardFragment;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.player_menu);
      getSupportActionBar().hide();

      // receive a message from LoginActivity and display here.
      Bundle intentExtras = getIntent().getExtras();
      if (intentExtras != null) {
         if (intentExtras.containsKey(LoginActivity.INTENT_LOGIN))
            this.clientPlayer = intentExtras.getParcelable(LoginActivity.INTENT_LOGIN);
         else if (intentExtras.containsKey(SummarizeActivity.INTENT_CLIENT))
            this.clientPlayer = intentExtras.getParcelable(SummarizeActivity.INTENT_CLIENT);
      }

      this.pseudoOpponentPlayer = new RPSPlayer("1234567890987654321", "Bot", "somesession");

      sessionBtn = findViewById(R.id.sessionBtn);
      sessionBtn.setOnClickListener((View view) -> switchToSessionFragment());

      scoreboardBtn = findViewById(R.id.scoreboardBtn);
      scoreboardBtn.setOnClickListener((View view) -> switchToScoreboardFragment());

      clientInfoLabel = findViewById(R.id.selectPlayerClientInfo);
      clientInfoLabel.setText(String.format("Hello %s!\nPlayed %d   |   Won %d",
            clientPlayer.getDisplayName(), clientPlayer.getTotalGamePlayed(),
            clientPlayer.getTotalGameWon()));

      this.exitBtn = findViewById(R.id.selectPlayerExitBtn);
      this.exitBtn.setOnClickListener((View view) -> onBackPressed());

      sessionFragment = SelectPlayerSessionManager.getInstance();
      sessionFragment.setClientPlayer(clientPlayer);
      sessionFragment.setPseudoOpponentPlayer(pseudoOpponentPlayer);

      switchToSessionFragment();
      getScoreboardFromServer();
   }

   private void switchToSessionFragment() {
      sessionBtn.setBackgroundColor(0xFFFFFFFF);
      scoreboardBtn.setBackgroundColor(0xFFFFCE70);

      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.selectPlayerMainFragment);

      if (currentFragment != null)
         fragmentManager.beginTransaction()
               .remove(currentFragment)
               .commit();

      fragmentManager.beginTransaction()
            .add(R.id.selectPlayerMainFragment, sessionFragment)
            .commit();
   }

   private void switchToScoreboardFragment() {
      sessionBtn.setBackgroundColor(0xFFFFCE70);
      scoreboardBtn.setBackgroundColor(0xFFFFFFFF);

      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.selectPlayerMainFragment);

      if (currentFragment != null)
         fragmentManager.beginTransaction()
               .remove(currentFragment)
               .commit();

      fragmentManager.beginTransaction()
            .add(R.id.selectPlayerMainFragment, scoreboardFragment)
            .commit();
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

      ConfirmDialog logoutDialog = ConfirmDialog.getInstance();

      if (currentFragment == null) {
         logoutDialog.setDialogTitle("Log Out");
         logoutDialog.setDialogDescription("You are about to logout and lose all the stat. Proceed?");
         logoutDialog.setOnCancel((View view) -> fragmentManager.beginTransaction()
               .remove(logoutDialog)
               .commit());
         logoutDialog.setOnConfirm((View view) -> {
            Intent intent = new Intent(SelectPlayer.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finishAndRemoveTask();
         });

         fragmentManager.beginTransaction()
               .add(R.id.playerMenuFragment, logoutDialog)
               .commit();
      } else {
         fragmentManager.beginTransaction()
               .remove(currentFragment)
               .commit();
      }
   }

   public void onSettingClicked(View view) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      SettingDialog settingDialog = SettingDialog.getInstance();

      fragmentManager.beginTransaction()
            .add(R.id.playerMenuFragment, settingDialog)
            .commit();
   }

   private void getScoreboardFromServer() {
      // load scoreboard
      String scoreboardPath = "/leaderboard";
      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void error(IOException e) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

            AlertDialog notfoundDialog = AlertDialog.getInstance();
            notfoundDialog.setDialogTitle("Network Error");
            notfoundDialog.setDialogDescription(e.getMessage());

            if (currentFragment != null)
               fragmentManager.beginTransaction()
                     .remove(currentFragment)
                     .commit();

            fragmentManager.beginTransaction()
                  .add(R.id.playerMenuFragment, notfoundDialog)
                  .commit();
         }

         @Override
         public void run() {
            Log.d("TAG", getResponse());
            scoreboardPlayers = RPSPlayer.getRPSPlayerArrayList(
                  RPSJson.fromJson(getResponse(), PlayerTemplate[].class));

            scoreboardFragment = ScoreboardFragment.getInstance();
            scoreboardFragment.setPlayers(scoreboardPlayers);
         }
      };

      RPSServer.get(this, scoreboardPath, runnable);
   }

}
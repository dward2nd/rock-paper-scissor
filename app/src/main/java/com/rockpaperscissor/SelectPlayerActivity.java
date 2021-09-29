package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockpaperscissor.server.RPSResponseRunnable;
import com.rockpaperscissor.server.RPSServer;
import com.rockpaperscissor.dialogs.AlertDialog;
import com.rockpaperscissor.dialogs.ConfirmDialog;
import com.rockpaperscissor.fragments.ScoreboardFragment;
import com.rockpaperscissor.fragments.SelectPlayerSessionManager;
import com.rockpaperscissor.dialogs.SettingDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.MiniData;
import com.rockpaperscissor.json.jsontemplate.PlayerTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

import okhttp3.FormBody;

public class SelectPlayerActivity extends AppCompatActivity {
   // activity global constant
   public static final String INTENT_CLIENT = "com.rockpaperscissor.CLIENT_PLAYER";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.OPPONENT_PLAYER";
   public static final String INTENT_SESSION = "com.rockpaperscissor.SESSION";

   private RPSPlayer clientPlayer;
   private final Handler selectPlayerHandler = new Handler();
   private ArrayList<RPSPlayer> scoreboardPlayers;

   // ui components
   private Button sessionBtn;
   private Button scoreboardBtn;

   // fragments
   private SelectPlayerSessionManager sessionFragment;
   private ScoreboardFragment scoreboardFragment;
   private boolean isOnSessionFragment;
   private final RPSResponseRunnable checkChallengeHelper = new RPSResponseRunnable() {
      @Override
      public void error(IOException e) {
         networkErrorDialogShow(e);
      }

      @Override
      public void run() {
         //Log.d("TAG", getResponse());
         PlayerTemplate playerUpdate = RPSJson.fromJson(getResponse(), PlayerTemplate.class);
         if (playerUpdate.getChallenge()) {
            String invitedPath = "/forcejoin";
            FormBody formBody = new FormBody.Builder()
                  .add("session", clientPlayer.getSession())
                  .build();
            RPSResponseRunnable runnable = new RPSResponseRunnable() {
               @Override
               public void run() {
//                  selectPlayerHandler.removeCallbacks(checkChallengeRunnable);
                  MiniData response = RPSJson.fromJson(getResponse(), MiniData.class);

                  // check if the data is actually out of update
                  if (!response.getStatus().equals("out of update")) {
                     RPSPlayer opponent = new RPSPlayer(response.getId(), response.getUsername(),
                           clientPlayer.getSession());

                     Intent intent = new Intent(SelectPlayerActivity.this, GameplayActivity.class);
                     intent.putExtra(SelectPlayerActivity.INTENT_CLIENT, clientPlayer);
                     intent.putExtra(SelectPlayerActivity.INTENT_OPPONENT, opponent);
                     intent.putExtra(SelectPlayerActivity.INTENT_SESSION, clientPlayer.getSession());
                     startActivity(intent);
                  }
               }

               @Override
               public void error(IOException e) {
                  networkErrorDialogShow(e);
               }
            };

            RPSServer.post(formBody, invitedPath, runnable);
         }
      }
   };
   private final Runnable checkChallengeRunnable = this::checkChallenge;

   private void networkErrorDialogShow(IOException e) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

      AlertDialog notfoundDialog = new AlertDialog();
      notfoundDialog.setOnCancel((View view) -> {
         Intent intent = new Intent(SelectPlayerActivity.this, LoginActivity.class);
         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(intent);
         finishAndRemoveTask();
      });
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

   private void checkChallenge() {
      String commonPath = "/playerstatus";
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .build();

      RPSServer.post(formBody, commonPath, checkChallengeHelper);
      selectPlayerHandler.postDelayed(checkChallengeRunnable, 2000);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_selectplayer);
      Objects.requireNonNull(getSupportActionBar()).hide();

      // receive a message from LoginActivity and display here.
      Bundle intentExtras = getIntent().getExtras();
      if (intentExtras != null) {
         if (intentExtras.containsKey(LoginActivity.INTENT_LOGIN))
            this.clientPlayer = intentExtras.getParcelable(LoginActivity.INTENT_LOGIN);
         else if (intentExtras.containsKey(SummaryActivity.INTENT_CLIENT))
            this.clientPlayer = intentExtras.getParcelable(SummaryActivity.INTENT_CLIENT);
      }

      sessionBtn = findViewById(R.id.sessionBtn);
      sessionBtn.setOnClickListener((View view) -> switchToSessionFragment());

      scoreboardBtn = findViewById(R.id.scoreboardBtn);
      scoreboardBtn.setOnClickListener((View view) -> switchToScoreboardFragment());

      TextView clientInfoLabel = findViewById(R.id.selectPlayerClientInfo);
      clientInfoLabel.setText(String.format("Hello %s!\nPlayed %d   |   Won %d",
            clientPlayer.getDisplayName(), clientPlayer.getTotalGamePlayed(),
            clientPlayer.getTotalGameWon()));

      ImageButton exitBtn = findViewById(R.id.selectPlayerExitBtn);
      exitBtn.setOnClickListener((View view) -> onBackPressed());

      sessionFragment = new SelectPlayerSessionManager();
      sessionFragment.setClientPlayer(clientPlayer);
//      sessionFragment.setKeepServerRunnable(checkChallengeRunnable, selectPlayerHandler);

      getScoreboardFromServer();

      isOnSessionFragment = false;
      switchToSessionFragment();
   }

   @Override
   public void onResume() {
      super.onResume();
      selectPlayerHandler.postDelayed(checkChallengeRunnable, 2000);
   }

   @Override
   public void onPause() {
      super.onPause();
      selectPlayerHandler.removeCallbacks(checkChallengeRunnable);
   }

   private void switchToSessionFragment() {
      if (!isOnSessionFragment) {
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

         isOnSessionFragment = true;
      }
   }

   private void switchToScoreboardFragment() {
      if (isOnSessionFragment) {
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

         isOnSessionFragment = false;
      }
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

      ConfirmDialog logoutDialog = new ConfirmDialog();

      if (currentFragment == null) {
         logoutDialog.setDialogTitle("Log Out");
         logoutDialog.setDialogDescription("You are about to logout and lose all the stat. Proceed?");
         logoutDialog.setOnCancel((View view) -> fragmentManager.beginTransaction()
               .remove(logoutDialog)
               .commit());
         logoutDialog.setOnConfirm((View view) -> {
            Intent intent = new Intent(SelectPlayerActivity.this, LoginActivity.class);
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
      SettingDialog settingDialog = new SettingDialog();

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
            networkErrorDialogShow(e);
         }

         @Override
         public void run() {
            Log.d("TAG", getResponse());
            scoreboardPlayers = RPSPlayer.getRPSPlayerArrayList(
                  RPSJson.fromJson(getResponse(), PlayerTemplate[].class));

            scoreboardPlayers.sort(Comparator.comparingInt(RPSPlayer::getTotalGameWon));

            scoreboardFragment = new ScoreboardFragment();
            scoreboardFragment.setPlayers(scoreboardPlayers);
         }
      };

      RPSServer.get(scoreboardPath, runnable);
   }

}
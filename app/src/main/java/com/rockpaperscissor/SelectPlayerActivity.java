package com.rockpaperscissor;

import androidx.fragment.app.Fragment;

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
import com.rockpaperscissor.fragments.ScoreboardFragment;
import com.rockpaperscissor.fragments.SelectPlayerSessionManager;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.MiniData;
import com.rockpaperscissor.json.jsontemplate.PlayerTemplate;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;

public class SelectPlayerActivity extends RPSActivity {
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

   @Override
   public void networkErrorDialogShow(IOException e) {
      selectPlayerHandler.removeCallbacks(checkChallengeRunnable);
      super.networkErrorDialogShow(e);
   }

   private void checkChallenge() {
      String commonPath = "/playerstatus";
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .build();

      RPSServer.post(formBody, commonPath, checkChallengeHelper);
      selectPlayerHandler.postDelayed(checkChallengeRunnable, 5000);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_selectplayer);

      setDialogFragmentId(R.id.playerMenuFragment);

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
      selectPlayerHandler.postDelayed(checkChallengeRunnable, 5000);
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
      Fragment currentFragment = getCurrentDialog();
      if (currentFragment == null) {
         showConfirmDialog("Log Out",
               "You are about to logout and lose all the stat. Proceed?", (View view) -> {
                  Intent intent = new Intent(SelectPlayerActivity.this, LoginActivity.class);
                  intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                  startActivity(intent);
                  finishAndRemoveTask();
               });
      } else {
         removeExistingDialog();
      }
   }

   public void onSettingClicked(View view) {
      showSettingDialog();
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

            scoreboardPlayers.sort((rpsPlayer, t1) ->
                  -rpsPlayer.getTotalGameWon() + t1.getTotalGameWon());

            scoreboardFragment = new ScoreboardFragment();
            scoreboardFragment.setPlayers(scoreboardPlayers);
         }
      };

      RPSServer.get(scoreboardPath, runnable);
   }

}
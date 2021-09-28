package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.components.AlertDialog;
import com.rockpaperscissor.components.ConfirmDialog;
import com.rockpaperscissor.components.GameplayFinalResultFragment;
import com.rockpaperscissor.components.GameplayResultFragment;
import com.rockpaperscissor.components.SettingDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.data.SessionData;

import java.io.IOException;

import okhttp3.FormBody;

public class GameplayActivity extends AppCompatActivity {
   public static final String INTENT_RPSCLIENT = "com.rockpaperscissor.GAMEPLAY_RPSCLIENT";
   public static final String INTENT_RPSOPPONENT = "com.rockpaperscissor.GAMEPLAY_RPSOPPONENT";
   public static final String INTENT_SCORE_CLIENT = "com.rockpaperscissor.GAMEPLAY_SCORE_CLIENT";
   public static final String INTENT_SCORE_OPPONENT = "com.rockpaperscissor.GAMEPLAY_SCORE_OPPONENT";
   public static final String INTENT_SURRENDER = "com.rockpaperscissor.GAMEPLAY_SURRENDER";
   public static final String INTENT_OPPONENT_OUT = "com.rockpaperscissor.GAMEPLAY_OPPONENT_OUT";

   private RPSPlayer clientPlayer;
   private RPSPlayer opponentPlayer;
   private final Handler gameplayHandler = new Handler();
   private static final String[] shapeName = {"rock", "paper", "scissors"};
   private static final String[] shapeNameCapitalized = {"Rock", "Paper", "Scissors"};
   private final GameplayResultFragment resultFragment = GameplayResultFragment.getInstance();
   private final GameplayFinalResultFragment finalResultFragment = GameplayFinalResultFragment.getInstance();
   private String sessionId;
   // ui components
   private ImageButton gamePlayBackBtn;
   private ImageButton gamePlaySettingBtn;
   private ImageButton gamePlayRock;
   private ImageButton gamePlayPaper;
   private ImageButton gamePlayScissor;
   private TextView gamePlayChoose;
   private TextView gamePlayClientPlayerScore;
   private TextView gamePlayOpponentPlayerScore;
   private TextView gamePlayClientPlayerName;
   private TextView gamePlayOpponentPlayerName;
   private TextView gamePlayStatus;
   // game variables
   private int round = 1;
   private int clientChoice = 0;
   private int clientScore = 0;
   private int opponentScore = 0;
   private boolean surrendered = false;
   private boolean opponentOut = false;
   private final Runnable goToSummarizeActivity = () -> {
      getSupportFragmentManager().beginTransaction()
            .remove(finalResultFragment)
            .commit();

      Intent intent = new Intent(GameplayActivity.this, SummarizeActivity.class);
      intent.putExtra(INTENT_RPSCLIENT, clientPlayer);
      intent.putExtra(INTENT_RPSOPPONENT, opponentPlayer);
      intent.putExtra(INTENT_SCORE_CLIENT, clientScore);
      intent.putExtra(INTENT_SCORE_OPPONENT, opponentScore);
      intent.putExtra(INTENT_SURRENDER, surrendered);
      intent.putExtra(INTENT_OPPONENT_OUT, opponentOut);
      startActivity(intent);
   };
   private RPSResponseRunnable keepServerResponse = new RPSResponseRunnable() {
      @Override
      public void run() {
         SessionData session = RPSJson.fromJson(getResponse(), SessionData.class);
         int opponentChoice = session.getRound().get(round).get(opponentPlayer.getUid());
         if (opponentChoice == 4) {
            // cut the connection to the server.
            opponentOut = true;
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentResultFragment = fragmentManager.findFragmentById(R.id.gamePlayShowResult);
            if (currentResultFragment != null)
               fragmentManager.beginTransaction()
                     .remove(resultFragment)
                     .commit();

            surrendered = true;
            gameplayHandler.removeCallbacks(afterResultFragmentShowed);
            gameplayHandler.removeCallbacks(keepServerRunnable);
            onFinishedEvent();
         } else if (opponentChoice != 0 && clientChoice != 0) {
            // both have chosen.
            evalScore(opponentScore);
         } // else, just wait
      }

      @Override
      public void error(IOException e) {
         networkErrorDialogShow(e);
      }
   };

   private Runnable keepServerRunnable = () -> {
      FormBody formBody = new FormBody.Builder()
            .add("sessionid", sessionId)
            .build();
      RPSServer.post(this, formBody, keepServerResponse, "/sessionstatus");
   };
   private boolean gameFinished = false;
   private final Runnable afterResultFragmentShowed = () -> {
      showChoice();
      getSupportFragmentManager().beginTransaction()
            .remove(resultFragment)
            .commit();

      if (round > 5)
         onFinishedEvent();
      else
         gameplayHandler.postDelayed(keepServerRunnable, 2000);

      gamePlayRock.setAlpha(1.0f);
      gamePlayPaper.setAlpha(1.0f);
      gamePlayScissor.setAlpha(1.0f);
   };

   private void hideChoices() {
      gamePlayChoose.setVisibility(TextView.INVISIBLE);
      gamePlayRock.setVisibility(TextView.INVISIBLE);
      gamePlayPaper.setVisibility(TextView.INVISIBLE);
      gamePlayScissor.setVisibility(TextView.INVISIBLE);
   }

   private void showChoice() {
      gamePlayChoose.setVisibility(TextView.VISIBLE);
      gamePlayRock.setVisibility(TextView.VISIBLE);
      gamePlayPaper.setVisibility(TextView.VISIBLE);
      gamePlayScissor.setVisibility(TextView.VISIBLE);
   }

   private RPSResponseRunnable sendChooseRunnable = new RPSResponseRunnable() {
      @Override
      public void run() {
         if (!getResponse().equals("done"))
            networkErrorDialogShow(new IOException("There's a problem related to the server."));
      }

      @Override
      public void error(IOException e) {
         networkErrorDialogShow(e);
      }
   };

   private void networkErrorDialogShow(IOException e) {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

      AlertDialog notfoundDialog = AlertDialog.getInstance();
      notfoundDialog.setOnCancel((View view) -> {
         Intent intent = new Intent(GameplayActivity.this, LoginActivity.class);
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

   private void rewriteScore() {
      gamePlayClientPlayerScore.setText(Integer.toString(clientScore));
      gamePlayOpponentPlayerScore.setText(Integer.toString(opponentScore));
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.gameplay);
      getSupportActionBar().hide();

      Intent gameplayIntent = getIntent();
      this.clientPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_CLIENT);
      this.opponentPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_OPPONENT);
      this.sessionId = gameplayIntent.getStringExtra(SelectPlayer.INTENT_SESSION);

      this.gamePlayClientPlayerScore = findViewById(R.id.gamePlayClientPlayerScore);
      this.gamePlayOpponentPlayerScore = findViewById(R.id.gamePlayOpponentPlayerScore);

      rewriteScore();

      this.gamePlayClientPlayerName = findViewById(R.id.gamePlayClientDisplayName);
      this.gamePlayOpponentPlayerName = findViewById(R.id.gamePlayOpponentDisplayName);

      this.gamePlayClientPlayerName.setText(clientPlayer.getDisplayName());
      this.gamePlayOpponentPlayerName.setText(opponentPlayer.getDisplayName());

      this.gamePlayStatus = findViewById(R.id.gamePlayStatus);
      this.gamePlayStatus.setText("Game started!");

      this.gamePlayChoose = findViewById(R.id.gamePlayChoose);

      this.gamePlayBackBtn = findViewById(R.id.gamePlayBackBtn);
      this.gamePlaySettingBtn = findViewById(R.id.gamePlaySettingBtn);
      this.gamePlayRock = findViewById(R.id.gamePlayRock);
      this.gamePlayPaper = findViewById(R.id.gamePlayPaper);
      this.gamePlayScissor = findViewById(R.id.gamePlayScissor);

      this.gamePlayBackBtn.setOnClickListener((View view) -> {
         onBackPressed();
      });

      this.gamePlaySettingBtn.setOnClickListener((View view) -> {
         FragmentManager fragmentManager = getSupportFragmentManager();
         SettingDialog settingDialog = SettingDialog.getInstance();

         fragmentManager.beginTransaction()
               .add(R.id.gamePlaySurrenderConfirmDialog, settingDialog)
               .commit();
      });

      this.gamePlayRock.setOnClickListener((View view) -> {
         userChoose(1);
      });
      this.gamePlayPaper.setOnClickListener((View view) -> {
         userChoose(2);
      });
      this.gamePlayScissor.setOnClickListener((View view) -> {
         userChoose(3);
      });

      resultFragment.setClientPlayerName(clientPlayer.getDisplayName());
      resultFragment.setOpponentPlayerName(opponentPlayer.getDisplayName());

      // keep connection to the server
      gameplayHandler.postDelayed(keepServerRunnable, 2000);
   }

   private void onFinishedEvent() {
      hideChoices();
      gameFinished = true;

      boolean doesWin = clientScore > opponentScore && !surrendered || opponentOut;
      boolean isDraw = clientScore == opponentScore && (!surrendered || !opponentOut);

      clientPlayer.countPlayed();
      opponentPlayer.countPlayed();

      if (!isDraw) {
         if (doesWin)
            clientPlayer.countWon();
         else
            opponentPlayer.countWon();
      }

      finalResultFragment.setResult(doesWin, isDraw);
      getSupportFragmentManager().beginTransaction()
            .add(R.id.gamePlayShowResult, finalResultFragment)
            .commit();

      gameplayHandler.postDelayed(goToSummarizeActivity, 2000L);
   }

   private void userChoose(int choice) {
      clientChoice = choice;
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .add("round", Integer.toString(round))
            .add("choose", Integer.toString(choice))
            .build();
      String path = "/choose";
      RPSServer.post(this, formBody, sendChooseRunnable, path);

      if (clientChoice != 1)
         gamePlayRock.setAlpha(0.2f);
      if (clientChoice != 2)
         gamePlayPaper.setAlpha(0.2f);
      if (clientChoice != 3)
         gamePlayScissor.setAlpha(0.2f);

      gamePlayStatus.setText("Wait for the opponent to choose.");
   }

   /*
   1 - rock
   2 - paper
   3 - scissor
    */
   private void evalScore(int opponentChoice) {
      // temporarily stop the connection.
      gameplayHandler.removeCallbacks(keepServerRunnable);
      // simulate the opponent choice
      /*
      int opponentChoice = 0;
      while (opponentChoice == 0)
         opponentChoice = (int) Math.ceil(Math.random() * 3);
       */

      boolean isDraw = false;
      boolean doesWin = true;

      // the choice must not draw to be able to evaluate the score
      if (clientChoice != opponentChoice) {
         // win case
         if ((clientChoice == 1 && opponentChoice == 3) ||
               (clientChoice == 2 && opponentChoice == 1) ||
               (clientChoice == 3 && opponentChoice == 2)) {
            ++clientScore;
         } else {
            ++opponentScore;
            doesWin = false;
         }
      } else
         isDraw = true;

      rewriteScore();
      gamePlayStatus.setText(String.format("Round %d: %s!\n%s %s %s.",
            ++round, isDraw ? "Draw" : doesWin ? "You win" : "You lose",
            shapeNameCapitalized[clientChoice - 1],
            isDraw ? "equals" : doesWin ? "wins" : "loses to",
            shapeName[opponentChoice - 1]));

      hideChoices();
      resultFragment.setClientChoice(clientChoice);
      resultFragment.setOpponentChoice(opponentChoice);
      getSupportFragmentManager().beginTransaction()
            .add(R.id.gamePlayShowResult, resultFragment)
            .commit();

      gameplayHandler.postDelayed(afterResultFragmentShowed, 2000L);
      // set back for the next round.
      this.clientChoice = 0;
   }

   @Override
   public void onBackPressed() {
      if (!gameFinished) {
         FragmentManager fragmentManager = getSupportFragmentManager();
         Fragment currentFragment = fragmentManager.findFragmentById(R.id.gamePlaySurrenderConfirmDialog);

         ConfirmDialog surrenderConfirmDialog = ConfirmDialog.getInstance();

         if (currentFragment == null) {
            surrenderConfirmDialog.setDialogTitle("Surrender");
            surrenderConfirmDialog.setDialogDescription("If you quit now, you will lose immediately.\nProceed?");
            surrenderConfirmDialog.setOnCancel((View view) -> {
               fragmentManager.beginTransaction()
                     .remove(surrenderConfirmDialog)
                     .commit();
            });
            surrenderConfirmDialog.setOnConfirm((View view) -> {
               fragmentManager.beginTransaction()
                     .remove(surrenderConfirmDialog)
                     .commit();

               Fragment currentResultFragment = fragmentManager.findFragmentById(R.id.gamePlayShowResult);
               if (currentResultFragment != null)
                  fragmentManager.beginTransaction()
                        .remove(resultFragment)
                        .commit();

               surrendered = true;
               gameplayHandler.removeCallbacks(afterResultFragmentShowed);
               gameplayHandler.removeCallbacks(keepServerRunnable);
               onFinishedEvent();
            });

            fragmentManager.beginTransaction()
                  .add(R.id.gamePlaySurrenderConfirmDialog, surrenderConfirmDialog)
                  .commit();
         } else
            fragmentManager.beginTransaction()
                  .remove(currentFragment)
                  .commit();
      }
   }
}

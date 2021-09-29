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

import com.rockpaperscissor.server.RPSResponseRunnable;
import com.rockpaperscissor.server.RPSServer;
import com.rockpaperscissor.dialogs.AlertDialog;
import com.rockpaperscissor.dialogs.ConfirmDialog;
import com.rockpaperscissor.fragments.GameplayFinalResultFragment;
import com.rockpaperscissor.fragments.GameplayResultFragment;
import com.rockpaperscissor.dialogs.SettingDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.SessionData;

import java.io.IOException;
import java.util.Objects;

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
   private final GameplayResultFragment resultFragment = new GameplayResultFragment();
   private final GameplayFinalResultFragment finalResultFragment = new GameplayFinalResultFragment();
   private String sessionId;
   private ImageButton gamePlayRock;
   private ImageButton gamePlayPaper;
   private ImageButton gamePlayScissor;
   private TextView gamePlayClientPlayerScore;
   private TextView gamePlayOpponentPlayerScore;
   private TextView gamePlayStatus;
   // game variables
   private int round = 1;
   private int clientChoice = 0;
   private int clientScore = 0;
   private int opponentScore = 0;
   private boolean surrendered = false;
   private boolean opponentOut = false;
   private final Runnable goToSummarizeActivity = () -> {
      Fragment currentFragemnt = getSupportFragmentManager().findFragmentById(R.id.gamePlayShowResult);
      if (currentFragemnt != null)
         getSupportFragmentManager().beginTransaction()
               .remove(currentFragemnt)
               .commit();

      Intent intent = new Intent(GameplayActivity.this, SummaryActivity.class);
      intent.putExtra(INTENT_RPSCLIENT, clientPlayer);
      intent.putExtra(INTENT_RPSOPPONENT, opponentPlayer);
      intent.putExtra(INTENT_SCORE_CLIENT, clientScore);
      intent.putExtra(INTENT_SCORE_OPPONENT, opponentScore);
      intent.putExtra(INTENT_SURRENDER, surrendered);
      intent.putExtra(INTENT_OPPONENT_OUT, opponentOut);
      startActivity(intent);
   };
   private boolean gameFinished = false;
   private final Runnable afterResultFragmentShowed = () -> {
      getSupportFragmentManager().beginTransaction()
            .remove(resultFragment)
            .commit();

      if (round > 5)
         onFinishedEvent();
      else
         gameplayHandler.postDelayed(keepServerRunnable, 5000);

      runOnUiThread(this::setButtonClickable);
   };
   private final Runnable keepServerRunnable = () -> {
      FormBody formBody = new FormBody.Builder()
            .add("sessionid", sessionId)
            .build();
      RPSServer.post(formBody, "/sessionstatus", keepServerResponse);
   };
   private final RPSResponseRunnable keepServerResponse = new RPSResponseRunnable() {
      @Override
      public void run() {
         SessionData session = RPSJson.fromJson(getResponse(), SessionData.class);
         int opponentChoice = session.getRound().get(round).get(opponentPlayer.getUid());
         Log.d("TAG", "opponentChoice = " + opponentChoice);
         if (clientChoice != 4) {
            if (opponentChoice == 4) {
               // cut the connection to the server.
               opponentOut = true;
               FragmentManager fragmentManager = getSupportFragmentManager();
               Fragment currentResultFragment = fragmentManager.findFragmentById(R.id.gamePlayShowResult);
               if (currentResultFragment != null)
                  fragmentManager.beginTransaction()
                        .remove(resultFragment)
                        .commit();

               onFinishedEvent();
            } else if (opponentChoice != 0 && clientChoice != 0) {
               // both have chosen.
               runOnUiThread(() -> evalScore(opponentChoice));
            } // else, just wait
         }

         gameplayHandler.postDelayed(keepServerRunnable, 5000L);
      }

      @Override
      public void error(IOException e) {
         networkErrorDialogShow(e);
      }
   };

   //private void hideChoices() {
   //   gamePlayChoose.setVisibility(TextView.INVISIBLE);
   //   gamePlayRock.setVisibility(TextView.INVISIBLE);
   //   gamePlayPaper.setVisibility(TextView.INVISIBLE);
   //   gamePlayScissor.setVisibility(TextView.INVISIBLE);
   //}

   //private void showChoice() {
   //   gamePlayChoose.setVisibility(TextView.VISIBLE);
   //   gamePlayRock.setVisibility(TextView.VISIBLE);
   //   gamePlayPaper.setVisibility(TextView.VISIBLE);
   //   gamePlayScissor.setVisibility(TextView.VISIBLE);
   //}

   private final RPSResponseRunnable sendChooseRunnable = new RPSResponseRunnable() {
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

      AlertDialog notfoundDialog = new AlertDialog();
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
            .add(R.id.gamePlaySurrenderConfirmDialog, notfoundDialog)
            .commit();
   }

   private void rewriteScore() {
      gamePlayClientPlayerScore.setText(Integer.toString(clientScore));
      gamePlayOpponentPlayerScore.setText(Integer.toString(opponentScore));
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_gameplay);
      Objects.requireNonNull(getSupportActionBar()).hide();

      Intent gameplayIntent = getIntent();
      this.clientPlayer = gameplayIntent.getParcelableExtra(SelectPlayerActivity.INTENT_CLIENT);
      this.opponentPlayer = gameplayIntent.getParcelableExtra(SelectPlayerActivity.INTENT_OPPONENT);
      this.sessionId = gameplayIntent.getStringExtra(SelectPlayerActivity.INTENT_SESSION);

      this.gamePlayClientPlayerScore = findViewById(R.id.gamePlayClientPlayerScore);
      this.gamePlayOpponentPlayerScore = findViewById(R.id.gamePlayOpponentPlayerScore);

      rewriteScore();

      TextView gamePlayClientPlayerName = findViewById(R.id.gamePlayClientDisplayName);
      TextView gamePlayOpponentPlayerName = findViewById(R.id.gamePlayOpponentDisplayName);

      gamePlayClientPlayerName.setText(clientPlayer.getDisplayName());
      gamePlayOpponentPlayerName.setText(opponentPlayer.getDisplayName());

      this.gamePlayStatus = findViewById(R.id.gamePlayStatus);
      this.gamePlayStatus.setText("Game started!");

      // ui components
      ImageButton gamePlayBackBtn = findViewById(R.id.gamePlayBackBtn);
      ImageButton gamePlaySettingBtn = findViewById(R.id.gamePlaySettingBtn);
      this.gamePlayRock = findViewById(R.id.gamePlayRock);
      this.gamePlayPaper = findViewById(R.id.gamePlayPaper);
      this.gamePlayScissor = findViewById(R.id.gamePlayScissor);

      gamePlayBackBtn.setOnClickListener((View view) -> onBackPressed());

      gamePlaySettingBtn.setOnClickListener((View view) -> {
         FragmentManager fragmentManager = getSupportFragmentManager();
         SettingDialog settingDialog = new SettingDialog();

         fragmentManager.beginTransaction()
               .add(R.id.gamePlaySurrenderConfirmDialog, settingDialog)
               .commit();
      });

      this.gamePlayRock.setOnClickListener((View view) -> userChoose(1));
      this.gamePlayPaper.setOnClickListener((View view) -> userChoose(2));
      this.gamePlayScissor.setOnClickListener((View view) -> userChoose(3));

      resultFragment.setClientPlayerName(clientPlayer.getDisplayName());
      resultFragment.setOpponentPlayerName(opponentPlayer.getDisplayName());

      // keep connection to the server
      gameplayHandler.postDelayed(keepServerRunnable, 5000);
   }

   private void onFinishedEvent() {
      runOnUiThread(this::setButtonUnclickable);
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

      gameplayHandler.postDelayed(goToSummarizeActivity, 5000L);
   }

   private void setButtonUnclickable() {
      if (clientChoice != 1)
         gamePlayRock.setAlpha(0.2f);
      if (clientChoice != 2)
         gamePlayPaper.setAlpha(0.2f);
      if (clientChoice != 3)
         gamePlayScissor.setAlpha(0.2f);

      gamePlayRock.setClickable(false);
      gamePlayPaper.setClickable(false);
      gamePlayScissor.setClickable(false);
   }

   private void setButtonClickable() {
      gamePlayRock.setAlpha(1.0f);
      gamePlayPaper.setAlpha(1.0f);
      gamePlayScissor.setAlpha(1.0f);

      gamePlayRock.setClickable(true);
      gamePlayPaper.setClickable(true);
      gamePlayScissor.setClickable(true);
   }

   private void userChoose(int choice) {
      clientChoice = choice;
      FormBody formBody = new FormBody.Builder()
            .add("session", sessionId)
            .add("Id", clientPlayer.getUid())
            .add("round", Integer.toString(round))
            .add("choose", Integer.toString(choice))
            .build();
      String path = "/choose";
      RPSServer.post(formBody, path, sendChooseRunnable);

      runOnUiThread(this::setButtonUnclickable);

      if (choice == 4) {
         gamePlayStatus.setText("You have just surrendered.");
         gameplayHandler.removeCallbacks(afterResultFragmentShowed);
         gameplayHandler.removeCallbacks(keepServerRunnable);
         onFinishedEvent();
      } else
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
      Log.d("TAG", String.format("%d %d", clientChoice, opponentChoice));
      gamePlayStatus.setText(String.format("Round %d: %s!\n%s %s %s.",
            ++round, isDraw ? "Draw" : doesWin ? "You win" : "You lose",
            shapeNameCapitalized[clientChoice - 1],
            isDraw ? "equals" : doesWin ? "wins" : "loses to",
            shapeName[opponentChoice - 1]));

      runOnUiThread(this::setButtonUnclickable);
      resultFragment.setClientChoice(clientChoice);
      resultFragment.setOpponentChoice(opponentChoice);
      getSupportFragmentManager().beginTransaction()
            .add(R.id.gamePlayShowResult, resultFragment)
            .commit();

      gameplayHandler.postDelayed(afterResultFragmentShowed, 3000L);
      // set back for the next round.
      this.clientChoice = 0;
   }

   @Override
   public void onBackPressed() {
      if (!gameFinished) {
         FragmentManager fragmentManager = getSupportFragmentManager();
         Fragment currentFragment = fragmentManager.findFragmentById(R.id.gamePlaySurrenderConfirmDialog);

         ConfirmDialog surrenderConfirmDialog = new ConfirmDialog();

         if (currentFragment == null) {
            surrenderConfirmDialog.setDialogTitle("Surrender");
            surrenderConfirmDialog.setDialogDescription("If you quit now, you will lose immediately.\nProceed?");
            surrenderConfirmDialog.setOnCancel((View view) -> fragmentManager.beginTransaction()
                  .remove(surrenderConfirmDialog)
                  .commit());
            surrenderConfirmDialog.setOnConfirm((View view) -> {
               fragmentManager.beginTransaction()
                     .remove(surrenderConfirmDialog)
                     .commit();

               Fragment currentResultFragment = fragmentManager.findFragmentById(R.id.gamePlayShowResult);
               if (currentResultFragment != null)
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();

               surrendered = true;
               userChoose(4);
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

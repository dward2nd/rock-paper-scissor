package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rockpaperscissor.components.ConfirmDialog;
import com.rockpaperscissor.components.GameplayFinalResultFragment;
import com.rockpaperscissor.components.GameplayResultFragment;

import java.util.Timer;

public class GameplayActivity extends AppCompatActivity {
   public static final String INTENT_RPSCLIENT = "com.rockpaperscissor.GAMEPLAY_RPSCLIENT";
   public static final String INTENT_RPSOPPONENT = "com.rockpaperscissor.GAMEPLAY_RPSOPPONENT";
   public static final String INTENT_SCORE_CLIENT = "com.rockpaperscissor.GAMEPLAY_SCORE_CLIENT";
   public static final String INTENT_SCORE_OPPONENT = "com.rockpaperscissor.GAMEPLAY_SCORE_OPPONENT";
   public static final String INTENT_SURRENDER = "com.rockpaperscissor.GAMEPLAY_SURRENDER";

   private RPSPlayer clientPlayer;
   private RPSPlayer opponentPlayer;
   private static final String[] shapeName = {"rock", "paper", "scissors"};
   private static final String[] shapeNameCapitalized = {"Rock", "Paper", "Scissors"};
   private final GameplayResultFragment resultFragment = GameplayResultFragment.getInstance();
   private final GameplayFinalResultFragment finalResultFragment = GameplayFinalResultFragment.getInstance();
   private final Handler resultFragmentHandler = new Handler();
   // ui components
   private ImageButton gamePlayBackBtn;
   private ImageButton gamePlayRock;
   private ImageButton gamePlayPaper;
   private ImageButton gamePlayScissor;
   private TextView gamePlayClientPlayerScore;
   private TextView gamePlayOpponentPlayerScore;
   private TextView gamePlayClientPlayerName;
   private TextView gamePlayOpponentPlayerName;
   private TextView gamePlayStatus;
   // game variables
   private int round = 0;
   private int clientScore = 0;
   private int opponentScore = 0;
   private boolean surrendered = false;
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
      startActivity(intent);
   };
   private boolean gameFinished = false;
   private boolean isResultShowing = false;
   private final Runnable afterResultFragmentShowed = () -> {
      isResultShowing = false;

      getSupportFragmentManager().beginTransaction()
            .remove(resultFragment)
            .commit();

      if (round >= 5)
         onFinishedEvent();
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.gameplay);
      getSupportActionBar().hide();

      Intent gameplayIntent = getIntent();
      this.clientPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_CLIENT);
      this.opponentPlayer = gameplayIntent.getParcelableExtra(SelectPlayer.INTENT_OPPONENT);

      this.gamePlayClientPlayerScore = findViewById(R.id.gamePlayClientPlayerScore);
      this.gamePlayOpponentPlayerScore = findViewById(R.id.gamePlayOpponentPlayerScore);

      rewriteScore();

      this.gamePlayClientPlayerName = findViewById(R.id.gamePlayClientDisplayName);
      this.gamePlayOpponentPlayerName = findViewById(R.id.gamePlayOpponentDisplayName);

      this.gamePlayClientPlayerName.setText(clientPlayer.getDisplayName());
      this.gamePlayOpponentPlayerName.setText(opponentPlayer.getDisplayName());

      this.gamePlayStatus = findViewById(R.id.gamePlayStatus);
      this.gamePlayStatus.setText("Game started!");

      this.gamePlayBackBtn = findViewById(R.id.gamePlayBackBtn);
      this.gamePlayRock = findViewById(R.id.gamePlayRock);
      this.gamePlayPaper = findViewById(R.id.gamePlayPaper);
      this.gamePlayScissor = findViewById(R.id.gamePlayScissor);

      this.gamePlayBackBtn.setOnClickListener((View view) -> {
         onBackPressed();
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
   }

   private void rewriteScore() {
      gamePlayClientPlayerScore.setText(Integer.toString(clientScore));
      gamePlayOpponentPlayerScore.setText(Integer.toString(opponentScore));
   }

   private void onFinishedEvent() {
      gameFinished = true;

      boolean doesWin = clientScore > opponentScore && !surrendered;
      boolean isDraw = clientScore == opponentScore && !surrendered;

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

      resultFragmentHandler.postDelayed(goToSummarizeActivity, 2000L);
   }

   /*
   1 - rock
   2 - paper
   3 - scissor
    */
   private void userChoose(int clientChoice) {
      // simulate the opponent choice
      int opponentChoice = 0;
      while (opponentChoice == 0)
         opponentChoice = (int) Math.ceil(Math.random() * 3);

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

      resultFragment.setClientChoice(clientChoice);
      resultFragment.setOpponentChoice(opponentChoice);
      getSupportFragmentManager().beginTransaction()
            .add(R.id.gamePlayShowResult, resultFragment)
            .commit();

      isResultShowing = true;
      resultFragmentHandler.postDelayed(afterResultFragmentShowed, 2000L);
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
               resultFragmentHandler.removeCallbacks(afterResultFragmentShowed);
               onFinishedEvent();
            });

            fragmentManager.beginTransaction()
                  .add(R.id.gamePlaySurrenderConfirmDialog, surrenderConfirmDialog)
                  .commit();
         } else
            fragmentManager.beginTransaction()
                  .remove(surrenderConfirmDialog)
                  .commit();
      }
   }
}

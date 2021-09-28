package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class SummaryActivity extends AppCompatActivity {
   public static final String INTENT_CLIENT = "com.rockpaperscissor.SUMMARIZE_CLIENT";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.SUMMARIZE_OPPONENT";
   private static final int[] SHAPE_ICON = {
         R.drawable.rock_small,
         R.drawable.paper_small,
         R.drawable.scissor_small
   };
   private RPSPlayer clientPlayer;
   private RPSPlayer opponentPlayer;

   @Override
   public void onBackPressed() {
      Intent intent = new Intent(SummaryActivity.this, SelectPlayerActivity.class);
      intent.putExtra(INTENT_CLIENT, clientPlayer);
      intent.putExtra(INTENT_OPPONENT, opponentPlayer);
      startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_summary);
      Objects.requireNonNull(getSupportActionBar()).hide();

      Bundle intentExtras = getIntent().getExtras();
      this.clientPlayer = intentExtras.getParcelable(GameplayActivity.INTENT_RPSCLIENT);
      this.opponentPlayer = intentExtras.getParcelable(GameplayActivity.INTENT_RPSOPPONENT);
      int clientScore = intentExtras.getInt(GameplayActivity.INTENT_SCORE_CLIENT);
      int opponentScore = intentExtras.getInt(GameplayActivity.INTENT_SCORE_OPPONENT);
      boolean surrendered = intentExtras.getBoolean(GameplayActivity.INTENT_SURRENDER);
      boolean opponentOut = intentExtras.getBoolean(GameplayActivity.INTENT_OPPONENT_OUT);

      ImageButton backBtn = findViewById(R.id.sumBackBtn);
      backBtn.setOnClickListener((View view) -> onBackPressed());

      ImageView clientImage = findViewById(R.id.sumClientImage);
      ImageView opponentImage = findViewById(R.id.sumOpponentImage);

      int clientImageChoice = 3;
      while (clientImageChoice == 3)
         clientImageChoice = (int) Math.floor(Math.random() * 3);
      int opponentImageChoice = 3;
      while (opponentImageChoice == 3)
         opponentImageChoice = (int) Math.floor(Math.random() * 3);
      clientImage.setImageResource(SHAPE_ICON[clientImageChoice]);
      opponentImage.setImageResource(SHAPE_ICON[opponentImageChoice]);

      TextView clientLabel = findViewById(R.id.sumClientLabel);
      TextView clientScoreLabel = findViewById(R.id.sumClientScore);
      TextView opponentLabel = findViewById(R.id.sumOpponentLabel);
      TextView opponentScoreLabel = findViewById(R.id.sumOpponentScore);
      TextView clientStatus = findViewById(R.id.sumClientStatus);

      clientLabel.setText(clientPlayer.getDisplayName() +
            (clientScore > opponentScore && !surrendered ? " 👑" : ""));
      clientScoreLabel.setText(surrendered ? "-" : Integer.toString(clientScore));
      opponentLabel.setText(opponentPlayer.getDisplayName() +
            (clientScore < opponentScore || surrendered ? " 👑" : ""));
      opponentScoreLabel.setText(surrendered ? "-" : Integer.toString(opponentScore));

      clientStatus.setText(surrendered ? "You surrendered." :
            opponentOut ? "The opponent unexpectedly disconnected." :
                  clientScore > opponentScore ? "You won." :
                        clientScore == opponentScore ? "Draw." : "You lost.");
   }

}

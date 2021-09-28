package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rockpaperscissor.components.GameplayResultFragment;

public class SummarizeActivity extends AppCompatActivity {
   public static final String INTENT_CLIENT = "com.rockpaperscissor.SUMMARIZE_CLIENT";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.SUMMARIZE_OPPONENT";
   private static final int[] SHAPE_ICON = {
         R.drawable.rock_small,
         R.drawable.paper_small,
         R.drawable.scissor_small
   };
   private ImageButton backBtn;
   private ImageButton settingBtn;
   private ImageView clientImage;
   private ImageView opponentImage;
   private TextView clientLabel;
   private TextView clientScoreLabel;
   private TextView opponentLabel;
   private TextView opponentScoreLabel;
   private TextView clientStatus;
   private RPSPlayer clientPlayer;
   private RPSPlayer opponentPlayer;
   private int clientScore;
   private int opponentScore;
   private boolean surrendered;
   private boolean opponentOut;

   @Override
   public void onBackPressed() {
      Intent intent = new Intent(SummarizeActivity.this, SelectPlayer.class);
      intent.putExtra(INTENT_CLIENT, clientPlayer);
      intent.putExtra(INTENT_OPPONENT, opponentPlayer);
      startActivity(intent);
   }

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.result);
      getSupportActionBar().hide();

      Bundle intentExtras = getIntent().getExtras();
      this.clientPlayer = intentExtras.getParcelable(GameplayActivity.INTENT_RPSCLIENT);
      this.opponentPlayer = intentExtras.getParcelable(GameplayActivity.INTENT_RPSOPPONENT);
      this.clientScore = intentExtras.getInt(GameplayActivity.INTENT_SCORE_CLIENT);
      this.opponentScore = intentExtras.getInt(GameplayActivity.INTENT_SCORE_OPPONENT);
      this.surrendered = intentExtras.getBoolean(GameplayActivity.INTENT_SURRENDER);
      this.opponentOut = intentExtras.getBoolean(GameplayActivity.INTENT_OPPONENT_OUT);

      this.backBtn = findViewById(R.id.sumBackBtn);
      this.settingBtn = findViewById(R.id.sumSettingBtn);
      this.backBtn.setOnClickListener((View view) -> {
         onBackPressed();
      });
      this.settingBtn.setOnClickListener((View view) -> {
         Intent intent = new Intent(SummarizeActivity.this, SettingActivity.class);
         startActivity(intent);
      });

      this.clientImage = findViewById(R.id.sumClientImage);
      this.opponentImage = findViewById(R.id.sumOpponentImage);

      int clientImageChoice = 3;
      while (clientImageChoice == 3)
         clientImageChoice = (int) Math.floor(Math.random() * 3);
      int opponentImageChoice = 3;
      while (opponentImageChoice == 3)
         opponentImageChoice = (int) Math.floor(Math.random() * 3);
      this.clientImage.setImageResource(SHAPE_ICON[clientImageChoice]);
      this.opponentImage.setImageResource(SHAPE_ICON[opponentImageChoice]);

      this.clientLabel = findViewById(R.id.sumClientLabel);
      this.clientScoreLabel = findViewById(R.id.sumClientScore);
      this.opponentLabel = findViewById(R.id.sumOpponentLabel);
      this.opponentScoreLabel = findViewById(R.id.sumOpponentScore);
      this.clientStatus = findViewById(R.id.sumClientStatus);

      this.clientLabel.setText(clientPlayer.getDisplayName() +
            (clientScore > opponentScore && !surrendered ? " 👑" : ""));
      this.clientScoreLabel.setText(surrendered ? "-" : Integer.toString(clientScore));
      this.opponentLabel.setText(opponentPlayer.getDisplayName() +
            (clientScore < opponentScore || surrendered ? " 👑" : ""));
      this.opponentScoreLabel.setText(surrendered ? "-" : Integer.toString(opponentScore));

      this.clientStatus.setText(surrendered ? "You surrendered." :
            clientScore > opponentScore ? "You won." :
                  clientScore == opponentScore ? "Draw." : "You lost.");
   }

}

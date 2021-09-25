package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.components.AlertDialog;
import com.rockpaperscissor.components.ConfirmDialog;
import com.rockpaperscissor.components.SelectPlayerAdapter;

import okhttp3.FormBody;

public class SelectPlayer extends AppCompatActivity {
   // activity global constant
   public static final String INTENT_CLIENT = "com.rockpaperscissor.CLIENT_PLAYER";
   public static final String INTENT_OPPONENT = "com.rockpaperscissor.OPPONENT_PLAYER";
   public static final String INTENT_SESSION = "com.rockpaperscissor.SESSION";

   private RPSPlayer clientPlayer;

   // ui components
   private EditText clientSessionIdLabel;
   private EditText opponentSessionIdLabel;

   private Button startBtn;
   private Button scoreboardBtn;
   private ImageButton exitBtn;

   private TextView clientInfoLabel;

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

      clientSessionIdLabel = findViewById(R.id.sessionClientSessionIdBox);
      clientSessionIdLabel.setText(clientPlayer.getSession());

      opponentSessionIdLabel = findViewById(R.id.sessionOpponentSessionIdBox);

      startBtn = findViewById(R.id.sessionStartGameBtn);
      startBtn.setOnClickListener((View view) -> {

      });

      scoreboardBtn = findViewById(R.id.scoreboardBtn);
      scoreboardBtn.setOnClickListener((View view) -> {

      });

      clientInfoLabel = findViewById(R.id.selectPlayerClientInfo);
      clientInfoLabel.setText(String.format("Hello %s!\nPlayed %d   |   Won %d",
            clientPlayer.getDisplayName(), clientPlayer.getTotalGamePlayed(),
            clientPlayer.getTotalGameWon()));

      this.exitBtn = findViewById(R.id.selectPlayerExitBtn);
      this.exitBtn.setOnClickListener((View view) -> {
         onBackPressed();
      });
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

   private void startGame(String session) {
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .add("Session", session)
            .build();

      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void run() {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuConfirmDialog);
            AlertDialog notfoundDialog = AlertDialog.getInstance();
            notfoundDialog.setOnCancel((View view) -> {
               fragmentManager.beginTransaction()
                     .remove(notfoundDialog)
                     .commit();
            });

            if (getResponse().equals("joined")) {
               Intent intent = new Intent(SelectPlayer.this, GameplayActivity.class);
               intent.putExtra(INTENT_CLIENT, clientPlayer);
               intent.putExtra(INTENT_SESSION, session);
               startActivity(intent);
            } else if (getResponse().equals("full")) {
               if (currentFragment == null) {
                  notfoundDialog.setDialogTitle("Already taken");
                  notfoundDialog.setDialogDescription("The session `" + session + "` is currently active with the other player.");
               } else {
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
               }
               fragmentManager.beginTransaction()
                     .add(R.id.playerMenuConfirmDialog, notfoundDialog)
                     .commit();
            } else {
               if (currentFragment == null) {
                  notfoundDialog.setDialogTitle("Not found");
                  notfoundDialog.setDialogDescription("The session `" + session + "` not found on the server.");
               } else {
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
               }
               fragmentManager.beginTransaction()
                     .add(R.id.playerMenuConfirmDialog, notfoundDialog)
                     .commit();

            }
         }
      };

      RPSServer.post(formBody, runnable, String.format("%s/join", clientPlayer.getUid()));
   }
}
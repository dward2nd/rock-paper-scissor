package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.rockpaperscissor.server.RPSResponseRunnable;
import com.rockpaperscissor.server.RPSServer;
import com.rockpaperscissor.dialogs.AlertDialog;
import com.rockpaperscissor.dialogs.ConfirmDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.PlayerTemplate;

import java.io.IOException;
import java.util.Objects;

import okhttp3.FormBody;

public class LoginActivity extends AppCompatActivity {
   public static final String INTENT_LOGIN = "com.rockpaperscissor.LOGIN";

   // components
   private EditText userInputBox;
   private EditText serverUrlBox;
   private ImageButton letsPlayBtn;

   private boolean loggingIn;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      Objects.requireNonNull(getSupportActionBar()).hide();

      this.userInputBox = findViewById(R.id.userInputBox);
      this.serverUrlBox = findViewById(R.id.serverURLInputBox);
      this.letsPlayBtn = findViewById(R.id.nextBtn);

      boolean loggingIn = true;
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.loginConfirmDialog);

      ConfirmDialog exitDialog = new ConfirmDialog();

      if (currentFragment == null) {
         exitDialog.setDialogTitle("Exit Game");
         exitDialog.setDialogDescription("Are you sure you want to quit the game?");
         exitDialog.setOnCancel((View view) -> fragmentManager.beginTransaction()
               .remove(exitDialog)
               .commit());
         exitDialog.setOnConfirm((View view) -> {
            finishAffinity();
            System.exit(0);
         });

         // show the dialog
         fragmentManager.beginTransaction()
               .add(R.id.loginConfirmDialog, exitDialog)
               .commit();
      } else {

         // close the dialog
         fragmentManager.beginTransaction()
               .remove(exitDialog)
               .commit();
      }
   }

   /**
    * Event handler when user tap the send button
    **/
   public void onNextBtnClicked(View view) {
      if (!loggingIn) {
         loggingIn = true;

         letsPlayBtn.setAlpha(0.2f);

         String displayName = userInputBox.getText().toString();
         RPSServer.setServerUrl(serverUrlBox.getText().toString());

         // connect to the server
         login(displayName);
      }
   }

   private void login(String displayName) {
      String path = "/register";
      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void error(IOException e) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.loginConfirmDialog);

            AlertDialog alertDialog = new AlertDialog();
            alertDialog.setDialogTitle("Network Error");
            alertDialog.setDialogDescription(e.getMessage());

            if (currentFragment != null)
               fragmentManager.beginTransaction()
                     .remove(currentFragment)
                     .commit();

            fragmentManager.beginTransaction()
                  .add(R.id.loginConfirmDialog, alertDialog)
                  .commit();

            loggingIn = false;
            letsPlayBtn.setAlpha(1.0f);
         }

         @Override
         public void run() {
            String responseString = getResponse().substring(1, getResponse().length() - 1);
            RPSPlayer player = new RPSPlayer((PlayerTemplate) RPSJson.fromJson(responseString, PlayerTemplate.class));
            Log.d("TAG", "Display name: " + player.getDisplayName());

            Intent intent = new Intent(LoginActivity.this, SelectPlayerActivity.class);
            intent.putExtra(INTENT_LOGIN, player);
            startActivity(intent);
            finish();
         }
      };
      FormBody formBody = new FormBody.Builder()
            .add("username", displayName)
            .build();
      RPSServer.post(formBody, path, runnable);
   }
}
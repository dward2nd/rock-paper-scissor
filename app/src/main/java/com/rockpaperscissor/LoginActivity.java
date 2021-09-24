package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.components.ConfirmDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.data.LoginTemplate;
import com.rockpaperscissor.json.jsontemplate.data.PlayerTemplate;

public class LoginActivity extends AppCompatActivity {
   public static final String INTENT_LOGIN = "com.rockpaperscissor.LOGIN";

   // components
   private EditText userInputBox;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.login);
      getSupportActionBar().hide();

      this.userInputBox = findViewById(R.id.userInputBox);
   }

   @Override
   public void onBackPressed() {
      FragmentManager fragmentManager = getSupportFragmentManager();
      Fragment currentFragment = fragmentManager.findFragmentById(R.id.loginConfirmDialog);

      ConfirmDialog exitDialog = ConfirmDialog.getInstance();

      if (currentFragment == null) {
         exitDialog.setDialogTitle("Exit Game");
         exitDialog.setDialogDescription("Are you sure you want to quit the game?");
         exitDialog.setOnCancel((View view) -> {
            fragmentManager.beginTransaction()
                  .remove(exitDialog)
                  .commit();
         });
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
      String displayName = userInputBox.getText().toString();

      // connect to the server
      login(displayName);
   }

   private void login(String displayName) {
      String body = RPSJson.toJson(LoginTemplate.createRequestObjectModel(displayName));
      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void run() {
            RPSPlayer player = RPSJson.fromJson(getResponse(), PlayerTemplate.class);
            Log.d("TAG", "Display name: " + player.getDisplayName());

            Intent intent = new Intent(LoginActivity.this, SelectPlayer.class);
            intent.putExtra(INTENT_LOGIN, player);
            startActivity(intent);
            finish();
         }
      };

      RPSServer.post(body, runnable);
   }
}
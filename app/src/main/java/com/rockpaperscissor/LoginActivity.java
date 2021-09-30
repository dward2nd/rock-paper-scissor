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

public class LoginActivity extends RPSActivity {
   public static final String INTENT_LOGIN = "com.rockpaperscissor.LOGIN";

   // components
   private EditText userInputBox;
   private EditText serverUrlBox;
   private ImageButton letsPlayBtn;

   private boolean loggingIn;

   private final View.OnClickListener quitGameHandler = (View view) -> {
      finishAffinity();
      System.exit(0);
   };

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);

      setDialogFragmentId(R.id.loginConfirmDialog);

      this.userInputBox = findViewById(R.id.userInputBox);
      this.serverUrlBox = findViewById(R.id.serverURLInputBox);
      this.letsPlayBtn = findViewById(R.id.nextBtn);

      boolean loggingIn = true;
   }

   @Override
   public void onBackPressed() {
      if (getCurrentDialog() != null)
         showConfirmDialog("Exit Game", "Are you sure you want to quit the game?",
               quitGameHandler);
      else
         removeExistingDialog();

   }

   /**
    * Event handler when user tap the send button
    **/
   public void onNextBtnClicked(View view) {
      if (!loggingIn) {
         loggingIn = true;
         letsPlayBtn.setAlpha(0.2f);
         letsPlayBtn.setClickable(false);

         String displayName = userInputBox.getText().toString();
         RPSServer.setServerUrl(serverUrlBox.getText().toString());

         // connect to the server
         login(displayName);
      }
   }

   private void login(String displayName) {
      FormBody formBody = new FormBody.Builder()
            .add("username", displayName)
            .build();

      String path = "/register";

      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void error(IOException e) {
            showAlertDialog("Network Error", e.getMessage());
            letsPlayBtn.setAlpha(1.0f);
            letsPlayBtn.setClickable(true);
            loggingIn = false;
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

      RPSServer.post(formBody, path, runnable);
   }
}
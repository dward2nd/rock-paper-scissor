package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.RequestTemplate;
import com.rockpaperscissor.json.jsontemplate.data.LoginTemplate;
import com.rockpaperscissor.json.jsontemplate.data.PlayerTemplate;

import java.lang.reflect.Type;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
   public static String INTENT_LOGIN = "com.rockpaperscissor.LOGIN";
   private long pressedTime;    // checking the time user press back button
   private EditText userInputBox;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.login);
      getSupportActionBar().hide();
      //Intent intent = getIntent();

      this.userInputBox = findViewById(R.id.userInputBox);
   }

   @Override
   public void onBackPressed() {
      if (pressedTime + 2000 > System.currentTimeMillis()) {
         Intent homeIntent = new Intent(Intent.ACTION_MAIN);
         homeIntent.addCategory(Intent.CATEGORY_HOME);
         homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
         startActivity(homeIntent);
      } else {
         Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
      }
      pressedTime = System.currentTimeMillis();
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
         }
      };

      RPSServer.post(body, runnable);
   }
}
package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
   public static final String EXTRA_LOGIN = "com.rockpaperscissor.LOGIN";
   private long pressedTime;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_login);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
      //Intent intent = getIntent();
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
      EditText editText = findViewById(R.id.userInputBox);
      String displayName = editText.getText().toString();

      // connect to the server
      if (!RPSServer.login(displayName)) {
         Toast.makeText(getApplicationContext(), "Something's wrong with the server.",
               Toast.LENGTH_LONG).show();
         return;
      }

      // launch new activity.
      Intent intent = new Intent(this, SelectPlayer.class);
      startActivity(intent);
   }

}
package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
   public static final String EXTRA_MESSAGE = "com.rockpaperscissor.MAIN";

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      getSupportActionBar().setDisplayShowTitleEnabled(false);
   }

   /**
    * Event handler when user tap the send button
    **/
   public void onNextBtnClicked(View view) {
      // try connecting to the server.

      // launch new activity.
      Intent intent = new Intent(this, SelectPlayer.class);
      EditText editText = (EditText) findViewById(R.id.userInputBox);
      String message = editText.getText().toString();
      intent.putExtra(EXTRA_MESSAGE, message);
      startActivity(intent);
   }
}
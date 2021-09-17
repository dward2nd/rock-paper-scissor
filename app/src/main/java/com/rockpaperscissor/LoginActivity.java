package com.rockpaperscissor;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {
   public static final String EXTRA_MESSAGE = "com.rockpaperscissor.LOGIN";
   public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   public final String postUrl = "http://192.168.1.8:3000";
   public final String postBody = "{\n" +
         "    \"name\": \"morpheus\",\n" +
         "    \"job\": \"leader\"\n" +
         "}";
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
      // connect to the server
      try {
         postRequest(postUrl, postBody);
      } catch (IOException e) {
         e.printStackTrace();
      }

      // launch new activity.
      Intent intent = new Intent(this, SelectPlayer.class);
      EditText editText = (EditText) findViewById(R.id.userInputBox);
      String message = editText.getText().toString();
      intent.putExtra(EXTRA_MESSAGE, message);
      startActivity(intent);
   }

   void postRequest(String postUrl, String postBody) throws IOException {
      OkHttpClient client = new OkHttpClient();
      RequestBody body = RequestBody.create(JSON, postBody);
      Request request = new Request.Builder()
            .url(postUrl)
            .post(body)
            .build();

      client.newCall(request).enqueue(new Callback() {
         @Override
         public void onFailure(Call call, IOException e) {
            call.cancel();
         }

         @Override
         public void onResponse(Call call, Response response) throws IOException {
            Log.e("TAG", response.body().string());
         }
      });
   }
}
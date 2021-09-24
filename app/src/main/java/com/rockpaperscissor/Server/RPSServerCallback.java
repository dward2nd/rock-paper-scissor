package com.rockpaperscissor.Server;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RPSServerCallback implements Callback {

   private RPSResponseRunnable runnable;

   public RPSServerCallback(RPSResponseRunnable runnable) {
      super();
      this.runnable = runnable;
   }

   @Override
   public void onFailure(Call call, IOException e) {
      e.printStackTrace();
   }

   @Override
   public void onResponse(Call call, Response response) throws IOException {
      try (ResponseBody responseBody = response.body()) {
         if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

         // for debugging pupose only
         Headers responseHeaders = response.headers();
         for (int i = 0, size = responseHeaders.size(); i < size; i++) {
            Log.d("TAG", responseHeaders.name(i) + ": " + responseHeaders.value(i));
         }

         String responseBodyString = responseBody.string();
         responseBodyString = responseBodyString.substring(1,responseBodyString.length()-1);
         runnable.setResponse(responseBodyString);
         runnable.run();

         Log.d("TAG", responseBodyString);
      }
   }
}

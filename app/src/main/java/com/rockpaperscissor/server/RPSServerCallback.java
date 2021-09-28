package com.rockpaperscissor.server;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RPSServerCallback implements Callback {

   private final RPSResponseRunnable runnable;

   public RPSServerCallback(RPSResponseRunnable runnable) {
      this.runnable = runnable;
   }

   @Override
   public void onFailure(@NonNull Call call, @NonNull IOException e) {
      this.runnable.error(e);
      e.printStackTrace();
   }

   @Override
   public void onResponse(@NonNull Call call, Response response) throws IOException {
      try (ResponseBody responseBody = response.body()) {
         if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

         // for debugging pupose only
         Headers responseHeaders = response.headers();
         for (int i = 0, size = responseHeaders.size(); i < size; i++) {
            Log.d("TAG", "HEADER " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
         }

         assert responseBody != null;
         String responseBodyString = responseBody.string();
         // For long-term development purpose, if you wish to remove the array part,
         // do that in Runnable objects instead.
         //responseBodyString = responseBodyString.substring(1, responseBodyString.length() - 1);
         runnable.setResponse(responseBodyString);
         runnable.run();

         Log.d("TAG", "BODY " + responseBodyString);
      }
   }
}

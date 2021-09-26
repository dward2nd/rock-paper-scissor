package com.rockpaperscissor.Server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

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
      this.runnable = runnable;
   }

   @Override
   public void onFailure(Call call, IOException e) {
      this.runnable.error(e);
      e.printStackTrace();
   }

   @Override
   public void onResponse(Call call, Response response) throws IOException {
      try (ResponseBody responseBody = response.body()) {
         if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

         // for debugging pupose only
         Headers responseHeaders = response.headers();
         for (int i = 0, size = responseHeaders.size(); i < size; i++) {
            Log.d("TAG", "HEADER " + responseHeaders.name(i) + ": " + responseHeaders.value(i));
         }

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

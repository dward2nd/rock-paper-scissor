package com.rockpaperscissor.Server;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RPSServer {
   private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   private static final String SERVER_ADDRESS = "192.168.1.8";
   private static final String SERVER_PORT = "8080";
   private static final String SERVER_URL = String.format("http://%s:%s", SERVER_ADDRESS, SERVER_PORT);
   // http api constants
   private static final OkHttpClient client = new OkHttpClient();

   /* Maybe use later, if needed.
   public static void get(String path, RPSResponseRunnable runnable) {
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .build();

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }
    */

   public static void post(FormBody body, RPSResponseRunnable runnable, String path) {
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .post(body)
            .build();

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

   public static void post(String postBody, RPSResponseRunnable runnable, String path) {
      RequestBody body = RequestBody.create(JSON, postBody);
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .post(body)
            .build();

      Log.d("TAG", postBody);

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }
}

   // Calling postRequest with no parameters is used for testing purposes.


package com.rockpaperscissor.Server;

import android.util.Log;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RPSServer {
   private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   private static final String SERVER_ADDRESS = "cbf4-2405-9800-b910-8003-c155-fc34-bc49-fb47.ngrok.io";
   private static final String SERVER_PORT = "8080";
   private static final String SERVER_URL = String.format("http://%s", SERVER_ADDRESS);
   // http api constants
   private static final OkHttpClient client = new OkHttpClient();

   public static void get(String path, RPSResponseRunnable runnable) {
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .build();

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

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


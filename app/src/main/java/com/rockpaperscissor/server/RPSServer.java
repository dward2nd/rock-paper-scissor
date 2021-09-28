package com.rockpaperscissor.server;

import android.content.Context;
import android.util.Log;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RPSServer {
   private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   private static String serverUrl = "http://192.168.1.8:8080";
   // http api constants
   private static final OkHttpClient client = new OkHttpClient();

   public static void setServerUrl(String newServerUrl) {
      serverUrl = newServerUrl;
   }

   public static void get(String path, RPSResponseRunnable runnable) {
      Request request = new Request.Builder()
            .url(serverUrl + path)
            .build();

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

   public static void post(FormBody body, String path, RPSResponseRunnable runnable) {
      Request request = new Request.Builder()
            .url(serverUrl + path)
            .post(body)
            .build();

      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }
}
package com.rockpaperscissor.Server;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rockpaperscissor.RPSPlayer;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RPSServer {
   public static final String DEMO_BODY = "{\n" +
         "    \"name\": \"morpheus\",\n" +
         "    \"job\": \"leader\"\n" +
         "}";
   private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   private static final String SERVER_URL = "http://192.168.1.8:8080";
   // http api constants
   private static final OkHttpClient client = new OkHttpClient();

   public static void get(String path, RPSResponseRunnable runnable) {
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .build();

//      Log.d("TAG", postBody);

      //return "{\"action\":\"login_welcome\",\"data\":{\"uid\":\"locheurollko\",\"displayName\":\"Takumi\"}}";
      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

   public static void post(String postBody, RPSResponseRunnable runnable, String path) {
      RequestBody body = RequestBody.create(JSON, postBody);
      Request request = new Request.Builder()
            .url(SERVER_URL + path)
            .post(body)
            .build();

      Log.d("TAG", postBody);

      //return "{\"action\":\"login_welcome\",\"data\":{\"uid\":\"locheurollko\",\"displayName\":\"Takumi\"}}";
      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

   public static void post(String postBody, RPSResponseRunnable runnable) {
      RequestBody body = RequestBody.create(JSON, postBody);
      Request request = new Request.Builder()
            .url(SERVER_URL)
            .post(body)
            .build();

      Log.d("TAG", postBody);

      //return "{\"action\":\"login_welcome\",\"data\":{\"uid\":\"locheurollko\",\"displayName\":\"Takumi\"}}";
      client.newCall(request).enqueue(new RPSServerCallback(runnable));
   }

   // Calling postRequest with no parameters is used for testing purposes.
   public static void post() {
      post(DEMO_BODY, new RPSResponseRunnable() {
         @Override
         public void run() {
            Log.d("TAG", getResponse());
         }
      });
   }
}

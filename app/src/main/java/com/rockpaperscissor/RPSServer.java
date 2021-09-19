package com.rockpaperscissor;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.rockpaperscissor.jsonobjectmodel.data.LoginObjectModel;
import com.rockpaperscissor.jsonobjectmodel.data.PlayerObjectModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RPSServer {
   public static final String DEMO_BODY = "{\n" +
         "    \"name\": \"morpheus\",\n" +
         "    \"job\": \"leader\"\n" +
         "}";
   private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
   private static final String SERVER_URL = "http://192.168.1.8:3000";
   // gson-related instances.
   private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
   private static final Gson GSON = GSON_BUILDER.create();
   // http api constants
   private static OkHttpClient client = new OkHttpClient();
   // game variables
   private static RPSPlayer player;
   private static boolean loggedIn = false;

   public static RPSPlayer getClientPlayer() {
      return player;
   }

   public static boolean isLoggedIn() {
      return loggedIn;
   }

   private static String post(String postBody) {
      RequestBody body = RequestBody.create(JSON, postBody);
      Request request = new Request.Builder()
            .url(SERVER_URL)
            .post(body)
            .build();

      Log.d("TAG", postBody);

      //return "{\"action\":\"login_welcome\",\"data\":{\"uid\":\"locheurollko\",\"displayName\":\"Takumi\"}}";
      try (Response response = client.newCall(request).execute()) {
         if (!response.isSuccessful())
            throw new IOException("Unexpected code " + response);

         return response.body().string();
      } catch (Exception e) {
         e.printStackTrace();

         return null;
      }
   }

   // Calling postRequest with no parameters is used for testing purposes.
   private static void post() throws IOException {
      post(DEMO_BODY);
   }

   public static boolean login(String displayName) {
      String body = GSON.toJson(LoginObjectModel.createRequestObjectModel(displayName));
      try {
         player = GSON.fromJson(post(body), PlayerObjectModel.class);
         System.out.println(player.getDisplayName());

         return true;
      } catch (Exception e) {
         e.printStackTrace();
      }

      return false;
   }
}

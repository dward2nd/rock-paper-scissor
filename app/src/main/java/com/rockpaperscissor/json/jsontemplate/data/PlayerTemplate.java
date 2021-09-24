package com.rockpaperscissor.json.jsontemplate.data;

import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.json.jsontemplate.RequestTemplate;

import java.util.HashMap;

public class PlayerTemplate extends RPSPlayer {
   public PlayerTemplate(String uid, String displayName) {
      super(uid, displayName);
   }

   public static RequestTemplate createRequestObjectModel(String uid, String displayName) {
      return new RequestTemplate("login_welcome", new PlayerTemplate(uid, displayName));
   }

   private String session;
   private int score;
   private HashMap<String, HashMap<String, String>> request;

   public String getSession() {
      return session;
   }

   public int getScore() {
      return score;
   }

   public HashMap<String, HashMap<String, String>> getRequest() {
      return request;
   }

}
package com.rockpaperscissor.json.jsontemplate.data;

import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.json.jsontemplate.RequestTemplate;

public class PlayerTemplate extends RPSPlayer {
   public PlayerTemplate(String uid, String displayName) {
      super(uid, displayName);
   }

   public static RequestTemplate createRequestObjectModel(String uid, String displayName) {
      return new RequestTemplate("login_welcome", new PlayerTemplate(uid, displayName));
   }
}
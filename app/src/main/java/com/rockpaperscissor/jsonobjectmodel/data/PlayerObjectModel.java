package com.rockpaperscissor.jsonobjectmodel.data;

import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.jsonobjectmodel.RequestObjectModel;

public class PlayerObjectModel extends RPSPlayer {
   public PlayerObjectModel(String uid, String displayName) {
      super(uid, displayName);
   }

   public static RequestObjectModel createRequestObjectModel(String uid, String displayName) {
      return new RequestObjectModel("login_welcome", new PlayerObjectModel(uid, displayName));
   }
}
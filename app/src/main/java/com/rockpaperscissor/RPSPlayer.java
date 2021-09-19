package com.rockpaperscissor;

public class RPSPlayer {
   private String uid;
   private String displayName;
   private int totalGamePlayed = 0;
   private int totalGameWon = 0;

   public RPSPlayer(String uid, String displayName) {
      this.uid = uid;
      this.displayName = displayName;
   }

   public String getUid() {
      return uid;
   }

   public String getDisplayName() {
      return displayName;
   }

   public int getTotalGamePlayed() {
      return totalGamePlayed;
   }

   public int getTotalGameWon() {
      return totalGameWon;
   }

}

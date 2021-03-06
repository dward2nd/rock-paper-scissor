package com.rockpaperscissor.json.jsontemplate;

import androidx.annotation.NonNull;

public class PlayerTemplate {
   private String id;
   private String username;
   private String session;
   private int score;
   private int played;
   private boolean challenge = false;

   public int getPlayed() {
      return played;
   }

   public void setPlayed(int played) {
      this.played = played;
   }

   public boolean getChallenge() {
      return challenge;
   }

   public void setChallenge(boolean b) {
      this.challenge = b;
   }

   public PlayerTemplate(String id, String username) {
      this.id = id;
      this.username = username;
   }

   public PlayerTemplate(String id, String username, String session) {
      this.id = id;
      this.username = username;
      this.session = session;
   }

   public PlayerTemplate(String username) { this.username = username;}

   public PlayerTemplate(String id, String username, String session, int score) {
      this.id = id;
      this.username = username;
      this.session = session;
      this.score = score;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getUsername() {
      return username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getSession() {
      return session;
   }

   public void setSession(String session) {
      this.session = session;
   }

   public int getScore() {
      return score;
   }

   public void setScore(int score) {
      this.score = score;
   }


   @NonNull
   @java.lang.Override
   public java.lang.String toString() {
      return "userData{" +
            "id='" + id + '\'' +
            ", username='" + username + '\'' +
            ", session='" + session + '\'' +
            ", score=" + score +
            '}';
   }

}
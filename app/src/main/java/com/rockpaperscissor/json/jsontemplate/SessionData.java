package com.rockpaperscissor.json.jsontemplate;

import androidx.annotation.NonNull;

import java.util.HashMap;

public class SessionData {
   protected String winner;
   protected HashMap<String, String> Player1;
   protected HashMap<String, String> Player2;
   protected String session;
   //round: ID: choice
   protected HashMap<Integer, HashMap<String, Integer>> round = new HashMap<>();
   protected Integer current;

   public HashMap<String, Integer> createHash(Integer choose, String userId) {
      HashMap<String, Integer> hashTemp = new HashMap<>();
      hashTemp.put(userId, choose);
      return hashTemp;
   }

   private HashMap<String, String> getPlayertemp(String Id, String UserName, String Score) {
      HashMap<String, String> dataTmp = new HashMap<>();
      dataTmp.put("Id", Id);
      dataTmp.put("username", UserName);
      dataTmp.put("score", Score);
      return dataTmp;
   }

   public void setPlayer2Null() {
      this.Player2 = null;
   }

   public HashMap<String, String> getPlayer1() {
      return Player1;
   }

   public void setPlayer1(String Id, String UserName, String Score) {
      Player1 = getPlayertemp(Id, UserName, Score);
   }

   public HashMap<String, String> getPlayer2() {
      return Player2;
   }

   public void setPlayer2(String Id, String UserName, String Score) {
      Player2 = getPlayertemp(Id, UserName, Score);
   }

   public void putHash(String userId, Integer choose) {

   }

   public String getWinner() {
      return winner;
   }

   public void setWinner(String winner) {
      this.winner = winner;
   }


   public HashMap<Integer, HashMap<String, Integer>> getRound() {
      return round;
   }

   public void setRound(HashMap<Integer, HashMap<String, Integer>> round) {
      this.round = round;
   }

   public void setRound(Integer round, HashMap<String, Integer> Arr) {
      this.round.put(round, Arr);
   }

   public Integer getCurrent() {
      return current;
   }

   public void setCurrent(Integer string) {
      this.current = string;
   }

   public String getSession() {
      return session;
   }

   public void setSession(String session) {
      this.session = session;
   }

   @NonNull
   @java.lang.Override
   public java.lang.String toString() {
      return "SessionData{" +
            "winner='" + winner + '\'' +
            ", Player1=" + Player1 +
            ", Player2=" + Player2 +
            ", session='" + session + '\'' +
            ", round=" + round +
            ", current=" + current +
            '}';
   }
}

package com.rockpaperscissor.Server;

public abstract class RPSResponseRunnable implements Runnable {
   private String response;

   public abstract void run();

   public String getResponse() {
      return response;
   }

   public void setResponse(String response) {
      this.response = response;
   }
}

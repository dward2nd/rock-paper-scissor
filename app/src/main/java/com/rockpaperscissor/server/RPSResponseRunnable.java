package com.rockpaperscissor.server;

import java.io.IOException;

public abstract class RPSResponseRunnable implements Runnable {
   private String response;

   public abstract void run();

   public abstract void error(IOException e);

   public String getResponse() {
      return response;
   }

   public void setResponse(String response) {
      this.response = response;
   }
}

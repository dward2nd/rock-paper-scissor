package com.rockpaperscissor.jsonobjectmodel;

public class RequestObjectModel {
   private String action;
   private Object data;

   public RequestObjectModel(String action, Object data) {
      this.action = action;
      this.data = data;
   }

   public String getAction() {
      return action;
   }

   public Object getData() {
      return data;
   }
}

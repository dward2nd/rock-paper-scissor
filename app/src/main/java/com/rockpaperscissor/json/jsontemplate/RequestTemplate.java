package com.rockpaperscissor.json.jsontemplate;

public class RequestTemplate {
   private String action;
   private Object data;

   public RequestTemplate(String action, Object data) {
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

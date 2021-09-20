package com.rockpaperscissor.json.jsontemplate.data;

import com.rockpaperscissor.json.jsontemplate.RequestTemplate;

public class LoginTemplate {
   private String displayName;

   public LoginTemplate(String displayName) {
      this.displayName = displayName;
   }

   public static RequestTemplate createRequestObjectModel(String displayName) {
      return new RequestTemplate("login", new LoginTemplate(displayName));
   }

   public String getDisplayName() {
      return displayName;
   }
}

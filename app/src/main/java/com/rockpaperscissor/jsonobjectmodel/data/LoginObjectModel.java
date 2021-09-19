package com.rockpaperscissor.jsonobjectmodel.data;

import com.rockpaperscissor.jsonobjectmodel.RequestObjectModel;

public class LoginObjectModel {
   private String displayName;

   public LoginObjectModel(String displayName) {
      this.displayName = displayName;
   }

   public static RequestObjectModel createRequestObjectModel(String displayName) {
      return new RequestObjectModel("login", new LoginObjectModel(displayName));
   }

   public String getDisplayName() {
      return displayName;
   }
}

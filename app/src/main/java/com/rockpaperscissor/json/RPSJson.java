package com.rockpaperscissor.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

public class RPSJson {
   // gson-related instances.
   private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
   private static final Gson GSON = GSON_BUILDER.create();

   public static <T> T fromJson(String json, Type typeOfT) {
      return GSON.fromJson(json, typeOfT);
   }

   /*
   public static String toJson(Object obj) {
      return GSON.toJson(obj);
   }
    */
}

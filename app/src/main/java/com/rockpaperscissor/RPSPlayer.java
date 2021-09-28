package com.rockpaperscissor;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.rockpaperscissor.json.jsontemplate.data.PlayerTemplate;

import java.util.ArrayList;
import java.util.HashMap;

public class RPSPlayer implements Parcelable {
   private String uid;
   private String displayName;
   private String session;
   private int totalGamePlayed = 0;
   private int totalGameWon = 0;

   public RPSPlayer(String uid, String displayName, String session, int totalGamePlayed, int totalGameWon) {
      this.uid = uid;
      this.displayName = displayName;
      this.session = session;
      this.totalGamePlayed = totalGamePlayed;
      this.totalGameWon = totalGameWon;
   }

   public RPSPlayer(String uid, String displayName, String session) {
      this.uid = uid;
      this.displayName = displayName;
      this.session = session;
   }

   public static final Creator<RPSPlayer> CREATOR = new Creator<RPSPlayer>() {
      @Override
      public RPSPlayer createFromParcel(Parcel in) {
         return new RPSPlayer(in);
      }

      @Override
      public RPSPlayer[] newArray(int size) {
         return new RPSPlayer[size];
      }
   };

   public RPSPlayer(Parcel in) {
      this.uid = in.readString();
      this.displayName = in.readString();
      this.session = in.readString();
      this.totalGamePlayed = in.readInt();
      this.totalGameWon = in.readInt();
   }

   public String getUid() {
      return uid;
   }

   public String getDisplayName() {
      return displayName;
   }

   public RPSPlayer(PlayerTemplate template) {
      this.uid = template.getId();
      this.displayName = template.getUsername();
      this.session = template.getSession();
      this.totalGamePlayed = template.getPlayed();
      this.totalGameWon = template.getScore();
   }

   public static RPSPlayer[] getRPSPlayerArray(PlayerTemplate[] template) {
      RPSPlayer[] newInstance = new RPSPlayer[template.length];
      for (int i = 0; i < template.length; ++i)
         newInstance[i] = new RPSPlayer(template[i].getId(),
               template[i].getUsername(), template[i].getSession(), template[i].getPlayed(),
               template[i].getScore());

      return newInstance;
   }

   public static ArrayList<RPSPlayer> getRPSPlayerArrayList(PlayerTemplate[] template) {
      ArrayList<RPSPlayer> newInstance = new ArrayList<RPSPlayer>();
      for (PlayerTemplate el : template)
         newInstance.add(new RPSPlayer(el.getId(), el.getUsername(), el.getSession(), el.getPlayed(),
               el.getScore()));

      return newInstance;
   }

   public int getTotalGamePlayed() {
      return totalGamePlayed;
   }

   public int getTotalGameWon() {
      return totalGameWon;
   }

   public void countPlayed() {
      ++this.totalGamePlayed;
   }

   public void countWon() {
      ++this.totalGameWon;
   }

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(this.uid);
      parcel.writeString(this.displayName);
      parcel.writeString(this.session);
      parcel.writeInt(this.totalGamePlayed);
      parcel.writeInt(this.totalGameWon);
   }

   public String getSession() {
      return session;
   }
}

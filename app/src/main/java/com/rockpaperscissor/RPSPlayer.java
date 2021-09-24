package com.rockpaperscissor;

import android.os.Parcel;
import android.os.Parcelable;

import com.rockpaperscissor.json.jsontemplate.data.PlayerTemplate;

import java.util.HashMap;

public class RPSPlayer implements Parcelable {
   private String uid;
   private String displayName;
   private String session;
   private int totalGamePlayed = 0;
   private int totalGameWon = 0;
   private HashMap<String, HashMap<String, String>> request;

   public RPSPlayer(String uid, String displayName, String session, int totalGamePlayed, int totalGameWon, HashMap<String, HashMap<String, String>> request) {
      this.uid = uid;
      this.displayName = displayName;
      this.session = session;
      this.totalGamePlayed = totalGamePlayed;
      this.totalGameWon = totalGameWon;
      this.request = request;
   }
   public RPSPlayer(String uid, String displayName) {
      this.uid = uid;
      this.displayName = displayName;
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
      this.request = in.readHashMap(String.class.getClassLoader());
   }

   public String getUid() {
      return uid;
   }

   public String getDisplayName() {
      return displayName;
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
      parcel.writeMap(this.request);
   }
   public static RPSPlayer getInstance(PlayerTemplate template) {
      return new RPSPlayer(template.getId(), template.getUsername(), template.getSession(), template.getPlayed(), template.getScore(), template.getRequest());
   }
}

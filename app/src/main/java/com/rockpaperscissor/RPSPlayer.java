package com.rockpaperscissor;

import android.os.Parcel;
import android.os.Parcelable;

public class RPSPlayer implements Parcelable {
   private String uid;
   private String displayName;
   private int totalGamePlayed = 0;
   private int totalGameWon = 0;

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
      this.totalGamePlayed = in.readInt();
      this.totalGameWon = in.readInt();
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

   @Override
   public int describeContents() {
      return 0;
   }

   @Override
   public void writeToParcel(Parcel parcel, int i) {
      parcel.writeString(this.uid);
      parcel.writeString(this.displayName);
      parcel.writeInt(this.totalGamePlayed);
      parcel.writeInt(this.totalGameWon);
   }
}

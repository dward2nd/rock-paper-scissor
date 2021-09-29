package com.rockpaperscissor.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class GameplayFinalResultFragment extends Fragment {
   private boolean doesClientWin;
   private boolean isDraw;

   public GameplayFinalResultFragment() {
      super(R.layout.fragment_finalresult);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      TextView resultLabel = view.findViewById(R.id.finalGameplayVictoryResultLabel);
      if (isDraw)
         resultLabel.setText("draw");
      else if (doesClientWin)
         resultLabel.setText("win");
      else
         resultLabel.setText("lose");
   }

   public void setResult(boolean doesClientWin, boolean isDraw, boolean surrendered, boolean opponentOut) {
      this.doesClientWin = doesClientWin && !isDraw && !surrendered || opponentOut;
      this.isDraw = isDraw;
   }
}

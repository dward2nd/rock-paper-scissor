package com.rockpaperscissor.components;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class GameplayFinalResultFragment extends Fragment {
   private static final GameplayFinalResultFragment myself = new GameplayFinalResultFragment();
   private TextView resultLabel;
   private boolean doesClientWin;
   private boolean isDraw;

   public GameplayFinalResultFragment() {
      super(R.layout.victory_win_component);
   }

   public static GameplayFinalResultFragment getInstance() {
      return myself;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      this.resultLabel = view.findViewById(R.id.finalGameplayVictoryResultLabel);
      if (isDraw)
         resultLabel.setText("draw");
      else if (doesClientWin)
         resultLabel.setText("win");
      else
         resultLabel.setText("lose");
   }

   public void setResult(boolean doesClientWin, boolean isDraw) {
      this.doesClientWin = doesClientWin;
      this.isDraw = isDraw;
   }
}

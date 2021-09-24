package com.rockpaperscissor.components;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class GameplayResultFragment extends Fragment {
   public static final int[] LEFT_ICON = {
         R.drawable.rock_left,
         R.drawable.paper_left,
         R.drawable.scissor_left
   };
   public static final int[] RIGHT_ICON = {
         R.drawable.rock_right,
         R.drawable.paper_right,
         R.drawable.scissor_right
   };
   private static final GameplayResultFragment myself = new GameplayResultFragment();
   // ui components
   ImageView clientPlayerImageView;
   ImageView opponentPlayerImageView;
   TextView clientPlayerLabel;
   TextView opponentPlayerLabel;
   private String clientPlayerName;
   private String opponentPlayerName;
   private int clientChoice;
   private int opponentChoice;

   public GameplayResultFragment() {
      super(R.layout.gameplay2_component);
   }

   public static GameplayResultFragment getInstance() {
      return myself;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      clientPlayerImageView = view.findViewById(R.id.clientPlayerChoiceImageView);
      opponentPlayerImageView = view.findViewById(R.id.opponentPlayerChoiceImageView);

      clientPlayerImageView.setImageResource(LEFT_ICON[clientChoice - 1]);
      opponentPlayerImageView.setImageResource(RIGHT_ICON[opponentChoice - 1]);

      clientPlayerLabel = view.findViewById(R.id.clientPlayerNameLabel);
      opponentPlayerLabel = view.findViewById(R.id.opponentPlayerNameLabel);

      clientPlayerLabel.setText(clientPlayerName);
      opponentPlayerLabel.setText(opponentPlayerName);
   }

   public void setClientPlayerName(String clientPlayerName) {
      this.clientPlayerName = clientPlayerName;
   }

   public void setOpponentPlayerName(String opponentPlayerName) {
      this.opponentPlayerName = opponentPlayerName;
   }

   public void setClientChoice(int clientChoice) {
      this.clientChoice = clientChoice;
   }

   public void setOpponentChoice(int opponentChoice) {
      this.opponentChoice = opponentChoice;
   }
}

package com.rockpaperscissor.components;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rockpaperscissor.GameplayActivity;
import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.SelectPlayer;
import com.rockpaperscissor.Server.RPSResponseRunnable;
import com.rockpaperscissor.Server.RPSServer;

import okhttp3.FormBody;

public class SelectPlayerSessionManager extends Fragment {
   private static final SelectPlayerSessionManager myself = new SelectPlayerSessionManager();

   private EditText clientSessionIdLabel;
   private EditText opponentSessionIdLabel;

   private Button startBtn;

   private RPSPlayer clientPlayer;
   private RPSPlayer pseudoOpponentPlayer;

   public SelectPlayerSessionManager() {
      super(R.layout.player_session);
   }

   public static SelectPlayerSessionManager getInstance() {
      return myself;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      clientSessionIdLabel = view.findViewById(R.id.sessionClientSessionIdBox);
      clientSessionIdLabel.setText(clientPlayer.getSession());

      opponentSessionIdLabel = view.findViewById(R.id.sessionOpponentSessionIdBox);

      startBtn = view.findViewById(R.id.sessionStartGameBtn);
      startBtn.setOnClickListener((View listenerView) -> {
         startGame(opponentSessionIdLabel.getText().toString());
      });
   }

   public void setClientPlayer(RPSPlayer clientPlayer) {
      this.clientPlayer = clientPlayer;
   }

   public void setPseudoOpponentPlayer(RPSPlayer pseudoOpponentPlayer) {
      this.pseudoOpponentPlayer = pseudoOpponentPlayer;
   }

   private void startGame(String session) {
      String path = "/join";
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .add("Session", session)
            .build();

      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void run() {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);
            AlertDialog notfoundDialog = AlertDialog.getInstance();
            notfoundDialog.setOnCancel((View view) -> {
               fragmentManager.beginTransaction()
                     .remove(notfoundDialog)
                     .commit();
            });

            if (getResponse().equals("joined")) {
               Intent intent = new Intent(getActivity(), GameplayActivity.class);
               intent.putExtra(SelectPlayer.INTENT_CLIENT, clientPlayer);
               intent.putExtra(SelectPlayer.INTENT_OPPONENT, pseudoOpponentPlayer);
               intent.putExtra(SelectPlayer.INTENT_SESSION, session);
               startActivity(intent);
            } else if (getResponse().equals("full")) {
               if (currentFragment != null) {
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
               }
               notfoundDialog.setDialogTitle("Already taken");
               notfoundDialog.setDialogDescription("The session `" + session + "` is currently active with the other player.");
               fragmentManager.beginTransaction()
                     .add(R.id.playerMenuFragment, notfoundDialog)
                     .commit();
            } else if (getResponse().equals("yourself")) {
               if (currentFragment != null) {
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
               }
               notfoundDialog.setDialogTitle("This is yours.");
               notfoundDialog.setDialogDescription("The session `" + session + "` is your session ID. You cannot join yourself.");
               fragmentManager.beginTransaction()
                     .add(R.id.playerMenuFragment, notfoundDialog)
                     .commit();
            } else {
               if (currentFragment != null) {
                  fragmentManager.beginTransaction()
                        .remove(currentFragment)
                        .commit();
               }
               notfoundDialog.setDialogTitle("Not found");
               notfoundDialog.setDialogDescription("The session `" + session + "` not found on the server.");
               fragmentManager.beginTransaction()
                     .add(R.id.playerMenuFragment, notfoundDialog)
                     .commit();

            }
         }
      };

      RPSServer.post(formBody, runnable, path);
   }
}

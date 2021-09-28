package com.rockpaperscissor.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rockpaperscissor.GameplayActivity;
import com.rockpaperscissor.R;
import com.rockpaperscissor.RPSPlayer;
import com.rockpaperscissor.SelectPlayerActivity;
import com.rockpaperscissor.dialogs.AlertDialog;
import com.rockpaperscissor.json.RPSJson;
import com.rockpaperscissor.json.jsontemplate.MiniData;
import com.rockpaperscissor.server.RPSResponseRunnable;
import com.rockpaperscissor.server.RPSServer;

import java.io.IOException;

import okhttp3.FormBody;

public class SelectPlayerSessionManager extends Fragment {
   private EditText opponentSessionIdLabel;

   private RPSPlayer clientPlayer;

   public SelectPlayerSessionManager() {
      super(R.layout.fragment_sessionmanager);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      EditText clientSessionIdLabel = view.findViewById(R.id.sessionClientSessionIdBox);
      clientSessionIdLabel.setText(clientPlayer.getSession());

      opponentSessionIdLabel = view.findViewById(R.id.sessionOpponentSessionIdBox);

      Button startBtn = view.findViewById(R.id.sessionStartGameBtn);
      startBtn.setOnClickListener((View listenerView) ->
            startGame(opponentSessionIdLabel.getText().toString().toUpperCase()));
   }

   public void setClientPlayer(RPSPlayer clientPlayer) {
      this.clientPlayer = clientPlayer;
   }

   private void startGame(String session) {
      String path = "/join";
      FormBody formBody = new FormBody.Builder()
            .add("Id", clientPlayer.getUid())
            .add("Session", session)
            .build();

      RPSResponseRunnable runnable = new RPSResponseRunnable() {
         @Override
         public void error(IOException e) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);

            AlertDialog notfoundDialog = AlertDialog.getInstance();
            notfoundDialog.setDialogTitle("Network Error");
            notfoundDialog.setDialogDescription(e.getMessage());

            if (currentFragment != null)
               fragmentManager.beginTransaction()
                     .remove(currentFragment)
                     .commit();

            fragmentManager.beginTransaction()
                  .add(R.id.playerMenuFragment, notfoundDialog)
                  .commit();
         }

         @Override
         public void run() {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            Fragment currentFragment = fragmentManager.findFragmentById(R.id.playerMenuFragment);
            AlertDialog notfoundDialog = AlertDialog.getInstance();

            switch (getResponse()) {
               case "Room is not found":
                  if (currentFragment != null) {
                     fragmentManager.beginTransaction()
                           .remove(currentFragment)
                           .commit();
                  }
                  notfoundDialog.setDialogTitle("Not found");
                  notfoundDialog.setDialogDescription("The session '" + session + "' not found on the server.");
                  fragmentManager.beginTransaction()
                        .add(R.id.playerMenuFragment, notfoundDialog)
                        .commit();

                  break;
               case "Session is full":
                  if (currentFragment != null) {
                     fragmentManager.beginTransaction()
                           .remove(currentFragment)
                           .commit();
                  }
                  notfoundDialog.setDialogTitle("Already taken");
                  notfoundDialog.setDialogDescription("The session '" + session + "' is currently active with the other player.");
                  fragmentManager.beginTransaction()
                        .add(R.id.playerMenuFragment, notfoundDialog)
                        .commit();
                  break;
               case "yourself":
                  if (currentFragment != null) {
                     fragmentManager.beginTransaction()
                           .remove(currentFragment)
                           .commit();
                  }
                  notfoundDialog.setDialogTitle("This is yours.");
                  notfoundDialog.setDialogDescription("The session '" + session + "' is your session ID. You cannot join yourself.");
                  fragmentManager.beginTransaction()
                        .add(R.id.playerMenuFragment, notfoundDialog)
                        .commit();
                  break;
               default:
                  Log.d("TAG", getResponse());
                  MiniData response = RPSJson.fromJson(getResponse(), MiniData.class);
                  RPSPlayer opponent = new RPSPlayer(response.getId(), response.getUsername(), session);

                  Intent intent = new Intent(getActivity(), GameplayActivity.class);
                  intent.putExtra(SelectPlayerActivity.INTENT_CLIENT, clientPlayer);
                  intent.putExtra(SelectPlayerActivity.INTENT_OPPONENT, opponent);
                  intent.putExtra(SelectPlayerActivity.INTENT_SESSION, session);
                  startActivity(intent);

                  break;
            }
         }
      };

      RPSServer.post(formBody, path, runnable);
   }
}

package com.rockpaperscissor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.rockpaperscissor.dialogs.AlertDialog;
import com.rockpaperscissor.dialogs.ConfirmDialog;
import com.rockpaperscissor.dialogs.SettingDialog;

import java.io.IOException;
import java.util.Objects;

public abstract class RPSActivity extends AppCompatActivity {
   // ui components
   private final AlertDialog alertDialog = new AlertDialog();
   private final ConfirmDialog confirmDialog = new ConfirmDialog();
   private final SettingDialog settingDialog = new SettingDialog();
   private final View.OnClickListener networkErrorHandler = (View view) -> {
      Intent intent = new Intent(this, LoginActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
      startActivity(intent);
      finishAndRemoveTask();
   };
   // others
   protected FragmentManager fragmentManager;
   // game data
   private int dialogFragmentId;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      Objects.requireNonNull(getSupportActionBar()).hide();
      this.fragmentManager = getSupportFragmentManager();
   }

   public void setDialogFragmentId(int dialogFragmentId) {
      this.dialogFragmentId = dialogFragmentId;
      this.fragmentManager = getSupportFragmentManager();
   }

   public Fragment getCurrentDialog() {
      return fragmentManager.findFragmentById(dialogFragmentId);
   }

   public void removeExistingDialog() {
      Fragment currentFragment = getCurrentDialog();
      if (currentFragment != null)
         fragmentManager.beginTransaction()
               .remove(currentFragment)
               .commit();
   }

   public void showAlertDialog(String title, String description) {
      removeExistingDialog();

      alertDialog.setDialogTitle(title);
      alertDialog.setDialogDescription(description);
      fragmentManager.beginTransaction()
            .add(dialogFragmentId, alertDialog)
            .commit();
   }

   public void showAlertDialog(String title, String description, View.OnClickListener onCancel) {
      alertDialog.setOnCancel(onCancel);
      this.showAlertDialog(title, description);
   }

   public void showConfirmDialog(String title, String description, View.OnClickListener onConfirm) {
      removeExistingDialog();

      confirmDialog.setDialogTitle(title);
      confirmDialog.setDialogDescription(description);
      confirmDialog.setOnConfirm(onConfirm);
      fragmentManager.beginTransaction()
            .add(dialogFragmentId, confirmDialog)
            .commit();
   }

   public void showConfirmDialog(String title, String description, View.OnClickListener onConfirm,
                                 View.OnClickListener onCancel) {
      confirmDialog.setOnCancel(onCancel);
      this.showConfirmDialog(title, description, onConfirm);
   }

   public void showSettingDialog() {
      removeExistingDialog();

      fragmentManager.beginTransaction()
            .add(dialogFragmentId, settingDialog)
            .commit();
   }

   public void networkErrorDialogShow(IOException e) {
      showAlertDialog("Network Error", e.getMessage(), networkErrorHandler);
   }

   @Override
   public void onBackPressed() {
      removeExistingDialog();
      super.onBackPressed();
   }
}

package com.rockpaperscissor.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class ConfirmDialog extends Fragment {
   // data
   private String dialogTitle;
   private String dialogDescription;
   // event handler
   private View.OnClickListener onConfirm;
   private View.OnClickListener onCancel;

   public ConfirmDialog() {
      super(R.layout.dialog_confirm);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      // ui components
      TextView confirmDialogTitle = view.findViewById(R.id.confirmDialogTitle);
      TextView confirmDialogDescription = view.findViewById(R.id.confirmDialogDescription);

      confirmDialogTitle.setText(dialogTitle);
      confirmDialogDescription.setText(dialogDescription);

      ImageButton confirmBtn = view.findViewById(R.id.confirmBtn);
      ImageButton cancelBtn = view.findViewById(R.id.cancelBtn);

      confirmBtn.setOnClickListener(onConfirm);
      cancelBtn.setOnClickListener(onCancel);

      view.setClickable(true);
   }

   public void setDialogTitle(String dialogTitle) {
      this.dialogTitle = dialogTitle;
   }

   public void setDialogDescription(String dialogDescription) {
      this.dialogDescription = dialogDescription;
   }

   public void setOnConfirm(View.OnClickListener onConfirm) {
      this.onConfirm = onConfirm;
   }

   public void setOnCancel(View.OnClickListener onCancel) {
      this.onCancel = onCancel;
   }

}

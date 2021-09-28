package com.rockpaperscissor.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class ConfirmDialog extends Fragment {
   private static final ConfirmDialog confirmDialog = new ConfirmDialog();
   // ui components
   private TextView confirmDialogTitle;
   private ImageButton confirmBtn;
   private TextView confirmDialogDescription;
   private ImageButton cancelBtn;
   // data
   private String dialogTitle;
   private String dialogDescription;
   // event handler
   private View.OnClickListener onConfirm;
   private View.OnClickListener onCancel;

   public ConfirmDialog() {
      super(R.layout.dialog_confirm);
   }

   public static ConfirmDialog getInstance() {
      return confirmDialog;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      confirmDialogTitle = view.findViewById(R.id.confirmDialogTitle);
      confirmDialogDescription = view.findViewById(R.id.confirmDialogDescription);

      confirmDialogTitle.setText(dialogTitle);
      confirmDialogDescription.setText(dialogDescription);

      confirmBtn = view.findViewById(R.id.confirmBtn);
      cancelBtn = view.findViewById(R.id.cancelBtn);

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

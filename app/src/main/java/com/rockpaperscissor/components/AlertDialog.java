package com.rockpaperscissor.components;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class AlertDialog extends Fragment {
   private static final AlertDialog myself = new AlertDialog();
   // ui components
   private TextView confirmDialogTitle;
   private TextView confirmDialogDescription;
   private ImageButton cancelBtn;
   // data
   private String dialogTitle;
   private String dialogDescription;
   // event handler
   private View.OnClickListener onCancel;

   public AlertDialog() {
      super(R.layout.alert_dialog);
   }

   public static AlertDialog getInstance() {
      return myself;
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      confirmDialogTitle = view.findViewById(R.id.confirmDialogTitle);
      confirmDialogDescription = view.findViewById(R.id.confirmDialogDescription);

      confirmDialogTitle.setText(dialogTitle);
      confirmDialogDescription.setText(dialogDescription);

      cancelBtn = view.findViewById(R.id.cancelBtn);

      cancelBtn.setOnClickListener(onCancel);

      view.setClickable(true);
   }

   public void setDialogTitle(String dialogTitle) {
      this.dialogTitle = dialogTitle;
   }

   public void setDialogDescription(String dialogDescription) {
      this.dialogDescription = dialogDescription;
   }

   public void setOnCancel(View.OnClickListener onCancel) {
      this.onCancel = onCancel;
   }
}
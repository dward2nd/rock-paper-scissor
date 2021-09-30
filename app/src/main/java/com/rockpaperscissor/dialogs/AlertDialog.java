package com.rockpaperscissor.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class AlertDialog extends Fragment {
   // data
   private String dialogTitle;
   private String dialogDescription;
   // event handler
   private View.OnClickListener onCancel;
   protected final View.OnClickListener DEFAULT_ON_CANCEL = (View view) -> {
      requireActivity().getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .remove(this)
            .commit();

      if (onCancel != null) {
         onCancel.onClick(getView());
         // this is to make sure if AlertDialog instance will be reused.
         onCancel = null;
      }
   };

   public AlertDialog() {
      super(R.layout.dialog_alert);
   }

   public AlertDialog(int dialogLayout) {
      super(dialogLayout);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      // ui components
      TextView alertDialogTitle = view.findViewById(R.id.alertDialogTitle);
      TextView alertDialogDescription = view.findViewById(R.id.alertDialogDescription);

      alertDialogTitle.setText(dialogTitle);
      alertDialogDescription.setText(dialogDescription);

      ImageButton cancelBtn = view.findViewById(R.id.alertDismissBtn);
      cancelBtn.setOnClickListener(DEFAULT_ON_CANCEL);

      view.setClickable(true);
   }

   public void setDialogTitle(String dialogTitle) {
      this.dialogTitle = dialogTitle;
   }

   public void setDialogDescription(String dialogDescription) {
      this.dialogDescription = dialogDescription;
   }

   public String getDialogTitle() {
      return dialogTitle;
   }

   public String getDialogDescription() {
      return dialogDescription;
   }

   public void setOnCancel(View.OnClickListener onCancel) {
      this.onCancel = onCancel;
   }
}

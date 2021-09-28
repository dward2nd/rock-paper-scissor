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
   private final View.OnClickListener DEFAULT_ON_CANCEL = (View view) -> {
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

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      // ui components
      TextView confirmDialogTitle = view.findViewById(R.id.alertDialogTitle);
      TextView confirmDialogDescription = view.findViewById(R.id.alertDialogDescription);

      confirmDialogTitle.setText(dialogTitle);
      confirmDialogDescription.setText(dialogDescription);

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

   public void setOnCancel(View.OnClickListener onCancel) {
      this.onCancel = onCancel;
   }
}

package com.rockpaperscissor.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.rockpaperscissor.R;

public class ConfirmDialog extends AlertDialog {
   // event handler
   private View.OnClickListener onConfirm;
   protected final View.OnClickListener DEFAULT_ON_CONFIRM = (View view) -> {
      requireActivity().getSupportFragmentManager().beginTransaction()
            .setReorderingAllowed(true)
            .remove(this)
            .commit();

      if (onConfirm != null) {
         onConfirm.onClick(getView());

         // this is to make sure if ConfirmDialog instance will always be reused.
         onConfirm = null;
      }
   };

   public ConfirmDialog() {
      super(R.layout.dialog_confirm);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      // ui components
      TextView confirmDialogTitle = view.findViewById(R.id.confirmDialogTitle);
      TextView confirmDialogDescription = view.findViewById(R.id.confirmDialogDescription);

      confirmDialogTitle.setText(getDialogTitle());
      confirmDialogDescription.setText(getDialogDescription());

      ImageButton confirmBtn = view.findViewById(R.id.confirmBtn);
      confirmBtn.setOnClickListener(DEFAULT_ON_CONFIRM);

      ImageButton cancelBtn = view.findViewById(R.id.cancelBtn);
      cancelBtn.setOnClickListener(DEFAULT_ON_CANCEL);

      view.setClickable(true);
   }

   public void setOnConfirm(View.OnClickListener onConfirm) {
      this.onConfirm = onConfirm;
   }
}

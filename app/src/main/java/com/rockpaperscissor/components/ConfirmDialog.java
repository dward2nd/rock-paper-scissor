package com.rockpaperscissor.components;

import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class ConfirmDialog extends Fragment {
   private TextView confirmDialogTitle;
   private TextView confirmDialogDescription;
   private View.OnClickListener onConfirm;
   private View.OnClickListener onCancel;

   public ConfirmDialog(this.) {
      super(R.layout.confirm_dialog);

   }
}

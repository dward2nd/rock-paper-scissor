package com.rockpaperscissor.dialogs;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.rockpaperscissor.R;

public class SettingDialog extends Fragment {
   private ImageButton bgmCheckBox;
   private ImageButton backBtn;
   private boolean bgmChecked = false;

   public SettingDialog() {
      super(R.layout.dialog_setting);
   }

   @Override
   public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
      bgmCheckBox = view.findViewById(R.id.settingBgmCheckBox);
      backBtn = view.findViewById(R.id.settingBackBtn);

      bgmCheckBox.setOnClickListener((View listenerView) -> {
         bgmChecked = !bgmChecked;
         if (bgmChecked)
            bgmCheckBox.setImageResource(R.drawable.check);
         else
            bgmCheckBox.setImageResource(R.drawable.checkbox);
      });
      backBtn.setOnClickListener((View listenerView) -> requireActivity().getSupportFragmentManager()
            .beginTransaction().remove(this).commit());
   }
}

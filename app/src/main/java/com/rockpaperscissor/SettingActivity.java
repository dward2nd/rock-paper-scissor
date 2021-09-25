package com.rockpaperscissor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
   private ImageButton bgmCheckBox;
   private ImageButton backBtn;

   private boolean bgmChecked = false;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.setting);
      getSupportActionBar().hide();

      bgmCheckBox = findViewById(R.id.oldSettingBgmCheckBox);
      backBtn = findViewById(R.id.oldSettingBackBtn);

      bgmCheckBox.setOnClickListener((View view) -> {
         bgmChecked = !bgmChecked;
         if (bgmChecked)
            bgmCheckBox.setImageResource(R.drawable.check);
         else
            bgmCheckBox.setImageResource(R.drawable.checkbox);
      });
      backBtn.setOnClickListener((View view) -> {
         onBackPressed();
      });
   }

   @Override
   public void onBackPressed() {
      finishActivity(0);
   }

   public void onBackBtnPressed(View view) {
      onBackPressed();
   }
}

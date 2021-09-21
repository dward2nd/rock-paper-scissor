package com.rockpaperscissor;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class SettingActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.setting);
      getSupportActionBar().hide();
   }

   public void onBackBtnPressed(View view) {
      finish();
   }
}

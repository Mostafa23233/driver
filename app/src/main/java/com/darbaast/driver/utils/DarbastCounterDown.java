package com.darbaast.driver.utils;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;

public class DarbastCounterDown {
    CountDownTimer countDownTimer;
public void startCounter(ProgressBar mProgressBar, TextView textView, AppCompatButton appCompatButton , TextView txtSecond){

    appCompatButton.setEnabled(false);
    appCompatButton.setAlpha(0.6f);
    final int[] i = {120};
    final int b = 0;
    countDownTimer = new CountDownTimer(120000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            i[0]--;
            appCompatButton.setText("ارسال مجدد");
            appCompatButton.setAlpha(0.6f);
            appCompatButton.setEnabled(false);
            mProgressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            textView.setText(i[0]+"");
            txtSecond.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(i[0]);
        }

        @Override
        public void onFinish() {
            mProgressBar.setProgress(100);
            appCompatButton.setAlpha(1);
            appCompatButton.setEnabled(true);
            mProgressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            txtSecond.setVisibility(View.GONE);
        }
    };
    countDownTimer.start();


}
public void stop(){
    countDownTimer.cancel();

}

}

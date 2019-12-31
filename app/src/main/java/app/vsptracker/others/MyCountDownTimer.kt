package app.vsptracker.others

import android.os.CountDownTimer
import android.util.Log

class MyCountDownTimer(millisInFuture: Long, countDownInterval: Long) : CountDownTimer(millisInFuture, countDownInterval) {
    
    override fun onFinish() {
        Log.e("MyCountDownTimer", "onFinish")
    }
    
    override fun onTick(p0: Long) {
//        Log.e("MyCountDownTimer", "onTick")
    }
    
    
}
package dev.main.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andrognito.pinlockview.IndicatorDots
import com.andrognito.pinlockview.PinLockListener
import com.andrognito.pinlockview.PinLockView


class PinCode : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_view)
        val mPinLockView  = findViewById<PinLockView>(R.id.pin_lock_view);
        mPinLockView.setPinLockListener(mPinLockListener);
        val mIndicatorDots = findViewById<IndicatorDots>(R.id.indicator_dots);
        mPinLockView.attachIndicatorDots(mIndicatorDots);
    }
    private val mPinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {

        }

        override fun onEmpty() {

        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

        }
    }
}

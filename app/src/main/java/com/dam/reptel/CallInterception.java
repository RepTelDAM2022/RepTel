package com.dam.reptel;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;
import android.telecom.Call;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CallInterception extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        TelephonyManager tmgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        MyPhoneStateListener phoneListener = new MyPhoneStateListener();
        tmgr.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);

    }
    private class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    // CALL_STATE_IDLE;
                    Log.i(TAG, "onCallStateChanged: IDLE " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    // CALL_STATE_OFFHOOK;
                    Log.i(TAG, "onCallStateChanged: OFF HOOK " + incomingNumber);
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    // CALL_STATE_RINGING
                    Log.i(TAG, "onCallStateChanged: RINGING " + incomingNumber);

                    break;
                default:
                    break;
            }
        }
    }

    private static final String TAG = "CallInterception : ";
}


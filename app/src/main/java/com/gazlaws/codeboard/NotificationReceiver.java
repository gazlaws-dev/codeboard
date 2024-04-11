package com.gazlaws.codeboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class NotificationReceiver extends BroadcastReceiver {
    private CodeBoardIME mIME;
    static public final String ACTION_SHOW = "com.gazlaws.codeboard.SHOW";

    NotificationReceiver(CodeBoardIME ime) {
        super();
        mIME = ime;
        Log.i(getClass().getSimpleName(), "NotificationReceiver created, ime=" + mIME);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(getClass().getSimpleName(), "NotificationReceiver.onReceive called, action=" + action);

        if (action != null){
        if (action.equals(ACTION_SHOW)) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    Log.i(getClass().getSimpleName(), "Version >= P" + Build.VERSION.SDK_INT);
                    mIME.requestShowSelf(InputMethodManager.SHOW_FORCED);
                } else {
                    Log.i(getClass().getSimpleName(), "Version < P" + Build.VERSION.SDK_INT);
                    imm.showSoftInputFromInputMethod(mIME.mToken, InputMethodManager.SHOW_FORCED);
                }
            }

            //Seems to be disabled for android 12
//            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            context.sendBroadcast(it);
        }

    }}
}
package com.gazlaws.codeboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class NotificationReceiver extends BroadcastReceiver {
    private CodeBoardIME mIME;
    static public final String ACTION_SHOW = "com.gazlaws.codeboard.SHOW";
    static public final String ACTION_SETTINGS = "com.gazlaws.codeboard.SETTINGS";

    NotificationReceiver(CodeBoardIME ime) {
        super();
        mIME = ime;
        Log.i(getClass().getSimpleName(), "NotificationReceiver created, ime=" + mIME);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(getClass().getSimpleName(), "NotificationReceiver.onReceive called, action=" + action);

        if (action.equals(ACTION_SHOW)) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.showSoftInputFromInputMethod(mIME.mToken, InputMethodManager.SHOW_FORCED);
            }
        } else if (action.equals(ACTION_SETTINGS)) {
            //Seems to be disabled for android 12
//            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
//            context.sendBroadcast(it);
            context.startActivity(new Intent(mIME, MainActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra("notification", 1));

        }
    }
}
package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.inputmethodservice.KeyboardView;
import android.view.MotionEvent;
import android.view.View;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.Timer;
import java.util.TimerTask;

public class KeyboardButtonView extends View {

    private static final String TAG = "KeyboardButtonView";

    private final Key key;
    private final KeyboardView.OnKeyboardActionListener inputService;
    private Timer timer;

    public KeyboardButtonView(Context context, Key key, KeyboardView.OnKeyboardActionListener inputService) {
        super(context);
        this.inputService = inputService;
        this.key = key;
    }

    @Override
    public boolean onTouchEvent(MotionEvent e)
    {
        int action = e.getAction();
        switch(action){
            case MotionEvent.ACTION_DOWN:
                onPress();
                break;
            case MotionEvent.ACTION_UP:
                onRelease();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void layout(int l, int t, int r, int b) {
        Box box = key.box;
        int w = r-l;
        int h = b-t;
        int left = (int)(l + w * box.getLeft());
        int right = (int)(l + w * box.getRight());
        int top = (int)(t + h * box.getTop());
        int bottom = (int)(t + h * box.getBottom());
        super.layout(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas){
        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        canvas.drawRoundRect(.0f, .0f, this.getWidth(), this.getHeight(), 16.0f, 16.0f, paint);

        Paint paint2 = new Paint();
        paint2.setColor(0xff000000);
        float fontHeight = 48.0f;
        paint2.setTextSize(fontHeight);
        paint2.setTextAlign(Paint.Align.CENTER);
        paint2.setAntiAlias(true);
        paint2.setTypeface(Typeface.DEFAULT);
        canvas.drawText(this.key.info.label,this.getWidth()/2,this.getHeight()/2 + fontHeight/3,paint2);
        super.draw(canvas);
    }

    private void onPress() {
        if (key.info.code != 0){
            inputService.onPress(key.info.code);
        }
        if (this.key.info.outputText != null){
            inputService.onText(key.info.outputText);
        }
        if (key.info.isRepeatable){
            startRepeating();
        }
        submitKeyEvent();
        animateClick();
    }

    private void onRelease() {
        if (key.info.code != 0){
            inputService.onRelease(key.info.code);
        }
        if (key.info.isRepeatable){
            stopRepeating();
        }
    }

    private void submitKeyEvent(){
        if (key.info.code != 0){
            inputService.onKey(key.info.code, null);
        }
        if (this.key.info.outputText != null){
            inputService.onText(key.info.outputText);
        }
    }

    private void stopRepeating() {
        if (timer == null){
            return;
        }
        timer.cancel();
        timer = null;
    }

    private void startRepeating() {
        if (timer != null){
            return;
        }
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                submitKeyEvent();
            }
        },400, 50);
    }

    private void animateClick(){
        this.setAlpha(.1f);
        this.animate().alpha(1.0f).setDuration(300);
    }
}

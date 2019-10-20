package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

public class KeyboardButtonView extends View {

    private final Key key;

    public KeyboardButtonView(Context context, Key key) {
        super(context);
        this.key = key;
        this.setTextAlignment(TEXT_ALIGNMENT_CENTER);
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
}

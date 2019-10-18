package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.widget.Button;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

public class KeyboardButtonView extends android.support.v7.widget.AppCompatButton {

    private final Key key;

    public KeyboardButtonView(Context context, Key key) {
        super(context);
        this.key = key;
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
}

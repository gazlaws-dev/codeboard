package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.gazlaws.codeboard.theme.UiTheme;

import static android.content.ContentValues.TAG;

public class KeyboardLayoutView extends ViewGroup {

    int availableWidth = 0;
    int availableHeight = 0;
    boolean measured = false;
    private final UiTheme uiTheme;

    public KeyboardLayoutView(Context context, UiTheme uiTheme) {
        super(context);
        setBackgroundColor(uiTheme.backgroundColor);
        this.uiTheme = uiTheme;
        Log.d(TAG, "KeyboardLayoutView: CREATED!!!");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        // when measure is called for first time store the width and height
        if (!measured){
            availableWidth = widthSize;
            availableHeight = heightSize;
            measured = true;
        }
        float size = availableHeight > availableWidth ? uiTheme.portraitSize : uiTheme.landscapeSize;

        Log.d(TAG, "onMeasure" + widthSize + " " + heightSize);
        setMeasuredDimension(availableWidth, (int)(availableHeight * size));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.d(TAG, "onLayout: " + l + " " + t + " " + r + " " + b);
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.layout(l,t,r,b);
        }
    }
}

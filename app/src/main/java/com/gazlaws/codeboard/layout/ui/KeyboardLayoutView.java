package com.gazlaws.codeboard.layout.ui;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

import com.gazlaws.codeboard.theme.UiTheme;

public class KeyboardLayoutView extends ViewGroup {

    private final UiTheme uiTheme;
    // Height of the system IME bar (arrow-to-hide + language globe) at the
    // bottom of the screen. Reserving it keeps the bottom row of keys visible.
    private int bottomInset = 0;

    public KeyboardLayoutView(Context context, UiTheme uiTheme) {
        super(context);
        setBackgroundColor(uiTheme.backgroundColor);
        this.uiTheme = uiTheme;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();

        int availableHeight = metrics.heightPixels;
        int availableWidth = metrics.widthPixels;

        float keyboardSize = availableHeight > availableWidth
                ? uiTheme.portraitSize
                : uiTheme.landscapeSize;

        setMeasuredDimension(availableWidth, (int)(availableHeight * keyboardSize));
    }

    @Override
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        updateBottomInset(insets.getSystemWindowInsetBottom());
        return insets;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getRootWindowInsets() != null) {
            updateBottomInset(getRootWindowInsets().getSystemWindowInsetBottom());
        }
    }

    private void updateBottomInset(int inset) {
        if (inset != bottomInset) {
            bottomInset = inset;
            requestLayout();
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // Reserve the system IME bar height at the bottom so the bottom row of
        // keys is not hidden underneath the arrow-to-hide / language globe bar.
        int contentBottom = b - bottomInset;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            child.layout(l, t, r, contentBottom);
        }
    }

    public void applyShiftModifier(boolean shiftPressed){
        for (KeyboardButtonView button : getKeyboardButtons()){
             button.applyShiftModifier(shiftPressed);
        }
    }

    public void applyCtrlModifier(boolean ctrlPressed){
        for (KeyboardButtonView button : getKeyboardButtons()){
            button.applyCtrlModifier(ctrlPressed);
        }
    }

    private Collection<KeyboardButtonView> getKeyboardButtons(){
        int childCount = this.getChildCount();
        ArrayList<KeyboardButtonView> list = new ArrayList<>(childCount);
        for (int i=0; i<childCount; i++){
            View child = getChildAt(i);
            if (child instanceof KeyboardButtonView){
                list.add((KeyboardButtonView)child);
            }
        }
        return list;
    }
}

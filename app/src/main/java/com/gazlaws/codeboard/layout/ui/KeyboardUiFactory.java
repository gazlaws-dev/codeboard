package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.widget.RelativeLayout;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.Collection;

public class KeyboardUiFactory {

    private final KeyboardView.OnKeyboardActionListener inputService;

    public KeyboardUiFactory(KeyboardView.OnKeyboardActionListener inputService) {
        this.inputService = inputService;
    }

    public View getView(Context context, Collection<Key> keys){
        KeyboardLayoutView layout = new KeyboardLayoutView(context);
        for (Key key :keys){
            RelativeLayout.LayoutParams params = getKeyLayoutParams(key);
            View view = createKeyView(context,key);
            layout.addView(view,params);
        }
        return layout;
    }

    private RelativeLayout.LayoutParams getKeyLayoutParams(Key key) {
        int width = (int) key.box.width;
        int height = (int) key.box.height;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = (int)key.box.x;
        params.topMargin = (int)key.box.y;
        return params;
    }

    private View createKeyView(Context context, Key key) {

        KeyboardButtonView view =  new KeyboardButtonView(context, key, inputService);
        Box box = key.box;
        view.layout((int)box.getLeft(), (int)box.getTop(), (int)box.getRight(), (int)box.getBottom());
        return view;
    }
}

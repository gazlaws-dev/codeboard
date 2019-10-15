package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;

import java.util.Collection;

public class KeyboardUiFactory {

    public View getView(Context context, Collection<Key> keys){
        RelativeLayout layout = new RelativeLayout(context);
        for (Key key :keys){
            RelativeLayout.LayoutParams params = getKeyLayoutParams(key);
            View view = getKeyView(context,key);
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

    private View getKeyView(Context context, Key key) {

        Button view =  new Button(context);
        view.setText(key.str);
        Box box = key.box;

        view.layout((int)box.getLeft(), (int)box.getTop(), (int)box.getRight(), (int)box.getBottom());
        return view;
    }
}

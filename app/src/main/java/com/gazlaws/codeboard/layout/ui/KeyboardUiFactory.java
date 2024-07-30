package com.gazlaws.codeboard.layout.ui;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.gazlaws.codeboard.layout.Box;
import com.gazlaws.codeboard.layout.Key;
import com.gazlaws.codeboard.theme.ThemeDefinitions;
import com.gazlaws.codeboard.theme.ThemeInfo;
import com.gazlaws.codeboard.theme.UiTheme;
import com.gazlaws.codeboard.KeyboardPreferences;

import java.util.Collection;

public class KeyboardUiFactory {

    private final KeyboardView.OnKeyboardActionListener inputService;
    private final KeyboardPreferences keyboardPreferences;
    public ThemeInfo theme = ThemeDefinitions.Default();

    public KeyboardUiFactory(KeyboardView.OnKeyboardActionListener inputService, KeyboardPreferences keyboardPreferences) { // Modify constructor
        this.inputService = inputService;
        this.keyboardPreferences = keyboardPreferences; 
    }

    public KeyboardLayoutView createKeyboardView(Context context, Collection<Key> keys){
        UiTheme uiTheme = UiTheme.buildFromInfo(this.theme, this.keyboardPreferences); // Pass keyboardPreferences
        KeyboardLayoutView layout = createKeyGroupView(context, uiTheme);
        for (Key key : keys){
            RelativeLayout.LayoutParams params = getKeyLayoutParams(key);
            View view = createKeyView(context, key, uiTheme);
            layout.addView(view, params);
        }
        return layout;
    }

    private KeyboardLayoutView createKeyGroupView(Context context, UiTheme uiTheme){
        return new KeyboardLayoutView(context, uiTheme);
    }

    private KeyboardButtonView createKeyView(Context context, Key key, UiTheme uiTheme) {
        KeyboardButtonView view = new KeyboardButtonView(context, key, inputService, uiTheme, keyboardPreferences);
        Box box = key.box;
        view.layout((int) box.getLeft(), (int) box.getTop(), (int) box.getRight(), (int) box.getBottom());
        return view;
    }

    private RelativeLayout.LayoutParams getKeyLayoutParams(Key key) {
        int width = (int) key.box.width;
        int height = (int) key.box.height;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
        params.leftMargin = (int) key.box.x;
        params.topMargin = (int) key.box.y;
        return params;
    }
}

package com.gazlaws.codeboard;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.gazlaws.codeboard.theme.IOnFocusListenable;

/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);
        Bundle extras = getIntent().getExtras();
        SettingsFragment frag = new SettingsFragment();
        frag.setArguments(extras);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings_container, frag)
                .commit();
        //debug only
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.settings_container);
        if(currentFragment instanceof IOnFocusListenable) {
            ((IOnFocusListenable) currentFragment).onWindowFocusChanged(hasFocus);
        }
    }
}







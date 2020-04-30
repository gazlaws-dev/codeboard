package com.gazlaws.codeboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntroFragment;
import com.github.paolorotolo.appintro.model.SliderPage;

/**
 * Created by Ruby on 05/12/2016.
 */
public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        // Instead of fragments, you can also use our default slide.
        // Just create a `SliderPage` and provide title, description, background and image.
        // AppIntro will do the rest.


        addSlide(CodeboardIntro.newInstance(R.layout.codeboard_intro1));
        addSlide(CodeboardIntro.newInstance(R.layout.codeboard_intro2));

        SliderPage sliderPage = new SliderPage();
        sliderPage.setTitle("All the shortcuts!");
        sliderPage.setDescription("Click 'ctrl' for select all, cut, copy, paste, or undo." +
                "\nCtrl+Shift+Z for redo" + "\n Long press Space to change keyboard \n More symbols");
        sliderPage.setImageDrawable(R.drawable.intro3);
        sliderPage.setBgColor(Color.parseColor("#3F51B5"));
        addSlide(AppIntroFragment.newInstance(sliderPage));


        // Hide Skip/Done button.
       showSkipButton(false);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        // Do something when users tap on Skip button.
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        finish();
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
    public void enableButtonIntro(View v){

        Intent intent=new Intent(android.provider.Settings.ACTION_INPUT_METHOD_SETTINGS);
        startActivity(intent);
            }

    public void changeButtonIntro(View v){

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();}
}


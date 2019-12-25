package com.gazlaws.codeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;




/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends AppCompatActivity {
    RadioGroup radioGroupColour,radioGroupLayout;
    SeekBar seekBar;
    KeyboardPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = new KeyboardPreferences(this);

        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

                boolean isFirstStart = preferences.isFirstStart();

                if (!isFirstStart) {
                    return;
                }

                //  If the activity has never started before...
                Button change = (Button) findViewById(R.id.change_button);
                change.setVisibility(View.GONE);

                //  Launch app intro
                Intent i = new Intent(MainActivity.this, IntroActivity.class);
                startActivity(i);

                preferences.setFirstStart(false);

            }
        });

        // Start the thread
        t.start();

        //debug only
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);




        seekBar = (SeekBar) findViewById(R.id.size_seekbar);
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = seekBar.getProgress();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.setPortraitSize(progressChangedValue);
            }
        });

        SeekBar landscapeSeekBar = (SeekBar) findViewById(R.id.size_landscape_seekbar);
        // perform seek bar change listener event used for getting the progress value
        landscapeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = seekBar.getProgress();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.setLandscapeSize(progressChangedValue);
            }
        });

        radioGroupColour = (RadioGroup) findViewById(R.id.radiogroupcolour);
        radioGroupColour.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerColour);

        radioGroupLayout = (RadioGroup) findViewById(R.id.radiogrouplayout);
        radioGroupLayout.setOnCheckedChangeListener(radioGroupOnCheckedChangeListenerLayout);


        LoadPreferences();

    }



    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerColour =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButtonColour = (RadioButton) radioGroupColour.findViewById(checkedId);
                    int checkedIndexColour = radioGroupColour.indexOfChild(checkedRadioButtonColour);
                    preferences.setThemeIndex(checkedIndexColour);
                }
            };

    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListenerLayout =
            new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    RadioButton checkedRadioButtonLayout = (RadioButton) radioGroupLayout.findViewById(checkedId);
                    int checkedIndexLayout = radioGroupLayout.indexOfChild(checkedRadioButtonLayout);
                    preferences.setLayoutIndex(checkedIndexLayout);
                }
            };


    public void changeButton(View v) {

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showInputMethodPicker();

//        Button enable = (Button) findViewById(R.id.enable_button);
//        enable.setText("Change Keyboard");
//
//        String id = KeyboardPreferences.Secure.getString(
//                getContentResolver(),
//                KeyboardPreferences.Secure.DEFAULT_INPUT_METHOD
//        );
//
//        if(!(id.equals("com.gazlaws.codeboard/.CodeBoardIME"))){
//            InputMethodManager imm = (InputMethodManager)
//                    getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showInputMethodPicker();
//        }

    }

    public void previewToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_preview);
        preferences.setPreviewEnabled(box.isChecked());
    }

    public void soundToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_sound);
        preferences.setSoundEnabled(box.isChecked());
        closeKeyboard(v);
    }

    public void vibratorToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_vibrator);
        preferences.setVibrateEnabled(box.isChecked());
        closeKeyboard(v);
    }

    public void arrowToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_no_arrow);
        preferences.setArrowRowEnabled(box.isChecked());
        closeKeyboard(v);
    }


    public void closeKeyboard(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    private void LoadPreferences() {

        int savedRadioColour = preferences.getThemeIndex();
        RadioButton savedCheckedRadioButtonColour = (RadioButton) radioGroupColour.getChildAt(savedRadioColour);
        savedCheckedRadioButtonColour.setChecked(true);

        int savedRadioLayout = preferences.getLayoutIndex();
        RadioButton savedCheckedRadioButtonLayout = (RadioButton) radioGroupLayout.getChildAt(savedRadioLayout);
        savedCheckedRadioButtonLayout.setChecked(true);

        CheckBox preview = (CheckBox) findViewById(R.id.check_preview);
        preview.setChecked(preferences.isPreviewEnabled());

        CheckBox sound   = (CheckBox) findViewById(R.id.check_sound);
        sound.setChecked(preferences.isSoundEnabled());

        CheckBox vibrate = (CheckBox) findViewById(R.id.check_vibrator);
        vibrate.setChecked(preferences.isVibrateEnabled());

        CheckBox noarrow = (CheckBox) findViewById(R.id.check_no_arrow);
        noarrow.setChecked(preferences.isArrowRowEnabled());

        SeekBar size = (SeekBar) findViewById(R.id.size_seekbar);
        size.setProgress(preferences.getPortraitSize());

        SeekBar sizeLandscape = (SeekBar) findViewById(R.id.size_landscape_seekbar);
        sizeLandscape.setProgress(preferences.getLandscapeSize());
    }

    public void openPlay(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=com.gazlaws.codeboard"));
        startActivity(i);
    }

    public void openTutorial(View v){
        Intent i = new Intent(MainActivity.this, IntroActivity.class);
        startActivity(i);
    }

    public void openGithub(View v) {
        Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse("https://github.com/gazlaws-dev/codeboard"));

        startActivity(i);
    }


}







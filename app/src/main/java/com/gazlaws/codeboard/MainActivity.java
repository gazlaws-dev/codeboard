package com.gazlaws.codeboard;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends AppCompatActivity {
    RadioGroup radioGroupColour,radioGroupLayout;
    SeekBar sizeSeekBar;
    TextView sizeText;

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




        sizeSeekBar = (SeekBar) findViewById(R.id.size_seekbar);
        sizeText = (TextView) findViewById(R.id.size_text);

        // perform seek bar change listener event used for getting the progress value
        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = sizeSeekBar.getProgress();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
                sizeText.setText(String.valueOf(progressChangedValue+30));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                preferences.setPortraitSize(progressChangedValue);
                closeKeyboard(seekBar);
            }
        });

//        SeekBar landscapeSeekBar = (SeekBar) findViewById(R.id.size_landscape_seekbar);
//        // perform seek bar change listener event used for getting the progress value
//        landscapeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progressChangedValue = seekBar.getProgress();
//
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progressChangedValue = progress;
//            }
//
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                preferences.setLandscapeSize(progressChangedValue);
//            }
//        });

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

    }

    public void previewToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_preview);
        preferences.setPreviewEnabled(box.isChecked());
        closeKeyboard(v);
    }
    public void keyBorderToggle(View v) {
        CheckBox box = (CheckBox) findViewById(R.id.check_border);
        preferences.setKeyBorderEnabled(box.isChecked());
        closeKeyboard(v);
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

    public void saveCustomSymbols(View v){
        EditText customSymbolView = findViewById(R.id.input_symbols_main);
        String newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsMain(newValue);

        customSymbolView = findViewById(R.id.input_symbols_main_2);
        newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsMain2(newValue);

        customSymbolView = findViewById(R.id.input_symbols_sym);
        newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsSym(newValue);

        customSymbolView = findViewById(R.id.input_symbols_sym_2);
        newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsSym2(newValue);

        customSymbolView = findViewById(R.id.input_symbols_main_bottom);
        newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsMainBottom(newValue);

        customSymbolView = findViewById(R.id.input_symbols_sym_bottom);
        newValue = String.valueOf(customSymbolView.getText());
        preferences.setCustomSymbolsSymBottom(newValue);

        closeKeyboard(v);
    }
    public void classicSymbols(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset?")
                .setMessage("This will reset all your custom symbols to the old CodeBoard layout")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        EditText customSymbolView = findViewById(R.id.input_symbols_main);
                        String newValue ="()1234567890#";
                        customSymbolView.setText(newValue);
                        preferences.setCustomSymbolsMain(newValue);


                        customSymbolView = findViewById(R.id.input_symbols_main_2);
                        newValue = "+-=:*/{}+$[]";
                        customSymbolView.setText(newValue);
                        preferences.setCustomSymbolsMain2(newValue);

                        customSymbolView = findViewById(R.id.input_symbols_main_bottom);
                        newValue = "&|%\\<>;',.";
                        customSymbolView.setText(newValue);
                        preferences.setCustomSymbolsMainBottom(newValue);

//                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
        saveCustomSymbols(v);
    }

    public void resetSymbols(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Reset?")
                .setMessage("This will reset all your custom symbols to the default")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        preferences.clearSymbols();
                        LoadPreferences();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .show();
        saveCustomSymbols(v);
    }

    public void closeKeyboard(View v) {

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

    }

    private void LoadPreferences() {

        EditText customSymbolViewMain = findViewById(R.id.input_symbols_main);
        String customSymbols = preferences.getCustomSymbolsMain();
        customSymbolViewMain.setText(customSymbols);

        EditText customSymbolViewMain2 = findViewById(R.id.input_symbols_main_2);
        customSymbols = preferences.getCustomSymbolsMain2();
        customSymbolViewMain2.setText(customSymbols);

        EditText customSymbolViewSym = findViewById(R.id.input_symbols_sym);
        customSymbols = preferences.getCustomSymbolsSym();
        customSymbolViewSym.setText(customSymbols);

        EditText customSymbolViewSym2 = findViewById(R.id.input_symbols_sym_2);
        customSymbols = preferences.getCustomSymbolsSym2();
        customSymbolViewSym2.setText(customSymbols);

        EditText customSymbolViewMainBottom = findViewById(R.id.input_symbols_main_bottom);
        customSymbols = preferences.getCustomSymbolsMainBottom();
        customSymbolViewMainBottom.setText(customSymbols);

        EditText customSymbolViewSymBottom = findViewById(R.id.input_symbols_sym_bottom);
        customSymbols = preferences.getCustomSymbolsSymBottom();
        customSymbolViewSymBottom.setText(customSymbols);

        int savedRadioColour = preferences.getThemeIndex();
        RadioButton savedCheckedRadioButtonColour = (RadioButton) radioGroupColour.getChildAt(savedRadioColour);
        savedCheckedRadioButtonColour.setChecked(true);

        int savedRadioLayout = preferences.getLayoutIndex();
        RadioButton savedCheckedRadioButtonLayout = (RadioButton) radioGroupLayout.getChildAt(savedRadioLayout);
        savedCheckedRadioButtonLayout.setChecked(true);

        CheckBox preview = (CheckBox) findViewById(R.id.check_preview);
        preview.setChecked(preferences.isPreviewEnabled());

        CheckBox border = (CheckBox) findViewById(R.id.check_border);
        border.setChecked(preferences.isBorderEnabled());

        CheckBox sound   = (CheckBox) findViewById(R.id.check_sound);
        sound.setChecked(preferences.isSoundEnabled());

        CheckBox vibrate = (CheckBox) findViewById(R.id.check_vibrator);
        vibrate.setChecked(preferences.isVibrateEnabled());

        CheckBox noarrow = (CheckBox) findViewById(R.id.check_no_arrow);
        noarrow.setChecked(preferences.isArrowRowEnabled());

        SeekBar size = (SeekBar) findViewById(R.id.size_seekbar);
        size.setProgress(preferences.getPortraitSize());

        TextView size_text = findViewById(R.id.size_text);
        size_text.setText(String.valueOf(preferences.getPortraitSize()+30));


//        SeekBar sizeLandscape = (SeekBar) findViewById(R.id.size_landscape_seekbar);
//        sizeLandscape.setProgress(preferences.getLandscapeSize());
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







package com.gazlaws.codeboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends ActionBarActivity {
    RadioGroup radioGroup;
    TextView textCheckedID, textCheckedIndex;
    SeekBar seekBar;


    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //  Declare a new thread to do a preference check
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //  Initialize SharedPreferences
                SharedPreferences getPrefs = PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext());

                //  Create a new boolean and preference and set it to true
                boolean isFirstStart = getPrefs.getBoolean("firstStart", true);

                //  If the activity has never started before...
                if (isFirstStart) {

                    //  Launch app intro
//                    Intent i = new Intent(MainActivity.this, DefaultIntro.class);
//                    startActivity(i);

                    TextView tutorial_text = (TextView) findViewById(R.id.hint_text);
                    tutorial_text.setText("Enable CodeBoard in Settings > Language and Keyboard");

                    //  Make a new preferences editor
                    SharedPreferences.Editor e = getPrefs.edit();

                    //  Edit preference to make it false because we don't want this to run again
                    e.putBoolean("firstStart", false);

                    //  Apply changes
                    e.apply();
                }
            }
        });

        // Start the thread
        t.start();

        //debug only
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);

        seekBar=(SeekBar)findViewById(R.id.size_seekbar);
        // perform seek bar change listener event used for getting the progress value
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChangedValue = seekBar.getProgress();

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChangedValue = progress;
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
//                Toast.makeText(MainActivity.this, "Seek bar progress is :" + progressChangedValue,
//                        Toast.LENGTH_SHORT).show();
                SavePreferences("SIZE", progressChangedValue);

            }
        });
//        textCheckedID = (TextView)findViewById(R.id.checkedid);
//        textCheckedIndex = (TextView)findViewById(R.id.checkedindex);

        LoadPreferences();

    }

    RadioGroup.OnCheckedChangeListener radioGroupOnCheckedChangeListener =
            new RadioGroup.OnCheckedChangeListener(){

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {

                    RadioButton checkedRadioButton = (RadioButton)radioGroup.findViewById(checkedId);
                    int checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
//                    textCheckedID.setText("checkedID = " + checkedId);
//                    textCheckedIndex.setText("checkedIndex = " + checkedIndex);
                    SavePreferences(KEY_SAVED_RADIO_BUTTON_INDEX, checkedIndex);
                }};






    private void SavePreferences(String key, int value){
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void previewToggle(View v){
        CheckBox preview = (CheckBox) findViewById(R.id.check_preview);
        if (preview.isChecked()){
            SavePreferences("PREVIEW",1);
        } else SavePreferences("PREVIEW",0);


    }

    public void vibratorToggle(View v){
        CheckBox preview = (CheckBox) findViewById(R.id.check_vibrator);
        if (preview.isChecked()){
            SavePreferences("VIBRATE",1);
        } else SavePreferences("VIBRATE",0);


    }

    public void closeKeyboard(View v){


        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);


    }
    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
        RadioButton savedCheckedRadioButton = (RadioButton)radioGroup.getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);

        int setPreview = sharedPreferences.getInt("PREVIEW",1);
        int setVibrator = sharedPreferences.getInt("VIBRATE",1);
        int setSize = sharedPreferences.getInt("SIZE",1);
        CheckBox preview = (CheckBox)findViewById(R.id.check_preview);
        CheckBox vibrate = (CheckBox)findViewById(R.id.check_vibrator);
        SeekBar size = (SeekBar)findViewById(R.id.size_seekbar);

        if (setPreview==1)
        preview.setChecked(true);
        else
         preview.setChecked(false);

        if (setVibrator==1)
            vibrate.setChecked(true);
        else
        vibrate.setChecked(false);

        size.setProgress(setSize);


    }


    }







package com.gazlaws.codeboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends ActionBarActivity {
    RadioGroup radioGroup;
    TextView textCheckedID, textCheckedIndex;

    final String KEY_SAVED_RADIO_BUTTON_INDEX = "SAVED_RADIO_BUTTON_INDEX";
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radioGroup = (RadioGroup)findViewById(R.id.radiogroup);
        radioGroup.setOnCheckedChangeListener(radioGroupOnCheckedChangeListener);

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

    private void LoadPreferences(){
        SharedPreferences sharedPreferences = getSharedPreferences("MY_SHARED_PREF", MODE_PRIVATE);
        int savedRadioIndex = sharedPreferences.getInt(KEY_SAVED_RADIO_BUTTON_INDEX, 0);
        RadioButton savedCheckedRadioButton = (RadioButton)radioGroup.getChildAt(savedRadioIndex);
        savedCheckedRadioButton.setChecked(true);

        int setPreview = sharedPreferences.getInt("PREVIEW",1);
        CheckBox preview = (CheckBox)findViewById(R.id.check_preview);

        if (setPreview==1)
        preview.setChecked(true);
        else
            preview.setChecked(false);
    }

//        public void onRadioPress(View view) {
//            Toast.makeText(MainActivity.this, "To Do:Colours", Toast.LENGTH_SHORT).show();
//            boolean checked = ((RadioButton) view).isChecked();
//            KeyboardView keyColour = (KeyboardView) findViewById(R.id.keyboard);
//
//            switch (view.getId()) {
//                case R.id.black_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.black_000000);
//                                            }
//                    break;
//
//                case R.id.white_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.white_ffffff);
//                    }
//                    break;
//                case R.id.material_dark_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.dark_263238);
//                    }
//                    break;
//                case R.id.material_light_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.light_eceff1);
//                    }
//                    break;
//                case R.id.blue_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.blue_0d47a1);
//                    }
//                    break;
//                case R.id.purple_button:
//                    if (checked) {
//                        keyColour.setBackgroundResource(R.drawable.purple_4a148c);
//                    }
//                    break;
//
//
//            }
//        }


}


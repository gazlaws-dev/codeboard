package com.gazlaws.codeboard;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

/**
 * Created by Ruby on 02/06/2016.
 */
public class MainActivity extends ActionBarActivity {
    int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        }


    public void onRadioPress(View view){
        Toast.makeText(MainActivity.this, "To Do:Colours", Toast.LENGTH_SHORT).show();
//        boolean checked = ((RadioButton) view).isChecked();
//        KeyboardView keyColour = (KeyboardView) findViewById(R.id.keyboard);
//
//        switch (view.getId()){
//            case R.id.black_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.black_000000);
//                }
//                break;
//
//            case R.id.white_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.white_ffffff);
//                }
//                break;
//            case R.id.material_dark_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.dark_263238);
//                }
//                break;
//            case R.id.material_light_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.light_eceff1);
//                }
//                break;
//            case R.id.blue_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.blue_0d47a1);
//                }
//                break;
//            case R.id.purple_button:
//                if(checked){
//                    keyColour.setBackgroundResource(R.drawable.purple_4a148c);
//                }
//                break;
//
//
//        }


    }
}

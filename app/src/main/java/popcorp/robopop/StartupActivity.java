package popcorp.robopop;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import static android.R.attr.id;
import static android.R.attr.layout;
import static android.R.attr.rotation;


/*
This activity will be the first one opened when the app is launched.
 */
public class StartupActivity extends AppCompatActivity {

    private int selectedPower = 0;
    private int selectedSound = R.raw.sound0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // MainActivity Intent
        final Intent mainIntent = new Intent(this, MainActivity.class);

        // ====Layout Creation====
        // Start Button
        ImageButton startButton = (ImageButton) findViewById(R.id.StartButton); // Create Start Button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent.putExtra("Power", selectedPower);
                mainIntent.putExtra("Sound", selectedSound);
                startActivity(mainIntent);
            }
        });

        // Action Bar background
        android.support.v7.app.ActionBar bar = getSupportActionBar();
        Drawable d = getResources().getDrawable(R.drawable.popcorn_top, this.getTheme());
        bar.setBackgroundDrawable(d);



        // Power Button
        ImageButton powerButton = (ImageButton)findViewById(R.id.powerButton);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialogue for power selection
                AlertDialog.Builder powerAlertDialogBuilder = new AlertDialog.Builder(StartupActivity.this, R.style.MyDialogTheme);
                String alertTitle = "Choose Microwave Power";
                powerAlertDialogBuilder.setTitle(alertTitle).setItems(R.array.powerArray, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int selectedOption){
                        selectedPower = selectedOption;
                        Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(selectedOption)).toString());
                    }
                });
                AlertDialog alertDialog = powerAlertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // Spinning Light + Rotation
        Animation rotation = AnimationUtils.loadAnimation(StartupActivity.this, R.anim.rotation);
        rotation.setFillAfter(true);
        ImageView spinningLight = (ImageView) findViewById(R.id.spinningLight);
        spinningLight.startAnimation(rotation);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu configMenu){
        getMenuInflater().inflate(R.menu.config_menu, configMenu);
        return super.onCreateOptionsMenu(configMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this, R.style.MyDialogTheme);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        switch (item.getItemId()) {
            case R.id.Manual:
                alertDialog.setContentView(R.layout.manual_dialog_layout);
                break;
            case R.id.IOT:
                break;
            case R.id.Sound:
                dialogSoundConstructor(alertDialog);
                break;
            case R.id.About:
                alertDialog.setContentView(R.layout.info_dialog_layout);
                break;
        }

        return true;
    }


    public void dialogSoundConstructor(final AlertDialog alertDialog) {
        alertDialog.show();
        alertDialog.setContentView(R.layout.sound_dialog_layout);
        String[] testArray = getResources().getStringArray(R.array.soundArray);

        final RadioGroup rg = (RadioGroup) alertDialog.findViewById(R.id.soundRadioGroup);

        // Setting OK Button
        Button soundOkButton = (Button) alertDialog.findViewById(R.id.OKbutton);

        soundOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sel = rg.getCheckedRadioButtonId();
                switch(sel) {
                    case 0:
                        selectedSound = R.raw.sound0;
                        Log.i("IGOR", "STARTUP - Sound selected == " + (new Integer(selectedSound)).toString());
                        break;

                    case 1:
                        selectedSound = R.raw.sound1;
                        Log.i("IGOR", "STARTUP - Sound selected == " + (new Integer(selectedSound)).toString());
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;

                    default:
                        selectedSound = R.raw.sound0;
                        Log.i("IGOR", "STARTUP - Default selected == " + (new Integer(selectedSound)).toString());
                }
                rg.clearCheck();
                alertDialog.cancel();
            }
        });

        // Setting the Radio Buttens
        for(int i=0;i<5;i++){
            RadioButton rb=new RadioButton(StartupActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(testArray[i]);
            rb.setId(i);
            rg.addView(rb);
        }
    }

    public void dialogPowerConstructor(final AlertDialog alertDialog) {
        alertDialog.show();
        alertDialog.setContentView(R.layout.power_dialog_layout);
        String[] testArray = getResources().getStringArray(R.array.powerArray);

        final RadioGroup rg = (RadioGroup) alertDialog.findViewById(R.id.powerRadioGroup);

        // Setting OK Button
        Button powerOkButton = (Button) alertDialog.findViewById(R.id.OKbutton);

        powerOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int sel = rg.getCheckedRadioButtonId();
                switch(sel) {
                    case 0:
                        selectedPower = R.raw.sound0;
                        Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(selectedPower)).toString());
                        break;

                    case 1:
                        selectedPower = R.raw.sound1;
                        Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(selectedPower)).toString());
                        break;

                    case 2:
                        break;

                    case 3:
                        break;

                    case 4:
                        break;

                    default:
                        selectedSound = R.raw.sound0;
                        Log.i("IGOR", "STARTUP - Default selected == " + (new Integer(selectedPower)).toString());
                }
                rg.clearCheck();
                alertDialog.cancel();
            }
        });

        // Setting the Radio Buttons
        for(int i=0;i<5;i++){
            RadioButton rb=new RadioButton(StartupActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(testArray[i]);
            rb.setId(i);
            rg.addView(rb);
        }
    }
}

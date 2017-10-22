package popcorp.robopop;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;


/*
This activity will be the first one opened when the app is launched.
 */
public class StartupActivity extends AppCompatActivity {

    /**
     * This class represents the Activity that appears on screen when the app is launched.
     * It handles the setting of alert sound (when popcorn is ready), the power setting of the
     * microwave, as well as a short manual and "about" dialogs.
     */
    private int selectedPower = 0;
    private int selectedSound = R.raw.get_to_the_choppa;

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

        // Power Manual Button
        ImageButton powerManualButton = (ImageButton) findViewById(R.id.powerManualButton);
        powerManualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder powerAlertDialogBuilder = new AlertDialog.Builder(StartupActivity.this, R.style.MyDialogTheme);
                AlertDialog alertDialog = powerAlertDialogBuilder.create();
                alertDialog.show();
                alertDialog.setContentView(R.layout.power_manual_dialog_layout);
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
                AlertDialog alertDialog = powerAlertDialogBuilder.create();
                alertDialog.show();
                dialogPowerConstructor(alertDialog);
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
            case R.id.Sound:
                dialogSoundConstructor(alertDialog);
                break;
            case R.id.About:
                alertDialog.setContentView(R.layout.info_dialog_layout);
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    /**
     * Handles the sound selection dialog. Updates the selected sound.
     * @param alertDialog is the dialog in which this is created
     */
    private void dialogSoundConstructor(final AlertDialog alertDialog) {
        alertDialog.show();
        alertDialog.setContentView(R.layout.sound_dialog_layout);
        String[] soundArray = getResources().getStringArray(R.array.soundArray);

        final RadioGroup rg = (RadioGroup) alertDialog.findViewById(R.id.soundRadioGroup);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            MediaPlayer mediaPlayer = new MediaPlayer();

            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                switch(rg.getCheckedRadioButtonId()) {
                    case 0:
                        selectedSound = R.raw.hallelujah;
                        Log.i("IGOR", "STARTUP - Sound selected == " + (new Integer(selectedSound)).toString());
                        break;
                    case 1:
                        selectedSound = R.raw.ding;
                        Log.i("IGOR", "STARTUP - Sound selected == " + (new Integer(selectedSound)).toString());
                        break;
                    //TODO: Or, add more sounds
                    case 2:
                        selectedSound = R.raw.super_mario;
                        break;
                    case 3:
                        selectedSound = R.raw.you_win;
                        break;
                    case 4:
                        selectedSound = R.raw.get_to_the_choppa;
                        mediaPlayer.setVolume(1, 1);
                        break;
                    default:
                        selectedSound = R.raw.get_to_the_choppa;
                        Log.i("IGOR", "STARTUP - Default selected == " + (new Integer(selectedSound)).toString());
                }
                mediaPlayer = MediaPlayer.create(getApplicationContext(), selectedSound);
                mediaPlayer.start();
            }
        });

        // Setting OK Button
        Button soundOkButton = (Button) alertDialog.findViewById(R.id.OKbutton);

        soundOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        // Setting the Radio Buttons
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Market_Deco.ttf");
        Context context = getApplicationContext();
        int textColor = R.color.colorAccent;
        for(int i=0;i<5;i++){
            RadioButton rb=new RadioButton(StartupActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(soundArray[i]);
            rb.setTypeface(font);
            rb.setTextColor(ContextCompat.getColor(context, textColor));
            rb.setTextSize(20);
            rb.setId(i);
            rg.addView(rb);
        }
    }

    /**
     * Handles the power selection dialog. Updates the selectedPower variable.
     * @param alertDialog is the dialog in which this is created
     */
    private void dialogPowerConstructor(final AlertDialog alertDialog) {
        alertDialog.show();
        alertDialog.setContentView(R.layout.power_dialog_layout);
        String[] powerArray = getResources().getStringArray(R.array.powerArray);

        final RadioGroup rg = (RadioGroup) alertDialog.findViewById(R.id.powerRadioGroup);

        // Setting OK Button
        Button powerOkButton = (Button) alertDialog.findViewById(R.id.OKbutton);

        powerOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPower = rg.getCheckedRadioButtonId();
                if (selectedPower == -1){
                    selectedPower = 0;
                } else if (selectedPower == 1){
                    // It's cool, don't worry about it
                    selectedPower = 2;
                }
                Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(selectedPower)).toString());
                rg.clearCheck();
                alertDialog.cancel();
            }
        });

        // Setting the Radio Buttons
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Market_Deco.ttf");
        Context context = getApplicationContext();
        int textColor = R.color.colorAccent;
        for(int i=0;i<2;i++){
            RadioButton rb=new RadioButton(StartupActivity.this); // dynamically creating RadioButton and adding to RadioGroup.
            rb.setText(powerArray[i]);
            rb.setTypeface(font);
            rb.setTextColor(ContextCompat.getColor(context, textColor));
            rb.setTextSize(20);
            rb.setId(i);
            rg.addView(rb);
        }
    }

}

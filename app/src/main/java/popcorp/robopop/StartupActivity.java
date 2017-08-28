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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
                dialogSoundConstructor();
                break;
            case R.id.About:
                alertDialog.setContentView(R.layout.info_dialog_layout);
                break;
        }

        return true;
    }


    public void dialogSoundConstructor(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this);
        String alertTitle = "Sound";
        alertDialogBuilder.setTitle(alertTitle).setItems(R.array.soundArray, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int which){
                selectedSound = which;
                switch (which){ //TODO: find sounds
                    case 0:
                        selectedSound = R.raw.sound0;
                        break;
                    case 1:
                        selectedSound = R.raw.sound1;
                        break;
                    case 2:

                        break;
                    case 3:

                        break;
                    case 4:
                        break;
                }
                Log.i("IGOR", "STARTUP - Sound selected == " + (new Integer(which)).toString());
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

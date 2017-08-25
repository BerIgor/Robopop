package popcorp.robopop;

import android.content.DialogInterface;
import android.content.Intent;
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



        // Power Button
        ImageButton powerButton = (ImageButton)findViewById(R.id.powerButton);
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create dialogue for power selection
                AlertDialog.Builder powerAlertDialogBuilder = new AlertDialog.Builder(StartupActivity.this, R.style.MyDialogTheme);
                String alertTitle = "Choose Microwave Power";
                powerAlertDialogBuilder.setTitle(alertTitle).setItems(R.array.powerArray, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        selectedPower = which;
                        Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(which)).toString());
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


    }
    @Override
    public boolean onCreateOptionsMenu(Menu configMenu){
        getMenuInflater().inflate(R.menu.config_menu, configMenu);
        return super.onCreateOptionsMenu(configMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.Manual:
                String manualText = new String("You have to do this and this..");
                dialogConstructor("Manual",manualText);
                break;
            case R.id.IOT:
                break;
            case R.id.Sound:
                dialogSoundConstructor();
                break;
            case R.id.About:
                String aboutText = new String("By Or Zamir et al.");
                dialogConstructor("About",aboutText);
                break;
        }



        return true;
    }

    public void dialogConstructor(String title, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this, R.style.MyDialogTheme);
        String alertTitle = title;
        String alertMessage = message;
        String alertButtonText = "Okay";
        alertDialogBuilder.setMessage(alertMessage).setTitle(alertTitle);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(alertButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
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

package popcorp.robopop;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;



/*
This activity will be the first one opened when the app is launched.
 */
public class StartupActivity extends AppCompatActivity {

    private int selectedPower = 0;
    private int selectedSound = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // MainActivity Intent
        final Intent mainIntent = new Intent(this, MainActivity.class);

        // ====Layout Creation====
        // Start Button
        Button startButton = (Button)findViewById(R.id.StartButton); // Create Start Button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent.putExtra("Power", selectedPower);
                mainIntent.putExtra("Sound", selectedSound);
                startActivity(mainIntent);
            }
        });

        // Exit Button
        Button exitButton = (Button)findViewById(R.id.ExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish(); System.exit(0);
            }
        });

        // Text
        TextView welcomeText = (TextView)findViewById(R.id.WelcomeText);
        String welcomeTextText = "How powerful is your microwave?";
        welcomeText.setText(welcomeTextText);
        welcomeText.setText(welcomeTextText); //TODO: remove (why twice?)

        // Power Selector
        final Spinner powerSpinner = (Spinner)findViewById(R.id.PowerSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.powerArray, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        powerSpinner.setAdapter(adapter);

        powerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPower = position;
                Log.i("IGOR", "STARTUP - Power selected == " + (new Integer(selectedPower)).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StartupActivity.this);
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
        //String alertMessage = "Choose your sound";
        String alertButtonText = "Okay";
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
       /* alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(alertButtonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id){

            }
        });*/
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

}

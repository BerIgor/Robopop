package popcorp.robopop;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
        welcomeText.setText(welcomeTextText);

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
}

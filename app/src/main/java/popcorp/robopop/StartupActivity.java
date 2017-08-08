package popcorp.robopop;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/*
This activity will be the first one opened when the app is launched.
 */
public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        // MainActivity Intent
        final Intent mainIntent = new Intent(this, MainActivity.class);

        // ====Layout Creation====
        Button startButton = (Button)findViewById(R.id.StartButton); // Create Start Button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mainIntent);
            }
        });

        Button exitButton = (Button)findViewById(R.id.ExitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                finish(); System.exit(0);
            }
        });

        TextView welcomeText = (TextView)findViewById(R.id.WelcomeText);
        String welcomeTextText = "To popcorn, or not to popcorn?";
        welcomeText.setText(welcomeTextText);

    }
}

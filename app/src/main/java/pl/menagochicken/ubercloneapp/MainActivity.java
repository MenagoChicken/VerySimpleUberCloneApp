package pl.menagochicken.ubercloneapp;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //tworzenie zmiennych
    private Switch whoSwitch;
    private TextView passengerTextView;
    private TextView driverTextView;


    public void goToSelectedActivity(View view) {

        Log.i("Switch value", String.valueOf(whoSwitch.isChecked()));
    }

    public void switchTextColor(View view) {
        if (whoSwitch.isChecked()) {
            passengerTextView.setTextColor(Color.BLUE);
            driverTextView.setTextColor(Color.BLACK);
        } else {
            driverTextView.setTextColor(Color.BLUE);
            passengerTextView.setTextColor(Color.BLACK);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // przypisanie zmiennych
        whoSwitch = findViewById(R.id.whoSwitch);
        passengerTextView = findViewById(R.id.passengerTextView);
        driverTextView = findViewById(R.id.driverTextView);

        driverTextView.setTextColor(Color.BLUE);

    }
}

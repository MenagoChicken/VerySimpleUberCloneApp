package pl.menagochicken.ubercloneapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //tworzenie zmiennych
    private Switch whoSwitch;
    private TextView passengerTextView;
    private TextView driverTextView;
    private String userType;

    // przejście do innego ekranu - kierowca - pasażer
    public void goToSelectedActivity(View view) {

        if (whoSwitch.isChecked()){
            userType = "passenger";
            ParseUser.getCurrentUser().put("passengerOrDriver", userType);
            Log.i("Switch value", userType + " " + String.valueOf(whoSwitch.isChecked()));
        } else {
            userType = "driver";
            ParseUser.getCurrentUser().put("passengerOrDriver", userType);
            Log.i("Switch value", userType + " " + String.valueOf(whoSwitch.isChecked()));
        }

        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null){
                    Log.i("SaveUser", " Save user successful");
                    redirectActivity();
                }
            }
        });

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

    public void redirectActivity(){
        if (ParseUser.getCurrentUser().get("passengerOrDriver").equals("passenger")){
            Intent intent = new Intent(getApplicationContext(), PassengerActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(),DriverActivity.class);
            startActivity(intent);
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

        // logowanie anonimowego użytkownika
        if (ParseUser.getCurrentUser() == null) {

            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        Log.i("Login", "Anonymous user successful");

                    } else {
                        Log.i("Login", "Anonymous user failed");

                    }
                }
            });
        } else {
            if(ParseUser.getCurrentUser().get("passengerOrDriver") != null){
                Log.i("Info", "Redirecting as " + ParseUser.getCurrentUser().get("passengerOrDriver"));
                redirectActivity();
            }

        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }
}

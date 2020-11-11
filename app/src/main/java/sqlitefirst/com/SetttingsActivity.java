package sqlitefirst.com;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SetttingsActivity extends AppCompatActivity {

    private static int sTheme;
    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int THEME_DARK= 1;
    private SwitchCompat switchCompat;
    SharedPref sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        sharedPreferences = new SharedPref(this);
        if(sharedPreferences.loadNightModeState()==false){

            setTheme(R.style.AppTheme);
        }
        else{
            setTheme(R.style.DarkAppTheme);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Settings");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setttings);
        switchCompat = findViewById(R.id.dark_switch);

        if(sharedPreferences.loadNightModeState()==false){
            switchCompat.setChecked(false);
        }

        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sharedPreferences.setNightModeState(true);
                    sTheme=1;
                    recreate();
                    Toast.makeText(SetttingsActivity.this, "Refresh Home Page or Restart App", Toast.LENGTH_SHORT).show();
                }
                else{
                    sharedPreferences.setNightModeState(false);
                    sTheme=0;
                    recreate();
                    Toast.makeText(SetttingsActivity.this, "Refresh Home Page or Restart App", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.AppTheme);
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case THEME_DARK:
                activity.setTheme(R.style.DarkAppTheme);
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }

    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

        finish();
        return super.onSupportNavigateUp();
    }
}
package sqlitefirst.com;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {

    public static final String EXTRA_ID="sqlitefirst.com.EXTRA_ID";
    public static final String EXTRA_TITLE="sqlitefirst.com.EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION="sqlitefirst.com.EXTRA_DESCRIPTION";
    public static final String EXTRA_PRIORITY="sqlitefirst.com.EXTRA_PRIORITY";
    private EditText editTextTitle, editTextDescription;
    private NumberPicker numberPickerPriority;
    private LinearLayout addNoteLL;
    SharedPref sharedPreferences;
    private Button tpAddBtn;
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = new SharedPref(this);
        themeMethod();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextTitle = findViewById(R.id.et_title);
        editTextDescription = findViewById(R.id.et_text_description);

        numberPickerPriority = findViewById(R.id.number_picker_priority);
        tpAddBtn = findViewById(R.id.tpAddBtn);
        numberPickerPriority.setMinValue(1);
        editTextTitle.requestFocus();
        editTextTitle.setFocusable(true);
        numberPickerPriority.setMaxValue(10);

        //numberPickerPriority.setTextColor(R.color.colorWhite);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();

        if(intent.hasExtra(EXTRA_ID)){

            setTitle("Edit Note");
            editTextTitle.setText(intent.getStringExtra(EXTRA_TITLE));
            editTextDescription.setText(intent.getStringExtra(EXTRA_DESCRIPTION));
            numberPickerPriority.setValue(intent.getIntExtra(EXTRA_PRIORITY,1));
        }
        else {
            setTitle("Add Note");
        }

        tpAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNote();
            }
        });
    }

 /*   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu,menu);
        return true;
    }


    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    private void saveNote() {

        String title = editTextTitle.getText().toString();
        String description = editTextDescription.getText().toString();
        int priority = numberPickerPriority.getValue();

        if(title.trim().isEmpty()){

            Toast.makeText(this,"Title required",Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_TITLE, title);
        data.putExtra(EXTRA_DESCRIPTION,description);
        data.putExtra(EXTRA_PRIORITY,priority);

        int id= getIntent().getIntExtra(EXTRA_ID,-1);
        if(id!=-1){

            data.putExtra(EXTRA_ID,id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    public void themeMethod(){

        SetttingsActivity.onActivityCreateSetTheme(this);
        if(sharedPreferences.loadNightModeState()==false){

            setTheme(R.style.AppTheme);
            //recreate();
        }
        else{
            setTheme(R.style.DarkAppTheme);
            //recreate();
        }

    }


}
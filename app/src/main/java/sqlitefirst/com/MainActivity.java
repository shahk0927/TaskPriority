package sqlitefirst.com;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final int ADD_NOTE_REQUEST = 1;

    RelativeLayout relativeLayout;
    TextView tvpriority, tvtitle, tvdescription;
    public static final int EDIT_NOTE_REQUEST = 2;
    public static Handler handler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NoteViewModel noteViewModel;
    SharedPref sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedPreferences = new SharedPref(this);
        themeMethod();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        FloatingActionButton buttonAddNote = findViewById(R.id.btn_add_note);

        relativeLayout = findViewById(R.id.rl_note);
        tvpriority = findViewById(R.id.text_view_priority);
        tvtitle = findViewById(R.id.text_view_title);
        tvdescription = findViewById(R.id.text_view_description);


        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);

            }
        });


        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);


        final long ans = adapter.getItemId(1);
       // adapter.


        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update recyclerview
                adapter.submitList(notes);

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            public void onChildDraw (Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red))
                        .addActionIcon(R.drawable.ic_delete)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, final int direction) {
                String description=  adapter.getNoteAt(viewHolder.getAdapterPosition()).getDescription();
                String title=  adapter.getNoteAt(viewHolder.getAdapterPosition()).getTitle();
                int priority=  adapter.getNoteAt(viewHolder.getAdapterPosition()).getPriority();
                final Note note = new Note(title, description, priority);
                Context context;
                AlertDialog.Builder builder = new AlertDialog.Builder(viewHolder.itemView.getContext());
                builder.setTitle("Are you sure?");
                //builder.setCanceledOnTouchOutside(false);

                builder.setCancelable(false);
                LinearLayout linearLayout = new LinearLayout(viewHolder.itemView.getContext());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                        Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_SHORT).show();

                        //adapter.notifyDataSetChanged();
                    /**    Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(intent);
                        overridePendingTransition(0, 0);*/
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                        noteViewModel.insert(note);
                       // noteViewModel.insert(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                        Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();

            }
        }).attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                intent.putExtra(AddEditNoteActivity.EXTRA_ID,note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION,note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY,note.getPriority());
                startActivityForResult(intent,EDIT_NOTE_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK){

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);


            final Note note = new Note(title, description, priority);
            noteViewModel.insert(note);
            Toast.makeText(this,"Note Saved"+priority,Toast.LENGTH_SHORT).show();



        }
        else if(requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK){

            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1);

            if(id==-1){
                Toast.makeText(this,"Note can't be updated",Toast.LENGTH_SHORT).show();
            }

            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String description = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            Note note = new Note(title, description, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();
        }
        else{

            Toast.makeText(this,"Note not Saved",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){


            case R.id.delete_all_notes:


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Delete All?");
                LinearLayout linearLayout = new LinearLayout(MainActivity.this);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        noteViewModel.deleteall();
                        Toast.makeText(MainActivity.this,"All notes deleted",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this,"Cancelled",Toast.LENGTH_SHORT).show();
                    }
                });

                builder.create().show();
                return true;

            case R.id.settings:
                Intent intent = new Intent(MainActivity.this, SetttingsActivity.class);
                startActivity(intent);
                //recreate();
            default:
                return super.onOptionsItemSelected(item);
        }

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

    @Override
    public void onRefresh() {

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(swipeRefreshLayout.isRefreshing()) {

                    swipeRefreshLayout.setRefreshing(false);
                    recreate();
                }
            }
        }, 1);
    }
}
package com.example.android.notepad;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.os.Bundle;

public class NoteSearch extends Activity implements SearchView.OnQueryTextListener
{
    ListView listView;
    SQLiteDatabase sqLiteDatabase;

    private static final String[] PROJECTION = new String[]{
            NotePad.Notes._ID,
            NotePad.Notes.COLUMN_NAME_TITLE,
            NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
    };

    //show the search key by Toast while submitted
    public boolean onQueryTextSubmit(String query){
        Toast.makeText(this,"Search:"+query, Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //layout of search activity
        setContentView(R.layout.note_search);
        //get the search_key
        SearchView searchView = findViewById(R.id.search_view);
        Intent intent = getIntent();

        if(intent.getData() == null){
            intent.setData(NotePad.Notes.CONTENT_URI);
        }
        //show the search result
        listView = findViewById(R.id.list_view);
        sqLiteDatabase = new NotePadProvider.DatabaseHelper(this).getReadableDatabase();

        //search button
        searchView.setSubmitButtonEnabled(true);
        //search default text
        searchView.setQueryHint("查找");
        searchView.setOnQueryTextListener(this);
    }

    @Override
    //show the search result dynamically(like ajax)
    public boolean onQueryTextChange(String string){
        //search notes by title or note
        String selection = NotePad.Notes.COLUMN_NAME_TITLE+" like ? or "+ NotePad.Notes.COLUMN_NAME_NOTE+" like ?";
        //set the data into "?"
        String[] selectionargs = {"%"+string+"%","%"+string+"%"};

        //ResultSet Cursor
        //get data
        Cursor cursor = sqLiteDatabase.query(
                NotePad.Notes.TABLE_NAME,
                PROJECTION,
                selection,
                selectionargs,
                null,
                null,
                NotePad.Notes.DEFAULT_SORT_ORDER
        );


        //display
        String[] dataColumns = {
                NotePad.Notes.COLUMN_NAME_TITLE,
                NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE
        };
        //ID
        int[] viewIDs = {
                android.R.id.text1,
                android.R.id.text2
        };
        //show the results
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                this,
                R.layout.noteslist_item,
                cursor,
                dataColumns,
                viewIDs
        );
        listView.setAdapter(simpleCursorAdapter);
        return true;
    }

    //select item

}

package com.sargent.mark.todolist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.sargent.mark.todolist.data.Contract;
import com.sargent.mark.todolist.data.DBHelper;

public class MainActivity extends AppCompatActivity implements AddToDoFragment.OnDialogCloseListener,
        UpdateToDoFragment.OnUpdateDialogCloseListener {
    private RecyclerView rv;
    private FloatingActionButton button;
    private DBHelper helper;
    private Cursor cursor;
    private SQLiteDatabase db;
    ToDoListAdapter adapter;
    private final String TAG = "mainactivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "oncreate called in main activity");
        button = (FloatingActionButton) findViewById(R.id.addToDo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddToDoFragment frag = new AddToDoFragment();
                frag.show(fm, "addtodofragment");
            }
        });
        rv = (RecyclerView) findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (db != null) db.close();
        if (cursor != null) cursor.close();
    }
    @Override
    protected void onStart() {
        super.onStart();
        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = getAllItems(db);
        adapter = new ToDoListAdapter(cursor, new ToDoListAdapter.ItemClickListener() {
            //added categories and task to onitemclick() inorder to match the arguments for interface for the
            //same in ToDOListAdapter file
              @Override
            public void onItemClick(int pos, String description, String duedate, long id,
                                    String categories, String task) {
                Log.d(TAG, "item click id: " + id);
                String[] dateInfo = duedate.split("-");
                int year = Integer.parseInt(dateInfo[0].replaceAll("\\s",""));
                int month = Integer.parseInt(dateInfo[1].replaceAll("\\s",""));
                int day = Integer.parseInt(dateInfo[2].replaceAll("\\s",""));
                FragmentManager fm = getSupportFragmentManager();
                  //added category to the update function
                  UpdateToDoFragment frag = UpdateToDoFragment.newInstance(year, month, day, description,
                          id, categories);
                frag.show(fm, "updatetodofragment");
            }
        });
        rv.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                long id = (long) viewHolder.itemView.getTag();
                Log.d(TAG, "passing id: " + id);
                removeToDo(db, id);
                adapter.swapCursor(getAllItems(db));
            }
        }).attachToRecyclerView(rv);
    }
    //just included again the category
    @Override
    public void closeDialog(int year, int month, int day, String description, String categories) {
        addToDo(db, description, formatDate(year, month, day), categories);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }
    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }
    //the above method will get allthe items that are in the database
    //i just implemented the new method to get the items based on particular category
    private Cursor getItems_categories(SQLiteDatabase db, String categories) {
        String selection;
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection="categories='"+categories+"'",//mentioned the selection on which it has to be made
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }
    //query(String table, String[] columns, String selection,
    // String[] selectionArgs, String groupBy, String having, String orderBy)
    // public ArrayList<ArrayList<String>> selectRecordsFromDBList(String tableName, String[] tableColumns,
    //String whereClase, String whereArgs[], String groupBy,
    //String having, String orderBy)
    // took the reference from https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html#query(java.lang.String, java.lang.String[], java.lang.String,
    // java.lang.String[], java.lang.String, java.lang.String, java.lang.String)
        private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,//tablename
                null,//table columns
                null,//whereclause
                null,//whereArgs[]
                null,//groupBy
                null,//having
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE//orderBY
        );
    }
    //category is passed as an argument into tis method,inorder to added to database
    private long addToDo(SQLiteDatabase db, String description, String duedate, String categories) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        // adding the category to database
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORIES, categories);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }
    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }
    // again adding the categories into this
    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String categories){
        String duedate = formatDate(year, month - 1, day);
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        // again i am adding the categoriess
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORIES, categories);
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }
    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String categories)
    {//again adding categories
        updateToDo(db, year, month, day, description, id, categories);
        //https://stackoverflow.com/questions/1985955/android-simplecursoradapter-doesnt-update-when-database-changes/12384208
        //database
        adapter.swapCursor(getAllItems(db));
    }
//implementing the method where the status of my checkbox is checked and it is updated to my sqlite database
    //https://stackoverflow.com/questions/26190444/how-to-update-the-textview-text-depending-on-the-state-of-a-checkbox-in-listview
    public static int ischeckBox_checked(SQLiteDatabase db,long id, boolean isChecked) {
        ContentValues cv = new ContentValues();
            if(isChecked) {
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_TASK, "done");
            }
            else
            {
                //https://www.codeproject.com/Questions/1080701/How-to-create-function-in-database-for-updating-va
                String notdone=" ";
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_TASK,notdone);
            }
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }
    //https://developer.android.com/guide/topics/ui/menus.html
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
//https://stackoverflow.com/questions/6680570/android-oncreateoptionsmenu-item-action
    //https://stackoverflow.com/questions/1985955/android-simplecursoradapter-doesnt-update-when-database-changes/12384208
    //private Db mDbAdapter;
    //private Cursor mCursor;
    //private SimpleCursorAdapter mCursorAd;
    //mCursor = mDbAdapter.getAllItems();
    //mCursorAd.swapCursor(mCursor);
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
                cursor=getAllItems(db);
                adapter.swapCursor(cursor);
                return true;
            case R.id.fav_place_to_visit:
                //getItems_categories has two arguments
                //first is the database variable and second argument which i passed is particular category
                cursor=getItems_categories(db,"place_to_visit");
                adapter.swapCursor(cursor);
                return true;
            case R.id.routine:
                cursor=getItems_categories(db,"routine");
                adapter.swapCursor(cursor);
                return true;
            case R.id.official:
                cursor=getItems_categories(db,"official");
                adapter.swapCursor(cursor);
                return true;
            case R.id.medecines:
                cursor = getItems_categories(db,"medecines");
                adapter.swapCursor(cursor);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

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
                  //To update function category added.
                  //https://stackoverflow.com/questions/32996697/android-studio-fragment-onbuttonpressed-method
                  //https://stackoverflow.com/questions/15137425/how-to-update-fragment-content-from-activity-viewpager
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
    @Override
    public void closeDialog(int year, int month, int day, String description, String categories) {
        addToDo(db, description, formatDate(year, month, day), categories);
        cursor = getAllItems(db);
        adapter.swapCursor(cursor);
    }
    public String formatDate(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month + 1, day);
    }

    //To get items based on particular category.
    //http://techqa.info/programming/question/38923502/how-to-update-data-in-a-custom-dialog
    private Cursor getItems_categories(SQLiteDatabase db, String categories) {
        String selection;
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,
                null,
                selection="categories='"+categories+"'",
                //selectio which has to be made
                null,
                null,
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE
        );
    }
    // https://stackoverflow.com/questions/1305272/get-all-item-from-cursor-in-android?rq=1
    //To get all items in the category

        private Cursor getAllItems(SQLiteDatabase db) {
        return db.query(
                Contract.TABLE_TODO.TABLE_NAME,//tablename
                null,//Table columns
                null,//whereclause
                null,//whereArgs[]
                null,//groupBy
                null,
                Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE//OrderBY
        );
    }
    //http://www.java2s.com/Code/Android/Database/Createdeleteupdate.htm
    //Adding category to the datqbase
    private long addToDo(SQLiteDatabase db, String description, String duedate, String categories) {
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORIES, categories);
        return db.insert(Contract.TABLE_TODO.TABLE_NAME, null, cv);
    }
    private boolean removeToDo(SQLiteDatabase db, long id) {
        Log.d(TAG, "deleting id: " + id);
        return db.delete(Contract.TABLE_TODO.TABLE_NAME, Contract.TABLE_TODO._ID + "=" + id, null) > 0;
    }
    private int updateToDo(SQLiteDatabase db, int year, int month, int day, String description, long id, String categories){
        String duedate = formatDate(year, month - 1, day);
        ContentValues cv = new ContentValues();
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION, description);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE, duedate);
        cv.put(Contract.TABLE_TODO.COLUMN_NAME_CATEGORIES, categories);
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }
    @Override
    public void closeUpdateDialog(int year, int month, int day, String description, long id, String categories)
    {
        updateToDo(db, year, month, day, description, id, categories);
        adapter.swapCursor(getAllItems(db));
    }
//Method for getting status of checkbox updated in SQLlite database.
//https://developer.android.com/reference/android/widget/CheckBox.html
    public static int ischeckBox_checked(SQLiteDatabase db,long id, boolean isChecked) {
        ContentValues cv = new ContentValues();
            if(isChecked) {
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_TASK, "done");
            }

            else
            {
                String notdone=" ";
                cv.put(Contract.TABLE_TODO.COLUMN_NAME_TASK,notdone);
            }
        return db.update(Contract.TABLE_TODO.TABLE_NAME, cv, Contract.TABLE_TODO._ID + "=" + id, null);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
//Referred by: https://stackoverflow.com/questions/7479992/handling-a-menu-item-click-event-android
    //https://developer.android.com/guide/topics/ui/menus.html
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.all:
                cursor=getAllItems(db);
                adapter.swapCursor(cursor);
                return true;
            case R.id.fav_place_to_visit:
                cursor=getItems_categories(db,"Gym");
                adapter.swapCursor(cursor);
                return true;
            case R.id.routine:
                cursor=getItems_categories(db,"Things_to_buy");
                adapter.swapCursor(cursor);
                return true;
            case R.id.official:
                cursor=getItems_categories(db,"Assigments_to_do");
                adapter.swapCursor(cursor);
                return true;
            case R.id.medecines:
                cursor = getItems_categories(db,"Travel_list");
                adapter.swapCursor(cursor);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

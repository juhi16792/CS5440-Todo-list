package com.sargent.mark.todolist;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sargent.mark.todolist.data.Contract;

/**
 * Created by mark on 7/4/17.
 */
public class ToDoListAdapter extends RecyclerView.Adapter<ToDoListAdapter.ItemHolder> {

    private Cursor cursor;
    private ItemClickListener listener;
    private String TAG = "todolistadapter";
    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item, parent, false);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }
    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        holder.bind(holder, position);
    }
    @Override
    public int getItemCount() {
        return cursor.getCount();
    }
    //included categories and task inorder to remove the error from mainactivity in onItemClick()method
    public interface ItemClickListener {
        void onItemClick(int pos, String description, String duedate, long id, String categories, String task);
    }
    public ToDoListAdapter(Cursor cursor, ItemClickListener listener) {
        this.cursor = cursor;
        this.listener = listener;
    }
    //https://stackoverflow.com/questions/31367599/how-to-update-recyclerview-adapter-data
    //Refersh recycler view
    public void swapCursor(Cursor newCursor){
        if (cursor != null) cursor.close();
        cursor = newCursor;
        //mAdapter.notifyDataSetChanged();
        if (newCursor != null) {
            this.notifyDataSetChanged();
            //http://sapandiwakar.in/pull-to-refresh-for-android-recyclerview-or-any-other-vertically-scrolling-view/
        }
    }
    class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        String dueDate;
        TextView dueDateTV;

        String description;
        TextView descriptionTV;

        String category;
        TextView categoryTV;

        CheckBox statusCB;
        String status;

        long id;
        ItemHolder(View view) {
            super(view);
            descriptionTV = (TextView) view.findViewById(R.id.description);
            dueDateTV = (TextView) view.findViewById(R.id.dueDate);
            categoryTV = (TextView)  view.findViewById(R.id.category);
            statusCB = (CheckBox) view.findViewById(R.id.task);
            view.setOnClickListener(this);
        }
        public void bind(ItemHolder holder, int pos) {
            cursor.moveToPosition(pos);
            id = cursor.getLong(cursor.getColumnIndex(Contract.TABLE_TODO._ID));
            Log.d(TAG, "deleting id: " + id);

            //To get all data
            dueDate = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DUE_DATE));
            description = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_DESCRIPTION));
            category = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_CATEGORIES));
            status = cursor.getString(cursor.getColumnIndex(Contract.TABLE_TODO.COLUMN_NAME_TASK));


         // Setting all values here for the text view.
            dueDateTV.setText(dueDate);
            descriptionTV.setText(description);
            categoryTV.setText(category);
            holder.itemView.setTag(id);
        }
        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            listener.onItemClick(pos, description, dueDate, id, category,status);
        }
    }
}

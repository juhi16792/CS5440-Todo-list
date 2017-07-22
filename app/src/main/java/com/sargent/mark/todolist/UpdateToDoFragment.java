package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Created by mark on 7/5/17.
 */

public class UpdateToDoFragment extends DialogFragment {

    private EditText toDo;
    private Spinner spinner; // Here I have created new variable for SPINNER.
    private DatePicker dp;
    private Button add;
    private final String TAG = "updatetodofragment";
    private long id;


    public UpdateToDoFragment(){}
        //adding categories!
    // https://stackoverflow.com/questions/18334063/update-fragment-data-after-newinstance
    public static UpdateToDoFragment newInstance(int year, int month, int day, String description,
                                                 long id, String categories) {
        UpdateToDoFragment f = new UpdateToDoFragment();
        Bundle args = new Bundle();
        args.putInt("year", year);
        args.putInt("month", month);
        args.putInt("day", day);
        args.putLong("id", id);
        args.putString("description", description);
        args.putString("category", categories);
        f.setArguments(args);
        return f;
    }
    //https://stackoverflow.com/questions/29547883/how-to-update-activity-when-dialog-is-closed
    public interface OnUpdateDialogCloseListener {

        void closeUpdateDialog(int year, int month, int day, String description, long id, String categories);
    //OnDialogCloseListener listener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);
        spinner = (Spinner) view.findViewById(R.id.categories);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);
        int year = getArguments().getInt("year");
        int month = getArguments().getInt("month");
        int day = getArguments().getInt("day");
        id = getArguments().getLong("id");
        String description = getArguments().getString("description");
        dp.updateDate(year, month, day);
        toDo.setText(description);
        //Creating array adapter Returns a view for each object in a collection of data
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.arraylist_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    // Getting adapter for the spinner here.
        spinner.setAdapter(adapter);

        add.setText("Update");
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateToDoFragment.OnUpdateDialogCloseListener activity = (UpdateToDoFragment.OnUpdateDialogCloseListener) getActivity();
                Log.d(TAG, "id: " + id);
                activity.closeUpdateDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(),
                        toDo.getText().toString(), id, spinner.getSelectedItem().toString());
                UpdateToDoFragment.this.dismiss();
            }
        });
        return view;
    }
}
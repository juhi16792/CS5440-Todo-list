package com.sargent.mark.todolist;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.Calendar;

/**
 * Created by mark on 7/4/17.
 */

public class AddToDoFragment extends DialogFragment{

    private Spinner spinner;
    private EditText toDo;
    private DatePicker dp;
    private Button add;
    private final String TAG = "addtodofragment";

    public AddToDoFragment() {
    }

    public interface OnDialogCloseListener {
        //Adding CATEGORY here in this method.
        void closeDialog(int year, int month, int day, String description, String categories);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_do_adder, container, false);
        toDo = (EditText) view.findViewById(R.id.toDo);

        //Adding spinner for category
        //https://stackoverflow.com/questions/9406920/android-spinner-with-date-picker-like-google-calendar-app
        spinner = (Spinner) view.findViewById(R.id.categories);
        dp = (DatePicker) view.findViewById(R.id.datePicker);
        add = (Button) view.findViewById(R.id.add);
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        dp.updateDate(year, month, day);

        //https://developer.android.com/reference/android/widget/ArrayAdapter.html#ArrayAdapter%28android.content.Context,%20int,%20java.util.List%3CT%3E%29
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.arraylist_category, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //https://developer.android.com/guide/topics/ui/controls/spinner.html
        spinner.setAdapter(adapter);
//Referred from: https://www.codota.com/android/methods/android.widget.DatePicker/getMonth
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnDialogCloseListener activity = (OnDialogCloseListener) getActivity();
                activity.closeDialog(dp.getYear(), dp.getMonth(), dp.getDayOfMonth(), toDo.getText().toString(),
                        // Spinner mySpinner = (Spinner)findViewbyId(R.id.spinner);
                        //int position = mySpinner.getSelectedItemPosition();
                        //String Text = yourCityList[position].toString();
                        spinner.getSelectedItem().toString());
                AddToDoFragment.this.dismiss();
            }
        });
        return view;
    }
}
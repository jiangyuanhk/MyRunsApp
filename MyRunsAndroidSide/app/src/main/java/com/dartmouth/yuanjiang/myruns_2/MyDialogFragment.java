package com.dartmouth.yuanjiang.myruns_2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;


public class MyDialogFragment extends DialogFragment {

    //define dialog's ID
    public static final int PROFILE_DIALOG_ID_ERROR = -1;
    public static final int PROFILE_DIALOG_ID_PHOTO = 0;
    public static final int PROFILE_DIALOG_ID_DATE = 1;
    public static final int PROFILE_DIALOG_ID_TIME = 2;
    public static final int PROFILE_DIALOG_ID_DURATION = 3;
    public static final int PROFILE_DIALOG_ID_DISTANCE = 4;
    public static final int PROFILE_DIALOG_ID_CALORIES = 5;
    public static final int PROFILE_DIALOG_ID_HEARTRATE = 6;
    public static final int PROFILE_DIALOG_ID_COMMENT = 7;

    public static MyDialogFragment newInstance(int dialog_id){
        MyDialogFragment newFrag = new MyDialogFragment();
        Bundle args = new Bundle();
        args.putInt("dialog_id", dialog_id);
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        int dialog_id = getArguments().getInt("dialog_id");

        final Calendar mDateAndTime = Calendar.getInstance();

        //Setup dialog appearance and onClick Listeners
        switch (dialog_id){
            case PROFILE_DIALOG_ID_PHOTO:
                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Photo)
                        .setItems(R.array.ui_photo_items,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int item) {
                                        ((ProfileActivity)getActivity()).onPhotoItemSelected(item);
                                    }
                                })
                        .create();

            case PROFILE_DIALOG_ID_DATE:
                DatePickerDialog.OnDateSetListener mDateListener = new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        ((ManualEntryActivity)getActivity()).onDateSet(year, monthOfYear, dayOfMonth);
                    }
                };

                return new DatePickerDialog(getActivity(), mDateListener,
                        mDateAndTime.get(Calendar.YEAR),
                        mDateAndTime.get(Calendar.MONTH),
                        mDateAndTime.get(Calendar.DAY_OF_MONTH));

            case PROFILE_DIALOG_ID_TIME:
                TimePickerDialog.OnTimeSetListener mTimeListener = new TimePickerDialog.OnTimeSetListener() {
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        ((ManualEntryActivity)getActivity()).onTimeSet(hourOfDay, minute);
                    }
                };

                return new TimePickerDialog(getActivity(),
                        mTimeListener,
                        mDateAndTime.get(Calendar.HOUR_OF_DAY),
                        mDateAndTime.get(Calendar.MINUTE), true);

            case PROFILE_DIALOG_ID_DURATION:
                final EditText textDuration = new EditText(getActivity());
                textDuration.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Duration)
                        .setView(textDuration)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String duration = textDuration.getText().toString();

                                        if (!duration.isEmpty()) {
//                                            Log.d("ssss", duration);
                                            ((ManualEntryActivity) getActivity()).onDurationSet(Double.parseDouble(duration));
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        dismiss();
                                    }
                                }).create();

            case PROFILE_DIALOG_ID_DISTANCE:
                final EditText textDistance = new EditText(getActivity());
                textDistance.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Distance)
                        .setView(textDistance)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String distance = textDistance.getText().toString();
                                        if(!distance.isEmpty()){
                                            ((ManualEntryActivity)getActivity()).onDistanceSet(Double.parseDouble(distance));
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton){
                                        dismiss();
                                    }
                                }).create();

            case PROFILE_DIALOG_ID_CALORIES:
                final EditText textCalories = new EditText(getActivity());
                textCalories.setInputType(InputType.TYPE_CLASS_NUMBER );

                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Calories)
                        .setView(textCalories)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String calories = textCalories.getText().toString();
                                        if(!calories.isEmpty()){
                                            ((ManualEntryActivity)getActivity()).onCaloriesSet(Integer.parseInt(calories));
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton){
                                        dismiss();
                                    }
                                }).create();

            case PROFILE_DIALOG_ID_HEARTRATE:
                final EditText textHeartrate = new EditText(getActivity());
                textHeartrate.setInputType(InputType.TYPE_CLASS_NUMBER);

                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Aveheart)
                        .setView(textHeartrate)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String heartrate = textHeartrate.getText().toString();
                                        if(!heartrate.isEmpty()){
                                            ((ManualEntryActivity)getActivity()).onHeartrateSet(Integer.parseInt(heartrate));
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton){
                                        dismiss();
                                    }
                                }).create();

            case PROFILE_DIALOG_ID_COMMENT:
                final EditText textComment = new EditText(getActivity());
                textComment.setHint(R.string.Hint);
                textComment.setInputType(InputType.TYPE_CLASS_TEXT);

                return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.Comment)
                        .setView(textComment)
                        .setPositiveButton(R.string.alert_dialog_ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        String comment = textComment.getText().toString();
                                        if(!comment.isEmpty()){
                                            ((ManualEntryActivity)getActivity()).onCommentSet(comment);
                                        }
                                    }
                                })
                        .setNegativeButton(R.string.alert_dialog_cancel,
                                new DialogInterface.OnClickListener(){
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton){
                                        dismiss();
                                    }
                                }).create();
            default:
                return null;
        }
    }
}

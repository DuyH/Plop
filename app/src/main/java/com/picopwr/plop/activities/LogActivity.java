package com.picopwr.plop.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.color.ColorChooserDialog;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.picopwr.plop.R;
import com.picopwr.plop.db.LogDBHelper;
import com.picopwr.plop.fragments.DatePickerDialogFragment;
import com.picopwr.plop.fragments.TimePickerDialogFragment;
import com.picopwr.plop.model.LogEntry;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogActivity extends AppCompatActivity implements ColorChooserDialog.ColorCallback {


    // Utility method:
    private Toast mToast;
    private Thread mThread;
    private Handler mHandler;

    // color chooser dialog
    private int primaryPreselect;
    private int accentPreselect;

    // Text views that need updating in activity view:
    TextView mTextDate, mTextTime, mTextBristol, mTextAmount, mTextDifficulty, mTextColor, mTextNote;

    // Strings to save for LogEntry object
    String mDate, mTime, mBristol, mAmount, mDifficulty, mColor, mNote;

    // Shared preferences
    SharedPreferences mSharedPreferences;

    private void showToast(String message) {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
        mToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private void showToast(@StringRes int message) {
        showToast(getString(message));
    }

    private void startThread(Runnable run) {
        if (mThread != null)
            mThread.interrupt();
        mThread = new Thread(run);
        mThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        mHandler = new Handler();
        primaryPreselect = DialogUtils.resolveColor(this, R.attr.colorPrimary);
        accentPreselect = DialogUtils.resolveColor(this, R.attr.colorAccent);
        mSharedPreferences = this.getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
    }


    @OnClick(R.id.buttonDate)
    public void showDatePickerDialog() {
        DatePickerDialogFragment datePickerFragment = new DatePickerDialogFragment();
        datePickerFragment.show(getSupportFragmentManager(), "date");

        mTextDate = (TextView) findViewById(R.id.textDate);
        mTextDate.setText("Date Set!");
    }

    @OnClick(R.id.buttonTime)
    public void showTimePickerDialog() {
        TimePickerDialogFragment timePickerFragment = new TimePickerDialogFragment();
        timePickerFragment.show(getSupportFragmentManager(), "time");

        mTextTime = (TextView) findViewById(R.id.textTime);
        mTextTime.setText("Time Set!");



    }

    @OnClick(R.id.buttonBristol)
    public void showBristolPickerDialog() {
        new MaterialDialog.Builder(this)
                .title("Classify according to Bristol")
                .items(R.array.bristol_scale_and_descriptions)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String bristolScale[] = getResources().getStringArray(R.array.bristol_scale);
                        mBristol = bristolScale[which];
                        mTextBristol = (TextView) findViewById(R.id.textBristolType);
                        mTextBristol.setText(mBristol);
                        Log.d("BRISTOL TYPE", mBristol);
                        showToast("You chose " + mBristol);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();




    }

    @OnClick(R.id.buttonAmount)
    public void showAmountPickerDialog() {
        new MaterialDialog.Builder(this)
                .title("How much was there?")
                .items(R.array.amount)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mAmount = text.toString();
                        mTextAmount = (TextView) findViewById(R.id.textAmount);
                        mTextAmount.setText(mAmount);
                        Log.d("AMOUNT!", mAmount);
                        showToast("You chose " + mAmount);
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();


    }

    @OnClick(R.id.buttonDifficulty)
    public void showDifficultyPickerDialog() {
        new MaterialDialog.Builder(this)
                .title("How difficult was it to pass?")
                .items(R.array.difficulty)
                .itemsCallbackSingleChoice(2, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mDifficulty = text.toString();
                        mTextDifficulty = (TextView) findViewById(R.id.textDifficulty);
                        mTextDifficulty.setText(mDifficulty);
                        Log.d("DIFFICULTY!", mDifficulty);
                        showToast("You had a(n) " + mDifficulty + " passing it");
                        return true; // allow selection
                    }
                })
                .positiveText(R.string.md_choose_label)
                .show();

    }

    @OnClick(R.id.buttonColor) // Thanks butterknife! (injection code)
    public void showColorChooserCustomColorsNoSub() {
        new ColorChooserDialog.Builder(this, R.string.app_name)
                .titleSub(R.string.dialogColor)
                .allowUserColorInput(false)
                .customColors(R.array.stool_colors_hex, null)
                .show();
    }

    @Override
    public void onColorSelection(ColorChooserDialog colorChooserDialog, int i) {
        String hexColor = String.format("#%06X", (0xFFFFFF & i));

        String[] hexColors = getResources().getStringArray(R.array.stool_colors_hex_string);
        String[] colors = getResources().getStringArray(R.array.stool_colors);
        for (int j = 0; j < hexColors.length; j++) {
            if (hexColors[j].equalsIgnoreCase(hexColor)) {
                mColor = colors[j];
                break;
            }
        }
        mTextColor = (TextView) findViewById(R.id.textColor);
        mTextColor.setText(mColor);
        Log.d("COLOR CHOSEN INT", "" + mColor);

    }

    @OnClick(R.id.buttonNotes)
    public void showNotesPickerDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("LogEntry notes");

        // Create edit text for alert dialog
        final EditText editNote = new EditText(this);
        editNote.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        builder.setView(editNote);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mNote = editNote.getText().toString();
                mTextNote = (TextView) findViewById(R.id.textNotes);
                mTextNote.setText(mNote);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @OnClick(R.id.buttonSubmit)
    public void onSubmit() {


        // Insert new values from shared prefs
        //mBristol = mSharedPreferences.getString("bristol", null);
        //mAmount = mSharedPreferences.getString("amount", null);
        //mDifficulty = mSharedPreferences.getString("difficulty", null);
        mTime = mSharedPreferences.getString("time", null);
        mDate = mSharedPreferences.getString("date", null);
        //mColor = mSharedPreferences.getString("color", null);
        // mNote already done from onclick listener above
        Log.d("I GOT A DATE", mDate);
        Log.d("I GOT A TIME", mTime);

        if (validate()) {

            // Retrieve currently logged in user's email address
            String email = mSharedPreferences.getString("email", null);
            Log.d("RETRIEVED EMAIL", email);

            Log.d("ALL STATS", email + mDate + mTime + mBristol + mAmount + mColor + mDifficulty + mNote);


            // Create a new LogEntry and insert into database
            LogEntry logEntry = new LogEntry(email, mDate, mTime, mBristol, mAmount, mColor, mDifficulty, mNote);
            LogDBHelper logDBHelper = new LogDBHelper(this);
            logDBHelper.createLog(logEntry);
//            logDBHelper.close();

            // Notify user of successful log entry
            Toast.makeText(this, "LogEntry successfully created!", Toast.LENGTH_SHORT).show();

            // Wipe existing shared pref data about this particular log
            wipeSharedPrefs();

            // Return to Main activity screen
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
        }

    }

    /**
     * Check if all the necessary entries are filled out to create a proper log.
     * @return Whether or not a log entry is completed and ready for insertion into db.
     */
    public boolean validate() {
        if (mDate != null && mTime != null && mBristol != null && mAmount != null && mDifficulty != null && mColor != null && mNote != null) {
            return true;
        }else{
            Toast.makeText(this, "You are missing some entries!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @OnClick(R.id.buttonCancel)
    public void onCancel() {

        // Wipe data pertaining to Log Entry (date, time, amount, bristol,
        wipeSharedPrefs();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    public void wipeSharedPrefs() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.remove("date");
        editor.remove("time");
        //editor.remove("bristol");
        //editor.remove("amount");
        //editor.remove("difficulty");
        //editor.remove("color");
        editor.commit();
    }
}

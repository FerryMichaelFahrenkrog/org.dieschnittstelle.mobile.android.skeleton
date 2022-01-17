package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity
{
    public static final int ACTION_PICK_CONTACT = 0;
    public static final String ARG_TODO_OBJECT = "todo";
    public static final String ARG_TODO_INDEX = "todoIndex";

    public static final String ARG_TODO_DELETE = "todoDelete";
    public static final String ARG_TODO_DATETIME = "todoDatetime";
    @SuppressLint("NewApi")
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    @SuppressLint("NewApi")
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    public static final String ARG_ITEM = "item";               // Zur Objektentgegennahme

    private ToDo toDo;
    private ActivityDetailviewBinding detailviewBinding;
    @SuppressLint("NewApi")
    private LocalDateTime PlaceholderDateTime = LocalDateTime.now();
    private LocalDateTime TodoDateTime = PlaceholderDateTime;
    private String errorStatus;

    private EditText editTextDatum;
    private EditText editTextUhr;
    private Button deleteButton;
    private boolean itemDelete = false;
    private int todoIndex;

    TextView kontaktName;
    Button timeButton;
    int hour, minute;

    Calendar c;
    DatePickerDialog datePickerDialog;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        instantiateCorrectLocalDateTime();

        editTextUhr = findViewById(R.id.editTextTime);
        editTextUhr.setText(TodoDateTime.format(TIME_FORMATTER));
        editTextUhr.setInputType(InputType.TYPE_NULL);
        editTextUhr.setOnClickListener(v -> setTimePickerDialog());

        editTextDatum = findViewById(R.id.editTextDate);
        editTextDatum.setText(TodoDateTime.format(DATE_TIME_FORMATTER));
        editTextDatum.setInputType(InputType.TYPE_NULL);
        editTextDatum.setOnClickListener(v -> setDatePickerDialog());

        deleteButton = findViewById(R.id.btnLoeschen);
        deleteButton.setOnClickListener(v -> deleteItem());

        toDo = (ToDo)getIntent().getSerializableExtra(ARG_ITEM);            // Hier nehme ich das Argument entgegen aus der Main-Activity und speichere es

        if(toDo == null){
            toDo = new ToDo();                                              // Leeres ToDoItem erzeugen, falls keines übergeben wurde.
        }

        todoIndex = getIntent().getIntExtra(ARG_TODO_INDEX, Integer.MAX_VALUE);




        Log.i("DetailviewActivity", "got contact ids: " + toDo.getContactIds());
        toDo.getContactIds().forEach(id -> {
            showContactDetailsForInternal(Long.parseLong(id));
        });

        detailviewBinding.setController(this);
    }

    @SuppressLint("NewApi")
    private void instantiateCorrectLocalDateTime() {
        if (getIntent().getSerializableExtra(ARG_TODO_DATETIME) != null) {
            TodoDateTime = (LocalDateTime) getIntent().getSerializableExtra(ARG_TODO_DATETIME);
        }
    }

    @SuppressLint("NewApi")
    public void setTimePickerDialog() {
        int hour = Integer.parseInt(editTextUhr.getText().toString().substring(0, 2));
        int minutes = Integer.parseInt(editTextUhr.getText().toString().substring(3, 5));

        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailviewActivity.this, R.style.Theme_MaterialComponents_Light_Dialog, (view, hourOfDay, minuteOfHour) -> {
            TodoDateTime = this.PlaceholderDateTime
                    .withHour(hourOfDay)
                    .withMinute(minuteOfHour);
            editTextUhr.setText(TodoDateTime.format(TIME_FORMATTER));
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void colorChange(View view){
      view.setBackgroundColor(Color.RED);
    }

    @SuppressLint("NewApi")
    public void setDatePickerDialog() {
        int day = Integer.parseInt(editTextDatum.getText().toString().substring(0, 2));
        int month = Integer.parseInt(editTextDatum.getText().toString().substring(3, 5));
        int year = Integer.parseInt(editTextDatum.getText().toString().substring(6, 10));
        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailviewActivity.this, R.style.Theme_MaterialComponents_Light_Dialog, (view, yearOfYear, monthOfYear, dayOfMonth) -> {
            TodoDateTime = this.PlaceholderDateTime
                    .withYear(yearOfYear)
                    .withMonth(monthOfYear + 1)
                    .withDayOfMonth(dayOfMonth);
            editTextDatum.setText(TodoDateTime.format(DATE_TIME_FORMATTER));
        }, year, month - 1, day);
        datePickerDialog.show();
    }

    public void deleteItem() {
        new AlertDialog.Builder(DetailviewActivity.this)
                .setTitle("Lösche ToDo")
                .setMessage("Wirklich löschen?")
                .setPositiveButton("löschen", (dialog, which) -> {
                    itemDelete = true;
                    onDeleteTodo();
                })
                .setNegativeButton("verwerfen", (dialog, which) -> Log.d("CancelButton", "Cancel clicked"))
                .show();
    }

    public void onDeleteTodo() {
        Intent returnData = new Intent();
        returnData.putExtra(ARG_ITEM, toDo);
        returnData.putExtra(ARG_TODO_INDEX, todoIndex);
        returnData.putExtra(ARG_TODO_DELETE, itemDelete);

        this.setResult(Activity.RESULT_OK, returnData);
        finish();
    }


    @SuppressLint("NewApi")
    public void onSaveItem(){
        updateLocalDateTime();

        Intent returnIntent = new Intent();                         // Rückgabe Inteent erstellen

        returnIntent.putExtra(ARG_ITEM, toDo);                      // Unsere Daten reinpacken
        returnIntent.putExtra(ARG_TODO_INDEX, todoIndex);
        returnIntent.putExtra(ARG_TODO_DATETIME, TodoDateTime);

        setResult(Activity.RESULT_OK, returnIntent);                // liefert RESULT_OK zurück, wird dann in der MainActivity geprüft

        finish();
    }



    @SuppressLint("NewApi")
    void updateLocalDateTime() {
        String hour = editTextUhr.getText().toString().substring(0, 2);
        String minute = editTextUhr.getText().toString().substring(3, 5);
        String day = editTextDatum.getText().toString().substring(0, 2);
        String month = editTextDatum.getText().toString().substring(3, 5);
        String year = editTextDatum.getText().toString().substring(6, 10);

        TodoDateTime = this.PlaceholderDateTime
                .withHour(Integer.parseInt(hour))
                .withMinute(Integer.parseInt(minute))
                .withDayOfMonth(Integer.parseInt(day))
                .withMonth(Integer.parseInt(month))
                .withYear(Integer.parseInt(year));
    }

    public ToDo getToDo() {
        return toDo;
    }

    public void setToDo(ToDo toDo) {
        this.toDo = toDo;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.selectContact){
            selectContact();
            return true;
        }
        else if(item.getItemId() == R.id.sendSMS)
            {
                sendSms();
            }
        else if(item.getItemId() == R.id.deleteRemoteItems)
        {

        }

        return super.onOptionsItemSelected(item);
    }

    protected void selectContact(){
        Intent selectContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(selectContactIntent,ACTION_PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == ACTION_PICK_CONTACT && resultCode == Activity.RESULT_OK)
        {
            Log.i("DetailviewActivity", "onActivityResult(): got data: " + data);
            showContactDetails(data.getData());
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i("DetailviewActivity", "onRequestPermissionsResult(): permission");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    protected void showContactDetails(Uri contactId){
        int hasReadContactsPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
        if(hasReadContactsPermission != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1 );
            return;
        }

        Cursor cursor = getContentResolver().query(contactId, null, null, null, null);
        if(cursor.moveToFirst()){
            String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            long internalContactId = cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID));

            if(!toDo.getContactIds().contains(String.valueOf(contactId))){
                toDo.getContactIds().add(String.valueOf(internalContactId));
            }


            Log.i("DetailviewActivity", "got contact with name " + contactName + " and internal id: " + internalContactId);

            showContactDetailsForInternal(internalContactId);
        }
        else{
            Log.i("DetailviewActivity", "no contact found");
        }
    }

    //UI BNEFÜLLEN
    public void showContactDetailsForInternal(long internalId) {
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + "=?", new String[]{String.valueOf(internalId)}, null);
        if(cursor.moveToNext()){
            String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Log.i("DetailviewActivity", "found display name for internal id " + internalId + ": " + displayName);
            kontaktName = (TextView)findViewById(R.id.txtVerknüpfteKontakteNummer1);
            kontaktName.setText(displayName);
        }else{
            Log.i("DetailviewActivity", "no contacts fount for internal id " + internalId);
        }

         cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[]{String.valueOf(internalId),}, null);
        while(cursor.moveToNext()){
            String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            int phoneNumberType = cursor.getInt(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

            Log.i("DetailviewActivity", "found number of type " + number + ", of type " + phoneNumberType + ", mobile: " + (phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE));
        }
    }

    public void onNameInputCompleted(boolean hasFocus) {
        Log.i("DetailviewActivity", "onNameInputCompleted: " + hasFocus);

        if(!hasFocus){
            String name = toDo.getName();

            if(name != null && name.length() >= 3){
                Log.i("DetailviewActivity", "validationSuccessful: " + toDo.getName());
                errorStatus = null;
            }
            else{
                Log.i("DetailviewActivity", "validation failed" + toDo.getName());
                errorStatus = "Name too short!";
                detailviewBinding.setController(this);
            }
        }
    }

    public void onNameInputChanged(){
        if(errorStatus != null){
            errorStatus = null;
            detailviewBinding.setController(this);
        }
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    protected void sendSms() {
        Uri smsUri = Uri.parse("smsto:000000");
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
        smsIntent.putExtra("sms body", toDo.getName() + ": " + toDo.getDescription());
        startActivity(smsIntent);
    }

}

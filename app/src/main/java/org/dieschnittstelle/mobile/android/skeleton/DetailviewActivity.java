package org.dieschnittstelle.mobile.android.skeleton;

import static org.dieschnittstelle.mobile.android.skeleton.MainActivity.REQCODE_ADD_CONTACT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {
    public static final int ACTION_PICK_CONTACT = 0;
    public static final String ARG_ITEM = "item";                               // Zur Objektentgegennahme
    public static final String ARG_TODO_INDEX = "todoIndex";
    public static final String ARG_TODO_DELETE = "todoDelete";
    public static final String ARG_TODO_DATETIME = "todoDatetime";


    private ToDo toDo;
    private String errorStatus;
    private EditText editTextDatum;
    private EditText editTextUhr;
    private Button deleteButton;
//    TextView kontaktName;

//    private ContactsToListItemAdapter contactListAdapter;
//    private ListView contactListView;
//    private String phoneNumber = "";
//    private String email = "";

    private boolean itemDelete = false;
    private int todoIndex;

    private LocalDateTime PlaceholderDateTime = LocalDateTime.now();
    private LocalDateTime TodoDateTime = PlaceholderDateTime;

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ArrayList<String> contactList;
    private ContactsToListItemAdapter contactListAdapter;
    private ListView contactListView;
    private String phoneNumber = "";
    private String email = "";
    private ActivityDetailviewBinding detailviewBinding;

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

        toDo = (ToDo) getIntent().getSerializableExtra(ARG_ITEM);            // Hier nehme ich das Argument entgegen aus der Main-Activity und speichere es

        if (toDo == null) {
            toDo = new ToDo();                                              // Leeres ToDoItem erzeugen, falls keines übergeben wurde.
        }

        todoIndex = getIntent().getIntExtra(ARG_TODO_INDEX, Integer.MAX_VALUE);

//        Log.i("DetailviewActivity", "got contact ids: " + toDo.getContacts());
//        toDo.getContacts().forEach(id -> {
//            showContactDetailsForInternal(Long.parseLong(id));
//        });

        contactList = new ArrayList<>();
        contactListView = findViewById(R.id.contactItemListView);
        registerForContextMenu(contactListView);
        contactListAdapter = new ContactsToListItemAdapter(getApplicationContext(), contactList, this);
        contactListView.setAdapter(contactListAdapter);

        requestContactPermission();

        detailviewBinding.setController(this);
    }

    public void requestContactPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Read contacts access needed");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setMessage("Please enable access to contacts.");
                builder.setOnDismissListener(dialog -> requestPermissions(
                        new String[]{
                                Manifest.permission.READ_CONTACTS
                        }, REQCODE_ADD_CONTACT));
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        REQCODE_ADD_CONTACT);
            }
        } else {
            loadContactDetailsFromTodo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQCODE_ADD_CONTACT) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContactDetailsFromTodo();
            } else {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadContactDetailsFromTodo() {
        contactList.clear();
        for (String id : toDo.getContacts()) {

            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, ContactsContract.Contacts._ID + " = ? ", new String[]{id}, null);
            if (cursor.moveToFirst()) {
                String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                Cursor phoneCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                        new String[]{id},
                        null,
                        null);

                while (phoneCursor.moveToNext()) {
                    String number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    int phoneNumberType = phoneCursor.getInt(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2));

                    if ((phoneNumberType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)) {
                        phoneNumber = number;
                    }
                }

                Cursor emailCursor = getContentResolver().query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + "= ?",
                        new String[]{id},
                        null,
                        null);

                while (emailCursor.moveToNext()) {
                    email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                }

                contactList.add("Contact with ID " + id + ": " + contactName);
            }
        }
        contactListAdapter.notifyDataSetChanged();
    }

    private void instantiateCorrectLocalDateTime() {
        if (getIntent().getSerializableExtra(ARG_TODO_DATETIME) != null) {
            TodoDateTime = (LocalDateTime) getIntent().getSerializableExtra(ARG_TODO_DATETIME);
        }
    }

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

    public void onSaveItem() {
        updateLocalDateTime();

        Intent returnIntent = new Intent();                         // Rückgabe Inteent erstellen

        returnIntent.putExtra(ARG_ITEM, toDo);                      // Unsere Daten reinpacken
        returnIntent.putExtra(ARG_TODO_INDEX, todoIndex);
        returnIntent.putExtra(ARG_TODO_DATETIME, TodoDateTime);

        setResult(Activity.RESULT_OK, returnIntent);                // liefert RESULT_OK zurück, wird dann in der MainActivity geprüft

        finish();
    }

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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.detailview_menu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.detailview_contact_context, menu);
    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        switch (item.getItemId()) {
//            case R.id.contactDelete:
//                deleteContact(info);
//                return true;
//            case R.id.contactMail:
//                if (email.equals("")) {
//                    Toast.makeText(this, "No Email found", Toast.LENGTH_SHORT).show();
//                } else
//                    sendToEmail();
//                return true;
//            case R.id.contactSMS:
//                if (phoneNumber.equals("")) {
//                    Toast.makeText(this, "No Number found", Toast.LENGTH_SHORT).show();
//                } else
//                    sendToSMS();
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    public void onNameInputChanged() {
        if (errorStatus != null) {
            errorStatus = null;
            detailviewBinding.setController(this);
        }
    }

    public void onNameInputCompleted(boolean hasFocus) {
        Log.i("DetailviewActivity", "onNameInputCompleted: " + hasFocus);

        if (!hasFocus) {
            String name = toDo.getName();

            if (name != null && name.length() >= 3) {
                Log.i("DetailviewActivity", "validationSuccessful: " + toDo.getName());
                errorStatus = null;
            } else {
                Log.i("DetailviewActivity", "validation failed" + toDo.getName());
                errorStatus = "Name too short!";
                detailviewBinding.setController(this);
            }
        }
    }

    private void deleteContact(AdapterView.AdapterContextMenuInfo info) {
        contactList.remove(info.position);
        toDo.getContacts().remove(info.position);

        contactListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return true;
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.addContact:
//                selectAndAddContact();
//                return true;
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.addContact:
                selectAndAddContact();
                return true;
            case R.id.contactDelete:
                deleteContact(info);
                return true;
            case R.id.contactMail:
                if (email.equals("")) {
                    Toast.makeText(this, "No Email found", Toast.LENGTH_SHORT).show();
                } else
                    sendToEmail();
                return true;
            case R.id.contactSMS:
                if (phoneNumber.equals("")) {
                    Toast.makeText(this, "No Number found", Toast.LENGTH_SHORT).show();
                } else
                    sendToSMS();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

//    @Override
//    public boolean onContextItemSelected(@NonNull MenuItem item) {
//        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//
//        switch (item.getItemId()) {
//            case R.id.contactDelete:
//                deleteContact(info);
//                return true;
//            case R.id.contactMail:
//                if (email.equals("")) {
//                    Toast.makeText(this, "No Email found", Toast.LENGTH_SHORT).show();
//                } else
//                    sendToEmail();
//                return true;
//            case R.id.contactSMS:
//                if (phoneNumber.equals("")) {
//                    Toast.makeText(this, "No Number found", Toast.LENGTH_SHORT).show();
//                } else
//                    sendToSMS();
//                return true;
//            default:
//                return super.onContextItemSelected(item);
//        }
//    }

    private void sendToSMS() {
        Intent sendSMS = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phoneNumber, null));
        sendSMS.putExtra("sms_body", toDo.getName() + " " + toDo.getDescription());
        startActivity(sendSMS);
    }

    private void sendToEmail() {
        Intent sendEmail = new Intent(Intent.ACTION_SEND);
        sendEmail.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        sendEmail.putExtra(Intent.EXTRA_SUBJECT, toDo.getName());
        sendEmail.putExtra(Intent.EXTRA_TEXT, toDo.getDescription());
        sendEmail.setType("message/rfc822");
        startActivity(sendEmail);
    }

    private void selectAndAddContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(pickContactIntent, REQCODE_ADD_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        handleAddContactActivity(requestCode, resultCode, data);

        if (requestCode != REQCODE_ADD_CONTACT) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleAddContactActivity(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQCODE_ADD_CONTACT) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri contactId = data.getData();

                    Cursor cursor = getContentResolver().query(contactId, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        String contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String internalContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));

                        if (toDo.getContacts() == null) {
                            toDo.setContacts(new ArrayList<>());
                        }

                        if (toDo.getContacts().indexOf(internalContactId) == -1) {
                            toDo.getContacts().add(internalContactId);
                        }

                        loadContactDetailsFromTodo();
                    }
                }
            }
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

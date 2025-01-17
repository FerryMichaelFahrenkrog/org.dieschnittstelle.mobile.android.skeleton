package org.dieschnittstelle.mobile.android.skeleton;

import static org.dieschnittstelle.mobile.android.skeleton.MainActivity.CALL_CONTACT_PICKER;

import android.Manifest;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {
    public static final String ARG_ITEM = "item";                               // Zur Objektentgegennahme
    public static final String ARG_TODO_INDEX = "todoIndex";
    public static final String ARG_TODO_DELETE = "todoDelete";
    public static final String ARG_TODO_DATETIME = "todoDatetime";

    private ToDo toDo;
    private EditText editTextDatum;
    private EditText ediTextUhrzeit;
    private Button deleteButton;
    private String errorStatus;

    private boolean itemDelete = false;
    private int todoIndex;

    private LocalDateTime platzhalter = LocalDateTime.now();
    private LocalDateTime TodoDateTime = platzhalter;

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private ArrayList<String> contactList;
    private ContactsToListItemAdapter contactListAdapter;
    private ListView contactListView;
    private String telefonnummer = "";
    private String emailAdresse = "";
    private ActivityDetailviewBinding detailviewBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Öffne Layout für die DetailView
        detailviewBinding = DataBindingUtil.setContentView(this, R.layout.activity_detailview);

        // LocalDateTime Übertragung
        localDateTimeTransfer();

        ediTextUhrzeit = findViewById(R.id.editTextTime);
        ediTextUhrzeit.setText(TodoDateTime.format(TIME_FORMATTER));
        ediTextUhrzeit.setInputType(InputType.TYPE_NULL);
        ediTextUhrzeit.setOnClickListener(v -> setTimePickerDialog());

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
                        }, CALL_CONTACT_PICKER));
                builder.show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_CONTACTS},
                        CALL_CONTACT_PICKER);
            }
        } else {
            loadContactDetailsFromTodo();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CALL_CONTACT_PICKER) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadContactDetailsFromTodo();
            } else {
                Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void localDateTimeTransfer() {
        if (getIntent().getSerializableExtra(ARG_TODO_DATETIME) != null) {
            TodoDateTime = (LocalDateTime) getIntent().getSerializableExtra(ARG_TODO_DATETIME);
        }
    }

    public void setTimePickerDialog() {
        int hour = Integer.parseInt(ediTextUhrzeit.getText().toString().substring(0, 2));
        int minutes = Integer.parseInt(ediTextUhrzeit.getText().toString().substring(3, 5));

        TimePickerDialog timePickerDialog = new TimePickerDialog(DetailviewActivity.this, R.style.Theme_MaterialComponents_Light_Dialog, (view, hourOfDay, minuteOfHour) -> {
            TodoDateTime = this.platzhalter
                    .withHour(hourOfDay)
                    .withMinute(minuteOfHour);
            ediTextUhrzeit.setText(TodoDateTime.format(TIME_FORMATTER));
        }, hour, minutes, true);
        timePickerDialog.show();
    }

    public void setDatePickerDialog() {
        int day = Integer.parseInt(editTextDatum.getText().toString().substring(0, 2));
        int month = Integer.parseInt(editTextDatum.getText().toString().substring(3, 5));
        int year = Integer.parseInt(editTextDatum.getText().toString().substring(6, 10));
        DatePickerDialog datePickerDialog = new DatePickerDialog(DetailviewActivity.this, R.style.Theme_MaterialComponents_Light_Dialog, (view, yearOfYear, monthOfYear, dayOfMonth) -> {
            TodoDateTime = this.platzhalter
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
        String hour = ediTextUhrzeit.getText().toString().substring(0, 2);
        String minute = ediTextUhrzeit.getText().toString().substring(3, 5);
        String day = editTextDatum.getText().toString().substring(0, 2);
        String month = editTextDatum.getText().toString().substring(3, 5);
        String year = editTextDatum.getText().toString().substring(6, 10);

        TodoDateTime = this.platzhalter
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
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.detailview_contact_context, menu);
    }

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
                errorStatus = "Name ist zu kurz!";
                detailviewBinding.setController(this);
            }
        }
    }

    private void deleteContact(AdapterView.AdapterContextMenuInfo info)
    {
        Log.i("Größe vorher: " , String.valueOf(contactList.size()));

        contactList.remove(info.position);
        Log.i("Größe nachher: " , String.valueOf(contactList.size()));

        toDo.getContacts().remove(info.position);
        contactListAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detailview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.addContact:
                selectAndAddContact();                                      // Kontakt auswählen, bzw. hinzufügen
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch (item.getItemId()) {
            case R.id.contactDelete:
                deleteContact(info);
                return true;
            case R.id.contactMail:
                if (emailAdresse.equals("")) {
                    Toast.makeText(this, "Keine E-Mail vorhanden", Toast.LENGTH_SHORT).show();
                } else
                    sendToEmail();
                return true;
            case R.id.contactSMS:
                if (telefonnummer.equals("")) {
                    Toast.makeText(this, "Keine Handynummer vorhanden", Toast.LENGTH_SHORT).show();
                } else
                    sendToSMS();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void selectAndAddContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI); // Wir wollen etwas auswählen, Identifikator für Kontakte
        startActivityForResult(pickContactIntent, CALL_CONTACT_PICKER);
    }

    //Das Ergebnis nehme ich hier entgegen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        addSelectedContactToContracts(requestCode, resultCode, data);

        // Wenn CALL_CONTACT_PICKER gewählt wurde mache das
        if (requestCode != CALL_CONTACT_PICKER) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //03.06.2020 VK!
    private void addSelectedContactToContracts(int requestCode, int resultCode, Intent data) {
        if (requestCode == CALL_CONTACT_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri contactId = data.getData();

                    Cursor cursor = getContentResolver().query(contactId, null, null, null, null); // Lies Basisinfos für Kontakt aus --> dafür Query
                    if (cursor.moveToFirst()) { // Über Ergebnisliste iterieren
                        String internalContactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID)); // Brauch ich um später auf die Telefonnr und Email zuzugreifen

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

    //VK vom 10.06.2020
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
                        telefonnummer = number;
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
                    emailAdresse = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
                }

                contactList.add("Kontakt: " + contactName);
            }
        }
        contactListAdapter.notifyDataSetChanged();
    }

    private void sendToSMS() {
        Intent smsIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", telefonnummer, null));
        smsIntent.putExtra("sms_body", toDo.getName() + " " + toDo.getDescription());
        startActivity(smsIntent);
    }

    private void sendToEmail() {
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "subject of email");
        i.putExtra(Intent.EXTRA_TEXT   , "body of email");
        try {
            startActivity(Intent.createChooser(i, "Send mail"));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "There are no email applications installed.", Toast.LENGTH_SHORT).show();
        }
    }

    public String getErrorStatus() {
        return errorStatus;
    }
}

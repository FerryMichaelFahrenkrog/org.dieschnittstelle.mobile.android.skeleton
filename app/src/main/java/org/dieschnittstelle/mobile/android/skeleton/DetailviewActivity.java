package org.dieschnittstelle.mobile.android.skeleton;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewBinding;

import java.time.LocalDate;
import java.util.Calendar;

import model.ToDo;

public class DetailviewActivity extends AppCompatActivity {

    public static final int ACTION_PICK_CONTACT = 0;

    public static final String ARG_ITEM = "item";

    private ToDo item;
    private ActivityDetailviewBinding dataBindingHandle;
    private String errorStatus;

    TextView tvDate;
    EditText etDate;

    DatePickerDialog.OnDateSetListener setListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //Zustand wiedeherstellen wenn man das Handy dreht

//        if(!((ToDo)getApplication()).isServerAva)

        this.dataBindingHandle = DataBindingUtil.setContentView(this, R.layout.activity_detailview); // braucht man bei DB

        item = (ToDo)getIntent().getSerializableExtra(ARG_ITEM);

        if(item == null){
            item = new ToDo(); // Leeres DataItem
        }

        Log.i("DetailviewActivity", "got contact ids: " + item.getContactIds());
        item.getContactIds().forEach(id -> {
            showContactDetailsForInternal(Long.parseLong(id));
        });

        this.dataBindingHandle.setController(this);

        tvDate = findViewById(R.id.txtTime);
        etDate = findViewById(R.id.editTextTimePicker);


        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        tvDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    DetailviewActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, setListener, year, month, day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        setListener = (view, year12, month12, dayOfMonth) -> {
            month12 = month12 + 1;
            String date = day+"/"+ month12 +"/"+ year12;
            tvDate.setText(date);


            Toast.makeText(this, "BLAAAAAA", Toast.LENGTH_SHORT).show();

        };

        etDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(DetailviewActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year1, int month1, int day1) {
                    month1 = month1 +1 ;
                    String date = day1 +"/"+ month1 +"/"+ year1;
                    etDate.setText(date);
                }
            },year, month, day);
            datePickerDialog.show();
        });
    }

    public void onSaveItem(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ARG_ITEM, item);

        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    public ToDo getItem() {
        return item;
    }

    public void setItem(ToDo item) {
        this.item = item;
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

            if(!item.getContactIds().contains(String.valueOf(contactId))){
                item.getContactIds().add(String.valueOf(internalContactId));
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
            String name = item.getName();

            if(name != null && name.length() >= 3){
                Log.i("DetailviewActivity", "validationSuccessful: " + item.getName());
                errorStatus = null;
            }
            else{
                Log.i("DetailviewActivity", "validation failed" + item.getName());
                errorStatus = "Name too short!";
                dataBindingHandle.setController(this);
            }

        }
    }

    public void onNameInputChanged(){
        if(errorStatus != null){
            errorStatus = null;
            dataBindingHandle.setController(this);
        }
    }

    public String getErrorStatus() {
        return errorStatus;
    }

    protected void sendSms() {
        Uri smsUri = Uri.parse("smsto:000000");
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, smsUri);
        smsIntent.putExtra("sms body", item.getName() + ": " + item.getDescription());
        startActivity(smsIntent);
    }

}

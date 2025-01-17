package org.dieschnittstelle.mobile.android.skeleton;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static java.lang.Boolean.FALSE;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListitemBinding;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import impl.SyncedDataItemCRUDOperationsImpl;
import model.IDataItemCRUDOperations;
import model.ToDo;
import model.User;
import tasks.AuthenticateUserTask;
import tasks.CheckWebapiAvailableTask;
import tasks.CreateTodosTask;
import tasks.DeleteAllToDosTask;
import tasks.DeleteTodosTask;
import tasks.ReadAllToDoTask;
import tasks.UpdateToDoTaskWithFuture;

public class MainActivity extends AppCompatActivity {               // macht die Klasse zu einer Activity
    private static String logtag = "MainActivity: ";                // Logger zur Ausgabe

    //Bedienelemente definieren
    private ListView listView;                                      // Liste in der alle ToDos drin sind
    private final List<ToDo> items = new ArrayList<>();             // Hier sind die ToDos drin

    private ArrayAdapter<ToDo> listViewAdapter;                     // Über einen Arrayadapter werden listenförmige Datensammlungen mit einem AdapterView verbunden. Ein AdapterView ist ein View Element dessen Kinder von einem Adapter vorgegeben werden. Ein AdapterView wird übeer einen Adapter mit einer Datenquelle verbunden. Über den Adapter erhält der AdapterView Zugang zu den Elementen der Datenquelle

    private ProgressBar progressBar;
    private AlertDialog dialog;
    private ProgressBar progressBarLogin;

    private FloatingActionButton addNewItemButton;
    private EditText username;
    private EditText password;

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;        // Damit sage ich der "startActivityForResult" Methode, dass ich etwas erzeugen will
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;          // Damit sage ich der "startActivityForResult" Methode, dass ich etwas editieren will
    public static final int CALL_CONTACT_PICKER = 3;

    private TextView loginErrorText;
    private boolean emailError;
    private boolean loginError;

    private Comparator<ToDo> currentComparisionMode = ToDo.importanceBeforeDate;  // Aktueller Zustand der Sortierung

    private IDataItemCRUDOperations crudOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.progressBar);
        addNewItemButton = findViewById(R.id.addNewItemButton);
        listView = findViewById(R.id.listView);

        RoomLocalDataItemCRUDOperationsImpl roomTodoCRUDOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
        RetrofitRemoteDataItemCRUDOperationsImpl retrofitTodoCRUDOperations = new RetrofitRemoteDataItemCRUDOperationsImpl();
        crudOperations = new SyncedDataItemCRUDOperationsImpl(roomTodoCRUDOperations, retrofitTodoCRUDOperations);

        new CheckWebapiAvailableTask(webapiAvailable -> {
            ((SyncedDataItemCRUDOperationsImpl) crudOperations).setConnectionStatus(webapiAvailable);

            if (webapiAvailable) {
                Toast.makeText(getApplicationContext(), "Webanwendung ist da :)", Toast.LENGTH_SHORT).show();
                showLoginDialog();
            } else {
                Toast.makeText(getApplicationContext(), "WEB API is not available", Toast.LENGTH_SHORT).show();
            }
        }).execute();

        listViewAdapter = new ToDoAdapter(this, R.layout.activity_main_listitem, items);
        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            oeffneDetailansichtFuer(items.get(position), position, items.get(position).getFinishDate()); // Bei Klick auf ein Item öffnet sich die Detailansicht (mit Vorbefüllung)
        });

        addNewItemButton.setOnClickListener(v -> this.erzeugeNeuesToDo()); // Bei Klick auf den New Button wird ein neues To Do erstellt (ohne Vorbefüllung)

        new ReadAllToDoTask(progressBar, crudOperations, toDos -> {
            listViewAdapter.addAll(toDos);
            sortitems(items);
        }).execute();
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.login_dialog2, null)).setPositiveButton("Login", null);

        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();

        dialog.getButton(BUTTON_POSITIVE).setOnClickListener(v -> {
//        btnLogin.setOnClickListener(v -> {
            String usernameInput = username.getText().toString();
            String passwordInput = password.getText().toString();

            if (istFormalKorrektesPasswort(passwordInput)) {
                User currentUser = new User(usernameInput, passwordInput);

                new AuthenticateUserTask(progressBarLogin, crudOperations, isAuthenticated -> {
                    if (isAuthenticated) {
//                        Toast.makeText(getApplicationContext(), "Da ist das Ding!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        loginErrorText.setText("Invalid username and/or password.");
                        loginError = true;
                    }
                }).execute(currentUser);
            } else {
                loginErrorText.setText("Password too short.");
                loginError = true;
            }
        });

        dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
//        btnLogin.setEnabled(false);
        username = dialog.findViewById(R.id.username);
        password = dialog.findViewById(R.id.password);
        loginErrorText = dialog.findViewById(R.id.loginError);
        progressBarLogin = dialog.findViewById(R.id.progressBarLogin);


        username.setOnFocusChangeListener((v, hasFocus) -> fokusChangeMeldung(v, hasFocus));
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (emailError || loginError) {
                    loginErrorText.setText("");
                    emailError = false;
                    loginError = false;
                }
                if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(true);
//                    btnLogin.setEnabled(true);
                } else {
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
//                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.setOnFocusChangeListener((v, hasFocus) -> fokusChangeMeldung(v, hasFocus));
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginError) {
                    loginErrorText.setText("");
                    loginError = false;
                }
                if (!username.getText().toString().equals("") && !password.getText().toString().equals("")) {
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(true);
//                    btnLogin.setEnabled(true);
                } else {
                    dialog.getButton(BUTTON_POSITIVE).setEnabled(false);
//                    btnLogin.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void fokusChangeMeldung(View v, boolean hasFocus) {
        if (!hasFocus) {
            verifiziereDenInput();
        }
    }

    private void verifiziereDenInput() {
        String usernameInput = username.getText().toString();

        if (usernameInput.equals("")) {
            loginErrorText.setText("");
        } else if (!istFormalKorrekteEmail(usernameInput)) {
            loginErrorText.setText("Der User-Name ist keine gültige E-Mail-Adresse!");
            loginErrorText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            loginErrorText.setTextColor(Color.RED);
            emailError = true;
        } else {
            loginErrorText.setText("");
        }
    }

    private boolean istFormalKorrekteEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    private boolean istFormalKorrektesPasswort(String password) {
        String emailRegex = "^[0-9]{6}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (password == null)
            return false;
        return pat.matcher(password).matches();
    }

    protected void oeffneDetailansichtFuer(ToDo itemName, int index, LocalDateTime localDateTime) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName);                       // In das ARG_ITEM wird unser To Do was wir übergeben reingepackt.
        detailviewIntent.putExtra(DetailviewActivity.ARG_TODO_INDEX, index);
        detailviewIntent.putExtra(DetailviewActivity.ARG_TODO_DATETIME, localDateTime);
        this.startActivityForResult(detailviewIntent, CALL_DETAILVIEW_FOR_EDIT);                // Wir übergeben das Intent und sagen, dass wir auf eine Rückgabe warten
    }

    protected void erzeugeNeuesToDo() {
        Intent detailviewForCreateIntent = new Intent(this, DetailviewActivity.class);
        detailviewForCreateIntent.putExtra(DetailviewActivity.ARG_TODO_DATETIME, (LocalDateTime) null);

        startActivityForResult(detailviewForCreateIntent, CALL_DETAILVIEW_FOR_CREATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {                   // Diese Methode reagiert auf die Rückgaben, die wir über onActivityResult bekommen
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALL_DETAILVIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {                                                             // Wenn etwas eingegeben wurde, dann rufe die Methode unten auf und übergebe das Item aus ARG_ITEM
                ToDo neuesToDoItem = (ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);
                LocalDateTime localDateTime = (LocalDateTime) data.getSerializableExtra(DetailviewActivity.ARG_TODO_DATETIME);
                neuesToDoItem.setFinishDate(localDateTime);
//                showFeedbackMessage("created new item " + neuesToDoItem.getFinishDate());                   // Hier kommt schon ein falsches Datum an!!!!!!!!!!!!!!!!!
                showFeedbackMessage("created new item " + neuesToDoItem.getName());                   // Hier kommt schon ein falsches Datum an!!!!!!!!!!!!!!!!!
                resortList();
                onNewItemCreated(neuesToDoItem);                                                                // Meine onXXX - Methoden werden zur Mainactivity zurückgegeben
            } else {
                showFeedbackMessage("Die To-Do-Erstellung wurde abgebrochen");           // Ansonsten ist quasi nichts passiert, trotzdem ne kleine Message zur Kontrolle
            }
        } else if (requestCode == CALL_DETAILVIEW_FOR_EDIT) {                                                    // In diesem Fall wird auf ein Item bearbeitet, also per Doppelklick editiert.
            if (resultCode == Activity.RESULT_OK) {

                // Wenn es geupdatet wurde (RESULT_OK), dann schreib die Daten in das Edited Item und übergebe das der unteren Funktion
                ToDo editedItem = (ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);

                if (editedItem == null) {
                    showFeedbackMessage("WERT IST NULL");
                }

                if (data.getBooleanExtra(DetailviewActivity.ARG_TODO_DELETE, FALSE)) {
                    deleteItemAndUpdateList(editedItem);
                } else {
                    LocalDateTime localDateTime = (LocalDateTime) data.getSerializableExtra(DetailviewActivity.ARG_TODO_DATETIME);
                    editedItem.setFinishDate(localDateTime);
                    updateItemAndUpdateList(editedItem);
                }
            } else {
                showFeedbackMessage("Returning from detailview for edit with: " + resultCode);
            }
        } else {
            showFeedbackMessage("Returning from detailview with " + resultCode);
        }
    }

    protected void onNewItemCreated(ToDo item) {
//        showFeedbackMessage("created new item " + item.getFinishDate());
        // TODO: 31.01.2022 schauen Sie mal Herr Kreutel --> Hier kommt das FinishDate schon nicht mehr an. Unschaffbar
        new CreateTodosTask(progressBar, crudOperations, created -> {
            items.add(created);
//            showFeedbackMessage("!!!!!!!: " + item.getFinishDate());
            MainActivity.this.sortListAndScrollToItem(created);
            resortList();
        }).execute(item);
    }

    protected void updateItemAndUpdateList(ToDo changedItem) {
        Toast.makeText(getApplicationContext(), "Update für: " + changedItem.getName(), Toast.LENGTH_SHORT).show();

//        new UpdateToDosTask(progressBar, crudOperations, updated -> {
//            handleResultFromUpdateTask(changedItem, updated);
//        }).execute(changedItem);

        new UpdateToDoTaskWithFuture(this, crudOperations).execute(changedItem).thenAccept(updated -> {
            resortList();
            handleResultFromUpdateTask(changedItem, updated);
        });
    }

    private void deleteItemAndUpdateList(ToDo editedItem) {
        new DeleteTodosTask(progressBar, crudOperations, deleted -> {
            if (deleted) {
                items.remove(editedItem);
                resortList();
                listViewAdapter.notifyDataSetChanged();
                showFeedbackMessage("Gelöscht: " + editedItem.getName());
            }
        }).execute(editedItem);
    }

    private void handleResultFromUpdateTask(ToDo changedItem, boolean updated) {

        if (updated) {
            int existingItemInListPost = listViewAdapter.getPosition(changedItem);
            if (existingItemInListPost > -1) {
                ToDo existingItem = listViewAdapter.getItem(existingItemInListPost);
                existingItem.setName(changedItem.getName());
                existingItem.setDescription(changedItem.getDescription());
                existingItem.setChecked(changedItem.isChecked());
                existingItem.setFinishDate(changedItem.getFinishDate());
                existingItem.setFinishDateLong(changedItem.getFinishDateLong());
                existingItem.setFavouriteToDo(changedItem.isFavouriteToDo());
                existingItem.setLocaldate(changedItem.getLocaldate());
                Toast.makeText(getApplicationContext(), "test " + changedItem.getName(), Toast.LENGTH_SHORT).show();
                listViewAdapter.notifyDataSetChanged();
            } else {
                showFeedbackMessage("Updated: " + changedItem.getName() + " cannot found in list");
            }
        }
    }

    public void onListItemChangedInList(ToDo toDo) {
        new UpdateToDoTaskWithFuture(this, crudOperations)
                .execute(toDo)
                .thenAccept((updated) -> {
                    showFeedbackMessage("ToDo: " + toDo.getName() + " wurde geupdated!");
                    sortListAndScrollToItem(toDo);
                    resortList();
                });
    }

    //Options Menu & Sorting
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menues :)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        RoomLocalDataItemCRUDOperationsImpl roomTodoCRUDOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
        RetrofitRemoteDataItemCRUDOperationsImpl retrofitTodoCRUDOperations = new RetrofitRemoteDataItemCRUDOperationsImpl();

        switch (item.getItemId()) {
            case R.id.deleteLocalItems:
                new DeleteAllToDosTask(progressBar, roomTodoCRUDOperations, v -> {
                    Toast.makeText(getApplicationContext(), "Die lokalen ToDos wurden gelöscht", Toast.LENGTH_SHORT).show();
                    items.clear();
                    listViewAdapter.notifyDataSetChanged();
                }).execute();
                return true;
            case R.id.deleteRemoteItems:
                new DeleteAllToDosTask(progressBar, retrofitTodoCRUDOperations, v -> {
                    Toast.makeText(getApplicationContext(), "Die remote ToDos wurden gelöscht", Toast.LENGTH_SHORT).show();
                }).execute();
                return true;
            case R.id.SyncTodos:
                SyncedDataItemCRUDOperationsImpl syncTodoCRUDOperations = new SyncedDataItemCRUDOperationsImpl(roomTodoCRUDOperations, retrofitTodoCRUDOperations);
                new ReadAllToDoTask(progressBar, syncTodoCRUDOperations, v -> {
                    Toast.makeText(getApplicationContext(), "Sync wurde durchgeführt", Toast.LENGTH_SHORT).show();
                    items.clear();
                    items.addAll(v);
                    resortList();
                    listViewAdapter.notifyDataSetChanged();
                }).execute();
                return true;
            case R.id.sortItems:
                sortListAndScrollToItem(null);
                return true;
            case R.id.sort_wichtigkeitDatum:
//                sortListAndScrollToItemByWichtigkeitDatum();
                resortList();
//                sortWichtigkeitDatum();
                return true;
            case R.id.sort_DatumWichtigkeit:
                sortListAndScrollToItemByDatumWichtigkeit();
//                sortDatumWichtigkeit();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void sortListAndScrollToItem(ToDo item) {
        sortitems(items);

        listViewAdapter.notifyDataSetChanged(); //Aktualisierung
        if (item != null) {
            int pos = listViewAdapter.getPosition(item);
            listView.setSelection(pos);
        }
    }

    protected void sortListAndScrollToItemByDatumWichtigkeit() {
        sortByDatumWichtigkeit(items);

        listViewAdapter.notifyDataSetChanged();
    }

    private class ToDoAdapter extends ArrayAdapter<ToDo> {
        private int layoutResource;

        public ToDoAdapter(@NonNull Context context, int resource, @NonNull List<ToDo> objects) {
            super(context, resource, objects);
            layoutResource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {
            Log.i(logtag, "getView(): for position " + position + " , and existingView: " + existingView);

            ActivityMainListitemBinding binding = null;
            View currentView = null;

            if (existingView != null) {
                currentView = existingView;
                binding = (ActivityMainListitemBinding) existingView.getTag();
            } else {
                binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.activity_main_listitem, null, false);
                currentView = binding.getRoot();
                currentView.setTag(binding);
            }

            ToDo toDo = getItem(position);
            binding.setItem(toDo);
            binding.setController(MainActivity.this);

            TextView itemName = binding.getRoot().findViewById(R.id.itemName);

            if (toDo.getFinishDate().isAfter(LocalDateTime.now())) {
                itemName.setTextColor(Color.BLACK);
            } else {
                itemName.setTextColor(Color.RED);
            }

            return currentView;
        }
    }

    public void resortList() {
        Collections.sort(items, currentComparisionMode);
        listViewAdapter.notifyDataSetChanged();
    }

    private void sortitems(List<ToDo> items) {
        items.sort(Comparator.comparing(ToDo::isChecked).thenComparing(ToDo::getName));
        resortList();
    }



    private void sortByDatumWichtigkeit(List<ToDo> items) {
        items.sort(Comparator.comparing(ToDo::isChecked).thenComparing(ToDo::getFinishDate).thenComparing(ToDo::isFavouriteToDo));
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }
    //    public void sortWichtigkeitDatum()
//    {
//            currentComparisionMode = ToDo.importanceBeforeDate;
//            resortList();
//
//    }
//
//    public void sortDatumWichtigkeit()
//    {
//            currentComparisionMode = dateBeforeImportance;
//            resortList();
//    }

//    private void sortByWichtigkeitDatum(List<ToDo> items) {
//        items.sort(Comparator.comparing(ToDo::isChecked).thenComparing(ToDo::isFavouriteToDo).thenComparing(ToDo::getFinishDate));
//    }
}
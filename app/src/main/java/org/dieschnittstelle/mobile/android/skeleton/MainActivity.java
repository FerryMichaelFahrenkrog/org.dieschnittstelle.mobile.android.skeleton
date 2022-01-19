package org.dieschnittstelle.mobile.android.skeleton;

import static java.lang.Boolean.FALSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityMainListitemBinding;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import impl.RetrofitRemoteDataItemCRUDOperationsImpl;
import impl.RoomLocalDataItemCRUDOperationsImpl;
import impl.SyncedDataItemCRUDOperationsImpl;
import model.IDataItemCRUDOperations;
import model.ToDo;
import tasks.CreateTodosTask;
import tasks.DeleteAllToDosTask;
import tasks.DeleteTodosTask;
import tasks.ReadAllToDoTask;
import tasks.UpdateToDoTaskWithFuture;
import tasks.UpdateToDosTask;

public class MainActivity extends AppCompatActivity {               // macht die Klasse zu einer Activity
    private static String logtag = "MainActivity: ";                // Logger zur Ausgabe

    //Bedienelemente definieren
    private ListView listView;                                      // Liste in der alle ToDos drin sind
    private final List<ToDo> items = new ArrayList<>();             // Hier sind die ToDos drin

    private ArrayAdapter<ToDo> listViewAdapter;                     // Über einen Arrayadapter werden listenförmige Datensammlungen mit einem AdapterView verbunden. Ein AdapterView ist ein View Element dessen Kinder von einem Adapter vorgegeben werden. Ein AdapterView wird übeer einen Adapter mit einer Datenquelle verbunden. Über den Adapter erhält der AdapterView Zugang zu den Elementen der Datenquelle

    private ProgressBar progressBar;

    private FloatingActionButton addNewItemButton;

    private static final int CALL_DETAILVIEW_FOR_CREATE = 0;        // Damit sage ich der "startActivityForResult" Methode, dass ich etwas erzeugen will
    private static final int CALL_DETAILVIEW_FOR_EDIT = 1;          // Damit sage ich der "startActivityForResult" Methode, dass ich etwas editieren will
    public static final int REQCODE_ADD_CONTACT = 21;


    private IDataItemCRUDOperations crudOperationsNormal;            // ??
    private IDataItemCRUDOperations crudOperations;            // ??

    private final LoginActivity loginActivity = new LoginActivity();
    private boolean SKIP_LOGIN = false;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);                     // setzen der Hauptansicht (Layout)

        //1. Access view elements
        progressBar = findViewById(R.id.progressBar);
        addNewItemButton = findViewById(R.id.addNewItemButton);
        listView = findViewById(R.id.listView);

        listViewAdapter = new ToDoAdapter(this, R.layout.activity_main_listitem, items); // Der Adapter erhält die Daten & die Art der Darstellung über das Layout.
        listView.setAdapter(listViewAdapter);

        //2. Prepare Elements 4 Interaction
        listView.setOnItemClickListener((parent, view, position, id) -> {
//            ToDo selectedItem = listViewAdapter.getItem(position);
            oeffneDetailansichtFuer(items.get(position), position, items.get(position).getFinishDate());                                              // Bei Klick auf ein Item öffnet sich die Detailansicht (mit Vorbefüllung)
        });

        addNewItemButton.setOnClickListener(v -> this.erzeugeNeuesToDo());                      // Bei Klick auf den New Button wird ein neues To Do erstellt (ohne Vorbefüllung)

        RoomLocalDataItemCRUDOperationsImpl roomTodoCRUDOperations = new RoomLocalDataItemCRUDOperationsImpl(this);
        RetrofitRemoteDataItemCRUDOperationsImpl retrofitTodoCRUDOperations = new RetrofitRemoteDataItemCRUDOperationsImpl();

        crudOperations = new SyncedDataItemCRUDOperationsImpl(roomTodoCRUDOperations, retrofitTodoCRUDOperations);

        new ReadAllToDoTask(progressBar, crudOperations, toDos -> {
            listViewAdapter.addAll(toDos);
            sortitems(items);
        }).execute();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void oeffneDetailansichtFuer(ToDo itemName, int index, LocalDateTime localDateTime) {
        Intent detailviewIntent = new Intent(this, DetailviewActivity.class);
        detailviewIntent.putExtra(DetailviewActivity.ARG_ITEM, itemName);                       // In das ARG_ITEM wird unser To Do was wir übergeben reingepackt.
        detailviewIntent.putExtra(DetailviewActivity.ARG_TODO_INDEX, index);
        detailviewIntent.putExtra(DetailviewActivity.ARG_TODO_DATETIME, localDateTime);
        this.startActivityForResult(detailviewIntent, CALL_DETAILVIEW_FOR_EDIT);                // Wir übergeben das Intent und sagen, dass wir auf eine Rückgabe warten
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void erzeugeNeuesToDo() {
        Intent detailviewForCreateIntent = new Intent(this, DetailviewActivity.class);
        detailviewForCreateIntent.putExtra(DetailviewActivity.ARG_TODO_DATETIME, (LocalDateTime) null);

        startActivityForResult(detailviewForCreateIntent, CALL_DETAILVIEW_FOR_CREATE);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {                   // Diese Methode reagiert auf die Rückgaben, die wir über onActivityResult bekommen
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CALL_DETAILVIEW_FOR_CREATE) {
            if (resultCode == Activity.RESULT_OK) {                                                             // Wenn etwas eingegeben wurde, dann rufe die Methode unten auf und übergebe das Item aus ARG_ITEM
                ToDo neuesToDoItem = (ToDo) data.getSerializableExtra(DetailviewActivity.ARG_ITEM);
                LocalDateTime localDateTime = (LocalDateTime) data.getSerializableExtra(DetailviewActivity.ARG_TODO_DATETIME);
                neuesToDoItem.setFinishDate(localDateTime);
                onNewItemCreated(neuesToDoItem);                                                                // Meine onXXX - Methoden werden zur Mainactivity zurückgegeben
            } else {
                showFeedbackMessage("Die Itemerstellung wurde abgebrochen");           // Ansonsten ist quasi nichts passiert, trotzdem ne kleine Message zur Kontrolle
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
                }                                                                 // Meine onXXX - Methoden werden zur Mainactivity zurückgegeben
            } else {
                showFeedbackMessage("Returning from detailview for edit with: " + resultCode);
            }
        } else {
            showFeedbackMessage("Returning from detailview with " + resultCode);
        }
    }


    @SuppressLint("NewApi")
    protected void onNewItemCreated(ToDo item) {                                                                // HIER CER CREATE TO DO TASK!!!!!!
        showFeedbackMessage("created new item " + item);

        new CreateTodosTask(progressBar, crudOperations, created -> {
            items.add(created);
            MainActivity.this.sortListAndScrollToItem(created);
        }).execute(item);
    }

    protected void updateItemAndUpdateList(ToDo changedItem) {


        Toast.makeText(getApplicationContext(), "HINWEIS: " + changedItem.getName(), Toast.LENGTH_SHORT).show();
//        new UpdateToDosTask(progressBar, crudOperations, updated -> {
//            handleResultFromUpdateTask(changedItem, updated);
//        }).execute(changedItem);

        new UpdateToDoTaskWithFuture(this, crudOperations).execute(changedItem).thenAccept(updated -> {
            handleResultFromUpdateTask(changedItem, updated);
        });
    }

    private void deleteItemAndUpdateList(ToDo editedItem) {
        new DeleteTodosTask(progressBar, crudOperations, deleted -> {
            if (deleted) {
                items.remove(editedItem);
                listViewAdapter.notifyDataSetChanged();
// was muss ich hier machen, damit das Item auch wirklich entfernt wird?
                showFeedbackMessage("DELETED: " + editedItem.getName());
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
                    showFeedbackMessage("Item " + toDo.getName() + " HAS BEEEN UPDATED JAA");
                    sortListAndScrollToItem(toDo);
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
//                SyncedDataItemCRUDOperationsImpl syncTodoCRUDOperations = new SyncedDataItemCRUDOperationsImpl(roomTodoCRUDOperations, retrofitTodoCRUDOperations);
//                new ReadAllToDoTask(progressBar, syncTodoCRUDOperations, v -> {
//                    Toast.makeText(getApplicationContext(), "Listen wurden synchronisiert", Toast.LENGTH_SHORT).show();
//                    items.clear();
//                    items.addAll(v);
//                }).execute();
//                return true;
            case R.id.sortItems:
                sortListAndScrollToItem(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void sortListAndScrollToItem(ToDo item) {
        showFeedbackMessage("Sort List!");
        sortitems(items);

        listViewAdapter.notifyDataSetChanged(); //Aktualisierung
        if (item != null) {
            int pos = listViewAdapter.getPosition(item);
            listView.setSelection(pos);
        }
    }

    private class ToDoAdapter extends ArrayAdapter<ToDo> {
        private int layoutResource;

        public ToDoAdapter(@NonNull Context context, int resource, @NonNull List<ToDo> objects) {
            super(context, resource, objects);
            layoutResource = resource;
        }

        @SuppressLint("NewApi")
        @NonNull
        @Override
        public View getView(int position, @Nullable View recycleableItemView, @NonNull ViewGroup parent) {
            Log.i(logtag, "getView(): for position " + position + " , and recycleableItemView: " + recycleableItemView);

            View itemView = null;
            ToDo currentItem = getItem(position);   // Hier wird die korrekte Stelle ausgelesen

//            if(currentItem.getFaelligkeitsDatum().isBefore(LocalDateTime.now()) && !currentItem.isChecked()){
//                itemView.setBackgroundColor(Color.RED);
//            }

            if (recycleableItemView != null) {
                TextView textView = (TextView) recycleableItemView.findViewById(R.id.itemName);
                if (textView != null) {
                    Log.i(logtag, "getView(): itemName in convertView: " + textView);
                }
                itemView = recycleableItemView;
                ActivityMainListitemBinding recycleBinding = (ActivityMainListitemBinding) itemView.getTag();
                recycleBinding.setItem(currentItem);
            } else {
                ActivityMainListitemBinding currentBinding =
                        DataBindingUtil.inflate(getLayoutInflater(),
                                this.layoutResource,
                                null,
                                false);

                currentBinding.setItem(currentItem);
                currentBinding.setController(MainActivity.this);

                itemView = currentBinding.getRoot();
                itemView.setTag(currentBinding);
            }
            return itemView;
        }
    }

    private void sortitems(List<ToDo> items) {
        items.sort(Comparator.comparing(ToDo::isChecked).thenComparing(ToDo::getName)); // rufe die Methode der Klasse auf, Rückgabe = name!
        //TODO: Hier z.B. mal ne Sortierung nach Fälligkeit --> Ich müsste dann im Model das Fälligkeitsdatum abspeichern und das Fälligkeitsdatum selbst aus dem DateTimePicker auslesen
    }

    protected void showFeedbackMessage(String msg) {
        Snackbar.make(findViewById(R.id.rootView), msg, Snackbar.LENGTH_SHORT).show();
    }
}


/*
TODO:
- THREADES UND ASYNC RAUSBALLERN überall wo es drin ist. LÖSCHEN BUTTON IN DETAIL implementieren ;) 17.01 VIDEO 2x!
- Connection Klasse                                                                                 18.01 VIDEO!
- Herausfinden wie ich die Fälligkeiten abspeichere und anzeigen lassen in Main                     19.01
- Kontakte implementierejn                                                                          19.01
- Sortierungen herausfinden                                                                         20.01
- Abgleich implementieren                                                                           21.01
- FineTuning durch Anforerungen                                                                     22.01, 23.01
 */
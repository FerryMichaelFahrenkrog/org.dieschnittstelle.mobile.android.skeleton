package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class ReadAllToDoTask extends AsyncTask<Void, Void, List<ToDo>> {
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<List<ToDo>> onDoneConsumer;

    public ReadAllToDoTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<List<ToDo>> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<ToDo> doInBackground(Void... voids) {
        return crudOperations.readAllToDos();
    }


    // UI THREAD
    @Override
    protected void onPostExecute(List<ToDo> todos) {
        onDoneConsumer.accept(todos);
        progressBar.setVisibility(View.INVISIBLE);
    }
}


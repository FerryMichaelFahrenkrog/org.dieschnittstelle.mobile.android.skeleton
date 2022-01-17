package tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class ReadSingleToDosTask extends AsyncTask<Long, Void, List<ToDo>> {
    //Die Warning irgendwann beheben
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<List<ToDo>> onDoneConsumer;

    public ReadSingleToDosTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<List<ToDo>> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected List<ToDo> doInBackground(Long... ids) {
        ArrayList<ToDo> retrievedTodos = new ArrayList<>();

        for (long id : ids) {
            ToDo todo = crudOperations.readDataItem(id);
            retrievedTodos.add(todo);
        }

        return retrievedTodos;
    }

    @Override
    protected void onPostExecute(List<ToDo> todos) {
        onDoneConsumer.accept(todos);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
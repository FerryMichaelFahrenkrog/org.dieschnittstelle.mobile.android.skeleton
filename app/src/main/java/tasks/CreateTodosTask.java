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

public class CreateTodosTask extends AsyncTask<ToDo, Void, ToDo> {
    //Die Warning irgendwann beheben
    @SuppressLint("StaticFieldLeak")
    private ProgressBar progressBar;
    private IDataItemCRUDOperations crudOperations;
    private Consumer<ToDo> onDoneConsumer;

    public CreateTodosTask(ProgressBar progressBar, IDataItemCRUDOperations crudOperations, Consumer<ToDo> onDoneConsumer) {
        this.progressBar = progressBar;
        this.crudOperations = crudOperations;
        this.onDoneConsumer = onDoneConsumer;
    }

    @Override
    protected void onPreExecute() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    protected ToDo doInBackground(ToDo... toDos) {
        return crudOperations.createDataItem(toDos[0]);
    }


    @Override
    protected void onPostExecute(ToDo todo) {
        onDoneConsumer.accept(todo);
        progressBar.setVisibility(View.INVISIBLE);
    }
}

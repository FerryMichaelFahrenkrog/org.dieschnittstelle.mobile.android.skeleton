package impl;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import model.IDataItemCRUDOperations;
import model.IDataItemCRUDOperationsAsync;
import model.ToDo;

public class ThreadedDataItemCRUDOperationsAsyncImpl implements IDataItemCRUDOperationsAsync
{
    IDataItemCRUDOperations crudExecutor; // Macht die eigentliche Arbeit
    private Activity uiThreadProvider;
    private ProgressBar progressBar;

    public ThreadedDataItemCRUDOperationsAsyncImpl(IDataItemCRUDOperations crudExecutor, Activity uiThreadProvider, ProgressBar progressBar) {
        this.crudExecutor = crudExecutor;
        this.uiThreadProvider = uiThreadProvider;
        this.progressBar = progressBar;
    }

    @Override
    public void createDataItem(ToDo toDo, Consumer<ToDo> oncreated) {
        ToDo created = crudExecutor.createDataItem(toDo);
        oncreated.accept(created);
    }

    @Override
    public void readAllDataItems(Consumer<List<ToDo>> onread) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            List<ToDo> items = crudExecutor.readAllDataItems();

            uiThreadProvider.runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                onread.accept(items);
            });
        }).start();
    }

    @Override
    public void readDataItem(long id, Consumer<ToDo> onread) {

    }

    @Override
    public void updateDataItem(ToDo toDo, Consumer<ToDo> onupdated) {
        ToDo updated = crudExecutor.updateDataItem(toDo);
        onupdated.accept(updated);
    }

    @Override
    public boolean deleteDataItem(long id, Consumer<Boolean> ondeleted) {
        return false;
    }
}

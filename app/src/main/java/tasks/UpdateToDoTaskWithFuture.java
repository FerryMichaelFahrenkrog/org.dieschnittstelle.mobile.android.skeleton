package tasks;

import android.app.Activity;

import java.util.concurrent.CompletableFuture;

import model.IDataItemCRUDOperations;
import model.ToDo;

//Aus Spaß als Alternative implementiert für Chkbox
public class UpdateToDoTaskWithFuture {
    private IDataItemCRUDOperations crudOperations;
    private Activity owner;

    public UpdateToDoTaskWithFuture(Activity owner, IDataItemCRUDOperations crudOperations) {
        this.crudOperations = crudOperations;
        this.owner = owner;
    }

    public CompletableFuture<Boolean> execute(ToDo toDo) {
        CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean updated = crudOperations.updateToDo(toDo);
                owner.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resultFuture.complete(updated);
                    }
                });
            }
        }).start();
        return resultFuture;
    }
}

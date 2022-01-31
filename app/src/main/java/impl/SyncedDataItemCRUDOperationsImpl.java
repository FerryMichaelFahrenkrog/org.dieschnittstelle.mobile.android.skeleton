package impl;

import static org.dieschnittstelle.mobile.android.skeleton.ToDoApplication.checkConnectivity;

import android.util.Log;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import model.IDataItemCRUDOperations;
import model.ToDo;
import model.User;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean remoteAvailable = false;

    public SyncedDataItemCRUDOperationsImpl(IDataItemCRUDOperations localCRUD, IDataItemCRUDOperations remoteCRUD){
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }

    @Override
    public ToDo createToDo(ToDo toDo)
    {
        remoteAvailable = checkConnectivity();

        toDo = localCRUD.createToDo(toDo);

        if(remoteAvailable)
        {
            remoteCRUD.createToDo(toDo);
        }
        return toDo;
    }

    @Override
    public List<ToDo> readAllToDos() {
        remoteAvailable = checkConnectivity();
        Log.i("CONN: " , "connectionmode: " + remoteAvailable);

        List<ToDo> localTodos = localCRUD.readAllToDos();

        if (remoteAvailable) {
            List<ToDo> remoteTodos = remoteCRUD.readAllToDos();

            if (remoteTodos != null) {
                if (!localTodos.isEmpty()) {
                    for (ToDo remoteTodo : remoteTodos) {
                        remoteCRUD.deleteToDo(remoteTodo);
                    }

                    for (ToDo localTodo : localTodos) {
                        remoteCRUD.createToDo(localTodo);
                    }

                    return localTodos;
                } else {
                    for (ToDo remoteTodo : remoteTodos) {
                        localCRUD.createToDo(remoteTodo);
                    }

                    return remoteTodos;
                }
            }
        }

        return localTodos;
    }

    @Override
    public ToDo readToDo(long id) {
        return null;
    }

    @Override
    public boolean updateToDo(ToDo toDo) {
        if(!remoteAvailable){
            return localCRUD.updateToDo(toDo);
        }else{
            if(localCRUD.updateToDo(toDo)){
                return remoteCRUD.updateToDo(toDo);
            }
        }
        return false;
    }

    @Override
    public boolean deleteToDo(ToDo toDo) {
        if(!remoteAvailable)
        {
            return localCRUD.deleteToDo(toDo);
        }else{
            if(localCRUD.deleteToDo(toDo)){
                return remoteCRUD.deleteToDo(toDo);
            }
        }

        return false;
    }

    @Override
    public boolean deleteAllToDos(boolean remote) {
        if(remote){
            return remoteCRUD.deleteAllToDos(remote);
        }else{
            return localCRUD.deleteAllToDos(remote);
        }
    }

    @Override
    public boolean authenticateUser(User user) {
        if (!remoteAvailable) {
            return localCRUD.authenticateUser(user);
        } else {
            if (localCRUD.authenticateUser(user)) {
                return remoteCRUD.authenticateUser(user);
            }
        }
        return false;
    }

    public void setConnectionStatus(boolean isAvailable) {
        this.remoteAvailable = isAvailable;
    }
}

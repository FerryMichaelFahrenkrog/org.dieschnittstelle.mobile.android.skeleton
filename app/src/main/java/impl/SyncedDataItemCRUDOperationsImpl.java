package impl;

import android.util.Log;

import java.util.List;

import model.IDataItemCRUDOperations;
import model.ToDo;
import model.User;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean synced;
    private boolean remoteAvailable = false;
    private boolean connectionChecked = false;


    public SyncedDataItemCRUDOperationsImpl(IDataItemCRUDOperations localCRUD, IDataItemCRUDOperations remoteCRUD){
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }

    @Override
    public ToDo createToDo(ToDo toDo)
    {
        //erstelle lokal createDataitem
        toDo = localCRUD.createToDo(toDo);

        //dann erstellt mans remote, sie nimmt die lokal zugewiesene ID
        remoteCRUD.createToDo(toDo);
        return toDo;
    }

    @Override
    public List<ToDo> readAllToDos() {
        //TODO !
        if(!synced){
            synclLocalandRemote();
        synced = true;
        }
        return localCRUD.readAllToDos();
    }

    private void synclLocalandRemote() {
        List<ToDo> anzahlDateneinträge = localCRUD.readAllToDos();
        int eintraege = anzahlDateneinträge.size();

        Log.i("EINTRAEGE ", "anzahl " + eintraege);

        if(eintraege > 0){
            //sind lokale ToDos da, dann lösche ich alles remote
            remoteCRUD.deleteAllToDos(true);

            //wie übertrage ich jetzt die lokalen ToDos auf Remote seite?

        }else{
            //Jetzt sollen in die lokale DB alles von der Remote Seite
        }
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
//        checkConnection();

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
        this.connectionChecked = true;
        this.remoteAvailable = isAvailable;
    }
}

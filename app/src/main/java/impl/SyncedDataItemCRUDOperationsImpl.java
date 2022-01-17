package impl;

import android.util.Log;

import org.dieschnittstelle.mobile.android.skeleton.LoginActivity;

import java.util.Arrays;
import java.util.List;

import model.IDataItemCRUDOperations;
import model.IDataItemCRUDOperationsAsync;
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
    public ToDo createDataItem(ToDo toDo)
    {
        //erstelle lokal createDataitem
        toDo = localCRUD.createDataItem(toDo);

        //dann erstellt mans remote, sie nimmt die lokal zugewiesene ID
        remoteCRUD.createDataItem(toDo);
        return toDo;
    }

    @Override
    public List<ToDo> readAllDataItems() {
        //TODO !
        if(!synced){
            synclLocalandRemote();
        synced = true;
        }
        return localCRUD.readAllDataItems();
    }

    private void synclLocalandRemote() {
        List<ToDo> anzahlDateneinträge = localCRUD.readAllDataItems();
        int eintraege = anzahlDateneinträge.size();

        Log.i("EINTRAEGE ", "anzahl " + eintraege);

        if(eintraege > 0){
            //sind lokale ToDos da, dann lösche ich alles remote
            remoteCRUD.deleteAllDataItems(true);

            //wie übertrage ich jetzt die lokalen ToDos auf Remote seite?

        }else{
            //Jetzt sollen in die lokale DB alles von der Remote Seite
        }
    }

    @Override
    public ToDo readDataItem(long id) {
        return null;
    }

    @Override
    public boolean updateDataItem(ToDo toDo) {
        if(!remoteAvailable){
            return localCRUD.updateDataItem(toDo);
        }else{
            if(localCRUD.updateDataItem(toDo)){
                return remoteCRUD.updateDataItem(toDo);
            }
        }
        return false;
    }

    @Override
    public boolean deleteDataItem(ToDo toDo) {
        return false;
    }

    @Override
    public boolean deleteAllDataItems(boolean remote) {
        if(remote){
            return remoteCRUD.deleteAllDataItems(remote);
        }else{
            return localCRUD.deleteAllDataItems(remote);
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

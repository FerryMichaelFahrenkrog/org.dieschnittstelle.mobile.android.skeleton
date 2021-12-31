package impl;

import java.util.List;

import model.IDataItemCRUDOperations;
import model.IDataItemCRUDOperationsAsync;
import model.ToDo;

public class SyncedDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    private IDataItemCRUDOperations localCRUD;
    private IDataItemCRUDOperations remoteCRUD;

    private boolean synced;

    public SyncedDataItemCRUDOperationsImpl(IDataItemCRUDOperations localCRUD, IDataItemCRUDOperations remoteCRUD){
        this.localCRUD = localCRUD;
        this.remoteCRUD = remoteCRUD;
    }

    @Override
    public ToDo createDataItem(ToDo toDo)
    {
        toDo = localCRUD.createDataItem(toDo);
        remoteCRUD.createDataItem(toDo);
        return toDo;
    }

    @Override
    public List<ToDo> readAllDataItems() {
        //TODO !
//        if(!synced){
//            synclLocalandRemote()
//        synced = true;
//        }
        return localCRUD.readAllDataItems();
    }

    private void synclLocalandRemote() {
        //Seite 2 unten
    }

    @Override
    public ToDo readDataItem(long id) {
        return null;
    }

    @Override
    public ToDo updateDataItem(ToDo toDo) {
        toDo = localCRUD.updateDataItem(toDo);
        remoteCRUD.updateDataItem(toDo);
        return toDo;

    }

    @Override
    public boolean deleteDataItem(long id) {
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
}

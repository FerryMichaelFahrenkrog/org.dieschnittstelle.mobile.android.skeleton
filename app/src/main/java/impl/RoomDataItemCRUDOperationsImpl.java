package impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Update;

import java.util.List;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class RoomDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {
    //Room ist ein Framework was Daten, die wir in unsere View als Objekte reingeben, in eine DB zu überführen
    //Oder Daten aus einer DB in unser JavaCode zu überführen

    //Angeben welche Inhalte ich in meiner DB Speichern will, welche Klassen meines Projekts sind Klassen deren Instanzen ich
    //dauerhaft in die DB übertragen will. DataItem nicht, Activitiys nicht, nur die to_do Klasse! Dafür ne abstrakteDB Class.

    //1tens Welche Daten
    //2tens welche Operationen brauch ich
    //3tens Struktur angeben

    @Database(entities = {ToDo.class}, version = 1)
    public static abstract class RoomToDoDatabase extends RoomDatabase {
        public abstract RoomDataItemCRUDAccess getDao();
    }

    @Dao
    public static interface RoomDataItemCRUDAccess{
        @Insert
        public long createItem(ToDo toDo); //TODO als Name!

        @Query("select * from todo")
        public List<ToDo> readAllItems();

        @Query("select * from todo where id = (:id)")
        public ToDo readItem(Long id);

        @Update
        public int updateItem(ToDo toDo);
    }

    private RoomDataItemCRUDAccess roomAccessor;

    public RoomDataItemCRUDOperationsImpl(Context databaseOwner){
        RoomToDoDatabase db = Room.databaseBuilder(databaseOwner.getApplicationContext(),
                RoomToDoDatabase.class,
                "dataitems-database-mad21").build();

        roomAccessor = db.getDao();

    }

    @Override
    public ToDo createDataItem(ToDo toDo) {
        long newID = roomAccessor.createItem(toDo);
        toDo.setId(newID);

        return toDo;
    }

    @Override
    public List<ToDo> readAllDataItems() {
        return roomAccessor.readAllItems();
    }

    @Override
    public ToDo readDataItem(long id) {
        return null;
    }

    @Override
    public ToDo updateDataItem(ToDo toDo) {
        roomAccessor.updateItem(toDo);
        return toDo;
    }

    @Override
    public boolean deleteDataItem(long id) {

        return false;
    }
}

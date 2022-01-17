package impl;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.IDataItemCRUDOperations;
import model.ToDo;
import model.User;

public class RoomLocalDataItemCRUDOperationsImpl implements IDataItemCRUDOperations {

    public static class ArrayListToStringDatabaseConverter {

        @TypeConverter
        public static ArrayList<String> fromString(String value) {
            if(value == null || value.length() == 0){
                return new ArrayList<>();
            }
            return new ArrayList<>(Arrays.asList(value.split(",")));
        }

        @TypeConverter
        public static String fromArrayList(ArrayList<String> value) {
            if(value == null){
                return "";
            }
            return value
                    .stream()
                    .collect(Collectors.joining(","));
        }
    }

    //Room ist ein Framework was Daten, die wir in unsere View als Objekte reingeben, in eine DB zu überführen
    //Oder Daten aus einer DB in unser JavaCode zu überführen

    //Angeben welche Inhalte ich in meiner DB Speichern will, welche Klassen meines Projekts sind Klassen deren Instanzen ich
    //dauerhaft in die DB übertragen will. DataItem nicht, Activitiys nicht, nur die to_do Klasse! Dafür ne abstrakteDB Class.

    //1tens Welche Daten
    //2tens welche Operationen brauch ich
    //3tens Struktur angeben

    @Database(entities = {ToDo.class}, version = 10)
    public static abstract class RoomToDoDatabase extends RoomDatabase {
        public abstract RoomDataItemCRUDAccess getDao();
    }

    @Dao
    public static interface RoomDataItemCRUDAccess {
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

    public RoomLocalDataItemCRUDOperationsImpl(Context databaseOwner) {
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
    public boolean updateDataItem(ToDo toDo) {
        if(roomAccessor.updateItem(toDo) > 0 ){
            return true;
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
            return false;
        }else{
            //TODO: delete all items in the local db
            return false;
        }
    }

    @Override
    public boolean authenticateUser(User user) {
        return true;
    }

    /*
        @Override
    public boolean deleteAllDataItems(boolean remote)
    {
        try {
            return webAPI.deleteAllToDos().execute().body();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
     */
}

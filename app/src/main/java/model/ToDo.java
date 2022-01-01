package model;

import android.util.Log;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

import impl.RoomLocalDataItemCRUDOperationsImpl;

@Entity // Damit sage ich, dass ich Instanzen der Klasse To-Do in meiner Datenbank ablegen möchte
public class ToDo implements Serializable // Objekte in Bytes verwandeln, und dann wieder Objekte draus machen, braucht man um Objekte in DB zu speichern
{
    protected static long ID_GENERATOR = 0;

    public static long nextId(){
        return ++ID_GENERATOR;
    }

    protected static String logtag ="ToDo";

    private String name;
    private String description;
    @SerializedName("done") // JSON java name = done        //TODO verstehen
    private boolean checked;

    private boolean isFavouriteToDo; // wenn ja dann markier später Rot oder so

    private LocalDateTime faelligkeitsdatum;

    @SerializedName("contacts")                             //TODO verstehen
    @TypeConverters(RoomLocalDataItemCRUDOperationsImpl.ArrayListToStringDatabaseConverter.class)
    private ArrayList<String> contactIds;

    @PrimaryKey(autoGenerate = true)
    private long id;

    public ToDo() {

    }

    public ToDo(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                '}';
    }

    public String getName() {
        Log.i(logtag, "getName()" + name);

        if(name == null)
        {
            return "";
        }

        return name;
    }

    public void setName(String name) {
        Log.i(logtag, "setName()" + name);
        this.name = name;
    }

    public String getDescription() {
        Log.i(logtag, "getDescription" + description);

        return description;
    }

    public void setDescription(String description) {
        Log.i(logtag, "setDescription" + description);
        this.description = description;
    }

    public boolean isChecked() {
        Log.i(logtag, "isChecked" + checked);

        return checked;
    }

    public void setChecked(boolean checked) {
        Log.i(logtag, "isChecked" + checked);
        this.checked = checked;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isFavouriteToDo() {
        return isFavouriteToDo;
    }

    public void setFavouriteToDo(boolean favouriteToDo) {
        isFavouriteToDo = favouriteToDo;
    }

    public LocalDateTime getFaelligkeitsdatum() {
        return faelligkeitsdatum;
    }

    public void setFaelligkeitsdatum(LocalDateTime faelligkeitsdatum) {
        this.faelligkeitsdatum = faelligkeitsdatum;
    }

    public ArrayList<String> getContactIds() {
        if(contactIds == null){
            contactIds = new ArrayList<>();
            return contactIds;
        }
        return contactIds;
    }

    public void setContactIds(ArrayList<String> contactIds) {
        this.contactIds = contactIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return id == toDo.id;
    }

    @Override
    public int hashCode() { // -21 Min rest VK 12.5)
        return Objects.hash(id);
    }
}

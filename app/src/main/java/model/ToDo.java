package model;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import impl.RoomLocalDataItemCRUDOperationsImpl;

//Mit @Entity sage ich, dass ich Instanzen der Klasse 'To-Do' in meiner DB ablegen möchte.
@Entity
public class ToDo implements Serializable // Serializable --> Objekte in Bytes verwandeln, und dann wieder Objekte draus machen, braucht man um Objekte in DB zu speichern
{
    @Expose
    @SerializedName("id")
    @PrimaryKey(autoGenerate = true)
    private long id;

    @Expose
    @SerializedName("name")
    @ColumnInfo(name = "name")
    private String name;

    @Expose
    @SerializedName("description")
    @ColumnInfo(name = "description")
    private String description = "";

    @Expose
    @SerializedName("done")
    @ColumnInfo(name = "done")
    private boolean checked;

    @Expose
    @SerializedName("favourite")
    @ColumnInfo(name = "marked")
    private boolean favouriteToDo;

    @Expose
    @SerializedName("expiry")
    @ColumnInfo(name = "finishDate")
    private long finishDateLong = 0;

    @Expose
    @SerializedName("contacts")
    @TypeConverters(RoomLocalDataItemCRUDOperationsImpl.ArrayListToStringDatabaseConverter.class)
    @Ignore
    private ArrayList<String> contacts = new ArrayList<>();

    @ColumnInfo(name = "contacts")
    private transient String contactsString = ""; // mit Transient sorge ich dafür, dass die Daten in der Webapplikation gespeichert werden können.

    @Ignore
    private transient LocalDateTime finishDate = LocalDateTime.now(); // mit Transient sorge ich dafür, dass die Daten in der Webapplikation gespeichert werden können.

    @Ignore
    private transient Date localdate = new Date(); // mit Transient sorge ich dafür, dass die Daten in der Webapplikation gespeichert werden können.

    //Kontstuktoren in vers. Varianten
    @Ignore
    public ToDo() {
        finishDateLong = System.currentTimeMillis();
    }

    public ToDo(String name) {
        this.name = name;
    }

    @Ignore
    public ToDo(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Ignore
    public ToDo(String name, String description, LocalDateTime finishDate) {
        this.name = name;
        this.description = description;
        this.finishDate = finishDate;
    }

    public ToDo(String name, String description, boolean favouriteToDo, LocalDateTime finishDate) {
        this.name = name;
        this.description = description;
        this.favouriteToDo = favouriteToDo;
        this.finishDate = finishDate;
    }

    // Getter & Setter
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName()
    {
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

    public boolean isFavouriteToDo() {
        return favouriteToDo;
    }

    public void setFavouriteToDo(boolean favouriteToDo) {
        this.favouriteToDo = favouriteToDo;
    }

    public long getFinishDateLong() {
        return finishDateLong;
    }

    public void setFinishDateLong(long finishDateLong) {
        this.finishDateLong = finishDateLong;
    }

    public Date getLocaldate() {
        return localdate;
    }

    public void setLocaldate(Date localdate) {
        this.localdate = localdate;
    }

    public ArrayList<String> getContacts() {
        if(contacts == null){
            contacts = new ArrayList<>();
            return contacts;
        }
        return contacts;
    }

    public void setContacts(ArrayList<String> contacts) {
        this.contacts = contacts;
    }

    public String getContactsString() {
        return contactsString;
    }

    public void setContactsString(String contactsString) {
        this.contactsString = contactsString;
    }

    public LocalDateTime getFinishDate() {
        this.finishDate = LocalDateTime.ofEpochSecond(this.finishDateLong / 1000, 0, ZoneOffset.UTC);
        return finishDate;
    }

    public void setFinishDate(LocalDateTime finishDate) {
        this.finishDate = finishDate;
    }

    @Override
    public String toString() {
        return "ToDo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", checked=" + checked +
                ", favouriteToDo=" + favouriteToDo +
                ", faelligkeitsDatumLong=" + finishDateLong +
                ", contactIds=" + contacts +
                ", faelligkeitsDatum=" + finishDate +
                ", localdate=" + localdate +
                '}';
    }

    public static Comparator<ToDo> importanceBeforeDate = (toDo_eins, toDo_zwei) -> {
        int toDo_eins_groesser = 1;
        int toDo_zwei_kleiner = -1;

        if (toDo_eins.isChecked() && !toDo_zwei.isChecked()) {
            return toDo_eins_groesser;
        } else if (!toDo_eins.isChecked() && toDo_zwei.isChecked()) {
            return toDo_zwei_kleiner;
        } else {
            if (toDo_eins.isFavouriteToDo() && !toDo_zwei.isFavouriteToDo()) {
                return toDo_zwei_kleiner;
            } else if (!toDo_eins.isFavouriteToDo() && toDo_zwei.isFavouriteToDo()) {
                return toDo_eins_groesser;
            } else {
                return toDo_eins.getFinishDate().compareTo(toDo_zwei.getFinishDate());
            }
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return id == toDo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    protected static long ID_GENERATOR = 0;

    public static long nextId(){
        return ++ID_GENERATOR;
    }

    protected static String logtag ="ToDo";
}

package model;

import java.util.List;
import java.util.function.Consumer;

public interface IDataItemCRUDOperationsAsync {
    public void createDataItem(ToDo toDo, Consumer<ToDo> oncreated);

    public void readAllDataItems(Consumer<List<ToDo>> onread);

    public void readDataItem(long id, Consumer<ToDo> onread);

    public void updateDataItem(ToDo toDo, Consumer<ToDo> onupdated);

    public boolean deleteDataItem(long id, Consumer<Boolean> ondeleted);
}

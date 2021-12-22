package impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import model.IDataItemCRUDOperations;
import model.ToDo;

public class SimpleDataItemCRUDOperationsImpl implements IDataItemCRUDOperations
{
    @Override
    public ToDo createDataItem(ToDo toDo) {
        toDo.setId(ToDo.nextId());
        return toDo;
    }

    @Override
    public List<ToDo> readAllDataItems() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<ToDo> items = Arrays.asList("lorem", "ipsum", "dolor", "sit", "amet", "adipiscing", "elit", "larem", "totkopf", "druggy", "Thorsten", "Horst", "Ferraldon", "Lenox")
                .stream()
                .map(v -> {
                    ToDo itemobj = new ToDo(v);
                    itemobj.setId(ToDo.nextId());
                    return itemobj;
                })
                .collect(Collectors.toList());

        return items;
    }

    @Override
    public ToDo readDataItem(long id) {
        return null;
    }

    @Override
    public ToDo updateDataItem(ToDo toDo) {
        return toDo;
    }

    @Override
    public boolean deleteDataItem(long id) {
        return false;
    }
}

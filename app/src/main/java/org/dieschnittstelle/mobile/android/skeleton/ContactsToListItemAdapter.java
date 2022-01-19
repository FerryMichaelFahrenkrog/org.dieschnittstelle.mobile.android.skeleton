package org.dieschnittstelle.mobile.android.skeleton;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.dieschnittstelle.mobile.android.skeleton.DetailviewActivity;
import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewListitemBinding;
import org.dieschnittstelle.mobile.android.skeleton.databinding.ActivityDetailviewListitemBinding;

import java.util.ArrayList;

public class ContactsToListItemAdapter extends ArrayAdapter<String>
{
    private Context currentContext;
    private ArrayList<String> contactNameList;
    private DetailviewActivity detailviewInstance;

    public ContactsToListItemAdapter(@NonNull Context context, ArrayList<String> list, DetailviewActivity detailviewInstance) {
        super(context, 0, list);
        currentContext = context;
        contactNameList = list;
        this.detailviewInstance = detailviewInstance;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View existingView, @NonNull ViewGroup parent) {
        ActivityDetailviewListitemBinding listitemBinding = null;

        if (existingView == null) {
            listitemBinding = ActivityDetailviewListitemBinding.inflate(LayoutInflater.from(currentContext), parent, false);
            existingView = listitemBinding.getRoot();
        } else {
            listitemBinding = (ActivityDetailviewListitemBinding) existingView.getTag();
        }

        String currentContact = contactNameList.get(position);
        listitemBinding.setContact(currentContact);

        existingView.setTag(listitemBinding);
        return existingView;
    }
}

package com.example.android.goalist;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * Created by Pranav on 14-Oct-17.
 */

public class MessageFragment extends Fragment {

    private ListView listView;
    private View emotyView;

    public static MessageFragment newInstance(){
        MessageFragment messageFragment = new MessageFragment();
        return messageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View messageView = inflater.inflate(R.layout.fargment_message,container,false);
        listView = (ListView) messageView.findViewById(R.id.messages_list);
        emotyView = (View) messageView.findViewById(R.id.empty_view_message);
        listView.setEmptyView(emotyView);
        return messageView;
    }
}

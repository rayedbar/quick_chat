package com.android.rayed.maidintown;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayed on 3/28/2015.
 */
public class InboxFragment extends ListFragment {

    protected List<ParseObject> mMessages;

    public InboxFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_MESSSAGES);
        query.whereEqualTo(ParseConstants.KEY_RECIPIENT_IDS, ParseUser.getCurrentUser().getObjectId());
        query.addDescendingOrder(ParseConstants.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> messages, ParseException e) {
                if(e == null){
                    // success
                    mMessages = messages;
                    String [] usernames = new String[mMessages.size()];
                    int i = 0;
                    for (ParseObject message : mMessages){
                        usernames[i] = message.getString(ParseConstants.KEY_SENDER_NAME);
                        ++i;
                    }

                    if(getListView().getAdapter() == null){
                        MessageAdapter messageAdapter = new MessageAdapter(getListView().getContext(), mMessages);
                        setListAdapter(messageAdapter);
                    } else {
                        ((MessageAdapter)getListView().getAdapter()).refill(mMessages);
                    }


                } else {
                    // failure
                    Toast.makeText(getListView().getContext(), getString(R.string.inbox_no_new_messages), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        ParseObject message = mMessages.get(position);
        String messageType = message.getString(ParseConstants.KEY_FILE_TYPE);
        ParseFile file = message.getParseFile(ParseConstants.KEY_FILE);
        Uri fileUri = Uri.parse(file.getUrl());
        if (messageType.equals(ParseConstants.TYPE_IMAGE)){
            // view image in a new viewimageactivity
            Intent intent = new Intent(getListView().getContext(), ViewImageActivity.class);
            intent.setData(fileUri);
            startActivity(intent);
        } else {
            // play video
            Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
            intent.setDataAndType(fileUri, "video/*");
            startActivity(intent);
        }

        // deleete upon viewing
        List<String> ids = message.getList(ParseConstants.KEY_RECIPIENT_IDS);

        if (ids.size() == 1){
            // delete entire message
            message.deleteInBackground();
        } else {
            // just delete from current user's inbox
            ids.remove(ParseUser.getCurrentUser().getObjectId()); // removes user locally

            ArrayList<String> idsToRemove = new ArrayList<>();
            idsToRemove.add(ParseUser.getCurrentUser().getObjectId());

            message.removeAll(ParseConstants.KEY_RECIPIENT_IDS, idsToRemove);
            message.saveInBackground();


        }

    }
}
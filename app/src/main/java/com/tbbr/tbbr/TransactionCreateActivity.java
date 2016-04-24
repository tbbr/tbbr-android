package com.tbbr.tbbr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.gustavofao.jsonapi.Models.Resource;
import com.tbbr.tbbr.models.Friendship;
import com.tbbr.tbbr.models.User;

import java.util.List;

/**
 * Created by Maaz on 2016-04-20.
 */
public class TransactionCreateActivity extends AppCompatActivity {

    User sender;
    User recipient;
    int amount;
    String memo;


    AutoCompleteTextView autocompleteSender;
    AutoCompleteTextView autocompleteRecipient;

    Friendship currentFriendship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_create);

        int position = getIntent().getIntExtra("friendship_index", -1);
        final List<Resource> friendships = ((TBBRApplication) getApplication()).getFriendships();

        if (position != -1) {
            currentFriendship = (Friendship) friendships.get(position);
        } else {
            currentFriendship = null;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_create_toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbar.setTitle(getTitle());
        }

        // Setup auto complete
        setupAutoCompleteViews();


    }


    private void setupAutoCompleteViews() {
        TBBRApplication app = (TBBRApplication) getApplication();
        User[] users = mapFriendshipsToUser();

        autocompleteSender = (AutoCompleteTextView) findViewById(R.id.autocomplete_username_sender);
        autocompleteRecipient = (AutoCompleteTextView) findViewById(R.id.autocomplete_username_recipient);

        ArrayAdapter<User> senderAdapter = new ArrayAdapter<User>(this,
                android.R.layout.simple_dropdown_item_1line, users);

        ArrayAdapter<User> recipientAdapter = new ArrayAdapter<User>(this,
                android.R.layout.simple_dropdown_item_1line, users);

        autocompleteSender.setThreshold(1);
        autocompleteSender.setAdapter(senderAdapter);

        autocompleteRecipient.setThreshold(1);
        autocompleteRecipient.setAdapter(recipientAdapter);



        autocompleteSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteSender.setText("");
            }
        });

        autocompleteRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autocompleteRecipient.setText("");
            }
        });


        autocompleteSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                User sender = (User) parent.getAdapter().getItem(position);

                Toast toast = Toast.makeText(TransactionCreateActivity.this, sender.getName(), Toast.LENGTH_SHORT);
                toast.show();

            }
        });

        autocompleteSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                User sender = (User) parent.getAdapter().getItem(position);

                Toast toast = Toast.makeText(TransactionCreateActivity.this, sender.getName(), Toast.LENGTH_SHORT);
                toast.show();

            }
        });
    }

    public User[] mapFriendshipsToUser() {
        // This means that create transaction was clicked when the user was already
        // on a friendship, hence, we should only consider the current user and the friend
        if (currentFriendship != null) {
            User[] arr = new User[2];
            arr[0] = currentFriendship.getFriend();
            arr[1] = currentFriendship.getUser();

            return arr;
        }

        // currentFriendship is null, add all friends in this case.
        List<Resource> friendships = ((TBBRApplication) getApplication()).getFriendships();
        User[] arr = new User[friendships.size() + 1];

        for(int i = 0; i < friendships.size(); i++) {
            Friendship f = (Friendship) friendships.get(i);
            arr[i] = f.getFriend();
        }

        arr[friendships.size()] = ((Friendship) friendships.get(friendships.size() - 1)).getUser();

        return arr;
    }
}

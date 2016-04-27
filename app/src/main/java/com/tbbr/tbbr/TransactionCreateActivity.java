package com.tbbr.tbbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.tbbr.tbbr.api.APIService;
import com.tbbr.tbbr.models.Friendship;
import com.tbbr.tbbr.models.Transaction;
import com.tbbr.tbbr.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Maaz on 2016-04-20.
 */
public class TransactionCreateActivity extends AppCompatActivity {

    User sender;
    User recipient;
    int amount;
    String memo;


    CoordinatorLayout layoutContainer;
    AutoCompleteTextView autocompleteSender;
    AutoCompleteTextView autocompleteRecipient;
    EditText amountEditView;
    EditText memoEditView;
    AppCompatButton transactionCreateBtn;

    Friendship currentFriendship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_create);

        int position = getIntent().getIntExtra("friendship_index", -1);
        final List<Resource> friendships = ((TBBRApplication) getApplication()).getFriendships();

        // TODO: Do this check earlier... before launching this activity
        if (friendships.size() == 0) {
            // Current user has no friendships
            Toast toast = Toast.makeText(TransactionCreateActivity.this, "Sorry, you have no friends!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
        }

        if (position != -1) {
            currentFriendship = (Friendship) friendships.get(position);
        } else {
            currentFriendship = null;
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.transaction_create_toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getTitle());
        }

        amountEditView = (EditText) findViewById(R.id.transaction_create_amount);
        memoEditView = (EditText) findViewById(R.id.transaction_create_memo);
        layoutContainer = (CoordinatorLayout) findViewById(R.id.transaction_create_layout_container);


        // Setup auto complete
        setupAutoCompleteViews();

        // Setup create button
        setupCreateButton();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void setupCreateButton() {
        APIService apiService = ((TBBRApplication) getApplication()).getAPIService();
        transactionCreateBtn = (AppCompatButton) findViewById(R.id.transaction_create_btn);

        transactionCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String transactionType = "Bill";
                String relatedObjectType = "Friendship";
                String stringAmount = amountEditView.getText().toString();

                if (stringAmount.equals("") || Double.valueOf(stringAmount) == 0) {
                    Snackbar.make(layoutContainer, "Amount cannot be zero", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                double amountInDecimal = Double.valueOf(stringAmount);
                amount = (int) (amountInDecimal * 100);
                memo = memoEditView.getText().toString();

                if (currentFriendship != null) {
                    Transaction newTransaction = new Transaction(sender, recipient, amount, memo,
                            currentFriendship.getId(), relatedObjectType, transactionType);


                    Snackbar.make(layoutContainer, "Creating transaction...", Snackbar.LENGTH_SHORT).show();

                    makeCreateTransactionRequest(newTransaction);

                }
            }
        });
    }

    private void makeCreateTransactionRequest(Transaction newTransaction) {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> createTransactionReq = service.createTransaction(newTransaction);


        createTransactionReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {

                if (response.body() == null) {
                    Snackbar.make(layoutContainer, "Something went wrong", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(layoutContainer, "Transaction Created!", Snackbar.LENGTH_SHORT).show();
                    // Finish this activity after transaction has been created
                    finish();
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Snackbar.make(layoutContainer, "Something went wrong, transaction not created", Snackbar.LENGTH_LONG).show();
            }
        });
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

        // Put default values to get lucky :P
        if (currentFriendship != null) {
            sender = currentFriendship.getUser();
            autocompleteSender.setText(sender.getName());

            recipient = currentFriendship.getFriend();
            autocompleteRecipient.setText(recipient.getName());

            amountEditView.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        } else {
            // TODO: Store current user in TBBRApplication
            // For now we will use a super hacky way
            // the last element in users array should be the current user.

            sender = users[users.length - 1];
            autocompleteSender.setText(sender.getName());

            autocompleteRecipient.requestFocus();
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }


        autocompleteSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AutoCompleteTextView) view).setText("");
                recipient = null;
            }
        });

        autocompleteRecipient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AutoCompleteTextView) view).setText("");
                recipient = null;
            }
        });

        autocompleteSender.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    ((AutoCompleteTextView) v).setText("");
                    sender = null;
                }
                return false;
            }
        });

        autocompleteRecipient.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL) {
                    ((AutoCompleteTextView) v).setText("");
                    recipient = null;
                }
                return false;
            }
        });


        autocompleteSender.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                sender = (User) parent.getAdapter().getItem(position);

                if (sender.getId().equals(app.getLoggedInUsersToken().getUserId())) {
                    // Sender is the current user
                    if (currentFriendship != null) {
                        recipient = currentFriendship.getFriend();
                        autocompleteRecipient.setText(recipient.getName());
                    } else {
                        autocompleteRecipient.requestFocus();
                    }
                } else {
                    // Sender is not the current user
                    // make recipient the current user then

                    // TODO: Store current user in TBBRApplication
                    // For now we will use a super hacky way
                    // the last element in users array should be the current user.

                    recipient = users[users.length - 1];
                    autocompleteRecipient.setText(recipient.getName());
                }
            }
        });

        autocompleteRecipient.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position,
                                    long id) {
                recipient = (User) parent.getAdapter().getItem(position);

                if (recipient.getId().equals(app.getLoggedInUsersToken().getUserId())) {
                    // recipient is the current user
                    if (currentFriendship != null) {
                        sender = currentFriendship.getFriend();
                        autocompleteSender.setText(sender.getName());
                    } else {
                        autocompleteSender.requestFocus();
                    }
                } else {
                    // recipient is not the current user
                    // make sender the current user then

                    // TODO: Store current user in TBBRApplication
                    // For now we will use a super hacky way
                    // the last element in users array should be the current user.

                    sender = users[users.length - 1];
                    autocompleteSender.setText(sender.getName());
                }
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

        // Return empty user array if user has no friendships
        if (friendships.size() == 0) {
            return new User[0];
        }

        User[] arr = new User[friendships.size() + 1];

        for(int i = 0; i < friendships.size(); i++) {
            Friendship f = (Friendship) friendships.get(i);
            arr[i] = f.getFriend();
        }

        arr[friendships.size()] = ((Friendship) friendships.get(friendships.size() - 1)).getUser();

        return arr;
    }
}

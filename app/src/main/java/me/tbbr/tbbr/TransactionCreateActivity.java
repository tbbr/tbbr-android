package me.tbbr.tbbr;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitchButton;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.Friendship;
import me.tbbr.tbbr.models.Transaction;
import me.tbbr.tbbr.models.User;

import java.util.ArrayList;
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


        Toolbar toolbar = findViewById(R.id.transaction_create_toolbar);
        setSupportActionBar(toolbar);

        if (toolbar != null && getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(getTitle());
        }

        amountEditView =  findViewById(R.id.transaction_create_amount);
        memoEditView =  findViewById(R.id.transaction_create_memo);
        layoutContainer = findViewById(R.id.transaction_create_layout_container);


        setupSenderToggle();

        // Setup create button
        setupCreateButton();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void setupSenderToggle() {
        ToggleSwitch toggleSwitch = findViewById(R.id.toggle_username_sender);
//        ArrayList<User> labels = new ArrayList<>();
//        labels.addAll(mapFriendshipsToUser());
        toggleSwitch.setNumEntries(2);
        toggleSwitch.setView(
            R.layout.user_badge, 2,
            new ToggleSwitchButton.ToggleSwitchButtonDecorator() {
                @Override
                public void decorate(ToggleSwitchButton toggleSwitchButton, @NotNull View view, int position) {
                    TextView textView  = view.findViewById(R.id.user_badge_name);
                    CircleImageView mainImage = view.findViewById(R.id.user_badge_image);
                    if (position == 0) {
                        textView.setText(R.string.me);
                        Picasso.with(TransactionCreateActivity.this)
                                .load(currentFriendship.getUser().getAvatarUrl("normal"))
                                .placeholder(TransactionCreateActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                                .error(TransactionCreateActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                                .into(mainImage);
                    } else {
                        textView.setText(currentFriendship.getFriend().getName());
                        Picasso.with(TransactionCreateActivity.this)
                                .load(currentFriendship.getFriend().getAvatarUrl("normal"))
                                .placeholder(TransactionCreateActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                                .error(TransactionCreateActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                                .into(mainImage);
                        view.getLayoutParams().width = 500;
                        view.requestLayout();
                    }
                }
            },
            new ToggleSwitchButton.ViewDecorator() {
                @Override
                public void decorate(@NotNull View view, int position) {
                    TextView textView  = view.findViewById(R.id.user_badge_name);
                    textView.setTextColor(getResources().getColor(R.color.secondaryBase));
                }
            },
            new ToggleSwitchButton.ViewDecorator() {
                @Override
                public void decorate(@NotNull View view, int position) {
                    TextView textView  = view.findViewById(R.id.user_badge_name);
                    textView.setTextColor(getResources().getColor(R.color.grey500));
                }
            }
        );

        toggleSwitch.setCheckedPosition(0);
        toggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener(){
            @Override
            public void onToggleSwitchChanged(int position) {
                if (currentFriendship == null) {
                    Log.e("TCA", "CurrentFriendship is null");
                    return;
                }
                // This is assuming position 0 is for the current user, in this case
                // we've set it up so that current user is in the first position
                if (position == 0) {
                    // Current user is sender
                    sender = currentFriendship.getUser();
                    recipient = currentFriendship.getFriend();
                } else {
                    // Friend is sender
                    sender = currentFriendship.getFriend();
                    recipient = currentFriendship.getUser();
                }
            }
        });

    }

    private void setupCreateButton() {
        transactionCreateBtn = findViewById(R.id.transaction_create_btn);

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
                            currentFriendship.getFriendshipDataId(), relatedObjectType, transactionType);

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
                    Snackbar.make(layoutContainer, "Transaction not created! Must be logged in!", Snackbar.LENGTH_LONG).show();
                    if (response.raw().code() == 401) {
                        LoginManager.getInstance().logOut();

                        Intent loginIntent = new Intent(TransactionCreateActivity.this, LoginActivity.class);
                        TransactionCreateActivity.this.startActivity(loginIntent);
                        TransactionCreateActivity.this.finish();
                    } else {
                        Snackbar.make(layoutContainer, "Something went wrong", Snackbar.LENGTH_LONG).show();
                    }
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

    public User[] mapFriendshipsToUser() {
        // This means that create transaction was clicked when the user was already
        // on a friendship, hence, we should only consider the current user and the friend
        if (currentFriendship != null) {
            User[] arr = new User[2];
            arr[0] = currentFriendship.getUser();
            arr[1] = currentFriendship.getFriend();

            return arr;
        }


        // currentFriendship is null, add all friends in this case.
        TBBRApplication app = (TBBRApplication) getApplication();
        List<Resource> friendships = app.getFriendships();

        // Return empty user array if user has no friendships
        if (friendships.size() == 0) {
            return new User[0];
        }

        User[] arr = new User[friendships.size() + 1];

        arr[0] = app.getCurrentUser();

        for(int i = 1; i < friendships.size() + 1; i++) {
            Friendship f = (Friendship) friendships.get(i);
            arr[i] = f.getFriend();
        }

        return arr;
    }
}

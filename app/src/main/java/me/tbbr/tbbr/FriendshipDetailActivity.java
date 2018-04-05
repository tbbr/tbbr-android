package me.tbbr.tbbr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.ColorFilterTransformation;
import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.helpers.RecyclerItemTouchHelper;
import me.tbbr.tbbr.models.Friendship;
import me.tbbr.tbbr.models.Transaction;

import com.wang.avi.AVLoadingIndicatorView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendshipDetailActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private Friendship friendship;
    private List<Resource> transactions;
    private SimpleItemRecyclerViewAdapter mAdapter;

    TextView balance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship_detail);

        int position = getIntent().getIntExtra("item_index", 0);
        TBBRApplication app = (TBBRApplication) getApplication();

        friendship = (Friendship) app.getFriendships().get(position);

        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageDrawable(
                    new IconDrawable(this, MaterialIcons.md_library_add).colorRes(R.color.grey100));

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, TransactionCreateActivity.class);
                    intent.putExtra("friendship_index", position);
                    context.startActivity(intent);
                }
            });
        }


        // Show the back button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setSubtitle(friendship.getFormattedBalance());
        }


        CollapsingToolbarLayout appBarLayout = findViewById(R.id.toolbar_layout);

        if (appBarLayout != null) {
            appBarLayout.setTitle(friendship.getFriend().getName());
        }

        setBalanceText();

        ImageView backdrop = findViewById(R.id.friend_image_backdrop);
        CircleImageView mainImage = findViewById(R.id.friendship_page_main_img);

        Picasso.with(this)
                .load(friendship.getFriend().getAvatarUrl("normal"))
                .transform(new BlurTransformation(this, 25))
                .transform(new ColorFilterTransformation(getResources().getColor(R.color.blackTransparent)))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(backdrop);

        Picasso.with(FriendshipDetailActivity.this)
                .load(friendship.getFriend().getAvatarUrl("normal"))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(mainImage);

        AVLoadingIndicatorView progressBar = findViewById(R.id.friendship_detail_progress_bar);
        if (progressBar != null) {
            progressBar.show();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        makeTransactionRequest();
        makeFriendshipRequest();
    }

    private void setBalanceText() {
        balance = findViewById(R.id.friendship_page_balance);
        balance.setText(friendship.getFormattedBalance());
        balance.setTextColor(friendship.getBalanceColor());
    }

    private void makeTransactionRequest() {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> transactionsReq = service.getTransactions(friendship.getFriendshipDataId(), "Friendship");


        transactionsReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {

                if (response.body() == null) {
                    handleNullBody(response);
                } else {
                    AVLoadingIndicatorView progressBar = findViewById(R.id.friendship_detail_progress_bar);
                    progressBar.hide();
                    RecyclerView recyclerView = findViewById(R.id.transaction_list);
                    assert recyclerView != null;

                    if (recyclerView.getItemDecorationCount() == 0) {
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(FriendshipDetailActivity.this).build());
                    }
                    transactions = response.body().getData();
                    setupRecyclerView(recyclerView, response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
            }
        });

    }

    private void makeFriendshipRequest() {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> friendshipReq = service.getFriendship(friendship.getId());


        friendshipReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {

                if (response.body() == null) {
                    handleNullBody(response);
                } else {
                    List<Resource> friendshipList = response.body().getData();

                    friendship = (Friendship) friendshipList.get(0);
                    setBalanceText();
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    private void makeDeleteTransactionRequest(Transaction transaction) {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> transactionDeleteReq = service.deleteTransaction(transaction.getId());


        transactionDeleteReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {
                Log.e("TEST", String.valueOf(response.code()));
                if (response.body() == null) {
                    handleNullBody(response);
                } else {
                    Toast.makeText(getApplicationContext(), "Transaction hard deleted!", Toast.LENGTH_LONG).show();
                    makeFriendshipRequest();
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to hard delete transaction!", Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }

    private void handleNullBody(Response response) {
        if (response.raw().code() == 401) {
            LoginManager.getInstance().logOut();

            Intent loginIntent = new Intent(FriendshipDetailActivity.this, LoginActivity.class);
            FriendshipDetailActivity.this.startActivity(loginIntent);
            FriendshipDetailActivity.this.finish();
        }
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Resource> transactions) {
        mAdapter = new SimpleItemRecyclerViewAdapter(transactions);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // adding item touch helper
        // only ItemTouchHelper.LEFT added to detect Right to Left swipe
        // if you want both Right -> Left and Left -> Right
        // add pass ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT as param
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

    }

    /**
     * callback when recycler view is swiped
     * item will be removed on swiped
     * undo option will be provided in snackbar to restore the item
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof SimpleItemRecyclerViewAdapter.ViewHolder) {
            TBBRApplication app = (TBBRApplication) getApplication();
            // backup of removed item for undo purpose
            final Transaction deletedTransaction = (Transaction) transactions.get(viewHolder.getAdapterPosition());

            // If the currently logged in user is not the creator of the transaction, do not try to delete it
            // and let them know they cannot delete it
            if (!deletedTransaction.getCreator().getId().equals(app.getLoggedInUsersToken().getUserId())) {
                Toast.makeText(getApplicationContext(), "You can only delete transactions you've created!", Toast.LENGTH_LONG).show();
                // Resets the item to it's unswiped position
                mAdapter.notifyItemChanged(position);
                return;
            }


            String amount = deletedTransaction.getFormattedAmount();
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // Update the balance locally, giving the user immediate feedback
            friendship.removeTransactionFromBalance(deletedTransaction);
            setBalanceText();

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(findViewById(R.id.friendship_detail_layout), "Transaction " + amount + " deleted, new balance: " + friendship.getFormattedBalance(), Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedTransaction, deletedIndex);
                    friendship.addTransactionToBalance(deletedTransaction);
                    setBalanceText();
                }
            });
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    Log.e("TEST", "Can be safely deleted Event: " + String.valueOf(event));
                    int timedOutDismissed = 2;
                    int undoEvent = 1;
                    // We may want to be strict and only allow deletion if the snackbar timed out
                    // Currently it will delete the transaction as long as it wasn't the undo event
                    if (event != undoEvent) {
                        makeDeleteTransactionRequest(deletedTransaction);
                    }
                }
            });
            snackbar.setActionTextColor(getResources().getColor(R.color.primaryBase));
            snackbar.show();
        }
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Resource> transactions;

        public SimpleItemRecyclerViewAdapter(List<Resource> transactions) {
            this.transactions = transactions;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.transaction_item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mItem = (Transaction) this.transactions.get(position);
            String senderName = holder.mItem.getSender().getName();
            String amount = holder.mItem.getFormattedAmount();
            String type = "paid";
            String memo = holder.mItem.getMemo();

            String month = holder.mItem.getCreatedAtMonth();
            String day = holder.mItem.getCreatedAtDay();
            String year = holder.mItem.getCreatedAtYear();

            holder.senderName.setText(senderName);
            holder.type.setText(type);
            holder.amount.setText(amount);
            holder.memo.setText(memo);

            Picasso.with(FriendshipDetailActivity.this)
                    .load(holder.mItem.getCreator().getAvatarUrl("normal"))
                    .placeholder(FriendshipDetailActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                    .error(FriendshipDetailActivity.this.getResources().getDrawable(R.drawable.default_profile_picture))
                    .into(holder.createdByIcon);

            holder.createdAtMonth.setText(month + " " + day);
            holder.createdAtYear.setText(year);
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public void removeItem(int position) {
            transactions.remove(position);

            // notify the item removed by position
            // to perform recycler view delete animations
            notifyItemRemoved(position);
        }

        public void restoreItem(Transaction item, int position) {
            transactions.add(position, item);

            // notify item added by position
            notifyItemInserted(position);
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final RelativeLayout backgroundView;
            public final LinearLayout cardView;

            public final TextView senderName;
            public final TextView type;
            public final TextView amount;
            public final TextView memo;
            public final CircleImageView createdByIcon;

            public final TextView createdAtMonth;
            public final TextView createdAtYear;

            public final ImageView transactionDeleteIcon;

            public Transaction mItem;

            public ViewHolder(View view) {
                super(view);
                transactionDeleteIcon = view.findViewById(R.id.transaction_delete_icon);
                transactionDeleteIcon.setImageDrawable(
                    new IconDrawable(FriendshipDetailActivity.this, MaterialIcons.md_delete)
                            .colorRes(R.color.grey100)
                            .actionBarSize());
                backgroundView = view.findViewById(R.id.transaction_delete_view);
                cardView = view.findViewById(R.id.transaction_card);

                senderName = view.findViewById(R.id.transaction_sender_name);
                type = view.findViewById(R.id.transaction_type);
                amount = view.findViewById(R.id.transaction_amount);
                memo = view.findViewById(R.id.transaction_memo);
                createdByIcon = view.findViewById(R.id.transaction_created_by_icon);

                createdAtMonth = view.findViewById(R.id.vertical_date_month);
                createdAtYear = view.findViewById(R.id.vertical_date_year);
            }
        }
    }
}

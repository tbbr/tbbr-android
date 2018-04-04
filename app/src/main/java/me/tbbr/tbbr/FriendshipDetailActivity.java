package me.tbbr.tbbr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.facebook.login.LoginManager;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;
import me.tbbr.tbbr.api.APIService;
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


public class FriendshipDetailActivity extends AppCompatActivity {

    private Friendship friendship;

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
                    new IconDrawable(this, MaterialIcons.md_library_add));

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

        balance = findViewById(R.id.friendship_page_balance);
        balance.setText(friendship.getFormattedBalance());
        balance.setTextColor(friendship.getBalanceColor());

        ImageView backdrop = findViewById(R.id.friend_image_backdrop);
        CircleImageView mainImage = findViewById(R.id.friendship_page_main_img);

        Picasso.with(this)
                .load(friendship.getFriend().getAvatarUrl("normal"))
                .transform(new BlurTransformation(this, 25))
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

                    if (recyclerView.getItemDecorationAt(0) == null) {
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(FriendshipDetailActivity.this).build());
                    }
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

                    balance.setText(friendship.getFormattedBalance());
                    balance.setTextColor(friendship.getBalanceColor());
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
                    Toast.makeText(getApplicationContext(), "Transaction successfully deleted!", Toast.LENGTH_LONG).show();
                    makeFriendshipRequest();
                    makeTransactionRequest();
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), "Unable to delete transaction!", Toast.LENGTH_LONG);
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
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(transactions));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Resource> transactions;

        // This object helps you save/restore the open/close state of each view
        private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

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
            viewBinderHelper.bind(holder.swipeRevealLayout, holder.mItem.getId());
            viewBinderHelper.setOpenOnlyOne(true);
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

            holder.createdAtMonth.setText(month);
            holder.createdAtDay.setText(day);
            holder.createdAtYear.setText(year);

            if (holder.deleteBtn != null) {
//                holder.deleteBtn.setEnabled(true);
                holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("BTN", "Delete btn clicked");
//                        Toast.makeText(getApplicationContext(), "Deleting Transaction...", Toast.LENGTH_SHORT).show();
//                        makeDeleteTransactionRequest(holder.mItem);
                    }
                });
            } else {
                Log.e("TEST", "I did not get attached");
            }
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final SwipeRevealLayout swipeRevealLayout;
            public final AppCompatButton deleteBtn;
            public final LinearLayout cardView;
            public final TextView senderName;
            public final TextView type;
            public final TextView amount;
            public final TextView memo;

            public final TextView createdAtMonth;
            public final TextView createdAtDay;
            public final TextView createdAtYear;

            public Transaction mItem;

            public ViewHolder(View view) {
                super(view);
                swipeRevealLayout = view.findViewById(R.id.transaction_card_swipe_reveal);
                deleteBtn = findViewById(R.id.transaction_delete_btn);
                cardView = view.findViewById(R.id.transaction_card);

                senderName = view.findViewById(R.id.transaction_sender_name);
                type = view.findViewById(R.id.transaction_type);
                amount = view.findViewById(R.id.transaction_amount);
                memo = view.findViewById(R.id.transaction_memo);

                createdAtMonth = view.findViewById(R.id.vertical_date_month);
                createdAtDay = view.findViewById(R.id.vertical_date_day);
                createdAtYear = view.findViewById(R.id.vertical_date_year);
            }
        }
    }
}

package com.tbbr.tbbr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.MaterialIcons;
import com.squareup.picasso.Picasso;
import com.tbbr.tbbr.api.APIService;
import com.tbbr.tbbr.models.Friendship;
import com.tbbr.tbbr.models.Transaction;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FriendshipDetailActivity extends AppCompatActivity {

    private Friendship friendship;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendship_detail);

        int position = getIntent().getIntExtra("item_index", 0);
        TBBRApplication app = (TBBRApplication) getApplication();

        friendship = (Friendship) app.getFriendships().get(position);

        System.out.println(friendship.getFriendshipDataId());

        makeTransactionRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setImageDrawable(
                    new IconDrawable(this, MaterialIcons.md_library_add));

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }


        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            appBarLayout.setTitle(friendship.getFriend().getName());
        }

        ImageView backdrop = (ImageView) this.findViewById(R.id.friend_image_backdrop);

        Picasso.with(this)
                .load(friendship.getFriend().getAvatarUrl())
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(backdrop);

    }

    private void makeTransactionRequest() {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> transactionsReq = service.getTransactions(friendship.getFriendshipDataId(), "Friendship");


        transactionsReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {

                if (response.body() == null) {
                    Toast err = Toast.makeText(getApplicationContext(), "Response body was null", Toast.LENGTH_LONG);
                    err.show();
                } else {
                    View recyclerView = findViewById(R.id.transaction_list);
                    assert recyclerView != null;

                    setupRecyclerView((RecyclerView) recyclerView, response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, FriendshipListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Resource> transactions) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(transactions));
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
            holder.amount.setText(holder.mItem.getFormattedAmount());

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Expand
                }
            });
        }

        @Override
        public int getItemCount() {
            return transactions.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final CardView cardView;
            public final TextView amount;
            public Transaction mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (CardView) view.findViewById(R.id.transaction_card);
                amount = (TextView) view.findViewById(R.id.amount);
            }
        }
    }
}

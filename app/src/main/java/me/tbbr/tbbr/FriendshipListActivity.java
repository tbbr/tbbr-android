package me.tbbr.tbbr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.squareup.picasso.Picasso;
import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.Friendship;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * An activity representing a list of friendships. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link FriendshipDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class FriendshipListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TBBRApplication app = (TBBRApplication) getApplication();

        if (! app.getIsUserLoggedIn()) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_friendship_list);
        makeFriendshipRequest();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }

    private void makeFriendshipRequest() {
        final TBBRApplication app = (TBBRApplication) getApplication();
        APIService service = app.getAPIService();

        Call<JSONApiObject> friendshipsReq = service.getFriendships();


        friendshipsReq.enqueue(new Callback<JSONApiObject>() {
            @Override
            public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {

                if (response.body() == null) {
                    Toast err = Toast.makeText(getApplicationContext(), response.errorBody().toString(), Toast.LENGTH_LONG);
                    err.show();

                    if (response.raw().code() == 401) {
                        LoginManager.getInstance().logOut();

                        Intent loginIntent = new Intent(FriendshipListActivity.this, LoginActivity.class);
                        FriendshipListActivity.this.startActivity(loginIntent);
                        FriendshipListActivity.this.finish();
                    }

                } else {
                    View recyclerView = findViewById(R.id.friendship_list);
                    assert recyclerView != null;

                    ((RecyclerView)recyclerView).addItemDecoration(new HorizontalDividerItemDecoration.Builder(FriendshipListActivity.this).build());
                    app.setFriendships(response.body().getData());
                    setupRecyclerView((RecyclerView)recyclerView, app.getFriendships());
                }
            }

            @Override
            public void onFailure(Call<JSONApiObject> call, Throwable t) {
                Toast toast = Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG);
                toast.show();
            }
        });

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, List<Resource> friendships) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(friendships));
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private List<Resource> friendships;

        public SimpleItemRecyclerViewAdapter(List<Resource> friendships) {
            this.friendships = friendships;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.friendship_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Context context = FriendshipListActivity.this;
            final int index = position;
            holder.mItem = (Friendship) this.friendships.get(position);
            holder.friendName.setText(holder.mItem.getFriend().getName());
            holder.friendBalance.setText(holder.mItem.getFormattedBalance());
            holder.friendBalance.setTextColor(holder.mItem.getBalanceColor());

            Picasso.with(FriendshipListActivity.this)
                    .load(holder.mItem.getFriend().getAvatarUrl("normal"))
                    .placeholder(context.getResources().getDrawable(R.drawable.default_profile_picture))
                    .error(context.getResources().getDrawable(R.drawable.default_profile_picture))
                    .into(holder.friendImage);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, FriendshipDetailActivity.class);
                    intent.putExtra("item_index", index);

                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return friendships.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final RelativeLayout cardView;
            public final CircleImageView friendImage;
            public final TextView friendName;
            public final TextView friendBalance;
            public Friendship mItem;

            public ViewHolder(View view) {
                super(view);
                cardView = (RelativeLayout) view.findViewById(R.id.friendship_card);
                friendImage = (CircleImageView) view.findViewById(R.id.friendship_card_image);
                friendName = (TextView) view.findViewById(R.id.friendship_card_name);
                friendBalance = (TextView) view.findViewById(R.id.friendship_card_balance);
            }
        }
    }
}

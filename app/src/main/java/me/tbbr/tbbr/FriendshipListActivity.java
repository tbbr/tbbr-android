package me.tbbr.tbbr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.gustavofao.jsonapi.Models.JSONApiObject;
import com.gustavofao.jsonapi.Models.Resource;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import me.tbbr.tbbr.api.APIService;
import me.tbbr.tbbr.models.Friendship;

import com.wang.avi.AVLoadingIndicatorView;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.tbbr.tbbr.models.Token;
import me.tbbr.tbbr.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FriendshipListActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TBBRApplication app = (TBBRApplication) getApplication();

        if (! app.getIsUserLoggedIn()) {
            Log.e("FriendshipListActivity", "User is not logged in, we do not have a token for them!");
            Intent loginIntent = new Intent(this, LoginActivity.class);
            this.startActivity(loginIntent);
            finish();
            return;
        }

        AVLoadingIndicatorView progressBar = findViewById(R.id.friendship_list_progress_bar);

        if (progressBar != null) {
            progressBar.show();
        }

        Log.e("FriendshipListActivity", "User is logged in: " + app.getIsUserLoggedIn());
        setContentView(R.layout.activity_friendship_list);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        setupNavigation();
    }

    private void setupNavigation() {
        TBBRApplication app = (TBBRApplication) getApplication();
        User curUser = app.getCurrentUser();
        if (curUser == null) {
            Call<JSONApiObject> curUserReq = app.apiService.getUser(app.getLoggedInUsersToken().getUserId());
            try {
                curUserReq.enqueue(new Callback<JSONApiObject>() {
                    @Override
                    public void onResponse(Call<JSONApiObject> call, Response<JSONApiObject> response) {
                        if (response.body() == null) {

                            if (response.raw().code() == 401) {
                                Toast.makeText(getApplicationContext(), "Failed to get current user, try logging in again!" + String.valueOf(response.code()), Toast.LENGTH_LONG).show();
                                LoginManager.getInstance().logOut();

                                Intent loginIntent = new Intent(FriendshipListActivity.this, LoginActivity.class);
                                FriendshipListActivity.this.startActivity(loginIntent);
                                FriendshipListActivity.this.finish();
                            }

                        } else {
                            Resource curUser = response.body().getData(0);
                            app.setCurrentUser(curUser);
                            setNavigationContent((User)curUser);
                        }
                    }

                    @Override
                    public void onFailure(Call<JSONApiObject> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Failed to respond", Toast.LENGTH_LONG).show();
                        Log.e("API", t.getMessage());
                    }
                });
            } catch (Exception ex) {
                Log.e("API", ex.getMessage());
            }
        } else {
            setNavigationContent(curUser);
        }

    }

    private void setNavigationContent(User curUser) {
        NavigationView navView = findViewById(R.id.nav_view);
        View header = navView.getHeaderView(0);

        TextView nameView = header.findViewById(R.id.nav_user_name);
        ImageView backdrop = header.findViewById(R.id.nav_image_backdrop);

        nameView.setText(curUser.getName());
        Picasso.with(this)
                .load(curUser.getAvatarUrl("normal"))
                .transform(new BlurTransformation(this, 25))
                .placeholder(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .error(this.getResources().getDrawable(R.drawable.default_profile_picture))
                .into(backdrop);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.e("FriendshipListActivity", "Im Running");
        makeFriendshipRequest();
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
                    AVLoadingIndicatorView progressBar = findViewById(R.id.friendship_list_progress_bar);
                    progressBar.hide();
                    RecyclerView recyclerView = findViewById(R.id.friendship_list);
                    assert recyclerView != null;

                    if (recyclerView.getItemDecorationAt(0) == null) {
                        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(FriendshipListActivity.this).build());
                    }
                    app.setFriendships(response.body().getData());
                    setupRecyclerView(recyclerView, app.getFriendships());
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
                cardView = view.findViewById(R.id.friendship_card);
                friendImage = view.findViewById(R.id.friendship_card_image);
                friendName = view.findViewById(R.id.friendship_card_name);
                friendBalance = view.findViewById(R.id.friendship_card_balance);
            }
        }
    }
}

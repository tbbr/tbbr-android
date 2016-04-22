package com.tbbr.tbbr;

import android.app.Activity;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tbbr.tbbr.dummy.DummyContent;
import com.tbbr.tbbr.models.Friendship;

/**
 * A fragment representing a single friendship detail screen.
 * This fragment is either contained in a {@link FriendshipListActivity}
 * in two-pane mode (on tablets) or a {@link FriendshipDetailActivity}
 * on handsets.
 */
public class FriendshipDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_POSITION = "position";

    /**
     * The dummy content this fragment is presenting.
     */
    private Friendship mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FriendshipDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_POSITION)) {
            TBBRApplication app = (TBBRApplication) getActivity().getApplication();
            mItem = (Friendship) app.getFriendships().get(getArguments().getInt(ARG_ITEM_POSITION));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getFriend().getName());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.friendship_detail, container, false);

        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.friendship_detail)).setText(mItem.getBalance());
//        }

        return rootView;
    }
}

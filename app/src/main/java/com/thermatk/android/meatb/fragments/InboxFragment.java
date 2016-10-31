package com.thermatk.android.meatb.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.adapters.InboxAdapter;
import com.thermatk.android.meatb.data.InboxMessage;
import com.thermatk.android.meatb.helpers.ServiceHelper;
import com.thermatk.android.meatb.services.InboxUpdateService;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class InboxFragment extends Fragment{

    private RealmRecyclerView realmRecyclerView;
    private View mProgressView;
    private InboxAdapter inboxAdapter;
    private Realm realm;
    private RealmResults<InboxMessage> inboxMessages;

    private boolean doingAsync = false;

    public InboxFragment() {
        // Required empty public constructor
    }



    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Agenda");
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                RetainFragment.setFragment(this);
                doingAsync = true;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Inbox");


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_inbox, container, false);

        realm = Realm.getDefaultInstance();
        realmRecyclerView = (RealmRecyclerView) rootView.findViewById(R.id.realm_recycler_view);
        mProgressView = rootView.findViewById(R.id.inbox_progress);
        if(doingAsync) {
            showProgress(true);
        } else {
            inboxMessages = realm.where(InboxMessage.class).findAllSorted("date", Sort.DESCENDING);
            boolean wasUpdated = inboxMessages.size() > 0;
            if (!wasUpdated) {

                // show progress
                // start service
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        InboxUpdateService.class);
                getActivity().startService(intent);

                doingAsync = true;
                showProgress(true);

                RetainFragment retainFragment =
                        RetainFragment.findOrCreateRetainFragment(getFragmentManager(), inboxMessages);
                RetainFragment.setFragment(this);
                retainFragment.waitAsync();

            } else {
                attachAdapter(inboxMessages);
            }
        }
        return rootView;
    }

    public void attachAdapter(RealmResults<InboxMessage> inboxMessages){
        inboxAdapter = new InboxAdapter(getActivity(), inboxMessages, true, false);
        // TODO: no animate to show on top, find a better way
        realmRecyclerView.setAdapter(inboxAdapter);
    }

    public void serviceIsDone(RealmResults<InboxMessage> resultMessages) {
        inboxMessages = resultMessages;
        doingAsync = false;
        attachAdapter(inboxMessages);
        showProgress(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(inboxMessages!=null) {
            inboxMessages.removeChangeListeners();
        }
        realm.close();
    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            realmRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            realmRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    realmRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            realmRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentInbox";
        private static InboxFragment mFragment;
        private static RealmResults<InboxMessage> rInboxMessagesRetain;


        public RetainFragment() {
        }
        public static void setFragment(InboxFragment current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, RealmResults<InboxMessage> inboxMessages) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                rInboxMessagesRetain = inboxMessages;
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void waitAsync() {

            Log.d(LogConst.LOG, "INBOX START WAITING");
            rInboxMessagesRetain.addChangeListener(new RealmChangeListener<RealmResults<InboxMessage>>() {
                @Override
                public void onChange(RealmResults<InboxMessage> results) {
                    // TODO fix not showing
                    Log.d(LogConst.LOG, "INBOX DONE, CALLBACK");
                    if (results.size() == 0 || ServiceHelper.isInboxServiceRunning(getActivity())) {
                        // wait more
                    } else {
                        rInboxMessagesRetain.removeChangeListeners();
                        mFragment.serviceIsDone(rInboxMessagesRetain);
                    }
                }
            });
        }
    }
}

package com.thermatk.android.meatb.controllers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Controller;
import com.mikepenz.materialdrawer.Drawer;
import com.thermatk.android.meatb.LogConst;
import com.thermatk.android.meatb.R;
import com.thermatk.android.meatb.activities.MainActivity;
import com.thermatk.android.meatb.adapters.InboxAdapter;
import com.thermatk.android.meatb.data.InboxMessage;
import com.thermatk.android.meatb.data.ServiceLock;
import com.thermatk.android.meatb.helpers.ServiceHelper;
import com.thermatk.android.meatb.services.InboxUpdateService;

import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;


public class InboxController extends Controller {

    private RealmRecyclerView realmRecyclerView;
    private View mProgressView;
    private InboxAdapter inboxAdapter;
    private Realm realm;
    private RealmResults<InboxMessage> inboxMessages;

    private boolean doingAsync = false;


    @Override
    public void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("doingAsync", doingAsync);
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Agenda");
        if(savedInstanceState != null) {
            if(savedInstanceState.getBoolean("doingAsync", false)) {
                RetainFragment.setFragment(this);
                doingAsync = true;
            }
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container) {
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.setTitle("Inbox");
        Drawer drawer = mainActivity.result;
        drawer.setSelection(drawer.getDrawerItem("Inbox"),false);


        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.controller_inbox, container, false);

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

                ///
                RealmResults<ServiceLock> sl = realm.where(ServiceLock.class).equalTo("lockId", ServiceHelper.LOCK_INBOX_UPDATE_SERVICE).findAll();
                ///


                RetainFragment retainFragment =
                        RetainFragment.findOrCreateRetainFragment(getActivity().getFragmentManager(), inboxMessages,sl);
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
    protected void onDestroyView(@NonNull View view) {
        super.onDestroyView(view);
        if(inboxMessages!=null) {
            inboxMessages.removeChangeListeners();
        }
        realm.close();
    }

    private void showProgress(final boolean show) {
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
    }
    public static class RetainFragment extends Fragment {
        private static final String TAG = "RetainFragmentInbox";
        private static InboxController mFragment;
        private static RealmResults<InboxMessage> rInboxMessagesRetain;
        private static RealmResults<ServiceLock> rServiceLock;


        public RetainFragment() {
        }
        public static void setFragment(InboxController current) {
            mFragment = current;
        }
        public static RetainFragment findOrCreateRetainFragment(FragmentManager fm, RealmResults<InboxMessage> inboxMessages, RealmResults<ServiceLock> serviceLock) {
            RetainFragment fragment = (RetainFragment) fm.findFragmentByTag(TAG);
            if (fragment == null) {
                fragment = new RetainFragment();
                fm.beginTransaction().add(fragment, TAG).commit();
                rInboxMessagesRetain = inboxMessages;
                rServiceLock = serviceLock;
            }
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
        }

        private void waitAsync() {
            rServiceLock.addChangeListener(new RealmChangeListener<RealmResults<ServiceLock>>() {
                @Override
                public void onChange(RealmResults<ServiceLock> results) {
                    if (rInboxMessagesRetain.size() == 0) {
                        Log.d(LogConst.LOG, "SERVICE INBOX DONE, BUT WOW NOTHING");
                        // wait more?
                    } else {
                        Log.d(LogConst.LOG, "SERVICE INBOX DONE, GOGOGO");
                        rInboxMessagesRetain.removeChangeListeners();
                        rServiceLock.removeChangeListeners();
                        mFragment.serviceIsDone(rInboxMessagesRetain);
                    }
                }
            });
        }
    }
}

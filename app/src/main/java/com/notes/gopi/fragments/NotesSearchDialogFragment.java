package com.notes.gopi.fragments;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.notes.gopi.R;
import com.notes.gopi.adapters.NotesListingAdapter;
import com.notes.gopi.domains.NotesItem;
import com.notes.gopi.interfaces.MVPNotesInterface;
import com.notes.gopi.presenters.NotesListingPresenter;
import com.notes.gopi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.jakewharton.rxbinding.widget.RxTextView.textChangeEvents;
import static com.notes.gopi.utils.CommonUtils.doUnsubscribe;
import static com.notes.gopi.utils.ViewUtils.setGone;
import static com.notes.gopi.utils.ViewUtils.setVisibleView;

/**
 * Created by gopikrishna on 16/11/16.
 */

public class NotesSearchDialogFragment extends DialogFragment implements MVPNotesInterface {

    private Toolbar toolbar;
    private RecyclerView notesSearchRv;
    private EditText searchBarEt;

    private NotesListingPresenter notesListingPresenter;
    private ArrayList<NotesItem> notesItemList;
    private NotesListingAdapter notesListingAdapter;
    private FloatingActionButton fabButton;
    private CompositeSubscription lifeCycle;
    private static final String NOTES_LIST = "NOTES_LIST";
    private ProgressDialog progressDialog;
    private View noNotesFoundView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Holo_Light);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_search, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        searchBarEt = (EditText) view.findViewById(R.id.notes_search_bar);
        notesSearchRv = (RecyclerView) view.findViewById(R.id.notes_search_rv);
        noNotesFoundView = view.findViewById(R.id.no_results_found_view);
        lifeCycle = new CompositeSubscription();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initActions();
    }

    private void initActions() {
        initializeToolbar();
        notesListingPresenter = new NotesListingPresenter(this, getActivity());
        notesItemList = new ArrayList<>();
        notesListingAdapter = new NotesListingAdapter(notesItemList);
        notesSearchRv.setLayoutManager(new LinearLayoutManager(getActivity()));
        notesSearchRv.setAdapter(notesListingAdapter);

        lifeCycle.add(notesListingAdapter.getItemClickSubject().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pair -> {
            notesListingPresenter.onItemClickedInSearch(pair);
            dismiss();
        }));

        progressDialog = new ProgressDialog(getActivity(), R.style.NotesDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage(getString(R.string.loading));

        lifeCycle.add(textChangeEvents(searchBarEt).debounce(400, TimeUnit.MILLISECONDS)
                .map(s -> s.text().toString())
                .filter(s -> s.length() > 0)   // Optimizing do only when non empty text.
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(searchTerm -> {
                    notesListingPresenter.fetchSearchedNotesList(searchTerm);
                }));
    }

    @Override
    public void showProgress() {
        if (!notesListingPresenter.isSubscribed()) {
            return;
        }
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if (!notesListingPresenter.isSubscribed()) {
            return;
        }
        CommonUtils.dismissDialog(progressDialog);
    }

    @Override
    public void showAllNotes(List<NotesItem> notesItems) {
        // Search Results are completely dynamic , any number of items can come. So using notifyDataSetChanged
        if (!notesListingPresenter.isSubscribed() || CommonUtils.isNullOrEmpty(notesItems)) {
            setVisibleView(noNotesFoundView);
            notesItemList.clear();
            notesListingAdapter.notifyDataSetChanged();
            return;
        }
        setGone(noNotesFoundView);
        notesItemList.clear();
        notesItemList.addAll(notesItems);
        notesListingAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAllNotes(List<NotesItem> notesItems, boolean isFromSearchFlow) {
        // Not used here.
    }

    @Override
    public void removeNotesOfPosition(int index) {
        // Not used here as Search doesn't has option to remove on Long Press.
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        doUnsubscribe(lifeCycle);
    }

    protected void initializeToolbar() {
        toolbar.setNavigationOnClickListener(s -> {
            dismiss();
        });
    }
}

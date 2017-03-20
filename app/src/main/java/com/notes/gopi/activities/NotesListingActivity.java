package com.notes.gopi.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.notes.gopi.R;
import com.notes.gopi.adapters.NotesListingAdapter;
import com.notes.gopi.domains.NotesItem;
import com.notes.gopi.fragments.NotesSearchDialogFragment;
import com.notes.gopi.intdefs.NotesActionType;
import com.notes.gopi.interfaces.MVPNotesInterface;
import com.notes.gopi.presenters.NotesListingPresenter;
import com.notes.gopi.utils.CommonUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.notes.gopi.utils.CommonUtils.doUnsubscribe;
import static com.notes.gopi.utils.CommonUtils.getIntegerIntentValue;
import static com.notes.gopi.utils.CommonUtils.getIntentValue;
import static com.notes.gopi.utils.CommonUtils.isNullOrEmpty;
import static com.notes.gopi.utils.ViewUtils.setGone;
import static com.notes.gopi.utils.ViewUtils.setVisibleView;

/*
* **** Components Used/Done in this project. ***
1. Used Latest Android SDK 24 ( which this app is currently targeting as well ) and all build tools etc using are updated ones.
2. Using Java8 with jackoptions which will enable Lambda expressions, method references etc.
3. Used RxJava, RxAndroid extensively to make Database calls in Background and call backs in the Main thread with unsubscription in the onDestroy.
4. Used RxBinding for binding view clicks, editText text changes event etc with throttleFirst and debounce , so that so avoid the multiple clicks
   /multiple events getting fired in a span of a second.
5. Using Design for Floating ActionButton.
6. Using PublishSubjects instead of interfaces for better code readability and handling of subscriptions.
6. Used MVP pattern in the code , to segregated UI part from the logic with Presenters.

**** Features included in this project. Most of them i added more than which is provided in the Document ***
1. Added Search Features to search items.
2. Added Add item, Delete item ( in two places Listing Page long press and Details page menu click ), update item.
3. Optimized on Item addition/deleting/modification , Only updating that item in Recyclerview. Also Optimizing on Database calls upon
   addition/deleting/modification by passing the respective item and avoiding the extra Database call.
4. Saving list in onSaveInstanceState so that on mobile flip again Database call can be avoided.
5. All Database calls in Background thread using RxJava to prevent UI lags.
6. Using RxBinding for view clicks, editText text changes event etc with throttleFirst and debounce , so that so avoid the multiple clicks
      /multiple events getting fired in a span of a second.
7. Auto Save option in Details page. So on back click will also save the item.
8. Title of notes is picked from the first non empty line entered in description by User ( provided if title is not entered by user).
9. Showing Modified Time and Time difference information in the Notes based on timezone.

10. In Detailspage double tap to edit ( also user can click on edit button below)
11. In Listing page showing description with empty lines removed. So that user can see lot of information in notes.
12. Every time ( even after adding/deletion/modification) the items in listing page are always sorted by time in descending order.
13. Delete confirmation Dialog for confirming with the user to delete the item.
14. Doing only Soft delete of the items ( isDeleted = 1 ). So that we can show these list of items in new Trash Page.
15. Done other features like showing progressbar while loading, publishsubjects instead of interfaces etc.

**** UI Features Added ******
1. Added Floating Action Button for Add.
2. Added Ripple Effects for clicks and long press for all buttons and listing page notes item.
3. Using custom EditText with lines drawn in the Details page.
* */
public class NotesListingActivity extends AppCompatActivity implements MVPNotesInterface {

    private RecyclerView notesListingRv;
    private NotesListingPresenter notesListingPresenter;
    private ArrayList<NotesItem> notesItemList;
    private NotesListingAdapter notesListingAdapter;
    private FloatingActionButton fabButton;
    private CompositeSubscription lifeCycle;
    private static final String NOTES_LIST = "NOTES_LIST";
    private ProgressDialog progressDialog;
    private View noNotesFoundView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_listing);
        initViews();
        initActions();
        fetchDataFromSavedInstanceIfPresent(savedInstanceState);
    }

    private void initViews() {
        notesListingRv = ((RecyclerView) findViewById(R.id.notes_listing_rv));
        fabButton = ((FloatingActionButton) findViewById(R.id.notes_fab));
        noNotesFoundView = findViewById(R.id.no_notes_found_view);
    }

    private void initActions() {
        lifeCycle = new CompositeSubscription();
        initializeToolbar(getString(R.string.notes_listing_title));
        notesListingPresenter = new NotesListingPresenter(this, this);
        notesItemList = new ArrayList<>();
        notesListingAdapter = new NotesListingAdapter(notesItemList);
        notesListingRv.setLayoutManager(new LinearLayoutManager(this));
        notesListingRv.setAdapter(notesListingAdapter);

        lifeCycle.add(notesListingAdapter.getItemClickSubject().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pair -> {
            notesListingPresenter.onItemClicked(pair);
        }));
        lifeCycle.add(notesListingAdapter.getItemLongClickSubject().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(pair -> {
            notesListingPresenter.onItemLongClicked(pair);
        }));
        lifeCycle.add(RxView.clicks(fabButton).throttleFirst(1, TimeUnit.SECONDS).subscribe(s -> {
            notesListingPresenter.onAddClick();
        }));

        progressDialog = new ProgressDialog(NotesListingActivity.this,R.style.NotesDialogTheme);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage(getString(R.string.loading));
    }

    // Doing this for Optimizing the Database calls in mobile flip case.
    private void fetchDataFromSavedInstanceIfPresent(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            notesListingPresenter.fetchNotesList();
        } else {
            ArrayList<NotesItem> parcelledNotesList = savedInstanceState.getParcelableArrayList(NOTES_LIST);
            if (isNullOrEmpty(parcelledNotesList)) {
                notesListingPresenter.fetchNotesList();
            } else {
                showAllNotes(parcelledNotesList);
            }
        }
    }

    @Override
    public void removeNotesOfPosition(int index) {
        notesItemList.remove(index);
        notesListingAdapter.notifyItemRemoved(index);
        performNoNotesFoundLogic();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                performActionsBasedOnType(data);
            }
        }
    }

    private void performActionsBasedOnType(Intent data) {
        if (data == null || data.getExtras() == null) {
            return;
        }
        // Get the data from Notes Details Page & update in only the item added/changed/deleted .
        // So that no need to fetch all data from database again .
        String actionType = getIntentValue(data, NotesDetailsActivity.ACTION_TYPE);
        int itemPostion = getIntegerIntentValue(data, NotesDetailsActivity.ITEM_POSITION);
        NotesItem notesItem = (NotesItem) data.getParcelableExtra(NotesDetailsActivity.NOTES_ITEM);
        switch (actionType) {
            case NotesActionType.SEARCH:
                notesListingPresenter.fetchAllNotesListAfterSearch();  // In Search Flow any thing could have been updated/deleted.
                break;                                                 // So refreshing full Adapter
            case NotesActionType.DELETE:
                if (itemPostion >= notesItemList.size()) {
                    break;
                }
                removeNotesOfPosition(itemPostion);   // Optimizing Only one item is removed . not refreshing whole page.
                break;
            case NotesActionType.ADD:
                if (notesItem != null) {
                    notesItemList.add(0, notesItem);  // Adding item at first position
                    notesListingAdapter.notifyItemInserted(0);  // Optimizing Only one item is inserted . not refreshing whole page.
                    notesListingRv.smoothScrollToPosition(0);
                }
                break;
            case NotesActionType.UPDATE:
                NotesItem notesItemExisting = notesItemList.remove(itemPostion);
                notesItemExisting.setTitle(notesItem.getTitle());
                notesItemExisting.setDescription(notesItem.getDescription());
                notesItemList.add(0, notesItemExisting);
                notesListingAdapter.notifyItemMoved(itemPostion, 0);
                notesListingAdapter.notifyItemChanged(0);    // Optimizing Only one item is moved and changed . not refreshing whole page.
                notesListingRv.smoothScrollToPosition(0);    // making Recyclerview go to the top.
                break;
        }
        performNoNotesFoundLogic();
    }

    private void performNoNotesFoundLogic() {
        if (isNullOrEmpty(notesItemList)) {
            setVisibleView(noNotesFoundView);
        } else {
            setGone(noNotesFoundView);
        }
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (!isNullOrEmpty(notesItemList)) {
            outState.putParcelableArrayList(NOTES_LIST, notesItemList);
        }
    }

    @Override
    public void showAllNotes(List<NotesItem> notesItems, boolean isFromSearchFlow) {
        if (!notesListingPresenter.isSubscribed() || CommonUtils.isNullOrEmpty(notesItems)) {
            setVisibleView(noNotesFoundView);
            return;
        }
        setGone(noNotesFoundView);
        notesItemList.clear();
        notesItemList.addAll(notesItems);
        if (isFromSearchFlow) {
            // Any item can be modified/deleted from search flow. So doing notifyDataSetChanged for complete refresh.
            notesListingAdapter.notifyDataSetChanged();
        } else {  // On First time Page load of Listing. fetching complete list from DB and inserting.
            notesListingAdapter.notifyItemRangeInserted(0, notesItemList.size());
        }
    }

    @Override
    public void showAllNotes(List<NotesItem> notesItems) {
        showAllNotes(notesItems, false);
    }

    protected void initializeToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(10);
        }
        toolbar.setNavigationIcon(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_listing_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Toast.makeText(NotesListingActivity.this, R.string.about_text, Toast.LENGTH_SHORT).show();
                break;
            case R.id.action_search:
                NotesSearchDialogFragment dialogFragment = new NotesSearchDialogFragment();
                dialogFragment.show(getFragmentManager(), getString(R.string.search_string));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesListingPresenter.unSubscribe();
        doUnsubscribe(lifeCycle);
    }
}

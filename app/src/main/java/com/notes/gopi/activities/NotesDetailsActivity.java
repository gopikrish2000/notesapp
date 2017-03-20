package com.notes.gopi.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.notes.gopi.R;
import com.notes.gopi.customviews.MultiLineEditText;
import com.notes.gopi.domains.NotesItem;
import com.notes.gopi.intdefs.NotesActionType;
import com.notes.gopi.interfaces.MVPNotesDetailsInterface;
import com.notes.gopi.presenters.NotesDetailsPresenter;
import com.notes.gopi.utils.CommonUtils;
import com.notes.gopi.utils.NotesConfiguration;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

import static com.notes.gopi.utils.CommonUtils.doUnsubscribe;
import static com.notes.gopi.utils.CommonUtils.getFormattedDateDifferenceFromNow;
import static com.notes.gopi.utils.CommonUtils.getIntegerIntentValue;
import static com.notes.gopi.utils.CommonUtils.isNullOrEmpty;
import static com.notes.gopi.utils.ViewUtils.setGone;
import static com.notes.gopi.utils.ViewUtils.setVisibleView;

public class NotesDetailsActivity extends AppCompatActivity implements MVPNotesDetailsInterface {

    private TextView notesLastModifiedDifference;
    private TextView notesLastModifiedDate;
    private MultiLineEditText notesMultilineEt;
    private NotesItem notesItem;
    private int itemPosition;

    private boolean isAddFlow; // if true => New Notes creation flow.
    private boolean isEditFlow;  // if true => Either New Notes flow or Update Notes flow.
    private boolean isSearchFlow;
    private Button editButton;
    private Button doneButton;
    private EditText headerEditText;
    private TextView headerTitleTv;
    private CompositeSubscription lifeCycle;
    private NotesDetailsPresenter notesDetailsPresenter;

    public static final String NOTES_ITEM = "NOTES_ITEM";
    public static final String ITEM_POSITION = "ITEM_POSITION";
    public static final String ADD_FLOW = "ADD_FLOW";
    public static final String SEARCH_FLOW = "SEARCH_FLOW";
    public static final String ACTION_TYPE = "ACTION_TYPE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_details);
        consumeIntentValues();
        initViews();
        initActions();
    }

    private void consumeIntentValues() {
        notesItem = (NotesItem) getIntent().getParcelableExtra(NOTES_ITEM);
        itemPosition = getIntegerIntentValue(getIntent(), ITEM_POSITION);
        isAddFlow = getIntent().getExtras().getBoolean(ADD_FLOW, false);
        isSearchFlow = getIntent().getExtras().getBoolean(SEARCH_FLOW, false);
    }

    private void initViews() {
        notesLastModifiedDifference = (TextView) findViewById(R.id.notes_last_modified_difference);
        notesLastModifiedDate = (TextView) findViewById(R.id.notes_last_modified_date);
        notesMultilineEt = (MultiLineEditText) findViewById(R.id.notes_multiline_et);
        headerEditText = ((EditText) findViewById(R.id.notes_header_et));
        headerTitleTv = ((TextView) findViewById(R.id.notes_header_tv));
        editButton = ((Button) findViewById(R.id.notes_edit_btn));
        doneButton = ((Button) findViewById(R.id.notes_done_btn));
    }

    private void initActions() {
        initializeClicks();
        initializeToolbar();
        performMainUILogic();
    }

    private void initializeClicks() {
        lifeCycle = new CompositeSubscription();
        notesDetailsPresenter = new NotesDetailsPresenter(this, this, isAddFlow, notesItem, itemPosition, isSearchFlow);
        lifeCycle.add(RxView.clicks(editButton).throttleFirst(1, TimeUnit.SECONDS).subscribe(s -> {
            notesDetailsPresenter.onEditButtonClick();
        }));
        lifeCycle.add(RxView.clicks(doneButton).throttleFirst(1, TimeUnit.SECONDS).subscribe(s -> {
            String description = notesMultilineEt.getText().toString();
            String titleText = headerEditText.getText().toString();
            notesDetailsPresenter.onDoneClick(description, titleText);
        }));
    }


    private void performMainUILogic() {
        String title;
        if (!isAddFlow) {  // Already notes item previously added. So show the data.
            title = notesItem.getTitle();
            notesLastModifiedDate.setText(notesItem.getUpdatedDate());
            Date lastUpdatedDate = CommonUtils.getDateTimeFromString(notesItem.getUpdatedDate());
            notesLastModifiedDifference.setText(getFormattedDateDifferenceFromNow(lastUpdatedDate));
            notesMultilineEt.setText(notesItem.getDescription());
            updateUI(false);
        } else {  //inside AddFlow => New notes item creation flow .
            title = "";
            notesLastModifiedDate.setText("");
            updateUI(true);
        }
        setHeaderTitle(title);
    }

    // Forward to listing page with type. So onActivityResult of Listing Page is being called.
    @Override
    public void returnToListingPage(String type, NotesItem notesItem) {
        if (NotesActionType.CLOSE.equalsIgnoreCase(type)) {
            finish();
            return;
        }
        if (isSearchFlow) {
            type = NotesActionType.SEARCH;  // This will refresh full page in listing.
        }
        Bundle bundle = new Bundle();
        bundle.putInt(ITEM_POSITION, itemPosition);
        bundle.putString(ACTION_TYPE, type);
        bundle.putParcelable(NOTES_ITEM, notesItem);
        Intent mIntent = new Intent();
        mIntent.putExtras(bundle);
        setResult(RESULT_OK, mIntent);
        finish();
    }

    protected void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (android.os.Build.VERSION.SDK_INT >= 21) {
            getSupportActionBar().setElevation(10);
        }
        toolbar.setNavigationOnClickListener(s -> {
            onBackPressed();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                notesDetailsPresenter.onDeleteOptionMenuClicked();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setHeaderTitle(String title) {
        getSupportActionBar().setTitle("");
        headerTitleTv.setText(title);
        if (!isNullOrEmpty(title)) {
            headerEditText.setText(title);
        }
    }

    @Override
    public void updateUI(boolean isEditFlow) {
        this.isEditFlow = isEditFlow;
        if (isEditFlow) {   // Edit flow where u can edit title, content.
            lifeCycle.add(RxView.clicks(notesMultilineEt).subscribe());
            notesMultilineEt.setFocusableInTouchMode(true);
            notesMultilineEt.setFocusable(true);
            notesMultilineEt.requestFocus();
            notesMultilineEt.setSelection(notesMultilineEt.getText().length());
            if (!isAddFlow) {
                notesLastModifiedDifference.setText(R.string.editing);
            } else {
                notesLastModifiedDifference.setText(R.string.new_note);
            }
            notesLastModifiedDifference.setTypeface(notesLastModifiedDifference.getTypeface(), Typeface.BOLD);
            setGone(editButton, headerTitleTv);
            setVisibleView(doneButton, headerEditText);
        } else {   // Not Edit flow => u cannot edit the title, content.
            notesMultilineEt.setFocusableInTouchMode(false);
            notesMultilineEt.setFocusable(false);

            // count the clicks in 0.5 sec duration.
            // Instead of clicking edit button . Single tap in 0.5 sec gives you toast. Double tap will perform like edit button click.
            lifeCycle.add(RxView.clicks(notesMultilineEt).buffer(500, TimeUnit.MILLISECONDS).map(List::size).filter(clicksCount -> clicksCount > 0)
                    .subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(clicksCount -> {
                        if (clicksCount == 1) {
                            Toast.makeText(NotesDetailsActivity.this, "Double Tap or click on Edit button to edit. ", Toast.LENGTH_SHORT).show();
                        } else if (clicksCount > 1) {
                            notesDetailsPresenter.onEditButtonClick();  // treat it like edit button click , if tapped twice or more in a span of 0.5 sec.
                        }
                    }));
            setVisibleView(editButton, headerTitleTv);
            setGone(doneButton, headerEditText);
        }
    }

    @Override
    public void onBackPressed() {
        // Autosaving note , even on Back Click based on Configuration set. By default its true.
        if (isEditFlow && NotesConfiguration.getInstance().isAutoSaveEnabled()) {
            String description = notesMultilineEt.getText().toString();
            String titleText = headerEditText.getText().toString();
            notesDetailsPresenter.onDoneClick(description, titleText);
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        notesDetailsPresenter.unSubscribe();
        doUnsubscribe(lifeCycle);
    }
}

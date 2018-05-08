package com.example.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TodoItemAdapter.OnListInteractionListener, DeleteDialogFragment.DeleteDialogListener {

    public static final String EXTRA_MESSAGE = "com.example.TodoList.ID";
    private static final String LIST_STATE = "list_state";
    private static final String MULTI_SELECT_ACTIVE = "multi_select";
    private static final String SELECTED_ITEMS = "selected_items";
    private static final String ADAPTER_ITEM_COUNT = "adapter_item_count";
    private static final String VIEW_MODE_STATE = "view_mode_state";
    private static VIEW_MODES VIEW_MODE = VIEW_MODES.UNARCHIVED;

    private enum VIEW_MODES {
        ARCHIVED, UNARCHIVED
    }

    private TodoViewModel todoViewModel;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private TodoItemAdapter adapter;
    private Integer adapterItemCount;
    private boolean listNeedsRefresh = false;
    private final RecyclerView.OnScrollListener hideFab = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dy > 0) {
                floatingActionButton.hide();
            } else {
                floatingActionButton.show();
            }
        }
    };
    private final View.OnClickListener viewTodo = (View v) -> {
        Intent intent = new Intent(MainActivity.this, ViewTodoActivity.class);
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            VIEW_MODE = (VIEW_MODES) savedInstanceState.getSerializable(VIEW_MODE_STATE);
        }

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        floatingActionButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        floatingActionButton.setOnClickListener(viewTodo);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(hideFab);
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));

        adapter = new TodoItemAdapter(this);
        recyclerView.setAdapter(adapter);

        refreshTodoItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_archive, menu);
        if (VIEW_MODE.equals(VIEW_MODES.ARCHIVED)) {
            menu.findItem(R.id.view_archive_toggle).setTitle(R.string.view_unarchived_todos);
            if (adapter.isMultiSelect()) {
                menu.findItem(R.id.unarchive).setVisible(true);
                menu.findItem(R.id.delete).setVisible(true);
            } else {
                menu.findItem(R.id.unarchive).setVisible(false);
                menu.findItem(R.id.delete).setVisible(false);
            }
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.view_archive_toggle:
                if (VIEW_MODE.equals(VIEW_MODES.UNARCHIVED)) {
                    VIEW_MODE = VIEW_MODES.ARCHIVED;
                } else {
                    VIEW_MODE = VIEW_MODES.UNARCHIVED;
                }
                listNeedsRefresh = true;  // Forces refreshTodoItems to be called in onMultiSelectFinish.
                onMultiSelectFinish();
                return true;
            case R.id.unarchive:
                for (int position : adapter.getSelectedItemPositions()) {
                    todoViewModel.setArchivedStatus(adapter.getItemAtPosition(position), false);
                }
                listNeedsRefresh = true;
                onMultiSelectFinish();
                return true;
            case R.id.delete:
                DialogFragment dialog = new DeleteDialogFragment();
                dialog.show(getSupportFragmentManager(), "deleteDialog");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTodoItems();
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable listState = savedInstanceState.getParcelable(LIST_STATE);
            // Restore scroll position on rotation
            recyclerView.getLayoutManager().onRestoreInstanceState(listState);
            if (savedInstanceState.getBoolean(MULTI_SELECT_ACTIVE)) {
                adapterItemCount = savedInstanceState.getInt(ADAPTER_ITEM_COUNT, 0);
                startMultiSelect(savedInstanceState.getIntegerArrayList(SELECTED_ITEMS));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
        outState.putBoolean(MULTI_SELECT_ACTIVE, adapter.isMultiSelect());
        outState.putIntegerArrayList(SELECTED_ITEMS, new ArrayList<>(adapter.getSelectedItemPositions()));
        outState.putInt(ADAPTER_ITEM_COUNT, adapter.getItemCount());
        outState.putSerializable(VIEW_MODE_STATE, VIEW_MODE);
    }

    @Override
    public void onBackPressed() {
        if (adapter.isMultiSelect()) {
            onMultiSelectFinish();
        } else {
            super.onBackPressed();
        }
    }

    // Because this can be called after onMultiSelectFinish, we retain the last state of multi-select
    // before TodoItemAdapter.ViewHolder.selectItem() is called. This prevents the intent from firing
    // after multi-select ends when all items are deselected.
    @Override
    public void onItemClick(TodoItem item, boolean multiSelectState) {
        if (adapter.isMultiSelect()) {
            updateTitleForMultiSelect();
        } else if (!multiSelectState) {
            Intent intent = new Intent(this, ViewTodoActivity.class);
            intent.putExtra(EXTRA_MESSAGE, item.getId());
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(TodoItem item) {
        startMultiSelect();
        // End multi-select when we deselect the last selected item with a long press.
        if (adapter.getSelectedItemPositions().isEmpty()) {
            onMultiSelectFinish();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        for (int position : adapter.getSelectedItemPositions()) {
            todoViewModel.deleteTodoItem(adapter.getItemAtPosition(position));
        }
        listNeedsRefresh = true;
        onMultiSelectFinish();
    }

    @Override
    public void onMultiSelectFinish() {
        adapter.setMultiSelect(false);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        if (VIEW_MODE.equals(VIEW_MODES.UNARCHIVED)) {
            getSupportActionBar().setTitle(R.string.app_name);
        } else {
            getSupportActionBar().setTitle(R.string.archived_todo_label);
        }
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        invalidateOptionsMenu();

        recyclerView.addOnScrollListener(hideFab);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_white_24px));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(viewTodo);
        floatingActionButton.show();
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));

        // If the user modifies the selected TodoItems (ex. marks them as done) , we can refresh
        // our list and let the adapter handle the change without flickering. Otherwise manually
        // notify the adapter of the change to remove selection highlight.
        if (listNeedsRefresh) {
            refreshTodoItems();
        } else {
            for (int position : adapter.getSelectedItemPositions()) {
                adapter.notifyItemChanged(position);
            }
        }
        listNeedsRefresh = false;
        adapter.getSelectedItemPositions().clear();
    }

    private void startMultiSelect() {
        adapter.setMultiSelect(true);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorDarkToolbar)));
        // adapter.getItemCount() returns 0 after rotation so we keep track of it manually
        updateTitleForMultiSelect();
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorDarkStatusBar));
        invalidateOptionsMenu();

        if (VIEW_MODE.equals(VIEW_MODES.UNARCHIVED)) {
            recyclerView.removeOnScrollListener(hideFab);
            floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_done_all_white_24dp));
            floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFabDoneAction)));
            floatingActionButton.setOnClickListener((View v) -> {
                listNeedsRefresh = true;
                removeSelectedItems();
                onMultiSelectFinish();
            });
            floatingActionButton.show();
            TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.mark_done));
        }
    }

    private void startMultiSelect(List<Integer> selectedItems) {
        adapter.setSelectedItemPositions(selectedItems);
        startMultiSelect();
        for (int position : selectedItems) {
            adapter.notifyItemChanged(position);
        }
    }

    private void updateTitleForMultiSelect() {
        int itemCount = adapterItemCount == null ? adapter.getItemCount() : adapterItemCount;
        getSupportActionBar().setTitle(getResources().getQuantityString(R.plurals.selection_title,
                itemCount, itemCount, adapter.getSelectedItemPositions().size()));
        adapterItemCount = null;
    }

    private void removeSelectedItems() {
        for (int position : adapter.getSelectedItemPositions()) {
            todoViewModel.setArchivedStatus(adapter.getItemAtPosition(position), true);
        }
    }

    private void refreshTodoItems() {
        todoViewModel.refreshTodoItems(VIEW_MODE.equals(VIEW_MODES.ARCHIVED));
        todoViewModel.getTodoItems().observe(this, items -> {
            if (items != null) {
                Collections.sort(items);
                adapter.submitList(items);
            }
        });
    }
}

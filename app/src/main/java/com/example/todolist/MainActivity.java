package com.example.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements TodoItemAdapter.OnListInteractionListener {

    public static final String EXTRA_MESSAGE = "com.example.TodoList.ID";
    private static final String LIST_STATE = "list_state";
    private static final String MULTI_SELECT_ACTIVE = "multi_select";
    private static final String SELECTED_ITEMS = "selected_items";
    private static final String ADAPTER_ITEM_COUNT = "adapter_item_count";

    private TodoViewModel todoViewModel;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private TodoItemAdapter adapter;
    private Integer adapterItemCount;
    private boolean fabPressed = false;
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

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        floatingActionButton = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        floatingActionButton.setOnClickListener(viewTodo);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(hideFab);
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));

        adapter = new TodoItemAdapter(this);
        recyclerView.setAdapter(adapter);

        todoViewModel.refreshTodoItems();
        todoViewModel.getTodoItems().observe(this, items -> {
            if (items != null) {
                Collections.sort(items);
                adapter.submitList(items);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        todoViewModel.refreshTodoItems();
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
            getSupportActionBar().setTitle(
                    String.format(getString(R.string.selection_title), adapter.getSelectedItemPositions().size(), adapter.getItemCount()));
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
    public void onMultiSelectFinish() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setTitle(R.string.app_name);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        recyclerView.addOnScrollListener(hideFab);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_add_white_24px));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        floatingActionButton.setOnClickListener(viewTodo);
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.fab_hint));
        adapter.setMultiSelect(false);

        // If the user marks the TodoItems as done, we can refresh our list and let the
        // adapter handle the change without flickering. Otherwise manually notify the adapter of
        // the change to remove selection highlight.
        if (fabPressed) {
            todoViewModel.refreshTodoItems();
        } else {
            for (int position : adapter.getSelectedItemPositions()) {
                adapter.notifyItemChanged(position);
            }
        }
        fabPressed = false;
        adapter.getSelectedItemPositions().clear();
    }

    private void startMultiSelect() {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorDarkToolbar)));
        // adapter.getItemCount() returns 0 after rotation so we keep track of it manually
        int itemCount = adapterItemCount == null ? adapter.getItemCount() : adapterItemCount;
        getSupportActionBar().setTitle(
                String.format(getString(R.string.selection_title), adapter.getSelectedItemPositions().size(), itemCount));
        adapterItemCount = null;
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorDarkStatusBar));

        adapter.setMultiSelect(true);
        recyclerView.removeOnScrollListener(hideFab);
        floatingActionButton.setImageDrawable(getDrawable(R.drawable.ic_done_all_white_24dp));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorFabDoneAction)));
        floatingActionButton.setOnClickListener((View v) -> {
            fabPressed = true;
            removeSelectedItems();
            onMultiSelectFinish();
        });
        floatingActionButton.show();
        TooltipCompat.setTooltipText(floatingActionButton, getString(R.string.mark_done));
    }

    private void startMultiSelect(List<Integer> selectedItems) {
        adapter.setSelectedItemPositions(selectedItems);
        startMultiSelect();
        for (int position : selectedItems) {
            adapter.notifyItemChanged(position);
        }
    }

    private void removeSelectedItems() {
        for (int position : adapter.getSelectedItemPositions()) {
            todoViewModel.deleteTodoItem(adapter.getItemAtPosition(position));
        }
    }
}

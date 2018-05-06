package com.example.todolist;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.TooltipCompat;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements TodoItemAdapter.OnListInteractionListener {

    public static final String EXTRA_MESSAGE = "com.example.TodoList.ID";
    private static final String LIST_STATE = "list_state";
    private TodoViewModel todoViewModel;
    private RecyclerView recyclerView;
    private TodoItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        todoViewModel = ViewModelProviders.of(this).get(TodoViewModel.class);
        Date date = new Date();
        TodoItem item = new TodoItem(date, "Test title", "test body");
        todoViewModel.addTodoItem(item);

        FloatingActionButton fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
        TooltipCompat.setTooltipText(fab, getString(R.string.fab_hint));

        adapter = new TodoItemAdapter(this);
        recyclerView.setAdapter(adapter);

        todoViewModel.refreshTodoItems();
        todoViewModel.getTodoItems().observe(this, items -> {
            if (items != null) {
                adapter.submitList(items);
                // Restore scroll position on rotation
                if (savedInstanceState != null) {
                    Parcelable listState = savedInstanceState.getParcelable(LIST_STATE);
                    recyclerView.getLayoutManager().onRestoreInstanceState(listState);
                }
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(LIST_STATE, recyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public void onItemClick(TodoItem item) {

        Intent intent = new Intent(this, ViewTodoActivity.class);
        intent.putExtra(EXTRA_MESSAGE, item.getId());
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(TodoItem item) {
        adapter.setActionMode(startSupportActionMode(new TodoItemActionModeCallBack()));
    }

    private class TodoItemActionModeCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            adapter.setMultiSelect(true);
            mode.getMenuInflater().inflate(R.menu.item_actions, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_done:
                    for (TodoItem todoItem : adapter.getSelectedItems()) {
                        todoViewModel.deleteTodoItem(todoItem);
                    }
                    mode.finish();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adapter.setMultiSelect(false);
            adapter.setActionMode(null);
            todoViewModel.refreshTodoItems();
            for (TodoItem todoItem : adapter.getSelectedItems()) {
                todoItem.setSelected(false);
            }
            adapter.getSelectedItems().clear();
        }
    }
}

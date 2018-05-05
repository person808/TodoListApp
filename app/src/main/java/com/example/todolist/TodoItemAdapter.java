package com.example.todolist;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TodoItemAdapter extends ListAdapter<TodoItem, TodoItemAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<TodoItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<TodoItem>() {
        @Override
        public boolean areItemsTheSame(TodoItem oldItem, TodoItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(TodoItem oldItem, TodoItem newItem) {
            return oldItem.equals(newItem);
        }
    };
    private final OnListInteractionListener listener;
    private final TodoViewModel todoViewModel;

    // Fields needed to implement multi-selection
    private ActionMode actionMode;
    private List<ViewHolder> selectedItems = new ArrayList<>();
    private boolean multiSelect = false;

    public interface OnListInteractionListener {
        void onItemClick(TodoItem item);

        boolean onItemLongClick(TodoItem item);
    }

    TodoItemAdapter(OnListInteractionListener listener, TodoViewModel todoViewModel) {
        super(DIFF_CALLBACK);
        this.listener = listener;
        this.todoViewModel = todoViewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.update(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TodoItem todoItem;
        private final View view;
        private final ConstraintLayout constraintLayout;
        private final TextView titleView;
        private final TextView dateView;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            constraintLayout = view.findViewById(R.id.layout);
            titleView = view.findViewById(R.id.tv_title);
            dateView = view.findViewById(R.id.tv_date);
        }

        void selectItem(TodoItem item) {
            if (multiSelect) {
                if (selectedItems.contains(this)) {
                    selectedItems.remove(this);
                    constraintLayout.setBackgroundColor(Color.WHITE);
                    // End multi-select mode if all selected items are deselected
                    if (actionMode != null && selectedItems.isEmpty()) {
                        actionMode.finish();
                    }
                } else {
                    selectedItems.add(this);
                    constraintLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void update(final TodoItem item) {
            todoItem = item;

            if (selectedItems.contains(this)) {
                constraintLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                constraintLayout.setBackgroundColor(Color.WHITE);
            }

            titleView.setText(item.getTitle());
            dateView.setText(Util.dateToString(item.getDate()));

            view.setOnClickListener((View v) -> {
                if (listener != null && !multiSelect) {
                    listener.onItemClick(item);
                }
                selectItem(item);
            });
            view.setOnLongClickListener((View v) -> {
                if (listener != null) {
                    actionMode = ((AppCompatActivity) view.getContext()).startSupportActionMode(new TodoItemActionModeCallBack());
                    selectItem(item);
                    return listener.onItemLongClick(item);
                }
                return false;
            });
        }
    }

    private class TodoItemActionModeCallBack implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            multiSelect = true;
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
                    List<ViewHolder> itemsToRemove = new ArrayList<>();
                    for (ViewHolder viewHolder : selectedItems) {
                        todoViewModel.deleteTodoItem(viewHolder.todoItem);
                        itemsToRemove.add(viewHolder);
                    }
                    selectedItems.removeAll(itemsToRemove);
                    mode.finish();
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            multiSelect = false;
            for (ViewHolder viewHolder : selectedItems) {
                viewHolder.constraintLayout.setBackgroundColor(Color.WHITE);
            }
            todoViewModel.refreshTodoItems();
            selectedItems.clear();
        }
    }
}

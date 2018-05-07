package com.example.todolist;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
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

    // Fields needed to implement multi-selection
    private ActionMode actionMode;
    private List<Integer> selectedItemPositions = new ArrayList<>();
    private boolean multiSelect = false;

    public interface OnListInteractionListener {
        void onItemClick(TodoItem item);
        void onItemLongClick(TodoItem item);
    }

    TodoItemAdapter(OnListInteractionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
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

    public TodoItem getItemAtPosition(int position) {
        return getItem(position);
    }

    public List<Integer> getSelectedItemPositions() {
        return selectedItemPositions;
    }

    public void setMultiSelect(boolean multiSelect) {
        this.multiSelect = multiSelect;
    }

    public void setActionMode(ActionMode actionMode) {
        this.actionMode = actionMode;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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

        void selectItem() {
            if (multiSelect) {
                if (selectedItemPositions.contains(getAdapterPosition())) {
                    selectedItemPositions.remove((Integer) getAdapterPosition());
                    constraintLayout.setBackgroundColor(Color.WHITE);
                    // End multi-select mode if all selected items are deselected
                    if (actionMode != null && selectedItemPositions.isEmpty()) {
                        actionMode.finish();
                    }
                } else {
                    selectedItemPositions.add(getAdapterPosition());
                    constraintLayout.setBackgroundColor(Color.LTGRAY);
                }
            }
        }

        void update(final TodoItem item) {
            if (selectedItemPositions.contains(getAdapterPosition())) {
                constraintLayout.setBackgroundColor(Color.LTGRAY);
            } else {
                constraintLayout.setBackgroundColor(Color.WHITE);
            }

            if (!item.getTitle().isEmpty()) {
                titleView.setText(item.getTitle());
            } else {
                titleView.setText(item.getBody());
            }
            dateView.setText(Util.dateToString(item.getDate()));

            view.setOnClickListener((View v) -> {
                if (listener != null && !multiSelect) {
                    listener.onItemClick(item);
                }
                selectItem();
            });
            view.setOnLongClickListener((View v) -> {
                if (listener != null) {
                    listener.onItemLongClick(item);
                    selectItem();
                }
                return true;
            });
        }
    }
}

package com.example.todolist;

import android.support.annotation.NonNull;
import android.support.v7.recyclerview.extensions.ListAdapter;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

    public interface OnListInteractionListener {
        void onItemClick(TodoItem item);
        boolean onItemLongClick(TodoItem item);
    }

    public TodoItemAdapter(OnListInteractionListener listener) {
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
        holder.item = getItem(position);
        holder.titleView.setText(holder.item.getTitle());
        holder.dateView.setText(Util.dateToString(holder.item.getDate()));
        holder.view.setOnClickListener((View v) -> {
            if (listener != null) {
                listener.onItemClick(holder.item);
            }
        });
        holder.view.setOnLongClickListener((View v) -> {
            if (listener != null) {
                return listener.onItemLongClick(holder.item);
            }
            return false;
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View view;
        private final TextView titleView;
        private final TextView dateView;
        private TodoItem item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            titleView = view.findViewById(R.id.tv_title);
            dateView = view.findViewById(R.id.tv_date);
        }
    }
}

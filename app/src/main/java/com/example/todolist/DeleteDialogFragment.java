package com.example.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

public class DeleteDialogFragment extends DialogFragment {

    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    private DeleteDialogListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            listener = (DeleteDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement DeleteDialogListener.");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.delete_item_prompt)
                .setPositiveButton(R.string.delete, (dialog, which) -> listener.onDialogPositiveClick(DeleteDialogFragment.this))
                .setNegativeButton(R.string.cancel, (dialog, which) -> listener.onDialogNegativeClick(DeleteDialogFragment.this));
        return builder.create();
    }
}

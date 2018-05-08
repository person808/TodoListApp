package com.example.todolist;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class DeleteDialogFragment extends DialogFragment {

    public interface DeleteDialogListener {
        void onDialogPositiveClick(DialogFragment dialog);
    }

    DeleteDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            listener = (DeleteDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_item_prompt)
                .setMessage(R.string.permanent_action_warning)
                .setPositiveButton(R.string.delete, (dialog, which) -> listener.onDialogPositiveClick(DeleteDialogFragment.this))
                .setNegativeButton(R.string.cancel, ((dialog, which) -> dismiss()));
        return builder.create();
    }
}

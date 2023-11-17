package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.treinchauffeur.roosterbuilder.R;

import java.util.Objects;

public abstract class DeleteDialog extends Dialog {
    private final MaterialTextView mainText;

    /**
     * A generic 'Material You' deletion dialog that can be used in all kinds of different applications.
     * @author Treinchauffeur
     * @param context the app's context
     */
    @SuppressLint("SetTextI18n")
    public DeleteDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.deletion_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton buttonDelete, buttonCancel;
        mainText = findViewById(R.id.deleteDialogMainText);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonCancel = findViewById(R.id.buttonCancelDatabase);

        buttonDelete.setOnClickListener(v -> onDeletePressed());
        buttonCancel.setOnClickListener(v -> dismiss());
    }

    /**
     * Defines what we should do when the 'Delete' button has been pressed by the user.
     */
    public abstract void onDeletePressed();

    /**
     * Sets the main text that should be displayed to the user.
     * @param text the body text of the dialog.
     */
    public void setMainText(String text) {
        mainText.setText(text);
    }

}

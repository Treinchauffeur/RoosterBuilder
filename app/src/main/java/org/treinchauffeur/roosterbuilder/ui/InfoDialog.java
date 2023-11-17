package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.treinchauffeur.roosterbuilder.R;

import java.util.Objects;

public class InfoDialog extends Dialog {
    private final MaterialTextView mainText;

    /**
     * A generic material you deletion dialog that can be used in all kinds of different applications.
     * @param context the app's context
     */
    @SuppressLint("SetTextI18n")
    public InfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.info_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialButton buttonUnderstood;
        mainText = findViewById(R.id.infoDialogMainText);
        buttonUnderstood = findViewById(R.id.buttonUnderstood);

        buttonUnderstood.setOnClickListener(v -> dismiss());
    }

    /**
     * Sets the main text that should be displayed to the user.
     * @param text the body text of the dialog.
     */
    public void setMainText(String text) {
        mainText.setText(text);
    }

}

package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;

import java.util.HashMap;
import java.util.Objects;

public abstract class DeleteDialog extends Dialog {

    public static final String TAG = "DeletionDialog";
    private final MaterialTextView mainText;

    /**
     * A generic material you deletion dialog that can be used in all kinds of different applications.
     * @param context
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

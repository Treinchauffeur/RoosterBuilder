package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.misc.Logger;
import org.treinchauffeur.roosterbuilder.obj.Mentor;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;
import org.treinchauffeur.roosterbuilder.obj.StoredPupil;

import java.util.HashMap;
import java.util.Objects;

public class DatabaseDialog extends Dialog {

    public static final String TAG = "PupilDialog";
    @SuppressLint("SetTextI18n")
    public DatabaseDialog(@NonNull Context context, MainActivity activity) {
        super(context);
        setContentView(R.layout.databases_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        TextInputLayout pupilsField = findViewById(R.id.pupilsDatabaseField);
        TextInputLayout mentorField = findViewById(R.id.mentorDatabaseField);

        MaterialButton buttonSave, buttonCancel;
        buttonSave = findViewById(R.id.buttonSaveDatabase);
        buttonCancel = findViewById(R.id.buttonCancelDatabase);

        SharedPreferences sharedPreferences = context.getSharedPreferences("RoosterBot", Context.MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, StoredPupil>());
        String json=sharedPreferences.getString("SavedPupilsMap",defValue);
        Objects.requireNonNull(pupilsField.getEditText()).setText(json);

        String mentorValue = new Gson().toJson(new HashMap<String, Mentor>());
        String mentorJson = sharedPreferences.getString("SavedMentorMap",mentorValue);
        Objects.requireNonNull(mentorField.getEditText()).setText(mentorJson);

        //Button actions
        buttonSave.setOnClickListener(v -> {
            if (!isJson(Objects.requireNonNull(pupilsField.getEditText()).getText().toString())) {
                pupilsField.setError("Geen geldige JSON");
                return;
            }

            if (!isJson(Objects.requireNonNull(mentorField.getEditText()).getText().toString())) {
                mentorField.setError("Geen geldige JSON");
                return;
            }

            String jsonString = Objects.requireNonNull(pupilsField.getEditText()).getText().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("SavedPupilsMap", jsonString);
            String mentorString = Objects.requireNonNull(mentorField.getEditText()).getText().toString();
            editor.putString("SavedMentorMap", mentorString);

            editor.apply();

            dismiss();
            //activity.syncSavedPupils();
            if(activity.pupilsMap.size() > 2)
                activity.displayData();
        });

        buttonCancel.setOnClickListener(v -> dismiss());
    }

    public static boolean isJson(String Json) {
        try {
            new JSONObject(Json);
        } catch (JSONException ex) {
            try {
                new JSONArray(Json);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}

package org.treinchauffeur.roosterbuilder.ui;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import org.treinchauffeur.roosterbuilder.MainActivity;
import org.treinchauffeur.roosterbuilder.R;
import org.treinchauffeur.roosterbuilder.obj.Pupil;
import org.treinchauffeur.roosterbuilder.obj.Shift;

import java.util.Objects;

public class PupilDialog extends Dialog {

    public static final String TAG = "PupilDialog";
    @SuppressLint("SetTextI18n")
    public PupilDialog(@NonNull Context context, MainActivity activity, Pupil pupil) {
        super(context);
        setContentView(R.layout.details_dialog);
        Objects.requireNonNull(getWindow()).setBackgroundDrawableResource(android.R.color.transparent);

        MaterialTextView nameView = findViewById(R.id.dialogPupilNameView);
        TextInputLayout fullNameField = findViewById(R.id.pupilNameLayout);
        TextInputLayout phoneNumberField = findViewById(R.id.pupilPhoneLayout);
        TextInputLayout emailField = findViewById(R.id.pupilMailLayout);

        MaterialTextView monday = findViewById(R.id.mondayShiftText);
        MaterialTextView tuesday = findViewById(R.id.tuesdayShiftText);
        MaterialTextView wednesday = findViewById(R.id.wednesdayShiftText);
        MaterialTextView thursday = findViewById(R.id.thursdayShiftText);
        MaterialTextView friday = findViewById(R.id.fridayShiftText);
        MaterialTextView saturday = findViewById(R.id.saturdayShiftText);
        MaterialTextView sunday = findViewById(R.id.sundayShiftText);

        MaterialButton buttonSave, buttonCancel;
        buttonSave = findViewById(R.id.buttonSavePupil);
        buttonCancel = findViewById(R.id.buttonCancelPupil);

        nameView.setText(pupil.getNeatName());
        fullNameField.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameView.setText(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(fullNameField.getEditText()).setText(pupil.getNeatName());
        Objects.requireNonNull(phoneNumberField.getEditText()).setText(pupil.getPhoneNumber());
        Objects.requireNonNull(emailField.getEditText()).setText(pupil.getEmail());

        //Warn user if certain details aren't known.
        if (emailField.getEditText().getText().length() < 2) emailField.setError("Geen e-mailadres bekend!");
        if (phoneNumberField.getEditText().getText().length() < 2) phoneNumberField.setError("Geen telefoonnummer bekend!");

        //If no email is known, we'll help the user along by generating a part of it. Same goes for phone number.
        emailField.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (emailField.getEditText().getText().length() < 2) {
                if (fullNameField.getEditText().getText().toString().length() > pupil.getName().length()) {
                    String address = fullNameField.getEditText().getText().toString().toLowerCase() + "@ns.nl";
                    address = address.replaceFirst(" ", ".");
                    address = address.replace(" ", "");
                    address = address.replace("ü", "ue");
                    address = address.replace("ä", "ae");
                    address = address.replace("ö", "oe");
                    address = address.replace("ë", "e");
                    Objects.requireNonNull(emailField.getEditText()).setText(address);
                } else {
                    Objects.requireNonNull(emailField.getEditText()).setText(pupil.generateApproxEmailAddress());
                }
            }
        });

        phoneNumberField.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (phoneNumberField.getEditText().getText().length() < 2) {
                Objects.requireNonNull(phoneNumberField.getEditText()).setText("06");
            }
        });

        //Button actions
        buttonSave.setOnClickListener(v -> {

            //Save full name
            if(fullNameField.getEditText().getText().toString().length() > pupil.getName().length()) {
                Objects.requireNonNull(activity.pupilsMap.get(pupil.getName())).setNeatName(fullNameField.getEditText().getText().toString());
                Objects.requireNonNull(activity.savedPupils.get(pupil.getName())).setNeatName(fullNameField.getEditText().getText().toString());
                activity.saveData();
            } else {
                fullNameField.setError("Voornaam + Achternaam");
                return;
            }

            //Save phone number
            if(phoneNumberField.getEditText().getText().toString().startsWith("06") && phoneNumberField.getEditText().getText().toString().length() == 10) {
                Objects.requireNonNull(activity.pupilsMap.get(pupil.getName())).setPhoneNumber(phoneNumberField.getEditText().getText().toString());
                Objects.requireNonNull(activity.savedPupils.get(pupil.getName())).setPhone(phoneNumberField.getEditText().getText().toString());
                activity.saveData();
            } else {
                phoneNumberField.setError("Formaat: 0612345678");
                return;
            }

            //Save email
            if(!emailField.getEditText().getText().toString().endsWith("@ns.nl")) {
                emailField.setError("E-mailadres eindigt niet op '@ns.nl'");
                return;
            } else if(emailField.getEditText().getText().toString().length() < (pupil.getName().split(" ")[0].length() + 6)) {
                emailField.setError("E-mailadres is te kort");
                return;
            } else {
                Objects.requireNonNull(activity.pupilsMap.get(pupil.getName())).setEmail(emailField.getEditText().getText().toString());
                Objects.requireNonNull(activity.savedPupils.get(pupil.getName())).setEmail(emailField.getEditText().getText().toString());
                activity.saveData();
            }

            dismiss();
            activity.displayData();
            activity.saveData();
        });

        buttonCancel.setOnClickListener(v -> dismiss());

        monday.setText(pupil.getShift(Shift.MAANDAG).toString());
        tuesday.setText(pupil.getShift(Shift.DINSDAG).toString());
        wednesday.setText(pupil.getShift(Shift.WOENSDAG).toString());
        thursday.setText(pupil.getShift(Shift.DONDERDAG).toString());
        friday.setText(pupil.getShift(Shift.VRIJDAG).toString());
        saturday.setText(pupil.getShift(Shift.ZATERDAG).toString());
        sunday.setText(pupil.getShift(Shift.ZONDAG).toString());
    }
}

package org.treinchauffeur.roosterbuilder.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import org.treinchauffeur.roosterbuilder.R
import org.treinchauffeur.roosterbuilder.obj.Pupil


class EditableTextView : LinearLayout {

    private var context: Context
    private lateinit var size: String
    private var editable: Boolean = false
    lateinit var editButton: MaterialCardView
    lateinit var saveButton: MaterialButton
    lateinit var editField: TextInputLayout
    lateinit var textView: MaterialTextView

    /**
     * View created by XML
     */
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.context = context
        init()
    }

    /**
     * View created programmatically.
     * @noinspection unused
     */
    constructor(context: Context, pupil: Pupil, size: String) : super(context) {
        this.context = context
        init()
        this.size = size
    }

    fun init() {
        inflate(context, R.layout.large_editable_text_view, this)
        editButton = findViewById(R.id.etv_edit_button)
        saveButton = findViewById(R.id.etv_save_button)
        editField = findViewById(R.id.etv_name_editable)
        textView = findViewById(R.id.etv_name)
        setEditable(false)

        editButton.setOnClickListener {
            setEditable(true)
        }

        saveButton.setOnClickListener {
            setEditable(false)
        }
    }

    fun setEditable(editable: Boolean) {
        if(editable) {
            findViewById<LinearLayout>(R.id.etv_non_edit_layout).visibility = View.GONE
            findViewById<LinearLayout>(R.id.etv_edit_layout).visibility = View.VISIBLE
            this.editable = true
            editField.editText!!.setSelectAllOnFocus(true)
            editField.editText!!.requestFocus()

            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editField.editText!!, InputMethodManager.SHOW_IMPLICIT)
            editField.editText!!.setText(textView.text.toString())
            editField.editText!!.selectAll()
        } else {
            this.editable = false
            textView.text = editField.editText!!.text
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
            findViewById<LinearLayout>(R.id.etv_non_edit_layout).visibility = View.VISIBLE
            findViewById<LinearLayout>(R.id.etv_edit_layout).visibility = View.GONE
        }
    }

    fun setName(name: String) {
        findViewById<TextInputLayout>(R.id.etv_name_editable).editText?.setText(name)
        findViewById<MaterialTextView>(R.id.etv_name).text = name
    }

    fun getText(): String {
        return editField.editText!!.text.toString()
    }
    fun setError() {
        editField.editText!!.error = "Sla eerst op!"
    }

}
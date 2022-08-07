package ca.sfu.BlueRadar.ui.menu

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import ca.sfu.BlueRadar.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * MyRunsDialogFragments is a DialoogFragments class that implements the on-click listener
 * The class creates different dialogs according to the corresponding companion object
 *
 */
class MyRunsDialogFragments : DialogFragment(){
    companion object{
        const val DIALOG_KEY = "dialog"
        const val DIALOG_CAMERA = 0
        const val DIALOG_GALLERY = 1
        const val DIALOG_SELECT_PHOTO = 0
    }
    private var key = ""

    var itemText:String = ""
    var calendarInp = ""

    /**
     * onCreateDialog creates dialogs according to the bundle dialogId.
     * @param savedInstanceState is the bundle object
     * @return ret is the dialog object, could either be AlertDialog,DatePickerDialog, or TimePickerDialog
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        lateinit var ret: Dialog
        val bundle = arguments
        val dialogId = bundle?.get(DIALOG_KEY)
        var builder = AlertDialog.Builder(requireActivity())
        if (dialogId != null) {
            if (dialogId == DIALOG_SELECT_PHOTO) {
                val view: View =
                    requireActivity().layoutInflater.inflate(R.layout.fragment_dialog, null)
                builder.setView(view)
                builder.setTitle("Photo Upload Selection\n")
                builder.setItems(R.array.profile_photo_selections) { dia, pos ->
                    (activity as SettingsAccountActivity).onPhotoPicker(pos)
                }
            }
            ret = builder.create()
        }
        return ret
    }
}
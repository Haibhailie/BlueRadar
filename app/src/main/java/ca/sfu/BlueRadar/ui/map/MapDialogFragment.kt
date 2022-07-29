package ca.sfu.BlueRadar.ui.map

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ca.sfu.BlueRadar.R

class MapDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        var view = requireActivity().layoutInflater.inflate(R.layout.fragment_map_dialog, null)

        builder.setView(view)
        return builder.create()
    }
}
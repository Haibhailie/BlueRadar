package ca.sfu.BlueRadar.ui.menu

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfilePhotoViewModel: ViewModel() {
    val userImg = MutableLiveData<Bitmap>()

}
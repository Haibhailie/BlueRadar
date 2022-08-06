package ca.sfu.BlueRadar.ui.menu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import ca.sfu.BlueRadar.R
import ca.sfu.BlueRadar.util.Util
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.concurrent.schedule

class SettingsAccountActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    private lateinit var  tempImgUri: Uri
    private lateinit var prevImgUri: Uri
    private val tempImgFileName = "new_img.jpg"
    private val prevImgFileName = "prev_img.jpg"
    private lateinit var cameraResult: ActivityResultLauncher<Intent>
    private lateinit var galleryResult: ActivityResultLauncher<String>

    private lateinit var myViewModel: ProfilePhotoViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var dataName: EditText
    private lateinit var dataEmail: EditText
    private lateinit var dataPhone: EditText
    private lateinit var currentName: TextView

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var phone: String
    private var containsEmptyFields = false
    private var saveStatus = false

    /**
     * Sets the view, asks for user permission of camera access, loads and save profile data,
     * @param savedInstanceState is the bundle object
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        // load profile data
        loadProfile()

        imageView = findViewById(R.id.profilePhoto)

        val imgFile = File(getExternalFilesDir(null), tempImgFileName)
        tempImgUri = FileProvider.getUriForFile(this,"ca.sfu.BlueRadar", imgFile)

        loadPhoto(imgFile)

        galleryResult = registerForActivityResult(ActivityResultContracts.GetContent()){ uri: Uri? ->
            uri?.let {
                val bitmapGallery = Util.getBitmap(this, uri)
                imageView.setImageBitmap(bitmapGallery)
                val out = FileOutputStream(imgFile)
                bitmapGallery.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK) {
                val bitmap = Util.getBitmap(this, tempImgUri)
                imageView.setImageBitmap(bitmap)
            }
        }
        imageView.setOnClickListener {
            Log.d("exs_onChangePhoto", "in onChangePhotoClicked() and launching camera")
            val myDialog = MyRunsDialogFragments()
            val bundle = Bundle()
            bundle.putInt(MyRunsDialogFragments.DIALOG_KEY, MyRunsDialogFragments.DIALOG_SELECT_PHOTO)
            myDialog.arguments = bundle
            myDialog.show(supportFragmentManager, "my dialog")
        }

        val backButton : Button = findViewById(R.id.profileBackButton)
        val saveButton : Button = findViewById(R.id.saveButton)
        backButton.setOnClickListener{
            onCancelButtonClicked()
        }
        saveButton.setOnClickListener() {
            onSaveButtonClicked()
        }
    }

    /**
     * Launch different intents according to the selected photo upload choice
     * @param item is the choice Integer that determines which intent to launch
     */
    internal fun onPhotoPicker(item: Int) {
        when(item) {

            MyRunsDialogFragments.DIALOG_CAMERA -> {
                Log.d("exs_onPhotoPicker", "camera in action")
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
                cameraResult.launch(intent)
            }

            MyRunsDialogFragments.DIALOG_GALLERY -> {
                Log.d("exs_onPhotoPicker", "gallery in action")
                val intent2 = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, tempImgUri)
                intent2.type = "image/*"
                galleryResult.launch("image/*")
            }
        }
    }

    /**
     * On-click of the "SAVE" button, saves all user data, delete previous image file, finish activity.
     */
    private fun onSaveButtonClicked() {
        Log.d("exs_onSaveButton", "in onSaveButtonClicked()")
        saveProfile()
        saveStatus = true
        val oldImgFile = File(getExternalFilesDir(null), prevImgFileName)
        oldImgFile.delete()
        val text:CharSequence = if (containsEmptyFields) {
            "Application Saved. There are some empty fields."
        } else {
            "Application Saved."
        }

        val toast: Toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast.show()
        Timer().schedule(500) {
            finish()
        }
    }

    /**
     * On-click of the "CANCEL" button, restore previous image file status, delete new image file, finish activity
     */
    private fun onCancelButtonClicked() {
        Log.d("exs_onCancelButton", "in onCancelButtonClicked()")
        if (saveStatus) this.finish()

        val newImgFile = File(getExternalFilesDir(null), tempImgFileName)
        val oldImgFile = File(getExternalFilesDir(null), prevImgFileName)

        if (oldImgFile.exists()) {
            Log.d("exs_onCancelButton", "in onCancelButtonClicked() and restoring old image to storage")
            oldImgFile.copyTo(newImgFile, true)
            oldImgFile.delete()
        } else if (!saveStatus) {
            Log.d("exs_onCancelButton", "in onCancelButtonClicked() and old image to storage DNE, deleting new image")
            newImgFile.delete()
        }
        this.finish()
    }

    /**
     * Loads and sets the ImageView of Profile Photo with the image file if it is not null.
     * Copies previous image file to prevImgUri, and sets ImageView with newly taken photo.
     * @param imgFile is the File object
     */
    private fun loadPhoto(imgFile: File) {
        if(imgFile.exists()) {
            Log.d("exs_loadPhoto", "imgFile exists, loading previous image")
            val oldImgFile = File(getExternalFilesDir(null), prevImgFileName)
            val obj2 = imgFile.copyTo(oldImgFile, true)
            prevImgUri = FileProvider.getUriForFile(this,"ca.sfu.BlueRadar", obj2)
            val bitmap = Util.getBitmap(this, tempImgUri)
            imageView.setImageBitmap(bitmap)
        }
    }

    /**
     * Load all user data to appropriate views if data is stored with shared preferences.
     * If no user data is stored inside shared preferences, default values will be displayed in views.
     */
    private fun loadProfile() {
        Log.d("exs_loadProfile", "in loadProfile()")
        sharedPreferences = this.getSharedPreferences(getString(R.string.profile_pref_name), Context.MODE_PRIVATE)

        dataName = this.findViewById(R.id.nameField)
        dataEmail = findViewById(R.id.emailField)
        dataPhone = findViewById(R.id.phoneField)
        currentName = findViewById(R.id.usernameDisplay)

        name = sharedPreferences.getString("name","").toString()
        email = sharedPreferences.getString("email","").toString()
        phone = sharedPreferences.getString("phone","").toString()

        currentName.text = name
        dataName.setText(name)
        dataEmail.setText(email)
        dataPhone.setText(phone)

    }

    /**
     * Save user data to shared preferences with SharedPreferences.Editor object.
     */
    private fun saveProfile() {
        Log.d("exs_saveProfile", "in saveProfile()")
        sharedPreferences = this.getSharedPreferences(getString(R.string.profile_pref_name), Context.MODE_PRIVATE) ?: return
        dataName = findViewById(R.id.nameField)
        dataEmail = findViewById(R.id.emailField)
        dataPhone = findViewById(R.id.phoneField)


        with(sharedPreferences.edit()) {
            Log.d("exs_saveProfile", "in saveProfile() and editing sharedPreferences")
            putString("name", dataName.text.toString())
            putString("email", dataEmail.text.toString())
            putString("phone", dataPhone.text.toString())

            apply()
        }
        val arrFields = arrayOf(dataName,dataEmail,dataPhone)

        if((imageView.drawable == null) or hasEmptyFields(arrFields)) containsEmptyFields = true

        // myViewModel is adapted from class code of Lecture 3: Using the Camera and Data Storage.
        myViewModel = ViewModelProvider(this)[ProfilePhotoViewModel::class.java]

        myViewModel.userImg.observe(this) {
            Log.d("exs_saveProfile", "in saveProfile() and observing change in image")
            val bitmap = Util.getBitmap(this, tempImgUri)
            imageView.setImageBitmap(bitmap)
        }
    }

    /**
     * Checks if there is any empty field from the fields array
     *
     * @param fields is the fields array of the EditText view objects
     * @return boolean, true if any of fields is null/blank/empty
     *
     */
    private fun hasEmptyFields(fields: Array<EditText>): Boolean {
        var curr: EditText?
        for (item in fields) {
            curr = item
            if(curr.text.isNullOrBlank() or curr.text.isNullOrEmpty()) return true
        }
        return false
    }
}
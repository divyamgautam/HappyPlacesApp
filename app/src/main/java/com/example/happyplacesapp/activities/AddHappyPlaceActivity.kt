package com.example.happyplacesapp.activities

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.core.view.isEmpty
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplacesapp.models.HappyPlaceModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener{

    private var binding: ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
    private var mHappyPlaceDetails: HappyPlaceModel? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        //setSupportActionBar(binding?.toolbarAddPlace)
        setContentView(binding?.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS)){
            mHappyPlaceDetails = intent.getSerializableExtra(MainActivity.EXTRA_PLACE_DETAILS) as HappyPlaceModel
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{_, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        updateDateInView()
        if(mHappyPlaceDetails != null){
            supportActionBar?.title = "Edit Happy Place"
            binding?.title?.setText(mHappyPlaceDetails!!.title)
            binding?.description?.setText(mHappyPlaceDetails!!.description)
            binding?.date?.setText(mHappyPlaceDetails!!.date)
            binding?.location?.setText(mHappyPlaceDetails!!.location)
            saveImageToInternalStorage = Uri.parse(mHappyPlaceDetails!!.image)
            binding?.image?.setImageURI(saveImageToInternalStorage)
            binding?.btnAdd?.setText("UPDATE")
        }
        binding?.date?.setOnClickListener(this)
        binding?.tvSelect?.setOnClickListener(this)
        binding?.btnAdd?.setOnClickListener(this)
    }

    //Implementing the onCLickListener for the TILs in the activity
    override fun onClick(v: View?) {
        when(v!!){
            binding?.tilDate-> {
                DatePickerDialog(this@AddHappyPlaceActivity, dateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            }
            binding?.tvSelect -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo from Gallery", "Capture Photo from Camera")
                pictureDialog.setItems(pictureDialogItems){
                    _,which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> takePhotoFromCamera()
                    }
                }
            }
            binding?.btnAdd ->{
              when{
                  binding?.title?.text.isNullOrEmpty() -> {
                      Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                  }
                  binding?.description?.text.isNullOrEmpty() -> {
                      Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                  }
                  binding?.location?.text.isNullOrEmpty() -> {
                      Toast.makeText(this, "Please enter a location", Toast.LENGTH_SHORT).show()
                  }
                  saveImageToInternalStorage == null ->{
                      Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                  }else->{
                         val happyPlaceModel = HappyPlaceModel(0,
                            binding?.title?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            binding?.description?.text.toString(),
                            binding?.date?.text.toString(),
                            binding?.location?.text.toString(),
                            mLatitude,
                            mLongitude)

                         val dbHandler = DatabaseHandler(this)
                         val addHappyPlace = dbHandler.addHappyPlace(happyPlaceModel)
                         if (addHappyPlace > 0){
                             setResult(Activity.RESULT_OK)
                             finish()
                         }
                  }
              }
            }
        }
    }
    //Printing out/showing the selected date in text input layout for date
    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = java.text.SimpleDateFormat(myFormat, Locale.getDefault())
        binding?.date?.setText(sdf.format(cal.time).toString())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == GALLERY){
                if(data != null){
                    val contentURI = data.data
                    try{
                        val selectedImageBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, contentURI)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        binding?.image?.setImageBitmap(selectedImageBitmap)
                    }catch (e: IOException){
                        e.printStackTrace()
                        Toast.makeText(this@AddHappyPlaceActivity, "Failed to load image from gallery!", Toast.LENGTH_SHORT).show()
                    }
                }
            }else if(requestCode == CAMERA){
                val thumbnail: Bitmap = data!!.extras!!.get("data") as Bitmap
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail)
                binding?.image?.setImageBitmap(thumbnail)
            }
        }
    }
    private fun choosePhotoFromGallery(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if(report.areAllPermissionsGranted()){
                        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(galleryIntent, GALLERY)

                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: MutableList<PermissionRequest>,token: PermissionToken) {
                    showRationaleForPermissions()
                }
            }).onSameThread().check()
    }

    private fun takePhotoFromCamera(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if(report.areAllPermissionsGranted()){
                        val galleryIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        startActivityForResult(galleryIntent, CAMERA)

                    }
                }

                override fun onPermissionRationaleShouldBeShown(permission: MutableList<PermissionRequest>,token: PermissionToken) {
                    showRationaleForPermissions()
                }
            }).onSameThread().check()
    }

    private fun showRationaleForPermissions(){
         AlertDialog.Builder(this).setMessage(" " +
                 "Seems like you have not granted the required permissions"
                  + "for this feature. Please grant permisions in"+
                  "Application Settings").setPositiveButton("GO TO SETTINGS"){
                      _,_ ->
                        try{
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package",packageName,null)
                            intent.data = uri
                            startActivity(intent)
                        }catch(e: ActivityNotFoundException){
                            e.printStackTrace()
                        }
         }.setNegativeButton("CANCEL"){
             dialog, _->
                dialog.dismiss()
         }.show()
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap):Uri{
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)
    }
    companion object{
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"
    }
}
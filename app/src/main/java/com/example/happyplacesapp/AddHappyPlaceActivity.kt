package com.example.happyplacesapp

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.media.audiofx.Equalizer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import com.example.happyplacesapp.databinding.ActivityAddHappyPlaceBinding
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), View.OnClickListener{

    private var binding: ActivityAddHappyPlaceBinding? = null
    private var cal = Calendar.getInstance()
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddHappyPlaceBinding.inflate(layoutInflater)
        setSupportActionBar(binding?.toolbarAddPlace)
        setContentView(binding?.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding?.toolbarAddPlace?.setNavigationOnClickListener {
            onBackPressed()
        }

        dateSetListener = DatePickerDialog.OnDateSetListener{view, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
        }
        binding?.date?.setOnClickListener(this)
        binding?.llImage?.setOnClickListener(this)
    }

    //Implementing the onCLickListener for the TILs in the activity
    override fun onClick(v: View?) {
        when(v!!){
            binding?.date -> {
                DatePickerDialog(this@AddHappyPlaceActivity, dateSetListener, cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH))
            }
            binding?.llImage -> {
                val pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Select Action")
                val pictureDialogItems = arrayOf("Select Photo from Gallery", "Capture Photo from Camera")
                pictureDialog.setItems(pictureDialogItems){
                    _,which ->
                    when(which){
                        0 -> choosePhotoFromGallery()
                        1 -> Toast.makeText(this,"Feature to b implemented soon",Toast.LENGTH_SHORT).show()
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

    private fun choosePhotoFromGallery(){
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object: MultiplePermissionsListener{

                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if(report.areAllPermissionsGranted()){
                        Toast.makeText(this@AddHappyPlaceActivity, "All Permissions Granted!!", Toast.LENGTH_SHORT).show()
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
}
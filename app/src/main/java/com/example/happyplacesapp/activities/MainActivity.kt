   package com.example.happyplacesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Adapter
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.happyplacesapp.activities.AddHappyPlaceActivity
import com.example.happyplacesapp.adapters.HappyPlacesAdapter
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.databinding.ActivityMainBinding
import com.example.happyplacesapp.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*
import pl.kitek.rvswipetodelete.SwipeToEditCallback

   class MainActivity : AppCompatActivity() {

       private var binding: ActivityMainBinding? = null

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding?.root)
            binding?.fabAddHappyPlace?.setOnClickListener {
                val intent = Intent(this, AddHappyPlaceActivity::class.java)
                startActivityForResult(intent, ADD_HAPPY_PLACE_REQUEST_CODE)
            }
            getHappyPlacesFromLocalDB()
        }

        private fun setUpHappyPlaceRV(happyPlaceList: ArrayList<HappyPlaceModel>){
            binding?.rvHappyPlaces?.layoutManager = LinearLayoutManager(this)
            binding?.rvHappyPlaces?.setHasFixedSize(true)
            val placesAdapter = HappyPlacesAdapter(this, happyPlaceList)
            binding?.rvHappyPlaces?.adapter = placesAdapter
            placesAdapter.setOnClickListener(object : HappyPlacesAdapter.onClickListener{
                override fun onClick(position: Int, model: HappyPlaceModel) {
                    val intent = Intent(this@MainActivity, HappyPlaceDetailActivity:: class.java)
                    intent.putExtra(EXTRA_PLACE_DETAILS, model)
                    startActivity(intent)
                }
            })
            val editSwipeHandler = object: SwipeToEditCallback(this){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val adapter = rvHappyPlaces.adapter as HappyPlacesAdapter
                    adapter.notifyEditItem(this@MainActivity, viewHolder.adapterPosition, ADD_HAPPY_PLACE_REQUEST_CODE)
                }
            }
            val editTouchHelper = ItemTouchHelper(editSwipeHandler)
            editTouchHelper.attachToRecyclerView(rvHappyPlaces)
        }

        private fun getHappyPlacesFromLocalDB(){
            val dbHandler = DatabaseHandler(this)
            val getHappyPlaceList: ArrayList<HappyPlaceModel> = dbHandler.getHappyPlacesList()
            if(getHappyPlaceList.size > 0){
                binding?.rvHappyPlaces?.visibility = View.VISIBLE
                binding?.tvNoRecords?.visibility = View.GONE
                setUpHappyPlaceRV(getHappyPlaceList)
            }else{
                binding?.rvHappyPlaces?.visibility = View.GONE
                binding?.tvNoRecords?.visibility = View.VISIBLE
            }
        }
       override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
           super.onActivityResult(requestCode, resultCode, data)
           if(requestCode == ADD_HAPPY_PLACE_REQUEST_CODE){
               if (resultCode == RESULT_OK){
                   getHappyPlacesFromLocalDB()
               }else{
                   Log.e("Activity", "Cancelled or Back Pressed")
               }
           }
       }
        companion object{
            var ADD_HAPPY_PLACE_REQUEST_CODE = 1
            var EXTRA_PLACE_DETAILS = "extra_place_details"
        }
}
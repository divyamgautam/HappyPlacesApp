package com.example.happyplacesapp.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.happyplacesapp.R

abstract class SwipeToEditCallback(context: Context): ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    private val editIcon = ContextCompat.getDrawable(context, R.drawable.ic_baseline_edit_24)
    private val intrinsicWidth = editIcon!!.intrinsicWidth
    private val intrinsicHeight = editIcon!!.intrinsicHeight
    private val background = ColorDrawable()
    private val backgroundColor = Color.parseColor("#24AE05")
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuffXfermode) }


}
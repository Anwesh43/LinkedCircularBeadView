package com.anwesh.uiprojects.linkedcircularbeadview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.anwesh.uiprojects.circularbeadview.CircularBeadView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CircularBeadView.create(this)
    }
}

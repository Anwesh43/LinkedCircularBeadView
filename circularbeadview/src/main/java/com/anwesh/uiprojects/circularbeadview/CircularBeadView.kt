package com.anwesh.uiprojects.circularbeadview

/**
 * Created by anweshmishra on 03/08/18.
 */

import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color

val nodes : Int = 6

val color : Int = Color.parseColor("#CCE729")

fun Canvas.drawBeadNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val l : Float = 3 * Math.min(w, h) / 8
    val r : Float = (2 * Math.PI.toFloat()) / (2.2f * nodes)
    val deg : Float = 360f / nodes
    val sc1 : Float = Math.min(0.5f, scale)
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f))
    paint.color = color
    save()
    translate(w / 2, h / 2)
    save()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    restore()
    rotate(deg * i)
    save()
    translate(l * (1 - sc1), 0f)
    paint.style = Paint.Style.FILL
    drawCircle(0f, 0f, r * (1 - sc2), paint)
    restore()
    restore()
}
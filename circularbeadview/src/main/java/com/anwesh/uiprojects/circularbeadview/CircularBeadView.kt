package com.anwesh.uiprojects.circularbeadview

/**
 * Created by anweshmishra on 03/08/18.
 */

import android.app.Activity
import android.view.View
import android.view.MotionEvent
import android.content.Context
import android.graphics.Paint
import android.graphics.Canvas
import android.graphics.Color
import android.util.Log

val nodes : Int = 6

val color : Int = Color.parseColor("#4CAF50")

fun Canvas.drawBeadNode(i : Int, scale : Float, paint : Paint) {
    val w : Float = width.toFloat()
    val h : Float = height.toFloat()
    val l : Float = 3 * Math.min(w, h) / 8
    val r : Float = (2 * Math.PI.toFloat() * l) / (2.2f * nodes * 5)
    val deg : Float = 360f / nodes
    val sc1 : Float = Math.min(0.5f, scale) * 2
    val sc2 : Float = Math.min(0.5f, Math.max(0f, scale - 0.5f)) * 2
    paint.color = color
    save()
    translate(w / 2, h / 2)
    save()
    paint.style = Paint.Style.STROKE
    paint.strokeWidth = Math.min(w, h) / 60
    paint.strokeCap = Paint.Cap.ROUND
    drawCircle(0f, 0f, l, paint)
    restore()
    rotate(deg * i)
    save()
    translate(l * (1 - sc1), 0f)
    paint.style = Paint.Style.FILL
    drawCircle(0f, 0f, r * (1 - sc2), paint)
    restore()
    restore()
}

class CircularBeadView (ctx : Context) : View(ctx) {

    private val paint : Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val renderer : Renderer = Renderer(this)

    override fun onDraw(canvas : Canvas) {
        renderer.render(canvas, paint)
    }

    override fun onTouchEvent(event : MotionEvent) : Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap()
            }
        }
        return true
    }

    data class State(var scale : Float = 0f, var prevScale : Float = 0f, var dir : Float = 0f) {

        fun update(cb : (Float) -> Unit) {
            scale += 0.1f * dir
            if (Math.abs(scale - prevScale) > 1) {
                scale = prevScale + dir
                dir = 0f
                prevScale = scale
                cb(prevScale)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            if (dir == 0f) {
                dir = 1 - 2 * prevScale
                cb()
            }
        }

    }

    data class Animator(var view : View, var animated : Boolean = false) {

        fun animate(cb : () -> Unit) {
            if (animated) {
                cb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                } catch(ex : Exception) {

                }
            }
        }

        fun start(cb : () -> Unit) {
            if (!animated) {
                animated = true
                view.postInvalidate()
            }
        }

        fun stop() {
            if (animated) {
                animated = false
            }
        }
    }

    data class CBNode(var i : Int, val state : State = State()) {

        private var next : CBNode? = null

        private var prev : CBNode? = null

        fun draw(canvas : Canvas, paint : Paint) {
            canvas.drawBeadNode(i, state.scale, paint)
            next?.draw(canvas, paint)
        }

        init {
            addNeighbor()
        }

        fun addNeighbor() {
            if (i < nodes - 1) {
                next = CBNode(i + 1)
                next?.prev = this
            }
        }

        fun startUpdating(cb : () -> Unit) {
            state.startUpdating(cb)
        }

        fun update(cb : (Int, Float) -> Unit) {
            state.update {
                cb(i, state.scale)
            }
        }

        fun getNext(dir : Int, cb : () -> Unit) : CBNode {
            var curr : CBNode? = prev
            if (dir == 1) {
                curr = next
            }
            if (curr != null) {
                return curr
            }
            cb()
            return this
        }
    }

    data class LinkedCircularBead(var i : Int) {

        var curr : CBNode = CBNode(0)

        var dir : Int = 1

        fun draw(canvas : Canvas, paint : Paint) {
            curr.draw(canvas, paint)
        }

        fun update(cb : (Int, Float) -> Unit) {
            curr.update {i, scl ->
                curr = curr.getNext(dir) {
                    dir *= -1
                }
                cb(i, scl)
            }
        }

        fun startUpdating(cb : () -> Unit) {
            curr.startUpdating(cb)
        }
    }

    data class Renderer(var view : CircularBeadView) {

        private val animator : Animator = Animator(view)

        private val lcb : LinkedCircularBead = LinkedCircularBead(0)

        fun render(canvas : Canvas, paint : Paint) {
            canvas.drawColor(Color.parseColor("#212121"))
            lcb.draw(canvas, paint)
            animator.animate {
                lcb.update {i, scl ->
                    animator.stop()
                }
            }
        }

        fun handleTap() {
            lcb.startUpdating {
                animator.start {
                    Log.d("starting animation at", "" + System.currentTimeMillis())
                }
            }
        }
    }

    companion object {
        fun create(activity : Activity) : CircularBeadView {
            val view : CircularBeadView = CircularBeadView(activity)
            activity.setContentView(view)
            return view
        }
    }
}
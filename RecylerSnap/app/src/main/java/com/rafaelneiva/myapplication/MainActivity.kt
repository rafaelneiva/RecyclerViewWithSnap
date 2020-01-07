package com.rafaelneiva.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller.ScrollVectorProvider
import androidx.recyclerview.widget.SnapHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnSnapPositionChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rv.adapter = CustomAdapter()
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = SnapHelperOneByOne()
        snapHelper.attachToRecyclerView(rv)

        val snapOnScrollListener =
            SnapOnScrollListener(
                snapHelper,
                SnapOnScrollListener.Behavior.NOTIFY_ON_SCROLL_STATE_IDLE,
                this
            )
        rv.addOnScrollListener(snapOnScrollListener)

    }

    class CustomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false);
            return CustomViewHolder(v)
        }

        override fun getItemCount(): Int {
            return 10
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        }

    }

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class SnapHelperOneByOne : LinearSnapHelper() {
        override fun findTargetSnapPosition(
            layoutManager: RecyclerView.LayoutManager,
            velocityX: Int,
            velocityY: Int
        ): Int {
            if (layoutManager !is ScrollVectorProvider) {
                return RecyclerView.NO_POSITION
            }
            val currentView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            val myLayoutManager = layoutManager as LinearLayoutManager
            val position1 = myLayoutManager.findFirstVisibleItemPosition()
            val position2 = myLayoutManager.findLastVisibleItemPosition()
            var currentPosition = layoutManager.getPosition(currentView)
            if (velocityX > 400) {
                currentPosition = position2
            } else if (velocityX < 400) {
                currentPosition = position1
            }
            return if (currentPosition == RecyclerView.NO_POSITION) {
                RecyclerView.NO_POSITION
            } else currentPosition

        }
    }

    class SnapOnScrollListener(
        private val snapHelper: SnapHelper,
        var behavior: Behavior = Behavior.NOTIFY_ON_SCROLL,
        var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
    ) : RecyclerView.OnScrollListener() {

        enum class Behavior {
            NOTIFY_ON_SCROLL,
            NOTIFY_ON_SCROLL_STATE_IDLE
        }

        private var snapPosition = RecyclerView.NO_POSITION

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (behavior == Behavior.NOTIFY_ON_SCROLL) {
                maybeNotifySnapPositionChange(recyclerView)
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (behavior == Behavior.NOTIFY_ON_SCROLL_STATE_IDLE
                && newState == RecyclerView.SCROLL_STATE_IDLE
            ) {
                maybeNotifySnapPositionChange(recyclerView)
            }
        }

        private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
            val snapPosition = snapHelper.getSnapPosition(recyclerView)
            val snapPositionChanged = this.snapPosition != snapPosition
            if (snapPositionChanged) {
                onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
                this.snapPosition = snapPosition
            }
        }

        fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
            val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
            val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
            return layoutManager.getPosition(snapView)
        }
    }

    override fun onSnapPositionChange(position: Int) {
        Toast.makeText(this, "Position: $position", Toast.LENGTH_SHORT).show()
    }
}

interface OnSnapPositionChangeListener {
    fun onSnapPositionChange(position: Int)
}

package br.com.marcelo.examples.paginatedlist

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PaginatedRecycleView: RecyclerView{

    @JvmOverloads
    constructor(context: Context,
                attrs: AttributeSet? = null): super(context, attrs)

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context,
                attrs: AttributeSet? = null,
                defStyle: Int = 0): super(context, attrs, defStyle)

    private var linearManager: LinearLayoutManager? = null

    override fun setLayoutManager(layout: LayoutManager?) {
        super.setLayoutManager(layout)

        if(layout is LinearLayoutManager){
            linearManager = layout
        }
    }

    abstract class Adapter<VH: ViewHolder>: RecyclerView.Adapter<VH>(){
        abstract fun loadMoreItems()
        abstract fun getTotalPageCount(): Int
        abstract fun isLastPage(): Boolean
        abstract fun isLoading(): Boolean
    }

    //Can be util
    /*override fun onScrollStateChanged(state: Int) {
        super.onScrollStateChanged(state)
        if (!canScrollVertically(1) && state==SCROLL_STATE_IDLE) {
            (adapter as? Adapter)?.apply{onEndOfList(itemCount)}
        }
    }*/

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        linearManager?.let {
            val visibleItemCount = it.childCount
            val totalItemCount = it.itemCount
            val firstVisibleItemPosition = it.findFirstVisibleItemPosition()

            val mAdapter = (adapter as? Adapter)

            if(mAdapter?.isLoading() == false && !mAdapter.isLastPage()){
                if((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= mAdapter.getTotalPageCount()){

                    mAdapter.loadMoreItems()
                }
            }
        }
    }
}


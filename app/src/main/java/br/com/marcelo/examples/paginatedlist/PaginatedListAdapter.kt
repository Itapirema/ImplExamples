package br.com.marcelo.examples.paginatedlist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.marcelo.examples.R
import kotlinx.android.synthetic.main.fragment_paginated_list_item.view.*



class PaginatedListAdapter(): PaginatedRecycleView.Adapter<RecyclerView.ViewHolder>() {

    private var contentList = mutableListOf<String>()
    private var isLoadingAdded = false
    private var totalPages: Int? = 5

    private var isLoading = false
    private var isLastPage = false
    private var isEnableNext = false
    private var currentPage = PAGE_START

    var onPaginationListener: OnPaginationListener? = null

    companion object{
        const val PAGE_START = 1
        //View Holders
        const val ITEM = 0
        const val FOOTER = 1
    }

    interface OnPaginationListener{
        fun loadNextPage()
    }

    fun addOnPaginationListener(listener: OnPaginationListener){
        onPaginationListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        val inflater= LayoutInflater.from(parent.context)

        return when(viewType){
            ITEM->{
                val v1= inflater.inflate(R.layout.fragment_paginated_list_item, parent, false)
                ContentViewHolder(v1)
            }
            FOOTER ->{
                val v2= inflater.inflate(R.layout.layout_progress_load_list, parent, false)
                FooterViewHolder(v2)
            }
            else-> EmptyViewHolder(View(parent.context))
        }
    }

    override fun getItemCount(): Int {
        return contentList.size
    }

    //Helpers

    fun add(item: String) {
        contentList.add(item)
        notifyItemInserted(contentList.size-1)
    }

    fun addAll(items: List<String>){
        items.forEach{
            add(it)
        }
    }

    fun remove(item: String){
        val position = contentList.indexOf(item)
        if(position > -1){
            contentList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        isLoadingAdded = false
        isEnableNext = false
        while (itemCount > 0){
            getItem(0)?.let{remove(it)}
        }
        //isEnableNext = true
    }

    fun isEmpty(): Boolean{
        return itemCount == 0
    }

    fun addFooter(){
        isLoadingAdded = true
        add("")
    }

    fun removeFooter(){
        isLoadingAdded = false

        var position = contentList.size-1
        getItem(position)?.let {
            contentList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun getItem(position: Int): String?{
        return if(position > 0){contentList[position]}else{null}
    }

    override fun getItemViewType(position: Int): Int {
        return if(position==(contentList.size-1)&&isLoadingAdded){FOOTER}else{ITEM}
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            ITEM ->{
                val item = contentList[position]
                val contentVH = holder as ContentViewHolder
                contentVH.itemText?.text = item
            }
            FOOTER ->{
                //If needs, do something
            }
        }
    }

    //View Holders

    class ContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView? = itemView.tx_item
    }

    class FooterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class EmptyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    //Custom adapter methods
    override fun loadMoreItems() {
        isLoading = true
        currentPage += 1

        onPaginationListener?.loadNextPage()
    }

    override fun getTotalPageCount(): Int {
        return totalPages ?: 1
    }

    override fun isLastPage(): Boolean {
        return isLastPage
    }

    override fun isLoading(): Boolean {
        return isLoading
    }

    fun loadFirstPage(items: List<String>){
        addAll(items)
        if(currentPage <= totalPages ?: 1) addFooter()
        else isLastPage = true
        isEnableNext = true
    }

    fun loadNextPage(items: List<String>){
        if(isEnableNext) {
            removeFooter()
            isLoading = false
            addAll(items)
            if(currentPage != totalPages ?: 1) addFooter()
            else isLastPage = true
        }
    }
}
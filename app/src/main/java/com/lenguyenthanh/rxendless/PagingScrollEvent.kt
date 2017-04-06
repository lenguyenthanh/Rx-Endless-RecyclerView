package com.lenguyenthanh.rxendless

import android.support.v7.widget.LinearLayoutManager
import com.jakewharton.rxbinding.support.v7.widget.RecyclerViewScrollEvent

data class PagingScrollEvent(val displayCount: Int, val totalCount: Int, val firstVisiblePosition: Int){
    fun shouldLoadMore(threshold: Int) = totalCount - displayCount <= firstVisiblePosition + threshold
    fun toPage(size: Int) = Page(size, totalCount / size)
}

data class Page(val pageSize: Int, val pageNumber: Int = 0)

fun Page.toList() = IntRange(this.pageSize * this.pageNumber + 1, (this.pageNumber + 1) * this.pageSize).toList()

fun RecyclerViewScrollEvent.toPagingScrollEvent(layoutManager: LinearLayoutManager): PagingScrollEvent {
    val visibleItemCount = this.view().childCount
    val totalItemCount = layoutManager.itemCount
    val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
    return PagingScrollEvent(visibleItemCount, totalItemCount, firstVisibleItem)
}

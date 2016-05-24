/*
 * Copyright 2016 Thanh Le.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lenguyenthanh.rxendless

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.jakewharton.rxbinding.support.v7.widget.scrollEvents
import kotlinx.android.synthetic.main.activity_main.*
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

class MainActivity() : AppCompatActivity() {

    private val TAG = "RxEndlessRecyclerView"
    private val PAGE_SIZE = 50
    private val THRESHOLD = 5

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var layoutManager: LinearLayoutManager
    private var subscription: Subscription? = null
    private val loadNumberUseCase = LoadNumberUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeData()
    }

    override fun onResume() {
        super.onResume()
        initPagingFlow()
    }

    override fun onPause() {
        super.onPause()
        subscription?.unsubscribe()
    }

    private fun initializeData() {
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        itemsAdapter = ItemsAdapter(this.layoutInflater)
        listItems.layoutManager = this.layoutManager
        listItems.adapter = this.itemsAdapter
        itemsAdapter.replaceData(Page(PAGE_SIZE, 0).toList())
    }

    private fun initPagingFlow() {
        subscription?.unsubscribe()
        subscription = getPagingObservable().subscribe(PagingSubscriber())
    }

    private fun getPagingObservable() = Observable.defer { listItems.scrollEvents() }
            .sample(100, TimeUnit.MILLISECONDS)
            .map { it.toPagingScrollEvent(layoutManager) }
            .filter { it.shouldLoadMore(THRESHOLD) }
            .map { it.toPage(PAGE_SIZE) }
            .distinct()
            .doOnNext { showLoadMore() }
            .flatMap { loadNumberUseCase.loadData(it) }
            .observeOn(AndroidSchedulers.mainThread())

    private fun showLoadMore() {
        runOnUiThread { itemsAdapter.setLoading(true) }
    }

    private fun showError(message: String) {
        Snackbar.make(container, message, Snackbar.LENGTH_SHORT).show()
    }

    private inner class PagingSubscriber : Observer<List<Int>> {

        override fun onCompleted() {
            lnt("onCompleted")
        }

        override fun onNext(t: List<Int>) {
            lnt("onNext: " + t.toString())
            itemsAdapter.setLoading(false)
            itemsAdapter.addData(t)
        }

        override fun onError(e: Throwable) {
            lnt("onError: " + e.toString())
            showError(e.message ?: "Error occur")
            initPagingFlow()
        }
    }

    private fun lnt(message: String) {
        Log.d(TAG, message)
    }
}

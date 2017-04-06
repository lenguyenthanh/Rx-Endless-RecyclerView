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

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

class ItemsAdapter(private val layoutInflater: LayoutInflater) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val VIEW_TYPE = 1
    private val VIEW_PROG = 0
    private val PROG_VALUE = -1

    private var items = emptyList<Int>()

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            items += PROG_VALUE
            notifyItemInserted(items.size - 1)
        } else {
            items = items.take(items.size - 1)
            notifyItemRemoved(items.size)
        }
    }

    override fun getItemViewType(position: Int): Int = if (items[position] == PROG_VALUE) VIEW_PROG else VIEW_TYPE

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TextViewHolder) {
            holder.textView.text = items[position].toString()
        } else if (holder is ProgressViewHolder) {
            holder.progressBar.isIndeterminate = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder? {
        if (VIEW_TYPE == viewType) {
            val view = layoutInflater.inflate(android.R.layout.simple_list_item_1, parent, false)
            return TextViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.progressbar_item, parent, false)
            return ProgressViewHolder(view)
        }
    }

    fun replaceData(items: List<Int>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun addData(items: List<Int>) {
        items.forEach {
            items.forEach {
                if (!this.items.contains(it)) {
                    this.items += it
                    notifyItemInserted(this.items.size - 1)
                }
            }
        }
    }

    class TextViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val textView: TextView = v.findViewById(android.R.id.text1) as TextView

    }

    class ProgressViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val progressBar: ProgressBar = v.findViewById(R.id.progressBar) as ProgressBar

    }
}
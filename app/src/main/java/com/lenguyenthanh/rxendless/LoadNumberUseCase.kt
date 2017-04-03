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

import rx.Observable

class LoadNumberUseCase {

    private val MAX_PAGE = 5

    fun loadData(page: Page): Observable<List<Int>> {
        // simulate network latency. Do not do this ^^
        lnt("loadData $page")
        Thread.sleep(2000)
        if (shouldError()) {
            return Observable.error(RuntimeException("Cannot load more Items"))
        } else {
            if (page.pageNumber < MAX_PAGE) {
                return Observable.just(page.toList())
            } else {
                return Observable.just(emptyList())
            }
        }
    }

    private var count = 0
    private fun shouldError() = count++ % 3 == 0
}

sealed class ItemsState {
    class Success(val items: List<Int>) : ItemsState()
    class Error(val ex: Throwable) : ItemsState()
}
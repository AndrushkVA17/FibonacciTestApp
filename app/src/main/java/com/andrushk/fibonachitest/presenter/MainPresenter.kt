package com.andrushk.fibonachitest.presenter

import android.util.Log
import com.andrushk.fibonachitest.view.MainView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import java.math.BigInteger

@InjectViewState
class MainPresenter : MvpPresenter<MainView>() {

    private var listFibonacci = listOf(Pair(0, BigInteger.valueOf(0)), Pair(1, BigInteger.valueOf(1)))
    private val listSize
        get() = listFibonacci.size

    private var isLoading = false

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getNextNItems(listFibonacci, PAGE_SIZE)
    }

    override fun attachView(view: MainView?) {
        super.attachView(view)
        viewState.updateList(listFibonacci)
    }

    fun getPreviousItems(lastVisiblePosition: Int) {
        if (isLoading || listFibonacci[0].first == 0) return
        val toIndex = if (lastVisiblePosition + COUNT_BEHIND_THE_SCREEN < listSize) {
            lastVisiblePosition + COUNT_BEHIND_THE_SCREEN
        } else {
            listSize
        }
        getPreviousNItems(listFibonacci.subList(0, toIndex), PAGE_SIZE)
    }

    fun getNextItems(firstVisiblePosition: Int) {
        if (isLoading) return
        val fromIndex = if (firstVisiblePosition - COUNT_BEHIND_THE_SCREEN > 0) {
            firstVisiblePosition - COUNT_BEHIND_THE_SCREEN
        } else {
            0
        }
        getNextNItems(listFibonacci.subList(fromIndex, listSize), PAGE_SIZE)
    }

    fun searchItem(item: Int, visibleItemsCount: Int) {
        listFibonacci.find { it.first == item }?.let {
            viewState.showItemAtPosition(listFibonacci.indexOf(it))
        } ?: run {
            if (item < listFibonacci[0].first) {
                getPreviousNItems(
                    listFibonacci,
                    listFibonacci[0].first - item,
                    visibleItemsCount + COUNT_BEHIND_THE_SCREEN,
                    true
                )
            } else {
                getNextNItems(
                    listFibonacci,
                    item - listFibonacci[listSize - 1].first,
                    visibleItemsCount + COUNT_BEHIND_THE_SCREEN,
                    true
                )
            }
        }
    }

    private fun getPreviousNItems(
        showedNumbersList: List<Pair<Int, BigInteger>>,
        n: Int,
        savingSize: Int? = null,
        showPosition: Boolean = false
    ) {
        isLoading = true
        Single.just(Pair(showedNumbersList[0], showedNumbersList[1]))
            .map { firstItems ->
                mutableListOf(firstItems.first, firstItems.second).apply {
                    for (i in 0 until n) {
                        add(0, Pair(get(0).first - 1, get(1).second - get(0).second))
                        savingSize?.let { if (size > it) removeAt(size - 1) }
                        if (get(0).first == 0) break
                    }
                }
            }
            .map { newList ->
                newList.subList(
                    0,
                    savingSize?.let {
                        if (it > newList.size) newList.size else it
                    } ?: newList.size - 2
                )
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ newItems ->
                listFibonacci = savingSize?.let { newItems } ?: newItems + showedNumbersList
                setList(if (showPosition) 0 else null)
            }, {
                isLoading = false
                Log.e("Calculating error ", it.localizedMessage ?: "Undefined error")
            })
    }

    private fun getNextNItems(
        showedNumbersList: List<Pair<Int, BigInteger>>,
        n: Int,
        savingSize: Int? = null,
        showPosition: Boolean = false
    ) {
        isLoading = true
        Single.just(Pair(showedNumbersList[showedNumbersList.size - 2], showedNumbersList[showedNumbersList.size - 1]))
            .map { lastItems ->
                mutableListOf(lastItems.first, lastItems.second).apply {
                    for (i in 0 until n) {
                        add(Pair(get(size - 1).first + 1, get(size - 2).second + get(size - 1).second))
                        savingSize?.let { if (size > it) removeAt(0) }
                    }
                }
            }
            .map { newList ->
                newList.subList(
                    savingSize?.let {
                        if (newList.size - savingSize > 0) newList.size - it else 0
                    } ?: 2,
                    newList.size
                )
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ newItems ->
                listFibonacci = savingSize?.let { newItems } ?: showedNumbersList + newItems
                setList(if (showPosition) listSize - 1 else null)
            }, {
                isLoading = false
                Log.e("Calculating error ", it.localizedMessage ?: "Undefined error")
            })
    }

    private fun setList(showPosition: Int? = null) {
        isLoading = false
        viewState.updateList(listFibonacci, showPosition)
    }

    companion object {
        const val PAGE_SIZE = 10

        const val COUNT_BEHIND_THE_SCREEN = 10
    }
}
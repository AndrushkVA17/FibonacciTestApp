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

    private val fibonacciNumbers = mutableListOf<BigInteger>(BigInteger.valueOf(0), BigInteger.valueOf(1))
    private val numbersCount
        get() = fibonacciNumbers.size

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getNextItems()
    }

    override fun attachView(view: MainView?) {
        super.attachView(view)
        viewState.updateAll(fibonacciNumbers)
    }

    fun getNextItems() {
        getNextNItems(PAGE_SIZE)
    }

    fun searchItem(item: Int) {
        if (item < numbersCount) {
            viewState.showItemAtPosition(item)
        } else {
            getNextNItems(item - numbersCount + 1, item)
        }
    }

    private fun getNextNItems(n: Int, showPosition: Int? = null) =
        Single.just(Pair(fibonacciNumbers[numbersCount - 2], fibonacciNumbers[numbersCount - 1]))
            .map {
                mutableListOf(it.first, it.second).apply {
                    for (i in 0 until n) {
                        add(get(size - 2) + get(size - 1))
                    }
                }
            }
            .map {
                it.subList(2, it.size)
            }
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe( { newItems ->
                fibonacciNumbers.addAll(newItems)
                viewState.addToList(newItems)
                showPosition?.let { viewState.showItemAtPosition(it) }
            }, {
                Log.e("Calculating error ", it.localizedMessage ?: "Undefined error")
            })

    companion object {
        const val PAGE_SIZE = 10
    }
}
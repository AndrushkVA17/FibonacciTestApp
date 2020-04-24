package com.andrushk.fibonachitest.ui

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andrushk.fibonachitest.R
import com.andrushk.fibonachitest.adapter.FibonacciRVAdapter
import com.andrushk.fibonachitest.presenter.MainPresenter
import com.andrushk.fibonachitest.view.MainView
import com.jakewharton.rxbinding3.widget.textChangeEvents
import kotlinx.android.synthetic.main.activity_main.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import java.math.BigInteger
import java.util.concurrent.TimeUnit

class MainActivity : MvpAppCompatActivity(), MainView {

    @InjectPresenter
    internal lateinit var presenter: MainPresenter

    @ProvidePresenter
    fun createMainPresenter() = MainPresenter()

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var fibonacciRVAdapter: FibonacciRVAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initRecyclerView()
        initEditText()
        savedInstanceState?.getInt(RV_POSITION_KEY)?.let { showItemAtPosition(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(RV_POSITION_KEY, layoutManager.findLastVisibleItemPosition())
        super.onSaveInstanceState(outState)
    }

    private fun initRecyclerView() {
        layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvFibonacci.layoutManager = layoutManager
        rvFibonacci.addOnScrollListener(scrollListener)
        fibonacciRVAdapter = FibonacciRVAdapter()
        rvFibonacci.adapter = fibonacciRVAdapter
    }

    private fun initEditText() {
        val result = etFindItem.textChangeEvents()
            .debounce(SEARCH_FIELD_DEBOUNCE, TimeUnit.MILLISECONDS)
            .map { it.text.toString() }
            .subscribe { strItem ->
                if (strItem.isNotEmpty()) presenter.searchItem(strItem.toInt(), layoutManager.childCount)
            }
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        var visibleItemCount = 0
        var totalItemCount = 0
        var firstVisibleItemPosition = 0

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            visibleItemCount = layoutManager.childCount
            totalItemCount = layoutManager.itemCount
            firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

            if (dy < 0) {
                if (firstVisibleItemPosition == 0) {
                    presenter.getPreviousItems(firstVisibleItemPosition + visibleItemCount)
                }
            } else {
                if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                    presenter.getNextItems(firstVisibleItemPosition)
                }
            }
        }
    }

    override fun updateList(newList: List<Pair<Int, BigInteger>>, scrollToPosition: Int?) {
        fibonacciRVAdapter.submitList(ArrayList(newList)) {
            scrollToPosition?.let { showItemAtPosition(it) }
        }
    }

    override fun showItemAtPosition(position: Int) {
        runOnUiThread { rvFibonacci.scrollToPosition(position) }
    }

    companion object {
        const val RV_POSITION_KEY = "rv_position"

        const val SEARCH_FIELD_DEBOUNCE = 300L
    }
}

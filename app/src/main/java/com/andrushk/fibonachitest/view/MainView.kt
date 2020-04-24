package com.andrushk.fibonachitest.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.math.BigInteger

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : MvpView {
    fun updateList(newList: List<Pair<Int, BigInteger>>, scrollToPosition: Int? = null)
    fun showItemAtPosition(position: Int)
}
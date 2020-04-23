package com.andrushk.fibonachitest.view

import moxy.MvpView
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import java.math.BigInteger

@StateStrategyType(OneExecutionStateStrategy::class)
interface MainView : MvpView {
    fun updateAll(newList: List<BigInteger>)
    fun addToList(newPartOfFibonacci: List<BigInteger>)
    fun showItemAtPosition(position: Int)
}
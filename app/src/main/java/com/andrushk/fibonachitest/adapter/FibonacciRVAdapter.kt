package com.andrushk.fibonachitest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.andrushk.fibonachitest.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fibonacci_item_view.view.*
import java.math.BigInteger

class FibonacciRVAdapter : RecyclerView.Adapter<FibonacciRVAdapter.FibonacciItemViewHolder>() {

    private val listFibonacci = mutableListOf<BigInteger>()

    fun updateAll(newList: List<BigInteger>) {
        listFibonacci.clear()
        listFibonacci.addAll(newList)
        notifyDataSetChanged()
    }

    fun addToList(newPart: List<BigInteger>) {
        listFibonacci.addAll(newPart)
        notifyDataSetChanged()
    }

    override fun getItemCount() = listFibonacci.size

    override fun onBindViewHolder(holder: FibonacciItemViewHolder, position: Int) =
        holder.bind(position, listFibonacci[position])

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        FibonacciItemViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.fibonacci_item_view, parent, false))

    class FibonacciItemViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        fun bind(position: Int, number: BigInteger) = with(containerView) {
            tvPosition.text = position.toString()
            tvNumber.text = number.toString(10)
        }
    }
}
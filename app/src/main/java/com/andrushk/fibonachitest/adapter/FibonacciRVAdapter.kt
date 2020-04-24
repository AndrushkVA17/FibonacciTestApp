package com.andrushk.fibonachitest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andrushk.fibonachitest.R
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fibonacci_item_view.view.*
import java.math.BigInteger

class FibonacciRVAdapter : ListAdapter<Pair<Int, BigInteger>, FibonacciRVAdapter.FibonacciItemViewHolder>(FibonacciItemDiffCallback()) {

    override fun onBindViewHolder(holder: FibonacciItemViewHolder, position: Int) =
        holder.bind(getItem(position).first, getItem(position).second)

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

    class FibonacciItemDiffCallback: DiffUtil.ItemCallback<Pair<Int,BigInteger>>() {
        override fun areItemsTheSame(oldItem: Pair<Int, BigInteger>, newItem: Pair<Int, BigInteger>) =
            oldItem.first == newItem.first

        override fun areContentsTheSame(oldItem: Pair<Int, BigInteger>, newItem: Pair<Int, BigInteger>) =
            oldItem.second == newItem.second
    }
}
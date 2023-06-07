package ru.practicum.sprint12koh5

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

class ActivityOne : AppCompatActivity(),
    ItemsAdapter.ItemListener,
    Cart.CartItemsListener {

    private val itemsAdapter: ItemsAdapter = ItemsAdapter(this)
    private val cart = Sprint12Application.application.cart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Sprint12Application.application.cart
        findViewById<RecyclerView>(R.id.items).apply {
            adapter = itemsAdapter
        }

    }

    override fun onStart() {
        super.onStart()
        cart.cartItemsListener = this
        onItemsChanged(cart.getItems())
    }

    override fun onStop() {
        super.onStop()

        cart.cartItemsListener = null
    }

    override fun onPlus(item: Item) {
        cart.plus(item)
    }

    override fun onMinus(item: Item) {
        cart.minus(item)
    }

    override fun onItemsChanged(items: List<Item>) {
        itemsAdapter.updateItems(items)
        val sums: List<Double> = items.map { item ->
            val sumByItem = item.count * item.price
            sumByItem
        }
        val totalSum = sums.sum()
        val button = findViewById<Button>(R.id.b_sum)
        button.text = "Оформить. Сумма: ${String.format("%.02f", totalSum)} Р"
    }
}


class ItemsAdapter(
    private val itemListener: ItemListener
) : RecyclerView.Adapter<ItemViewHolder>() {

    private var items: List<Item> = emptyList()

    fun updateItems(newItems: List<Item>) {
        val oldItems = items
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return oldItems.size
            }

            override fun getNewListSize(): Int {
                return newItems.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition].id == newItems[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldItems[oldItemPosition] == newItems[newItemPosition]
            }

        })
        items = newItems

        result.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.setOnPlusListener {
            itemListener.onPlus(item)
        }

        holder.setOnMinusListener {
            itemListener.onMinus(item)
        }
    }

    interface ItemListener {
        fun onPlus(item: Item)
        fun onMinus(item: Item)
    }
}
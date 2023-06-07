package ru.practicum.sprint12koh5

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random


class Cart(
    context: Context
) {

    private val gson = Gson()
    private val sharedPreferences = context.getSharedPreferences(
        PREFS_NAME, Context.MODE_PRIVATE
    )
    private var itemsInMemory: List<Item>? = null

    var cartItemsListener: CartItemsListener? = null


    fun plus(item: Item) {
        var items = getItems()
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            items = items.toMutableList().apply {
                this[index] = item.copy(
                    count = item.count + 1
                )
            }
        }
        notifyItemsChanged(items)
    }

    fun minus(item: Item) {
        var items = getItems()
        val index = items.indexOfFirst { it.id == item.id }
        if (index >= 0) {
            items = items.toMutableList().apply {
                this[index] = item.copy(
                    count = item.count - 1
                )
            }
        }
        notifyItemsChanged(items)
    }

    fun getItems(): List<Item> {
        val itemsInMemory = itemsInMemory
        return if (itemsInMemory != null) {
            itemsInMemory
        } else {
            val json = sharedPreferences.getString(KEY_ITEMS, null)
            val itemsFromSharedPreferences: List<Item>? = json?.let {
                gson.fromJson(it, object : TypeToken<ArrayList<Item>>() {}.type)
            }
            val items: List<Item> = itemsFromSharedPreferences ?: (1..5).map {
                Item(
                    id = "id_$it",
                    name = "Товар №$it",
                    price = Random.nextDouble(100.0),
                    count = 1,
                )
            }
            items
        }
    }

    private fun saveItems(items: List<Item>){
        val json = gson.toJson(items)
        sharedPreferences
            .edit()
            .putString(KEY_ITEMS, json)
            .apply()

    }

    private fun notifyItemsChanged(items: List<Item>) {
        cartItemsListener?.onItemsChanged(items)
        saveItems(items)
    }

    fun interface CartItemsListener {
        fun onItemsChanged(item: List<Item>)
    }

    companion object {
        private val PREFS_NAME = "Cart"
        private val KEY_ITEMS = "items"
    }
}
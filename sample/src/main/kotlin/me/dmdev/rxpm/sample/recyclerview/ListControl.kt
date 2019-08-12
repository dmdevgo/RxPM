package me.dmdev.rxpm.sample.recyclerview

import me.dmdev.rxpm.*

class ItemPmProvider<T: Any>(
    val clazz: Class<out T>,
    val pmProvider: () -> ItemPresentationModel<out T>
)

open class ItemPresentationModel<T : Any> : PresentationModel() {

    val item = state<T>()
    val changeItem = action<T>()

    override fun onCreate() {
        changeItem.observable
            .subscribe(item.consumer)
            .untilDestroy()
    }
}

class ListControl<T : Any> : PresentationModel() {

    private val pmProviders = mutableMapOf<Class<out T>, ItemPmProvider<out T>>()
    private val itemTypes = mutableMapOf<Class<out T>, Int>()
    private val itemClasses = mutableMapOf<Int, Class<out T>>()

    val items = state<List<T>>(listOf())

    fun getType(item: T): Int {
        return itemTypes[item::class.java]!!
    }

    fun registerItemPmProvider(pmProvider: ItemPmProvider<out T>) {
        pmProviders[pmProvider.clazz] = pmProvider
        itemTypes[pmProvider.clazz] = itemTypes.size
        itemClasses[itemClasses.size] = pmProvider.clazz
    }

    fun getItemPresentationModelForType(type: Int): PresentationModel {
        return pmProviders[itemClasses[type]]!!.pmProvider.invoke().apply {
            this.attachToParent(this@ListControl)
        }
    }
}

inline fun <T : Any> PresentationModel.listControl(init: ListControl<T>.() -> Unit): ListControl<T> {
    val lc = ListControl<T>()
    lc.init()
    lc.attachToParent(this)
    return lc
}

infix fun <T : Any> ListControl<T>.bindTo(listControlAdapter: ListControlAdapter<T>) {
    items bindTo { listControlAdapter.submitList(it) }
}
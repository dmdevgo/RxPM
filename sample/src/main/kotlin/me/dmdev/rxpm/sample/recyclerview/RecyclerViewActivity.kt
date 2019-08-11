package me.dmdev.rxpm.sample.recyclerview

import android.os.*
import androidx.recyclerview.widget.*
import kotlinx.android.synthetic.main.activity_recycler_view.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.*

class RecyclerViewActivity : PmActivity<RecyclerViewPm>() {

    private lateinit var colorsAdapter: ColorsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recycler_view)
        colorsAdapter = ColorsAdapter(presentationModel.itemsControl)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            recyclerView.adapter = colorsAdapter
        }
    }

    override fun providePresentationModel() = RecyclerViewPm()

    override fun onBindPresentationModel(pm: RecyclerViewPm) {
        pm.itemsControl bindTo colorsAdapter
    }
}
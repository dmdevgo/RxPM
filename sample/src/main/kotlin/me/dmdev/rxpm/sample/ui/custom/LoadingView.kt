package me.dmdev.rxpm.sample.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import me.dmdev.rxpm.sample.R

/**
 * @author Dmitriy Gorbunov
 */
class LoadingView : FrameLayout {

    private lateinit var progressBar: ProgressBar
    private lateinit var foregroundView: View

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).inflate(R.layout.layout_loading_view, this)
        foregroundView = findViewById(R.id.foregroundView)
        progressBar = findViewById(R.id.progressBar) as ProgressBar
        showLoading(false)
    }

    fun showLoading(loading: Boolean) {
        if (loading) {
            bringChildToFront(foregroundView)
            bringChildToFront(progressBar)
            foregroundView.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
        } else {
            foregroundView.visibility = View.INVISIBLE
            progressBar.visibility = View.INVISIBLE
        }
    }
}

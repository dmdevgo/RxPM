package me.dmdev.rxpm.sample.pager

import android.os.*
import android.view.*
import kotlinx.android.synthetic.main.screen_page.*
import me.dmdev.rxpm.base.*
import me.dmdev.rxpm.sample.R
import timber.log.*

class PageFragment : PmFragment<PagePm>() {

    companion object {

        private const val ARG_PAGE_NUMBER = "page_number"

        fun newInstance( pageNumber: Int): PageFragment {
            return PageFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PAGE_NUMBER, pageNumber)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.screen_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pageText.text = pageNumber.toString()
    }

    private val pageNumber: Int get() = arguments!!.getInt(ARG_PAGE_NUMBER)

    override fun providePresentationModel() = PagePm(pageNumber)


    override fun onBindPresentationModel(pm: PagePm) {
        Timber.d("Fragment #$pageNumber onBindPresentationModel")
    }

}
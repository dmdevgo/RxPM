package me.dmdev.rxpm.sample.pager

import androidx.fragment.app.*

class PagesAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return PageFragment.newInstance(position)
    }

    override fun getCount() = 10
}
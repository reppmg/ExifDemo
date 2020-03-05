package io.bakerystud.exifdemo

import androidx.viewpager.widget.ViewPager

interface OnPageSelectedListener : ViewPager.OnPageChangeListener {
    companion object {
        inline operator fun invoke(crossinline op: (arg1: Int) -> Unit) =
            object : OnPageSelectedListener {
                override fun onPageSelected(position: Int) {
                    op(position)
                }
            }
    }
    override fun onPageScrollStateChanged(state: Int) {
    }


    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int){
    }
}
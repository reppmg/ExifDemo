package io.bakerystud.exifdemo

import androidx.fragment.app.Fragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    val enterCode: FragmentScreen
        get() = FragmentScreen(EnterCodeFragment())
    val eventsList: FragmentScreen
        get() = FragmentScreen(EventsListFragment())
}

open class FragmentScreen(private val screenFragment: Fragment) : SupportAppScreen() {
    override fun getFragment() = screenFragment
    override fun toString(): String {
        return "${this::class.java.simpleName}:${screenFragment::class.java.simpleName}"
    }
}
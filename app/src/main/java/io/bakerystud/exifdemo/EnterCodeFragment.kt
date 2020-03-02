package io.bakerystud.exifdemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_code.*

class EnterCodeFragment : Fragment() {

    interface EnterCodeController {
        fun onNextClicked(code: String)
    }

    private val controller: EnterCodeController
        get() = activity as EnterCodeController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonStart.setOnClickListener {
            progressVisible(true)
            controller.onNextClicked(editCode.text.toString())
        }
    }

    fun progressVisible(visible: Boolean) {
        progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun showCode(code: String) {
        textYourCode.text = code
    }

}
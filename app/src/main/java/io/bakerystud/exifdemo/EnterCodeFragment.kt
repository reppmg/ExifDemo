package io.bakerystud.exifdemo

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
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
            if (editCode.text.length < 3) {
                activity?.showSnackbar("Code is too short")
                return@setOnClickListener
            }
            progressVisible(true)
            controller.onNextClicked(editCode.text.toString())
        }
        textYourCode.setOnClickListener { copyClipboard() }
        textLabelCode.setOnClickListener { copyClipboard() }
        textHintCode.setOnClickListener { copyClipboard() }
        buttonPaste.setOnClickListener { pasteFromClipboard() }
    }

    private fun copyClipboard() {
        val clipboardManager =
            requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        val clipData = ClipData.newPlainText("Source Text", textYourCode.text.toString())
        clipboardManager?.setPrimaryClip(clipData)
        activity?.showSnackbar("Copied to clipboard")
    }

    private fun pasteFromClipboard() {
        val clipboardManager =
            requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
        try {
            clipboardManager?.primaryClip?.getItemAt(0)?.text?.let {
                editCode.text = SpannableStringBuilder(it)
            }
        } catch (e: Exception) {
        }

    }

    fun progressVisible(visible: Boolean) {
        progress.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun showCode(code: String) {
        try {
            textYourCode.text = code
        } catch (e: Exception) {
        }
    }

}
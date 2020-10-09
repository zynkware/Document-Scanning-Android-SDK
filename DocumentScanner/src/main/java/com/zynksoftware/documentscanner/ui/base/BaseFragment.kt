package com.zynksoftware.documentscanner.ui.base

import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import com.zynksoftware.documentscanner.R
import com.zynksoftware.documentscanner.common.extensions.hide
import com.zynksoftware.documentscanner.common.extensions.show

internal abstract class BaseFragment : Fragment() {

    fun showProgressBar() {
        view?.findViewById<RelativeLayout>(R.id.progressLayout)?.show()
    }

    fun hideProgressBar() {
        view?.findViewById<RelativeLayout>(R.id.progressLayout)?.hide()
    }

}
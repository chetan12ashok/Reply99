package com.appdroid.reply99.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.appdroid.reply99.R
import com.appdroid.reply99.adapter.SupportedAppsAdapter
import com.appdroid.reply99.model.App
import com.appdroid.reply99.model.utils.Constants

import kotlinx.android.synthetic.main.fragment_enabled_apps.view.*

class EnabledAppsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_enabled_apps, container, false)

        val layoutManager = LinearLayoutManager(context)

        val supportedAppsAdapter = SupportedAppsAdapter(Constants.EnabledAppsDisplayType.VERTICAL, ArrayList<App>(Constants.SUPPORTED_APPS), null)
        view.supportedAppsList.layoutManager = layoutManager
        view.supportedAppsList.adapter = supportedAppsAdapter

        return view
    }
}
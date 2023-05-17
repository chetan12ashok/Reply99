package com.appdroid.reply99.activity.contactselector

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.appdroid.reply99.R
import com.appdroid.reply99.activity.BaseActivity
import com.appdroid.reply99.databinding.ActivityContactSelectorBinding
import com.appdroid.reply99.fragment.ContactSelectorFragment
import com.appdroid.reply99.model.utils.ContactsHelper
import com.appdroid.reply99.viewmodel.SwipeToKillAppDetectViewModel

class ContactSelectorActivity : BaseActivity() {
    private lateinit var contactSelectorFragment: ContactSelectorFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityContactSelectorBinding.inflate(layoutInflater).also {
            setContentView(it.root)
        }

        contactSelectorFragment = supportFragmentManager.findFragmentById(R.id.contact_selector_layout)
                as ContactSelectorFragment

        title = getString(R.string.contact_selector)

        ViewModelProvider(this).get(SwipeToKillAppDetectViewModel::class.java)
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ContactsHelper.CONTACT_PERMISSION_REQUEST_CODE && this::contactSelectorFragment.isInitialized) {
            contactSelectorFragment.loadContactList()
        }
    }
}
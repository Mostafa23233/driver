package com.darbaast.driver.feature.authentication

import android.os.Bundle
import com.darbaast.driver.R
import com.darbaast.driver.feature.authentication.login.LoginFragment
import com.darbaast.driver.custom.DarbastActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : DarbastActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        replace(R.id.fragment_container_authentication,LoginFragment())
    }
}
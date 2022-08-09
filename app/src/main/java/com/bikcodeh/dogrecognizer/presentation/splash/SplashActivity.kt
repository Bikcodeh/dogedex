package com.bikcodeh.dogrecognizer.presentation.splash

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bikcodeh.dogrecognizer.MainActivity
import com.bikcodeh.dogrecognizer.core.remote.interceptor.ApiServiceInterceptor
import com.bikcodeh.dogrecognizer.core.util.extension.launchSafeActivity
import com.bikcodeh.dogrecognizer.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpListeners()
        splashViewModel.getUser()
    }

    private fun setUpListeners() {
        binding.splashAnimationLottie.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator?) {}

            override fun onAnimationEnd(p0: Animator?) {
                setUpCollectors()
            }

            override fun onAnimationCancel(p0: Animator?) {}

            override fun onAnimationRepeat(p0: Animator?) {}
        })
    }

    private fun setUpCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                splashViewModel.userLogged.collect { user ->
                    if (user?.authenticationToken?.isNotEmpty() == true) {
                        ApiServiceInterceptor.setToken(user.authenticationToken)
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    } else {
                        launchSafeActivity("com.bikcodeh.dogrecognizer.authpresentation.AuthActivity")
                        finish()
                    }
                }
            }
        }
    }
}
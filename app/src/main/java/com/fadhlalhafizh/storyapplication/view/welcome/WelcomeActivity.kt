package com.fadhlalhafizh.storyapplication.view.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.fadhlalhafizh.storyapplication.databinding.ActivityWelcomeBinding
import com.fadhlalhafizh.storyapplication.view.signin.SignInActivity
import com.fadhlalhafizh.storyapplication.view.signup.SignUpActivity

@Suppress("DEPRECATION")
class WelcomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewWelcomeFullScreenSetup()
        playAnimation()
        buttonAction()
    }

    private fun viewWelcomeFullScreenSetup() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun buttonAction() {
        binding.btnLogin.setOnClickListener { startNewActivity(SignInActivity::class.java) }
        binding.btnSignup.setOnClickListener { startNewActivity(SignUpActivity::class.java) }
    }

    private fun startNewActivity(activityClass: Class<*>) {
        startActivity(Intent(this, activityClass))
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivWelcomeImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val elementsObjectAnim = listOf(
            ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.tvTittleWelcome, View.ALPHA, 1f),
            ObjectAnimator.ofFloat(binding.tvMessageWelcome, View.ALPHA, 1f)
        ).map { it.setDuration(100) }

        val together = AnimatorSet().apply {
            playTogether(*elementsObjectAnim.toTypedArray())
        }
        AnimatorSet().apply {
            playSequentially(together)
            start()
        }
    }
}
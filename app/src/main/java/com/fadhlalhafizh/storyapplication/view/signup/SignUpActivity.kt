package com.fadhlalhafizh.storyapplication.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.databinding.ActivitySignUpBinding
import com.fadhlalhafizh.storyapplication.view.signin.SignInActivity
import com.fadhlalhafizh.storyapplication.view.welcome.WelcomeActivity
import com.fadhlalhafizh.storyapplication.viewmodel.ViewModelFactory

@Suppress("DEPRECATION")
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModelSignUp by viewModels<SignUpViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelSignUp.isLoading.observe(this) {
            displayProgressLoadingBar(it)
        }

        viewModelSignUp.errorMessage.observe(this) {
            showAlertMessage(it)
        }

        navButtonAction()
        viewWelcomeFullScreenSetup()
        setupAction()
        playAnimation()
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

    private fun setupAction() {
        binding.btnSignup.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val inputName = getString(R.string.inputName)
            val inputEmail = getString(R.string.inputEmail)
            val inputPassword = getString(R.string.inputPassword)
            val passwordValidation = getString(R.string.passwordValidation)

            if (name.isEmpty()) {
                binding.nameEditText.error = inputName
            } else if (email.isEmpty()) {
                binding.emailEditText.error = inputEmail
            } else if (password.isEmpty()) {
                binding.passwordEditText.error = inputPassword
            } else if (password.length < 8) {
                binding.passwordEditText.error = passwordValidation
            } else {
                viewModelSignUp.register(name, email, password)
            }
        }
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSignupImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }
        val viewList = listOf(
            binding.tvTitleSignup,
            binding.tvMessageSignup,
            binding.tvName,
            binding.nameEditTextLayout,
            binding.tvEmail,
            binding.emailEditTextLayout,
            binding.tvPassword,
            binding.passwordEditTextLayout,
            binding.btnSignup
        )
        val animatorList = viewList.map {
            ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(200)
        }
        AnimatorSet().apply {
            playSequentially(*(animatorList.toTypedArray()))
            duration = 250
            startDelay = 500
        }.start()
    }

    private fun navButtonAction() {
        binding.ivArrowBack.setOnClickListener {
            val intent = Intent(this, WelcomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun displayProgressLoadingBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showAlertMessage(message: String) {
        val dialogBuilder = android.app.AlertDialog.Builder(this)
        dialogBuilder.setMessage(message)
            .setCancelable(false)
            .setPositiveButton("Ok") { dialog, _ ->
                dialog.dismiss()
                if (message == "User created") {
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    binding.nameEditText.text?.clear()
                    binding.emailEditText.text?.clear()
                    binding.passwordEditText.text?.clear()
                }
            }
        val alertMessage = dialogBuilder.create()
        alertMessage.setTitle("Alert")
        alertMessage.show()
    }
}
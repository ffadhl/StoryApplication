package com.fadhlalhafizh.storyapplication.view.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.data.api.response.SignInResponse
import com.fadhlalhafizh.storyapplication.data.pref.UserModel
import com.fadhlalhafizh.storyapplication.databinding.ActivitySignInBinding
import com.fadhlalhafizh.storyapplication.view.main.MainActivity
import com.fadhlalhafizh.storyapplication.view.welcome.WelcomeActivity
import com.fadhlalhafizh.storyapplication.viewmodel.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException

@Suppress("DEPRECATION")
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private val viewModelSignIn by viewModels<SignInViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModelSignIn.isLoading.observe(this) {
            displayProgressLoadingBar(it)
        }

        viewModelSignIn.getSession().observe(this) {
            if (it.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
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
        binding.btnLogin.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            val inputEmail = getString(R.string.inputEmail)
            val inputPassword = getString(R.string.inputPassword)
            val passwordValidation = getString(R.string.passwordValidation)

            if (email.isEmpty()) {
                binding.emailEditText.error = inputEmail
            } else if (password.isEmpty()) {
                binding.passwordEditText.error = inputPassword
            } else if (password.length < 8) {
                binding.passwordEditText.error = passwordValidation
            } else {
                lifecycleScope.launch {
                    try {
                        val loginResult = viewModelSignIn.signIn(email, password)
                        val error = loginResult.error
                        if (error == false) {
                            val token = loginResult.loginResult?.token
                            if (token != null) {
                                viewModelSignIn.saveSession(UserModel(email, token, true))
                                showSuccessDialog(loginResult.message)
                            }
                        }
                    } catch (e: HttpException) {
                        val errorMessage = getErrorMessageFromHttpException(e)
                        showErrorDialog(errorMessage)
                        e.printStackTrace()
                    } finally {
                        displayProgressLoadingBar(false)
                    }
                }
            }
        }
    }

    private fun showSuccessDialog(message: String?) {
        AlertDialog.Builder(this@SignInActivity).apply {
            setTitle("Success")
            setMessage(message)
            setPositiveButton("Ok") { _, _ ->
                navigateToMainActivity()
            }
            val alertMessage = create()
            alertMessage.show()
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this@SignInActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showErrorDialog(errorMessage: String) {
        AlertDialog.Builder(this@SignInActivity).apply {
            setTitle("Alert")
            setMessage(errorMessage)
            setPositiveButton("Ok") { _, _ ->
                clearInputFields()
            }
            val alertMessage = create()
            alertMessage.show()
        }
    }

    private fun clearInputFields() {
        binding.emailEditText.text?.clear()
        binding.passwordEditText.text?.clear()
    }

    private fun getErrorMessageFromHttpException(e: HttpException): String {
        val jsonInString = e.response()?.errorBody()?.string()
        val errorBody = Gson().fromJson(jsonInString, SignInResponse::class.java)
        return errorBody.message.toString()
    }

    @SuppressLint("Recycle")
    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivSigninImage, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }

        val viewsToAnimate = arrayOf(
            binding.tvTitleSignin,
            binding.tvMessageSignin,
            binding.tvEmail,
            binding.emailEditTextLayout,
            binding.tvPassword,
            binding.passwordEditTextLayout,
            binding.btnLogin
        )

        val alphaAnimators = viewsToAnimate.map {
            ObjectAnimator.ofFloat(it, View.ALPHA, 1f).setDuration(200)
        }

        AnimatorSet().apply {
            playSequentially(*(alphaAnimators.toTypedArray()))
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
}
package com.fadhlalhafizh.storyapplication.view.main

import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.databinding.ActivityMainBinding
import com.fadhlalhafizh.storyapplication.view.addstory.AddStoryActivity
import com.fadhlalhafizh.storyapplication.view.maps.MapsActivity
import com.fadhlalhafizh.storyapplication.view.signin.SignInActivity
import com.fadhlalhafizh.storyapplication.view.welcome.WelcomeActivity
import com.fadhlalhafizh.storyapplication.viewmodel.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModelMain by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.apply {
            val isDarkMode = isDarkModeEnabled()
            val actionBarColorRes = if (isDarkMode) {
                R.color.black
            } else {
                R.color.blueDark
            }

            val textColorRes = if (isDarkMode) R.color.white else R.color.white
            val actionBarColor = ContextCompat.getColor(this@MainActivity, actionBarColorRes)
            val textColor = ContextCompat.getColor(this@MainActivity, textColorRes)

            setBackgroundDrawable(ColorDrawable(actionBarColor))
            setDisplayHomeAsUpEnabled(true)

            val title = SpannableString("Story Application")
            title.apply {
                setSpan(
                    ForegroundColorSpan(textColor), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(AbsoluteSizeSpan(24, true), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            supportActionBar?.title = title
            setDisplayHomeAsUpEnabled(false)
        }

        window.statusBarColor = ContextCompat.getColor(
            this, if (isDarkModeEnabled()) R.color.black else R.color.blueDark
        )

        val layoutManager = LinearLayoutManager(this)
        binding.rvStoryList.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStoryList.addItemDecoration(itemDecoration)

        viewModelMain.isLoading.observe(this) {
            displayProgressLoadingBar(it)
        }

        binding.fbAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, AddStoryActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        setupAction()
    }

    override fun onCreateOptionsMenu(menuAppbar: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_option, menuAppbar)
        return super.onCreateOptionsMenu(menuAppbar)
    }

    override fun onOptionsItemSelected(itemMenuAppbar: MenuItem): Boolean {
        when (itemMenuAppbar.itemId) {
            R.id.menu_logout -> {
                CoroutineScope(Dispatchers.IO).launch { viewModelMain.logout() }
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
            }

            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
            }

            R.id.menu_map -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(itemMenuAppbar)
    }

    private fun setStoryList() {
        val adapter = ListStoryAdapter()
        binding.rvStoryList.adapter = adapter.withLoadStateFooter(footer = LoadingStateAdapter {
            adapter.retry()
        })
        viewModelMain.getStoriesWithPager.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun setupAction() {
        viewModelMain.getSession().observe(this) { user ->
            if (!user.isLogin) {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                setStoryList()
            }
        }
    }

    private fun displayProgressLoadingBar(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isDarkModeEnabled(): Boolean{
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

}
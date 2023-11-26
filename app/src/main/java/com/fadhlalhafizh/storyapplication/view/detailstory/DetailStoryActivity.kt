package com.fadhlalhafizh.storyapplication.view.detailstory

import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.databinding.ActivityDetailStoryBinding
import com.fadhlalhafizh.storyapplication.utils.withDateFormat

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBarStoryApp = supportActionBar
        if (actionBarStoryApp != null) {
            val isDarkMode = isDarkModeEnabled()
            val actionBarColorRes = if (isDarkMode) R.color.black else R.color.blueDark
            val textColorRes = if (isDarkMode) R.color.white else R.color.white

            val actionBarColor =
                ContextCompat.getColor(this@DetailStoryActivity, actionBarColorRes)
            val textColor = ContextCompat.getColor(this@DetailStoryActivity, textColorRes)

            if (isDarkMode) {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            }

            actionBarStoryApp.setBackgroundDrawable(ColorDrawable(actionBarColor))
            actionBarStoryApp.setDisplayHomeAsUpEnabled(true)

            val title = SpannableString("Detail Story")
            title.apply {
                setSpan(
                    ForegroundColorSpan(textColor),
                    0,
                    length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                setSpan(AbsoluteSizeSpan(24, true), 0, length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            supportActionBar?.title = title
        }

        val name = intent.getStringExtra(NAME)
        val desc = intent.getStringExtra(DESCRIPTION)
        val userStory = intent.getStringExtra(PHOTO_URL)
        val createAt = intent.getStringExtra(CREATE_AT)

        binding.apply {
            Glide.with(binding.root.context)
                .load(userStory)
                .into(ivUserPhotoStory)
            tvNameDetail.text = name
            tvDescDetail.text = desc
            binding.tvCreatedItem.text = createAt?.withDateFormat()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }

    private fun isDarkModeEnabled(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    companion object {
        const val NAME = "name"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
        const val CREATE_AT = "create_at"
    }
}
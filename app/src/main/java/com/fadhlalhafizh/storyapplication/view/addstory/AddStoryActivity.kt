package com.fadhlalhafizh.storyapplication.view.addstory

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.data.api.response.AddStoryResponse
import com.fadhlalhafizh.storyapplication.databinding.ActivityAddStoryBinding
import com.fadhlalhafizh.storyapplication.utils.reduceFileImage
import com.fadhlalhafizh.storyapplication.utils.uriToFile
import com.fadhlalhafizh.storyapplication.view.addstory.CamActivity.Companion.CAMERAX_RESULT
import com.fadhlalhafizh.storyapplication.view.main.MainActivity
import com.fadhlalhafizh.storyapplication.viewmodel.ViewModelFactory
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

@Suppress("DEPRECATION")
class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private val viewModelAddStory by viewModels<AddStoryViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            val message = if (isGranted) "Permission granted" else "Permission denied"
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!allPermissionsGranted()) {
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        val actionBarStoryApp = supportActionBar
        if (actionBarStoryApp != null) {
            val isDarkMode = isDarkModeEnabled()
            val actionBarColorRes = if (isDarkMode) R.color.black else R.color.blueDark
            val textColorRes = if (isDarkMode) R.color.white else R.color.white

            val actionBarColor =
                ContextCompat.getColor(this@AddStoryActivity, actionBarColorRes)
            val textColor = ContextCompat.getColor(this@AddStoryActivity, textColorRes)

            if (isDarkMode) {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            }

            actionBarStoryApp.setBackgroundDrawable(ColorDrawable(actionBarColor))
            actionBarStoryApp.setDisplayHomeAsUpEnabled(true)

            val title = SpannableString("Add Story")
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

        binding.btnGallery.setOnClickListener { startGallery() }
        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnUpload.setOnClickListener {
            currentImageUri?.let { uri ->
                val imageFile = uriToFile(uri, this).reduceFileImage()
                val description = binding.editAddDescription.text.toString()

                viewModelAddStory.isLoading.observe(this) {
                    showLoading(it)
                }

                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody =
                    MultipartBody.Part.createFormData("photo", imageFile.name, requestImageFile)

                viewModelAddStory.getSession().observe(this) { user ->
                    val token = user.token
                    lifecycleScope.launch {
                        try {
                            val uploadResult =
                                viewModelAddStory.upload(token, multipartBody, requestBody)
                            val error = uploadResult.error
                            val message = uploadResult.message
                            showLoading(false)

                            if (error == false) {
                                if (message != null) {
                                    showSuccessDialog(message)
                                }
                            }
                        } catch (e: HttpException) {
                            handleHttpException(e)
                        }
                    }
                }
            }
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            val mageUri = "Image URI"
            Log.d(mageUri, "showImage: $it")
            binding.ivResultStory.setImageURI(it)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this@AddStoryActivity).apply {
            setTitle("Success")
            setMessage(message)
            setPositiveButton("Next") { _, _ ->
                startActivityWithClearTask(MainActivity::class.java)
            }
            create().show()
        }
    }

    private fun handleHttpException(e: HttpException) {
        val errorResponse =
            Gson().fromJson(e.response()?.errorBody()?.string(), AddStoryResponse::class.java)
        val errorMessage = errorResponse.message

        AlertDialog.Builder(this@AddStoryActivity).apply {
            setTitle("Alert")
            setMessage(errorMessage)
            setPositiveButton("Ok") { _, _ -> binding.editAddDescription.text?.clear() }
            create().show()
        }
    }

    private fun startActivityWithClearTask(targetActivity: Class<*>) {
        val intent = Intent(this@AddStoryActivity, targetActivity)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun startCameraX() {
        val intent = Intent(this, CamActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CamActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
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
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}
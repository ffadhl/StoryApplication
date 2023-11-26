package com.fadhlalhafizh.storyapplication.view.maps

import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.fadhlalhafizh.storyapplication.R
import com.fadhlalhafizh.storyapplication.databinding.ActivityMapsBinding
import com.fadhlalhafizh.storyapplication.viewmodel.ViewModelFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModelMaps by viewModels<MapsViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBarStoryApp = supportActionBar
        if (actionBarStoryApp != null) {
            val isDarkMode = isDarkModeEnabled()
            val actionBarColorRes = if (isDarkMode) R.color.black else R.color.blueDark
            val textColorRes = if (isDarkMode) R.color.white else R.color.white

            val actionBarColor =
                ContextCompat.getColor(this@MapsActivity, actionBarColorRes)
            val textColor = ContextCompat.getColor(this@MapsActivity, textColorRes)

            if (isDarkMode) {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            } else {
                actionBarStoryApp.setHomeAsUpIndicator(R.drawable.ic_back)
            }

            actionBarStoryApp.setBackgroundDrawable(ColorDrawable(actionBarColor))
            actionBarStoryApp.setDisplayHomeAsUpEnabled(true)

            val title = SpannableString("Maps")
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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val indonesianMaps = LatLng(-0.789275, 113.921327)
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        getMyLocation()
        getStoryLocation()

        mMap.moveCamera(
            com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom(
                indonesianMaps,
                4f
            )
        )
    }

    private fun isDarkModeEnabled(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    override fun onCreateOptionsMenu(menuMap: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_map, menuMap)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.normal_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                true
            }

            R.id.satellite_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
                true
            }

            R.id.terrain_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
                true
            }

            R.id.hybrid_type -> {
                mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
                true
            }

            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }
        }

    private fun getMyLocation() {
        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getStoryLocation() {
        viewModelMaps.getSession().observe(this) { user ->
            if (user != null) {
                lifecycleScope.launch {
                    val responseLocation = viewModelMaps.getLocation(user.token).listStory
                    for (location in responseLocation) {
                        val lat = location.lat
                        val lng = location.lon
                        val title = location.name
                        val description = location.description
                        if (lat != null && lng != null) {
                            mMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(lat, lng))
                                    .title(title)
                                    .snippet(description)
                            )
                        }
                    }
                }
            }
        }
    }
}
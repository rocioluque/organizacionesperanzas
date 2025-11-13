package com.rocio.organizacionesperanzas

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize AppRepository
        AppRepository.initialize(this)

        // Initialize Cloudinary
        val config = mapOf(
            "cloud_name" to "doxchujph",
            "api_key" to "376445762272814",
            "api_secret" to "aSFLU4KRQmPSs4pYiRC2g0lh2wA"
        )
        MediaManager.init(this, config)
    }
}

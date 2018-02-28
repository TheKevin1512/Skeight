package com.kevindom.skeight.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.graphics.drawable.AnimatedVectorDrawableCompat
import bind
import com.github.salomonbrys.kodein.android.KodeinAppCompatActivity
import com.kevindom.skeight.R
import com.kevindom.skeight.databinding.ActivityFullscreenBinding
import com.squareup.picasso.Picasso
import loop

class FullScreenActivity : KodeinAppCompatActivity() {

    companion object {
        private const val DELAY = 500L
        private const val EXTRA_URL = "extra_url"

        fun create(context: Context, url: String): Intent {
            return Intent(context, FullScreenActivity::class.java).apply {
                putExtra(EXTRA_URL, url)
            }
        }
    }

    private lateinit var binding: ActivityFullscreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = R.layout.activity_fullscreen.bind(this)

        val url = intent.getStringExtra(EXTRA_URL)

        val placeholder = AnimatedVectorDrawableCompat.create(this, R.drawable.anim_loading)
        placeholder?.loop()

        Picasso.with(this)
                .load(url)
                .placeholder(placeholder)
                .into(binding.fullscreenImage)

        binding.fullscreenExit.setOnClickListener { onBackPressed() }
    }

    override fun onStart() {
        super.onStart()
        Handler().postDelayed({
            val drawable = AnimatedVectorDrawableCompat.create(this, R.drawable.anim_exit)
            binding.fullscreenExit.setImageDrawable(drawable)
            drawable?.start()
        }, DELAY)
    }
}
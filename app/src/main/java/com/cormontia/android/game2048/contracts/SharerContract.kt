package com.cormontia.android.game2048.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract

class SharerContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String?): Intent {
        //TODO!~ Share a picture, instead of the file format....
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, input)
        return sharingIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}
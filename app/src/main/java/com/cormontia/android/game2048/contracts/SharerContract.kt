package com.cormontia.android.game2048.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.cormontia.android.game2048.MainActivity

class SharerContract: ActivityResultContract<Uri, Uri?>() {
    override fun createIntent(context: Context, bitmapUri: Uri): Intent {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = MainActivity.bitmapMimeType
        sharingIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri)
        return sharingIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}
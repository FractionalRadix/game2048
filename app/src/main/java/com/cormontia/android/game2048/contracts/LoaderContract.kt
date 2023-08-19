package com.cormontia.android.game2048.contracts

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import com.cormontia.android.game2048.MainActivity

class LoaderContract : ActivityResultContract<String, Uri>() {
    override fun createIntent(context: Context, input: String?): Intent {
        val loadIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        loadIntent.addCategory(Intent.CATEGORY_OPENABLE)
        loadIntent.type = MainActivity.storageFileMimeType
        return loadIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        return intent?.data
    }
}

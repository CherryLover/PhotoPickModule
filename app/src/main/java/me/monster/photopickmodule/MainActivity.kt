package me.monster.photopickmodule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import me.iwf.photopicker.PhotoPicker
import me.iwf.photopicker.utils.LogUtil
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val tag = "MainActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener { preCheck() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PhotoPicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var photos: ArrayList<String>? = ArrayList()
            if (data != null) {
                photos = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS)
            }
            if (photos.isNullOrEmpty()) {
                return
            }
            val sb = StringBuilder()
            photos.forEach { sb.append(it).append('\n') }
            tv_selector_result.text = sb.toString()
            LogUtil.e(tag, "file path : ${tv_selector_result.text}")
        }
    }

    private fun preCheck() {
        val permissionList = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val actualCheckPermissionList = mutableListOf<String>()
        for (s in permissionList) {
            if (ContextCompat.checkSelfPermission(this, s) == PackageManager.PERMISSION_DENIED) {
                actualCheckPermissionList.add(s)
            }
        }
        if (actualCheckPermissionList.isEmpty()) {
            openSelector()
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(actualCheckPermissionList.toTypedArray(), 123)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            openSelector()
        }
    }

    private fun openSelector() {
        PhotoPicker.builder()
            .setPhotoCount(3)
            .setShowCamera(true)
            .setShowGif(false)
            .setPreviewEnabled(true)
            .setGridColumnCount(3)
            .start(this)
//            .start(mContext, this, PhotoPicker.REQUEST_CODE)
    }
}

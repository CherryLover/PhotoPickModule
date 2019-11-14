package me.monster.photopickmodule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import me.iwf.photopicker.PhotoPicker

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_test.setOnClickListener { preCheck() }
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

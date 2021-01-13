package com.hjq.bsdiff

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //权限申请
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val perms = arrayListOf<String>(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)
            if (checkSelfPermission(perms[0]) == PackageManager.PERMISSION_DENIED) {
                requestPermissions(perms.toTypedArray(), 1000)
            }
        }
        findViewById<TextView>(R.id.version_tv).text = getVersionName()

        findViewById<Button>(R.id.dispatch_btn).setOnClickListener {
            createNewApk()
        }
    }

    private fun createNewApk() {
        lifecycleScope.launch {
            //增量包
            val patch: String =
                File(Environment.getExternalStorageDirectory(), "patch").absolutePath
            //当前的Apk，用系统缓存的Apk
            val oldApk: String = applicationInfo.sourceDir
            // 生成的新的Apk
            val newApk = File(Environment.getExternalStorageDirectory(), "new.apk")
            if (!newApk.exists()) newApk.createNewFile()

            val output: String = newApk.absolutePath
            withContext(Dispatchers.IO) {
                BsPatcher.bsPatch(oldApk, patch, output);
            }
            Toast.makeText(this@MainActivity, "合并完成", Toast.LENGTH_SHORT).show()
            //安装请求
            val intent = Intent(Intent.ACTION_VIEW)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fileUri: Uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.hjq.bsdiff.fileprovider",
                    newApk
                )
                intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
            } else {
                intent.setDataAndType(
                    Uri.fromFile(newApk),
                    "application/vnd.android.package-archive"
                )
            }
            this@MainActivity.startActivity(intent)
        }
    }

    private fun getVersionName(): String {
        val packInfo = packageManager.getPackageInfo(packageName, 0)
        return packInfo.versionName
    }


}
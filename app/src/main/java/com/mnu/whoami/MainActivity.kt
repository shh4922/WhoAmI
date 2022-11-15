package com.mnu.whoami

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mnu.whoami.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    val CAMERA_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99

    val FLAG_REQ_CAMERA = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener(this)
        binding.btnAlbum.setOnClickListener(this)



    }

    private fun showToat(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /***
     * 액션리스너
     */

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_camera -> {

                if (isPermitted(CAMERA_PERMISSION)) {
                    openCamera()
                } else {
                    ActivityCompat.requestPermissions(this, CAMERA_PERMISSION, FLAG_PERM_CAMERA)
                }
            }
            R.id.btn_album -> {
                //로컬엘범에 접근
            }
        }
    }

    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, FLAG_REQ_CAMERA)


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            when(requestCode){
                FLAG_REQ_CAMERA->{
                    val bitmap = data?.extras?.get("data") as Bitmap
                    binding.faceImg.setImageBitmap(bitmap)
                }
            }
        }

    }



    //
    fun isPermitted(permissions: Array<String>): Boolean {

        val result = ContextCompat.checkSelfPermission(this, permissions.toString())
        if (result != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            FLAG_PERM_CAMERA->{
                var checked = true
                for(grant in grantResults){
                    if (grant!= PackageManager.PERMISSION_GRANTED){
                        checked =false
                        break
                    }
                }
                if(checked){
                    openCamera()
                }
            }


        }
    }
}




package com.mnu.whoami

import android.content.Intent
import android.content.pm.PackageManager
import android.icu.number.NumberFormatter.with
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.normal.TedPermission
import com.mnu.whoami.databinding.ActivityMainBinding
import java.security.Permission

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding : ActivityMainBinding

    val REQUEST_IMG_CAPTURE  =1
    lateinit var curPhotoPath : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener(this)
        binding.btnAlbum.setOnClickListener(this)

        setPermission()

    }
    private fun showToat(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
    private fun setPermission() {
        val permission = object  : PermissionListener{
            override fun onPermissionGranted() {//권한허용 완료
                showToat("허용완료")
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                showToat("권한 거부")
            }

        }

    }

    /***
     * 액션리스너
     */

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_camera->{
                //카메라 엑피비티 수행

            }
            R.id.btn_album->{
                //로컬엘범에 접근
            }
        }
    }


}




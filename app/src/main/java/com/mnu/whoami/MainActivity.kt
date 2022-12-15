package com.mnu.whoami

import android.Manifest
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mnu.whoami.databinding.ActivityMainBinding

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat


class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private var baseurl: String? = null
    private var gson: Gson? = null
    private var retrofit: Retrofit? = null

    var mCurrentPhotoPath: String? = null

    val CAMERA_PERMISSION = arrayOf(android.Manifest.permission.CAMERA)
    val STORAGE_PERMISSION = arrayOf(
        android.Manifest.permission.READ_EXTERNAL_STORAGE,
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    val FLAG_PERM_CAMERA = 98
    val FLAG_PERM_STORAGE = 99
    val FLAG_REQ_CAMERA = 101


    init {
        baseurl = "http://172.17.228.168:8080/"
        gson = GsonBuilder()
            .setLenient()
            .create()

        retrofit = Retrofit.Builder()
            .baseUrl(baseurl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamera.setOnClickListener(this)
        binding.btnAlbum.setOnClickListener(this)


    }


    /***
     * 카메라 권환 확인
     */
    private fun isPermitted(permissions: Array<String>): Boolean {

        val result = ContextCompat.checkSelfPermission(this, permissions.toString())
        if (result != PackageManager.PERMISSION_GRANTED) {
            return false
        }
        return true
    }

    /***
     * 카메라 start
     */
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, FLAG_REQ_CAMERA)
    }

    /***
     * 토스트메시지 출력
     */
    private fun showToat(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }


    /***
     * 서버로 이미지파일 전송
     */
    private fun sendToServer(faceimgFile: File) {

        var service = retrofit?.create(APIservice::class.java)
        //이미지는 png든 jpng든 모든파일이 가능하도록 설정
        val requestFile: RequestBody = RequestBody.create(MediaType.parse("image/*"), faceimgFile)

        var body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", faceimgFile.name, requestFile)

        service?.SendToServer_faceimg(body)?.enqueue(object : Callback<FaceImgResponse> {

            override fun onFailure(call: Call<FaceImgResponse>, t: Throwable) { // 통신실패
                Log.e("로그", "에러", t)
            }

            override fun onResponse(call: Call<FaceImgResponse>, response: Response<FaceImgResponse>) {
                if (response.isSuccessful) {
                    Log.e("로그",response.body()?.name.toString())

                    when(response.body()?.name.toString()){
                        "yuumi"->{
                            binding.tilte.text="당신은 '유미'! 를 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '유미' 입니다 \n 냥냥냥냥ㄴ야냥냐야냥나냐안안얀양!!!"
                            binding.resultImg.setImageResource(R.drawable.yuumi)
                        }
                        "leesin"->{
                            binding.tilte.text="당신은 '리신'! 을 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '리신' 입니다 \n 눈은 괜찮으신가요?"
                            binding.resultImg.setImageResource(R.drawable.leesin)
                        }
                        "singed"->{
                            binding.tilte.text="당신은 '신지드'! 를 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '신지드' 입니다 \n 방귀뀌기 좋아하시나보네요?"
                            binding.resultImg.setImageResource(R.drawable.singed)
                        }
                        "trundle"->{
                            binding.tilte.text="당신은 '트런들' 을 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '트런들' 입니다 \n 롤에서 제일 못생긴 챔피언중 하나입니다"
                            binding.resultImg.setImageResource(R.drawable.trundle)
                        }
                        "lux"->{
                            binding.tilte.text="당신은 '럭스' 를 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '럭스' 입니다 \n 빛으로 강타해요"
                            binding.resultImg.setImageResource(R.drawable.lux)
                        }
                        "garen"->{
                            binding.tilte.text="당신은 '가렌' 을 닮았네요!"
                            binding.textbox.text="당신이 닮은 챔피언은 '가렌' 입니다 \n 남자중에 남자 입니다."
                            binding.resultImg.setImageResource(R.drawable.garen)
                        }
                    }

                }
            }
        })
    }


    /***
     * 서버로 전송할 파일 생성.
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val imageFileName = "faceImage"
        val storageDir: File? = this@MainActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )
        mCurrentPhotoPath = image.absolutePath
        return image
    }


    /***
     * bitmapToFile 이미지 파일로 변환 테스트
     */
    private fun bitmapToFile(bitmap: Bitmap, path: String): File{
        var file = File(path)
        var out: OutputStream? = null
        try{
            file.createNewFile()
            out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
        }finally{
            out?.close()
        }
        return file
    }


    /***
     * pathToBitmap 파일을 bitmap이미지로 변환 테스트
     */
    private fun pathToBitmap(path: String?) {
        val bitmap = BitmapFactory.decodeFile(path)
        binding.resultImg.setImageBitmap(bitmap)
    }


    /***
     * 카메라 촬영이후 실행되는 함수
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                FLAG_REQ_CAMERA -> {
                    if (data?.extras?.get("data") != null) {
                        val bitmap = data?.extras?.get("data") as Bitmap
//                        binding.resultImg.setImageBitmap(bitmap)
                        var faceimgFile : File? =createImageFile()
                        var resultFile:File= bitmapToFile(bitmap, faceimgFile!!.path)
                        if (faceimgFile != null) {

                            sendToServer(resultFile)
                        }
                    }

                }
            }
        }

    }


    /***
     * 이건 뭔지모르겠는데 권한관련된거같음
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FLAG_PERM_CAMERA -> {
                var checked = true
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        checked = false
                        break
                    }
                }
                if (checked) {
                    openCamera()
                }
            }


        }
    }

    /***
     * 버튼 온클릭
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






}

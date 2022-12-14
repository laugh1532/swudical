package com.example.swudical

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.swudical.DTO.UserInfoDTO
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_staff_vali.*
import kotlinx.android.synthetic.main.activity_staff_vali_1.*
import kotlinx.android.synthetic.main.activity_staff_vali_1.chart
import kotlinx.android.synthetic.main.activity_staff_vali_1.txt_title
import org.nd4j.linalg.factory.Nd4j
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.jvm.Throws

class StaffVali_1_Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali_1)

        //OO님의 수술실
        val user = FirebaseAuth.getInstance()
        val db = FirebaseFirestore.getInstance()
        val uid = user.currentUser?.uid.toString()

        db.collection("user_info").document(uid)
            .get().addOnSuccessListener { result ->
                val userInfoDTO = result.toObject(UserInfoDTO::class.java)

                if(userInfoDTO?.name==null || userInfoDTO.sex ==null || userInfoDTO.email ==null || userInfoDTO.birthday ==null){
                    val intent = Intent(this, UserInfoActivity::class.java)
                    intent.putExtra("where", "main")
                    ContextCompat.startActivity(this, intent, null)
                }

                if (userInfoDTO != null) {
                    txt_title.text = userInfoDTO.name.toString() + "님의 수술실"
                }
                else{
                    txt_title.text = "환자님의 수술실"
                }
            }
            .addOnFailureListener() { exception ->
                Log.w("ERR", "err getting documents: ", exception)
            }



        animationView.playAnimation()
        val voice_path = intent.getStringExtra("voice_path")
        val doctor_id = intent.getStringExtra("doctor_id")

        val ONE_MEGABYTE: Long = 1024 * 1024
        val tflite = getTfliteInterpreter("model40.tflite") //모델 불러오기

        //region 화자인식
        val storage = Firebase.storage("gs://swudical.appspot.com")
        val storageRef: StorageReference = storage.reference
        val pathReference = storageRef.child(voice_path!!)
        pathReference.getBytes(ONE_MEGABYTE).addOnSuccessListener {

            val firebaseData = Nd4j.createNpyFromByteArray(it) //firebase에서 .npy 받아오기
            val input = firebaseData.toFloatMatrix() //(input) INDArray -> FloatArray""
            val output = Array<FloatArray>(firebaseData.rows(), {FloatArray(20)})  //output 초기화

            tflite!!.run(input, output)  //화자인식 run

            val answer = IntArray(20, {0}) //레이블 초기화
            var name = arrayOf<String>("정유경(여)", "익명남(남)", "한예진(여)", "최혜리(여)", "임찬주(여)", "조미맹(여)", "익명2(남)", "정해빈(여)", "정유민(여)", "이윤진(여)", "오가을(여)", "유하영(여)", "오준현(남)", "익명1(남)", "이재용(남)", "이은영(여)", "강국대(여)", "강나뭇잎(남)", "강정윤(여", "자스민(여)")


            //레이블 생성
            for (i in 0..firebaseData.rows()-1){
                answer[output[i].indexOf(output[i].max()!!)] += 1
            }

            var max = 0
            var third_max = 0
            var second_max = 0

            var index = -1
            var index_2 = -1
            var index_3 = -1

            //레이블 출력
            for(i in 0..19){
                val str = "answer(" + i + ") : " + answer[i] + "\n"
                Log.e("TAG", str)

                if(answer[i] > max){
                    third_max = second_max
                    second_max = max
                    max = answer[i]
                    index = i


                } else if(answer[i] > second_max && answer[i] != max){
                    third_max = second_max
                    second_max = answer[i]
                    index_2 = i


                } else if (answer[i] > third_max && answer[i] != max){
                    third_max = answer[i]
                    index_3 = i
                }
            }
            // 그래프 변환을 위한 소수점 변환
            val f_max = max.toFloat()
            val s_max = second_max.toFloat()
            val t_max = third_max.toFloat()
            // 정답 일치.toString 지웠


            if(doctor_id == index.toString()){
                testresult.text = "일치"
                animationView.isVisible = false
                animationView.pauseAnimation()
            }else{
                testresult.text="불일치"
                animationView.isVisible = false
                animationView.pauseAnimation()
            }
            // 그래프 데이터 임의로 넣기
            val entryList = mutableListOf<PieEntry>()
            entryList.add(PieEntry(f_max, name[index]))
            entryList.add(PieEntry(s_max, name[index_2]))
            entryList.add(PieEntry(t_max, name[index_3]))
            val colorsItems = ArrayList<Int>()

            colorsItems.add(resources.getColor(R.color.mainPurple))
            colorsItems.add(resources.getColor(R.color.subPurple2))
            colorsItems.add(resources.getColor(R.color.subPurple1))


            // 파이 그래프에 들어갈 데이터 set 생성
            val pieDataSet = PieDataSet(entryList, " ")
            // pie 커스터마이징
            pieDataSet.apply {

                sliceSpace = 2f
                selectionShift = 5f
                colors = colorsItems
                yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
                valueTextSize = 10f

            }
            // 어떻게 데이터 보여줄지 구성하
            val pieData = PieData(pieDataSet)
            chart.apply {
                data = pieData
                description.isEnabled = false
                isRotationEnabled = false
                setEntryLabelColor(Color.BLACK)

                animateY(1400, Easing.EaseInOutQuad)
                animate()
            }
        }.addOnFailureListener {
            Log.e("TAG", "error")
        }
        //endregion


        //하단 바
        Common.BottomBar(btn_staffvali, btn_home, btn_rcdvali)
    }


    //region 텐서플로우 인터프리터
    private fun getTfliteInterpreter(modelPath: String): Interpreter? {
        try {
            return Interpreter(loadModelFile(this, modelPath)!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    private fun loadModelFile(activity: Activity, modelPath: String): MappedByteBuffer? {
        val fileDescriptor = activity.assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
    //endregion

    override fun onBackPressed() {
        val intent = Intent(this, StaffValiActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }
}
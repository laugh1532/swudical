package com.example.swudical

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.firebase.database.collection.LLRBNode
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_home.btn_home
import kotlinx.android.synthetic.main.activity_home.btn_rcdvali
import kotlinx.android.synthetic.main.activity_home.btn_staffvali
import kotlinx.android.synthetic.main.activity_staff_vali_2.*

class StaffVali_2_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_vali_2)
        // 하단 바
        btn_staffvali.setOnClickListener {
            val intent = Intent(this, StaffValiActivity::class.java)
            startActivity(intent)
        }
        btn_home.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
        }
        btn_rcdvali.setOnClickListener {
            val intent = Intent(this, RecordsValiActivity::class.java)
            startActivity(intent)
        }

        // 그래프 데이터 임의로 넣기
        val entryList = mutableListOf<PieEntry>()
        entryList.add(PieEntry(5f, "A"))
        entryList.add(PieEntry(2f, "B"))
        entryList.add(PieEntry(2f, "C"))

        val colorsItems = ArrayList<Int>()
        for (c in ColorTemplate.VORDIPLOM_COLORS) colorsItems.add(c)
        for (c in ColorTemplate.JOYFUL_COLORS) colorsItems.add(c)
        colorsItems.add(ColorTemplate.getHoloBlue())

        // 파이 그래프에 들어갈 데이터 set 생성
        val pieDataSet = PieDataSet(entryList, "MyPieChart")
        // pie 커스터마이
        pieDataSet.apply {

//            //슬라이스 간격
            sliceSpace = 2f
            selectionShift = 5f
//
//            //슬라이스 색, 미리 정의하거나 resource로 가져온 색 리스트를 줘도 좋다
//            //colors = Color.BLACK
            colors = colorsItems
//            //value 위치, 크기 지정
            yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
            valueTextSize = 11f

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
        //chart.setUsePercentValues(true)
    }
        override fun onBackPressed() {
            startActivity(Intent(this, RecordsValiActivity::class.java))
            finish()
        }


}

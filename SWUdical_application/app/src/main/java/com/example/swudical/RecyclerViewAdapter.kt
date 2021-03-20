package com.example.swudical

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import kotlinx.android.synthetic.main.item_list.view.*

class RecyclerViewAdapter(private val items: ArrayList<MedicalConfirmDTO>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    //ViewHolder 새로 만들 때마다 이 메서드 호출 ViewHolder와 그에 연결된 View를 생성하고
    //초기화하지만, 뷰의 콘텐츠를 채우지는 않는다. 바인딩된 상태 아님
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("LOG", "onCreateViewHolder")

        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)

        return ViewHolder(inflateView)
    }

    //ViewHolder를 데이터와 연결할 때 이 메서드 호출
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("TEST", "onBindViewHolder");
        val item = items[position]
//        val listener = View.OnClickListener { it ->
//            Toast.makeText(it.context, "Clicked: ${item.surgery}", Toast.LENGTH_LONG).show()
//        }
        holder.bindViewHolder(item)
    }



    //데이터 세트 크기 호출
    override fun getItemCount() = items.size

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view: View = v

        fun bindViewHolder(item: MedicalConfirmDTO) {
            view.date.text = item.date
            view.surgery.text = item.surgery
            view.doctor.text = item.doctor_name
            view.hospital.text = item.hospital_name
            view.layout.setOnClickListener {
                Toast.makeText(it.context, "Clicked: ${item.surgery}", Toast.LENGTH_LONG).show()
                Log.d("TEST", "onBindViewHolder")
            }
        }

    }
}
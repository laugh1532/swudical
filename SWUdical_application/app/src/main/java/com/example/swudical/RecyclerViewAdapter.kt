package com.example.swudical

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
        holder.bindViewHolder(item)
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, StaffVali_1_Activity::class.java)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
        }
    }

    //데이터 세트 크기 호출
    override fun getItemCount() = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var view: View = itemView

        init{
            itemView.setOnClickListener {
//                val intent = Intent(holder.itemView?.context, StaffVali_1_Activity::class.java)
//                ContextCompat.startActivity(holder.itemView.context, intent, null)
            }
        }

        fun bindViewHolder(item: MedicalConfirmDTO) {
            view.date.text = item.date
            view.surgery.text = item.surgery
            view.doctor.text = item.doctor_name
            view.hospital.text = item.hospital_name
        }

    }
}
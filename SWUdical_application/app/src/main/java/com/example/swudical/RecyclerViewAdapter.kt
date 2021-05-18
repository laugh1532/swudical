package com.example.swudical

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import kotlinx.android.synthetic.main.item_list.view.*

class RecyclerViewAdapter(private val items: ArrayList<MedicalConfirmDTO>, private val layout_id: Int) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount() = items.size

    //리사이클러뷰 생성 이벤트
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("LOG", "onCreateViewHolder")

        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(inflateView, layout_id)
    }

    //바인딩 이벤트
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.apply{
            bindViewHolder(item, position)
        }
    }

    //뷰 홀더 클래스
    class ViewHolder(itemView: View, private var layout_id: Int) : RecyclerView.ViewHolder(itemView) {
        private var view: View = itemView

        private val btn_check: Button = itemView.findViewById(R.id.btn_check) as Button
        fun bindViewHolder(item: MedicalConfirmDTO, position: Int) {

            //데이터 바인딩(item_list-파이어베이스)
            view.txt_subtitle.text = item.surgery_date
            view.surgery.text = item.diagnosis
            view.hospital.text = item.hospital
            view.doctor_name.text = item.doctor_name
            view.num.text = (position+1).toString()

            //region 리사이클러뷰 클릭이벤트
            //의료진확인
            if (layout_id == R.layout.activity_staff_vali){
                btn_check.isInvisible
                btn_check.setOnClickListener{
                    val intent = Intent(it.context, StaffVali_1_Activity::class.java)
                    intent.putExtra("voice_path", item.voice_path)
                    intent.putExtra("doctor_id", item.doctor_id.toString())
                    ContextCompat.startActivity(it.context, intent, null)
                }
                itemView.setOnClickListener {
                    val intent = Intent(it.context, StaffVali_1_Activity::class.java)
                    intent.putExtra("voice_path", item.voice_path)
                    intent.putExtra("doctor_id", item.doctor_id.toString())
                    ContextCompat.startActivity(it.context, intent, null)
                }
            }
            //진료기록 확인
            else if (layout_id == R.layout.activity_records_vali){
                itemView.setOnClickListener {
                    val intent = Intent(it.context, Records_Vali_1_Activity::class.java)
                    intent.putExtra("item", item)
                    ContextCompat.startActivity(it.context, intent, null)
                }
                btn_check.setOnClickListener{
                    val intent = Intent(it.context, BlockchainValiActivity::class.java)
                    intent.putExtra("item", item)
                    ContextCompat.startActivity(it.context, intent, null)
                }

            }
            //endregion

        }

    }
}
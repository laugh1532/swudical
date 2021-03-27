package com.example.swudical

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.swudical.DTO.MedicalConfirmDTO
import kotlinx.android.synthetic.main.item_list.view.*

class RecyclerViewAdapter(private val items: ArrayList<MedicalConfirmDTO>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d("LOG", "onCreateViewHolder")

        val inflateView = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(inflateView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

//        val listener = View.OnClickListener{
//            Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()
//        }

        holder.itemView.setOnClickListener {
            Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()

            val intent = Intent(it.context, StaffVali_1_Activity::class.java)
            ContextCompat.startActivity(it.context, intent, null)
        }

//        holder.itemView.tableRow.setOnClickListener {
//            Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()
//        }

        holder.apply{
            bindViewHolder(item)
        }
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        init{
//            itemView.tableRow.setOnClickListener {
//                Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()
//            }
////            itemView.setOnClickListener {
////                Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()
////            }
//        }
        private var view: View = itemView

        fun bindViewHolder(item: MedicalConfirmDTO) {
            view.date.text = item.date
            view.surgery.text = item.surgery
            view.doctor.text = item.doctor_name
            view.hospital.text = item.hospital_name

//            itemView.setOnClickListener {
//                Toast.makeText(it.context, "Clicked", Toast.LENGTH_SHORT).show()
//
//                val intent = Intent(it.context, StaffVali_1_Activity::class.java)
//                ContextCompat.startActivity(view.context, intent, null)
//            }
        }

    }
}
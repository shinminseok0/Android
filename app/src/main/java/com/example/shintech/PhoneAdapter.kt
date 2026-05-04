package com.example.shintech

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.text.NumberFormat
import java.util.Locale

class PhoneAdapter(
    private var phoneList: List<Phone>,
    private val isAdmin: Boolean = false,
    private val onItemClick: (Phone) -> Unit,
    private val onDeleteClick: ((Phone) -> Unit)? = null
) : RecyclerView.Adapter<PhoneAdapter.PhoneViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_phone, parent, false)
        return PhoneViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhoneViewHolder, position: Int) {
        val phone = phoneList[position]
        holder.bind(phone, isAdmin, onDeleteClick)
        holder.itemView.setOnClickListener { onItemClick(phone) }
    }

    override fun getItemCount(): Int = phoneList.size

    fun updatePhones(newPhones: List<Phone>) {
        phoneList = newPhones
        notifyDataSetChanged()
    }

    class PhoneViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivPhoneImage: ImageView = itemView.findViewById(R.id.idPhoneImage)
        private val tvPhoneBrand: TextView = itemView.findViewById(R.id.tvPhoneBrand)
        private val tvPhoneName: TextView = itemView.findViewById(R.id.tvPhoneName)
        private val tvPhonePrice: TextView = itemView.findViewById(R.id.tvPhonePrice)
        private val btnDeletePhone: ImageButton = itemView.findViewById(R.id.btnDeletePhone)

        fun bind(phone: Phone, isAdmin: Boolean, onDeleteClick: ((Phone) -> Unit)?) {
            val context = itemView.context
            
            // 이미지 소스 확인: imageUrl이 단순 파일명일 경우 로컬 리소스 ID로 변환
            val resolvedResId = phone.imageResId ?: if (phone.imageUrl != null && !phone.imageUrl.startsWith("http")) {
                val resourceName = phone.imageUrl.substringBeforeLast(".")
                val id = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
                if (id != 0) id else null
            } else {
                null
            }

            val imageSource = resolvedResId ?: phone.imageUrl

            ivPhoneImage.load(imageSource) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_launcher_background)
            }

            tvPhoneBrand.text = phone.brand
            tvPhoneName.text = phone.name
            tvPhonePrice.text = "${NumberFormat.getNumberInstance(Locale.KOREA).format(phone.price)}원"

            if (isAdmin) {
                btnDeletePhone.visibility = View.VISIBLE
                btnDeletePhone.setOnClickListener {
                    onDeleteClick?.invoke(phone)
                }
            } else {
                btnDeletePhone.visibility = View.GONE
            }
        }
    }
}

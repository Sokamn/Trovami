package com.sokamn.trovami.ui.bottom_nav.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sokamn.trovami.R
import com.sokamn.trovami.databinding.ItemMasterBinding
import com.sokamn.trovami.domain.model.MasterInfo

class MasterAdapter(private val activity: String,
        private var horoscopeList: List<MasterInfo> = emptyList()
) : ListAdapter<MasterInfo, MasterAdapter.MasterViewHolder>(DiffCallBack){

    inner class MasterViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val binding = ItemMasterBinding.bind(view)

        fun render(master: MasterInfo){
            Glide.with(itemView)
                .load(master.imageOutlined)
                .into(binding.imvMasterJobKJI)
            binding.txvMasterJobKJI.setText(master.name)

            when(activity){
                "MKJobActivity"->{
                    if (master.isSelected){
                        onItemSelectedListener?.let { click ->
                            click(master)
                        }
                        setItemSelectedStyle(this)
                    }else{
                        setItemNonselectedStyle(this)
                    }
                    itemView.setOnClickListener {
                        currentList.forEach {
                            if (it.isSelected && master.name != it.name){
                                it.isSelected = false
                                notifyItemChanged(it.id)
                            }
                        }
                        master.isSelected = !master.isSelected
                        if (!master.isSelected){
                            onItemSelectedListener?.let { click ->
                                click(master)
                            }
                        }
                        notifyItemChanged(position)
                    }
                }
                "MainActivity"->{
                    itemView.setOnClickListener {
                        onItemClickListener?.let { click ->
                            click(master)
                        }
                    }
                }
            }

            itemView.setOnLongClickListener {
                Toast.makeText(itemView.context, master.name, Toast.LENGTH_SHORT).show()
                true
            }
        }
    }

    protected var onItemClickListener : ((MasterInfo) -> Unit)? = null
    protected var onItemSelectedListener: ((MasterInfo) -> Unit)? = null
    protected var onItemDeselectedListener: ((MasterInfo) -> Unit)? = null

    fun setItemClickListener(listener: (MasterInfo) -> Unit){
        onItemClickListener = listener
    }

    fun setItemSelectedListener(listener: (MasterInfo) -> Unit){
        onItemSelectedListener = listener
    }

    fun setItemDeselectedListener(listener: (MasterInfo) -> Unit){
        onItemDeselectedListener = listener
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MasterViewHolder {
        val view: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_master,parent,false)
        return MasterViewHolder(view)
    }

    override fun onBindViewHolder(holder: MasterViewHolder, position: Int) {
        val item = getItem(position)
        holder.render(item)
    }

    private fun setItemSelectedStyle(holder: MasterViewHolder) {
        with(holder.binding){
            crdBackgroundIM.background.setTint(holder.itemView.context.getColor(R.color.secundaryColor))
            crdBackgroundIM.strokeColor = holder.itemView.context.getColor(R.color.tertiaryColor)
            txvMasterJobKJI.setTextColor(Color.WHITE)
        }
    }

    private fun setItemNonselectedStyle(holder: MasterViewHolder) {
        with(holder.binding){
            crdBackgroundIM.background.setTint(holder.itemView.context.getColor(R.color.white))
            crdBackgroundIM.strokeColor = holder.itemView.context.getColor(R.color.bg)
            txvMasterJobKJI.setTextColor(Color.BLACK)
        }
    }

    companion object DiffCallBack: DiffUtil.ItemCallback<MasterInfo>() {
        override fun areItemsTheSame(oldItem: MasterInfo, newItem: MasterInfo): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: MasterInfo, newItem: MasterInfo): Boolean {
            return oldItem == newItem
        }
    }
}
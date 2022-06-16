package com.trdz.day_picture.w_view.segment_book

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.trdz.day_picture.databinding.ElementKnowlageElementBinding
import com.trdz.day_picture.databinding.ElementKnowlageTitleBinding

const val TYPE_EARTH = 1
const val TYPE_MARS = 2
const val TYPE_HEADER = 3

class WindowKnowledgeRecycle(private var list: List<Data>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	override fun getItemViewType(position: Int): Int {
		return list[position].type
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
		return when (viewType) {
			TYPE_EARTH -> {
				val view = ElementKnowlageTitleBinding.inflate(LayoutInflater.from(parent.context))
				EarthViewHolder(view.root)
			}
			TYPE_MARS -> {
				val view = ElementKnowlageElementBinding.inflate(LayoutInflater.from(parent.context))
				MarsViewHolder(view.root)
			}
			else -> {
				val view = ElementKnowlageElementBinding.inflate(LayoutInflater.from(parent.context))
				MarsViewHolder(view.root)
			}
		}

	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		when (getItemViewType(position)) {
			TYPE_EARTH -> {
				(holder as EarthViewHolder).myBind(list[position])
			}
			TYPE_MARS -> {
				(holder as MarsViewHolder).myBind(list[position])
			}
		}
	}

	override fun getItemCount(): Int {
		return list.size
	}

	class EarthViewHolder(view: View): RecyclerView.ViewHolder(view) {
		fun myBind(data: Data) {
			(ElementKnowlageTitleBinding.bind(itemView)).apply {
				title.text = data.someText
			}
		}
	}

	class MarsViewHolder(view: View): RecyclerView.ViewHolder(view) {
		fun myBind(data: Data) {
			(ElementKnowlageElementBinding.bind(itemView)).apply {
				title.text = data.someText
				descriptionTextView.text = data.someDescription
			}
		}
	}
}
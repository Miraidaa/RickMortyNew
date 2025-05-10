package com.example.rickandmorty.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.ProfileActivity

class CharacterAdapter : PagingDataAdapter<Character, CharacterAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Character>() {
            override fun areItemsTheSame(oldItem: Character, newItem: Character) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Character, newItem: Character) = oldItem == newItem
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.characterName)
        val species: TextView = itemView.findViewById(R.id.characterSpecies)
        val status: TextView = itemView.findViewById(R.id.characterStatus)
        val avatar: ImageView = itemView.findViewById(R.id.characterAvatar)

        fun bind(character: Character?) {
            character?.let {
                name.text = it.name
                species.text = it.species
                status.text = it.status
                Glide.with(itemView.context).load(it.image).into(avatar)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, ProfileActivity::class.java)
                    intent.putExtra("characterId", it.id)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.character_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

package com.example.rickandmorty

import android.os.Bundle
import android.telecom.Call
import android.view.WindowInsetsAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.tracing.perfetto.handshake.protocol.Response
import com.bumptech.glide.Glide
import com.example.rickandmorty.R
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Callback


class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val characterId = intent.getIntExtra("characterId", -1)
        val characterName = findViewById<TextView>(R.id.characterName)
        val characterSpecies = findViewById<TextView>(R.id.characterSpecies)
        val characterStatus = findViewById<TextView>(R.id.characterStatus)
        val characterAvatar = findViewById<ImageView>(R.id.characterAvatar)
        val backButton = findViewById<Button>(R.id.backButton)

        fetchCharacterDetails(
            characterId,
            characterName,
            characterSpecies,
            characterStatus,
            characterAvatar
        )

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun fetchCharacterDetails(
        characterId: Int,
        nameView: TextView,
        speciesView: TextView,
        statusView: TextView,
        avatarView: ImageView
    ) {
        RetrofitClient.instance.getCharacterById(characterId).enqueue(object : Callback<Character> {
            override fun onResponse(
                call: retrofit2.Call<Character>,
                response: retrofit2.Response<Character>
            ) {
                response.body()?.let { character ->
                    nameView.text = character.name
                    speciesView.text = character.species
                    statusView.text = character.status
                    Glide.with(this@ProfileActivity).load(character.image).into(avatarView)
                }
            }

            override fun onFailure(call: retrofit2.Call<Character>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Failed to load data", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}

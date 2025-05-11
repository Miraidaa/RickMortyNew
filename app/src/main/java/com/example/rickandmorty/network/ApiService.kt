package com.example.rickandmorty.network


import com.example.rickandmorty.model.Character
import com.example.rickandmorty.model.CharacterResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("character")
    fun getCharacters(@Query("page") page: Int): Call<CharacterResponse>

    @GET("character/{id}")
    fun getCharacterById(@Path("id") id: Int): Call<Character>
}

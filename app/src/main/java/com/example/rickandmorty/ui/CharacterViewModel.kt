package com.example.rickandmorty.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.network.ApiService
import com.example.rickandmorty.network.CharacterPagingSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cachedIn

class CharacterViewModel(private val apiService: ApiService) : ViewModel() {

    val characterFlow: Flow<PagingData<Character>> = Pager(
        PagingConfig(pageSize = 20)
    ) {
        CharacterPagingSource(apiService)
    }.flow.cachedIn(viewModelScope)
}

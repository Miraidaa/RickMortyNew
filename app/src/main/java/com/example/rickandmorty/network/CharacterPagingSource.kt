package com.example.rickandmorty.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.rickandmorty.model.Character
import com.example.rickandmorty.network.ApiService
import retrofit2.HttpException
import java.io.IOException
import com.example.rickandmorty.model.CharacterResponse


class CharacterPagingSource(private val apiService: ApiService) : PagingSource<Int, Character>() {
    override fun getRefreshKey(state: PagingState<Int, Character>): Int? {
        return state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.prevKey?.plus(1) ?: state.closestPageToPosition(anchor)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Character> {
        val currentPage = params.key ?: 1

        return try {
            val response: CharacterResponse = apiService.getCharacters(currentPage) // Ensure proper type
            val characters: List<Character> = response.results ?: emptyList()
            val nextPage: Int? = response.info.next?.substringAfter("page=", "")?.toIntOrNull()

            LoadResult.Page(
                data = characters,
                prevKey = if (currentPage == 1) null else currentPage - 1,
                nextKey = nextPage
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }


}

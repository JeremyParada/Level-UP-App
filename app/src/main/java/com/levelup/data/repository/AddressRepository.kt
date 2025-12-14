package com.levelup.data.repository

import com.levelup.data.model.Direccion
import com.levelup.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import retrofit2.Response

@Singleton
class AddressRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getAddresses(userId: Long): List<Direccion> {
        return withContext(Dispatchers.IO) {
            val response = apiService.getAddressesByUser(userId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun createAddress(direccion: Direccion): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.createAddress(direccion)
            response.isSuccessful
        }
    }

    suspend fun updateAddress(id: Long, direccion: Direccion): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.updateAddress(id, direccion)
            response.isSuccessful
        }
    }

    suspend fun deleteAddress(id: Long): Boolean {
        return withContext(Dispatchers.IO) {
            val response = apiService.deleteAddress(id)
            response.isSuccessful
        }
    }
}

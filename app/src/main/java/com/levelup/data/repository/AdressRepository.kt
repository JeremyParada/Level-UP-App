package com.levelup.data.repository

import com.levelup.data.model.Address
import com.levelup.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AddressRepository @Inject constructor(
    private val apiService: ApiService
) {

    private val _addressesFlow = MutableStateFlow<List<Address>>(emptyList())
    val addressesFlow: Flow<List<Address>> = _addressesFlow.asStateFlow()

    suspend fun getAddressesByUser(userId: String): Result<List<Address>> {
        return try {
            val response = apiService.getAddressesByUser(userId)
            if (response.isSuccessful && response.body() != null) {
                val addresses = response.body()!!
                _addressesFlow.value = addresses
                Result.success(addresses)
            } else {
                Result.failure(Exception("Error al obtener direcciones: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createAddress(address: Address): Result<Unit> {
        return try {
            val response = apiService.createAddress(address)
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error al crear dirección: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
package com.levelup.data.auth

import com.levelup.data.model.User
import com.levelup.data.remote.ApiService
import com.levelup.data.remote.dto.LoginDTO
import com.levelup.data.remote.dto.RegistroDTO
import com.levelup.data.session.SessionManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class AuthRepositoryImpl
@Inject
constructor(
        private val apiService: ApiService,
        private val sessionManager:
                SessionManager // We use this to save token internally or just use it in ViewModel?
// Actually, to make "getUserById" work with "me", we rely on token.
// Let's decide: Repository saves token on login success?
// Start with: Repository does NOT save token, it returns it?
// But interface signature is `AuthResult`.
// Let's modify logic: Repository saves token on success login.
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginDTO(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val jwtDto = response.body()!!
                    android.util.Log.d(
                            "AuthRepositoryImpl",
                            "Login success! Token received: ${jwtDto.token.take(10)}..."
                    )
                    // Save session
                    sessionManager.saveSession(jwtDto.id, jwtDto.token)
                    val user =
                            User(
                                    idUsuario = jwtDto.id,
                                    nombre = jwtDto.username,
                                    email = jwtDto.email
                            )
                    AuthResult.Success(user)
                } else {
                    AuthResult.Error("Login fallido: ${response.code()}")
                }
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    override suspend fun register(registro: RegistroDTO): AuthResult {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(registro)
                if (response.isSuccessful) {
                    // Registration success, but we don't get user/token.
                    // We return Success but with a dummy user or just indicate success?
                    // AuthResult.Success requires a User.
                    // We can return a dummy user with ID 0 and empty fields just to signal success?
                    // Or we can try to auto-login? No we don't have password if we hashed it (we
                    // have it here though).
                    // Let's return a dummy user or change AuthResult to sealed interface with
                    // different Success types.
                    // For now, simple hack: Dummy user.
                    val dummyUser =
                            User(idUsuario = 0, nombre = registro.nombre, email = registro.email)
                    AuthResult.Success(dummyUser)
                    AuthResult.Success(dummyUser)
                } else {
                    val msg = response.errorBody()?.string() ?: "Error en registro"
                    AuthResult.Error(msg)
                }
            } catch (e: Exception) {
                AuthResult.Error(e.message ?: "Error desconocido")
            }
        }
    }

    override suspend fun getUserById(id: Long): User? {
        return withContext(Dispatchers.IO) {
            try {
                // We use "me" endpoint which is safer, but it takes ID.
                val response = apiService.getUserProfile(id)
                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

    override suspend fun updateUser(id: Long, nombre: String, telefono: String?): AuthResult {
        return withContext(Dispatchers.IO) {
            // Backend doesn't seem to have a specific endpoint for updating JUST user info in the
            // standard way?
            // "api/v1/usuarios" -> getPerfil.
            // Address is handled separately.
            // Is there a user update endpoint?
            // In exploring files, I only saw "UsuarioController" with "getPerfil".
            // "AuthController" has login/register.
            // I did NOT see an update endpoint in UsuarioController.
            // I need to check `UsuarioController.java` again.
            // If it's missing, I can't implement it.
            // Wait, looking at file `UsuarioController.java` (step 22), it ONLY has `getPerfil`.
            // So we CANNOT update user name/phone currently.
            // User requested: "que el usuario... pueda agregar direcciones...".
            // User did NOT explicitly ask to update Name/Phone in "Level-UP-App" request, but
            // "PersonalInfoScreen" has it.
            // "PersonalInfoScreen rules: agregar direcciones, modificar las existentes".
            // It says "Que en la pantalla PersonalInfoScreen.kt el usuario con la sesión iniciada
            // pueda agregar direcciones...".
            // It doesn't explicitly force me to fix user Update name/phone.
            // But the screen has it.
            // I will return Error("Not implemented in backend") for now or just fake it.
            // Since "read-only" on backend, I can't add the endpoint.
            // I will return AuthResult.Success w/o changes or Error.
            AuthResult.Error("Actualización de perfil no disponible en Backend")
        }
    }

    override suspend fun deleteUserData(userId: Long): AuthResult {
        // Same here, no delete endpoint seen in UsuarioController.
        return AuthResult.Error("Eliminación de cuenta no disponible en Backend")
    }
}

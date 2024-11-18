import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emsi.tprestdataandroid.api.ApiService
import com.emsi.tprestdataandroid.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainViewModel : ViewModel() {
    private val _comptes = MutableStateFlow<UiState<List<Compte>>>(UiState.Loading)
    val comptes: StateFlow<UiState<List<Compte>>> = _comptes

    var contentType = "application/json"
    private var acceptType = "application/json"
    private lateinit var retrofit: Retrofit
    lateinit var apiService: ApiService

    init {
        setupApiService(GsonConverterFactory.create())
    }

    private fun setupApiService(converterFactory: Converter.Factory) {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val request: Request = chain.request()
                .newBuilder()
                .header("Content-Type", contentType)
                .header("Accept", acceptType)
                .build()
            chain.proceed(request)
        }.build()

        retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8082/")
            .client(client)
            .addConverterFactory(converterFactory)
            .build()

        apiService = retrofit.create(ApiService::class.java)
    }

    fun setFormat(format: String) {
        when (format) {
            "JSON" -> {
                contentType = "application/json"
                acceptType = "application/json"
                setupApiService(GsonConverterFactory.create())
            }
            "XML" -> {
                contentType = "application/xml"
                acceptType = "application/xml"
                setupApiService(SimpleXmlConverterFactory.create())
            }
        }
    }

    fun fetchComptes() {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val comptesList = if (acceptType == "application/json") {
                    val response = apiService.getAllComptesJson()
                    if (response.isSuccessful) response.body() ?: emptyList()
                    else emptyList()
                } else {
                    val response = apiService.getAllComptesXml()
                    Log.d("XML Response", "Comptes: ${response.body()?.comptes}")
                    if (response.isSuccessful) response.body()?.comptes?.toMutableList() ?: mutableListOf()
                    else mutableListOf()
                }

                _comptes.value = if (comptesList.isEmpty()) {
                    UiState.Error("No accounts found.")
                } else {
                    UiState.Success(comptesList)
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
            }
        }
    }

    fun deleteCompte(id: Long) {
        viewModelScope.launch {
            try {
                val response = if (acceptType == "application/json") {
                    apiService.deleteCompteJson(id)
                } else {
                    apiService.deleteCompteXml(id)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data.filter { it.id != id }
                        )
                        else -> currentState
                    }
                    Log.d("MainViewModel", "Account deleted successfully")
                } else {
                    _comptes.value = UiState.Error("Failed to delete account.")
                    Log.e("MainViewModel", "Failed to delete account")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error deleting account: ${e.message}")
            }
        }
    }

    fun createCompte(newCompte: Compte) {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val response = if (acceptType == "application/json") {
                    apiService.createCompteJson(newCompte)
                } else {
                    apiService.createCompteXml(newCompte)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data + (response.body() ?: newCompte)
                        )
                        else -> currentState
                    }
                    Log.d("MainViewModel", "Account created successfully")
                } else {
                    _comptes.value = UiState.Error("Failed to create account.")
                    Log.e("MainViewModel", "Failed to create account")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error creating account: ${e.message}")
            }
        }
    }

    fun updateCompte(updatedCompte: Compte) {
        viewModelScope.launch {
            _comptes.value = UiState.Loading
            try {
                val response = if (acceptType == "application/json") {
                    apiService.updateCompteJson(updatedCompte.id, updatedCompte)
                } else {
                    apiService.updateCompteXml(updatedCompte.id, updatedCompte)
                }

                if (response.isSuccessful) {
                    _comptes.value = when (val currentState = _comptes.value) {
                        is UiState.Success -> UiState.Success(
                            currentState.data.map {
                                if (it.id == updatedCompte.id) updatedCompte else it
                            }
                        )
                        else -> currentState
                    }
                    Log.d("MainViewModel", "Account updated successfully")
                } else {
                    _comptes.value = UiState.Error("Failed to update account.")
                    Log.e("MainViewModel", "Failed to update account")
                }
            } catch (e: Exception) {
                _comptes.value = UiState.Error("Error: ${e.message}")
                Log.e("MainViewModel", "Error updating account: ${e.message}")
            }
        }
    }
}
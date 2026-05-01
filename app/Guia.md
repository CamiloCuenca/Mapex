Construcción de aplicaciones móviles
Universidad del Quindío
Programa de Ingeniería de Sistemas y Computación
Título: Retrofit y Comunicación con APIs REST
Docente: Carlos Andrés Florez V.
Retrofit y Comunicación con APIs REST
Introducción
En el desarrollo de aplicaciones móviles, es común que necesitemos comunicarnos con servicios web para obtener o enviar datos. Una forma popular de hacerlo en Android es mediante el uso de Retrofit, una biblioteca que facilita la interacción con APIs RESTful.

¿Qué es Retrofit?
Retrofit es una biblioteca de cliente HTTP para Android y Java desarrollada por Square. Proporciona una forma sencilla y eficiente de consumir APIs RESTful al convertir las llamadas HTTP en interfaces de Java/Kotlin. Retrofit maneja automáticamente la serialización y deserialización de datos, lo que simplifica el proceso de comunicación con servicios web.

La documentación oficial de Retrofit se puede encontrar en el siguiente enlace: Retrofit.

Comparación con otras bibliotecas
Aunque existen diversas bibliotecas para realizar solicitudes HTTP en Android, como Volley y OkHttp, Retrofit se distingue por su facilidad de uso y por su estrecha integración con bibliotecas de serialización como Gson y Moshi. Una de sus principales ventajas es la posibilidad de definir las solicitudes HTTP mediante anotaciones, lo que permite escribir un código más declarativo, legible y fácil de mantener. Estas características hacen de Retrofit una opción especialmente adecuada para el consumo de APIs REST en aplicaciones Android modernas.

En esta tabla se comparan algunas características clave de Retrofit con otras bibliotecas populares:

Característica	Retrofit	Volley	OkHttp
Nivel de abstracción	Alto	Medio	Bajo
Propósito principal	Consumo de APIs REST	Gestión de solicitudes HTTP y cache	Cliente HTTP
Uso de anotaciones	Sí	No	No
Soporte para APIs REST	Sí (orientado a REST)	Sí	Sí (manual)
Serialización de datos	Mediante conversores (Gson, Moshi, etc.)	Manual	Manual
Integración directa con Gson	Sí (converter)	No	No
Manejo de concurrencia	Automático (con coroutines, RxJava, etc.)	Automático	Manual
Manejo de cache	No (requiere configuración adicional)	Sí (integrado)	Sí (configurable)
Facilidad de uso	Alta	Media	Baja
Uso típico	Apps modernas basadas en APIs REST	Apps con muchas peticiones pequeñas	Base para otras bibliotecas
Podemos decir que Retrofit abstrae a OkHttp para facilitar el consumo de servicios REST.

Architectura de Retrofit
En el siguiente diagrama se muestra un breve resumen de la arquitectura de Retrofit:

Arquitectura de Retrofit

Retrofit se basa en la creación de interfaces que definen los endpoints de la API, utilizando anotaciones para especificar el tipo de solicitud HTTP y los parámetros. Luego, Retrofit genera automáticamente las implementaciones de estas interfaces, lo que permite a los desarrolladores realizar solicitudes a la API de manera sencilla y eficiente. Además, Retrofit se integra con bibliotecas de serialización para convertir automáticamente los datos entre formatos JSON y objetos Java/Kotlin, lo que simplifica aún más el proceso de comunicación con servicios web.

Ejemplo Práctico
A continuación, se presenta un ejemplo práctico de cómo utilizar Retrofit para consumir una API REST en una aplicación Android.

1. Agregar Dependencias
   Primero, agregamos las siguientes líneas en el archivo libs.versions.toml:

[versions]
retrofit = "3.0.0"

[libraries]
retrofit = { module = "com.squareup.retrofit2:retrofit", version.ref = "retrofit" }
converter-gson = { module = "com.squareup.retrofit2:converter-gson", version.ref = "retrofit" }
Y luego, en el archivo build.gradle.kts del módulo de la aplicación, agregamos las dependencias:

dependencies {
implementation(libs.retrofit)
implementation(libs.converter.gson)
}
Aunque en este ejemplo utilizamos Gson como convertidor, Retrofit es compatible con otros convertidores como Moshi, Jackson o Protobuf, lo que permite a los desarrolladores elegir la opción que mejor se adapte a sus necesidades.

⚠️ Importante: Para que la aplicación pueda realizar solicitudes a través de la red, es necesario declarar el permiso de internet en el archivo AndroidManifest.xml:

<uses-permission android:name="android.permission.INTERNET" />
2. Definir la Interfaz de la API
Creamos una interfaz que defina los endpoints de la API utilizando anotaciones de Retrofit.

interface ApiService {
@GET("users")
suspend fun getUsers(): List<User>
}
En esta interfaz, definimos un método getUsers que realiza una solicitud GET al endpoint users y devuelve una lista de objetos User. El uso de la palabra clave suspend indica que esta función es una función de corrutina, lo que permite realizar operaciones asíncronas de manera más sencilla.

Así como usamos @GET para solicitudes GET, Retrofit también proporciona otras anotaciones como @POST, @PUT, @DELETE, entre otras, para manejar diferentes tipos de solicitudes HTTP.

3. Crear el Cliente Retrofit
   Configuramos Retrofit para crear una instancia del cliente API.

val retrofit = Retrofit.Builder()
.baseUrl("https://api.example.com/") // Aquí va la URL base de la API
.addConverterFactory(GsonConverterFactory.create()) // Usamos Gson para la serialización
.build()
val apiService = retrofit.create(ApiService::class.java)
4. Realizar Solicitudes a la API
   Utilizamos la instancia de apiService para realizar solicitudes a la API.

// Se lanza una corrutina para realizar la solicitud de manera asíncrona
GlobalScope.launch {
val users = apiService.getUsers()
users.forEach { user ->
Log.d("User", "Name: ${user.name}, Age: ${user.age}")
}
}
Este es un ejemplo básico de cómo utilizar Retrofit para consumir una API REST en una aplicación Android. En un entorno de producción, es recomendable manejar los errores y las respuestas de manera más robusta, así como utilizar un enfoque más estructurado para la gestión de corrutinas, como ViewModel y StateFlow.

5. Definir el Modelo de Datos
   Creamos una clase de datos que represente la estructura de los datos recibidos de la API.

data class User(
val id: Int,
val name: String,
val age: Int
)
Integrar Retrofit con una arquitectura más completa
El ejemplo anterior muestra el uso básico de Retrofit, pero en una aplicación real es recomendable integrarlo con Hilt y un patrón de repositorio, tal como hicimos con Room en la guía anterior. De esta forma, los ViewModels no dependen directamente de Retrofit, sino de una abstracción del repositorio, lo que mejora la separación de responsabilidades y facilita las pruebas unitarias.

1. Agregar dependencias de Hilt
   Si aún no lo ha hecho, agregue las dependencias de Hilt y KSP siguiendo los pasos de la guía de Arquitectura MVVM y Repositorios. Recuerde que la clase Application debe estar anotada con @HiltAndroidApp y la actividad principal con @AndroidEntryPoint.

2. Configurar Hilt para proporcionar Retrofit
   Creamos un módulo de Hilt que proporcione la instancia de Retrofit y del ApiService. De esta manera, no se debe construir Retrofit manualmente en la aplicación, sino que Hilt se encarga de crear y proporcionar las instancias necesarias.

Dentro del paquete di, cree un archivo llamado NetworkModule.kt con el siguiente contenido:

package com.example.demoapp.di

import com.example.demoapp.data.remote.ApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        // Proporciona la instancia de Retrofit con la URL base y el convertidor
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        // Se construye el ApiService a partir de la instancia de Retrofit
        return retrofit.create(ApiService::class.java)
    }
}
A diferencia del módulo de repositorios que usa @Binds, aquí utilizamos @Provides porque Retrofit se construye con un builder, de manera similar a Room.databaseBuilder en la guía anterior.

⚠️ Importante: Si necesita configurar OkHttpClient (por ejemplo, para agregar interceptores, logging o tiempos de espera), puede añadir un método provideOkHttpClient adicional en este mismo módulo y pasarlo al Retrofit.Builder mediante .client(...).

3. Crear el Repositorio remoto
   Definimos una interfaz del repositorio en la capa de dominio y su implementación en la capa de datos, igual que hicimos en la guía de MVVM y Repositorios.

Cree la interfaz en domain/repository/UserRepository.kt si aún no la tiene:

package com.example.demoapp.domain.repository

import com.example.demoapp.domain.model.User

interface UserRepository {
suspend fun getUsers(): List<User>
}
Y la implementación en data/repository/UserRepositoryImpl.kt:

package com.example.demoapp.data.repository

import com.example.demoapp.data.remote.ApiService
import com.example.demoapp.domain.model.User
import com.example.demoapp.domain.repository.UserRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
private val apiService: ApiService // Hilt inyecta el ApiService creado en NetworkModule
) : UserRepository {

    override suspend fun getUsers(): List<User> {
        return apiService.getUsers()
    }
}
Recuerde registrar la vinculación entre la interfaz y la implementación en el RepositoryModule con @Binds, tal como vimos en la guía de MVVM y Repositorios:

@Binds
@Singleton
abstract fun bindUserRepository(
userRepositoryImpl: UserRepositoryImpl
): UserRepository
4. Usar el Repositorio en el ViewModel
   Finalmente, inyectamos el repositorio en el ViewModel y lanzamos la solicitud dentro de viewModelScope, en lugar de utilizar GlobalScope como en el ejemplo básico. Esto asegura que la corrutina respete el ciclo de vida del ViewModel y se cancele automáticamente cuando este se destruya.

package com.example.demoapp.features.user.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.demoapp.domain.model.User
import com.example.demoapp.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
private val repository: UserRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users: StateFlow<List<User>> = _users.asStateFlow()

    init {
        loadUsers()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            // Se realiza la solicitud a la API a través del repositorio
            _users.value = repository.getUsers()
        }
    }
}
En la pantalla, el ViewModel se inyecta con hiltViewModel() como ya vimos en guías anteriores, y se observa users con collectAsState() para reflejar los datos en la UI de manera reactiva.

⚠️ Importante: En un caso real, debe manejar los errores de red (por ejemplo, con try/catch o exponiendo un RequestResult en el ViewModel) ya que las peticiones HTTP pueden fallar por problemas de conectividad, errores del servidor o respuestas inválidas.

Conclusión
Retrofit es una herramienta poderosa y fácil de usar para consumir APIs RESTful en aplicaciones Android. Su integración con bibliotecas de serialización como Gson facilita el manejo de datos, y su enfoque basado en anotaciones hace que el código sea limpio y mantenible. Al utilizar Retrofit, los desarrolladores pueden centrarse en la lógica de la aplicación sin preocuparse por los detalles complejos de las solicitudes HTTP. Además, al combinar Retrofit con Hilt y el patrón de repositorio, se obtiene una arquitectura escalable, mantenible y fácil de probar.
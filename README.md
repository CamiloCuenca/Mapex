# Mapex

Aplicación Android desarrollada con **Jetpack Compose** para explorar países del mundo usando la API pública de **REST Countries**. La app muestra una pantalla de inicio, un listado de países con búsqueda y filtros, y una pantalla de detalle con información ampliada de cada país.

## Funcionalidades

- Pantalla de inicio.
- Listado de países.
- Búsqueda por nombre.
- Filtro por continente / región.
- Pantalla de detalle por país.
- Carga de banderas desde URL.
- Datos como capital, población, área, monedas, idiomas, zonas horarias, fronteras y más.

## API utilizada

La app consume la API pública de **REST Countries**.

**Base URL**

```text
https://restcountries.com/
```

### Endpoints usados

| Endpoint | Uso en la app |
|---|---|
| `GET /v3.1/all?fields=cca2,cca3,name,region,subregion,capital,flags,population,continents` | Cargar el listado inicial de países con los datos necesarios para la pantalla principal. |
| `GET /v3.1/name/{name}` | Buscar países por nombre desde el campo de búsqueda. |
| `GET /v3.1/region/{region}` | Filtrar países por región / continente. |
| `GET /v3.1/alpha/{code}` | Obtener el detalle completo de un país a partir de su código. |

## Arquitectura del proyecto

El proyecto está organizado en una estructura simple por capas:

- **`core`**: navegación principal y rutas.
- **`data`**: acceso a datos remotos, DTOs y repositorio.
- **`domain`**: modelos de negocio e interfaces de repositorio.
- **`features`**: pantallas, estados y ViewModels por funcionalidad.
- **`ui`**: tema, componentes reutilizables y pantallas base.

### Flujo general

1. La UI navega entre pantallas con `Navigation Compose`.
2. Los `ViewModel` llaman al repositorio.
3. El repositorio consume la API REST Countries con `Retrofit`.
4. Los DTOs se transforman a modelos de dominio (`Country`).
5. La UI muestra los datos con Jetpack Compose y Material 3.

## Estructura del proyecto

```text
Mapex2/
├── app/
│   └── src/main/java/com/mapex/
│       ├── MainActivity.kt
│       ├── core/
│       │   ├── AppNavigation.kt
│       │   └── MainRoutes.kt
│       ├── data/
│       │   ├── remote/
│       │   │   ├── ApiInterface.kt
│       │   │   ├── ApiService.kt
│       │   │   └── dto/
│       │   └── repository/
│       ├── domain/
│       │   ├── model/
│       │   └── repository/
│       ├── features/
│       │   ├── countrylist/
│       │   └── countrydetail/
│       └── ui/
│           ├── components/
│           └── theme/
└── gradle/
    └── libs.versions.toml
```

## Pantallas principales

- **Inicio**: pantalla de bienvenida con acceso al listado de países.
- **Países**: listado con búsqueda, chips de continente y tarjetas por país.
- **Detalle del país**: muestra información general y ampliada de un país específico.

## Librerías utilizadas

### Principales en uso

- **Jetpack Compose**: UI declarativa.
- **Material 3**: componentes visuales y tema.
- **Navigation Compose**: navegación entre pantallas.
- **Lifecycle ViewModel / Runtime Compose**: estados observables y ciclo de vida.
- **Retrofit**: consumo de API REST.
- **Gson Converter**: conversión de JSON a objetos Kotlin.
- **Coil / Coil Compose**: carga de imágenes desde URL, especialmente banderas.
- **AndroidX Core KTX**: utilidades base de Android.
- **Activity Compose**: integración de Compose con la actividad principal.
- **Kotlinx Serialization (plugin/dependencia declarada)**: configurada en Gradle.

### Dependencias presentes pero no activas actualmente

- **Hilt**: está preparado en Gradle, pero actualmente se encuentra comentado y no está en uso.
- **Lottie**: declarada en Gradle, pero no se detecta uso en el código actual.
- **Media3**: declarada en Gradle, pero no se detecta uso en el código actual.

## Configuración del proyecto

### Requisitos

- Android Studio reciente.
- JDK 11.
- Android SDK con **compileSdk 36**.
- Dispositivo o emulador con **minSdk 28** o superior.
- Conexión a internet para consumir la API.

### Permiso necesario

La app necesita acceso a internet:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

## Archivos clave

- `app/src/main/java/com/mapex/MainActivity.kt`: punto de entrada de la app.
- `app/src/main/java/com/mapex/core/AppNavigation.kt`: navegación principal.
- `app/src/main/java/com/mapex/data/remote/ApiInterface.kt`: definición de endpoints.
- `app/src/main/java/com/mapex/data/remote/ApiService.kt`: cliente Retrofit.
- `app/src/main/java/com/mapex/data/repository/CountryRepositoryImpl.kt`: acceso a datos y mapeo de DTO a dominio.
- `app/src/main/java/com/mapex/features/countrylist/CountryListScreen.kt`: pantalla de listado.
- `app/src/main/java/com/mapex/features/countrydetail/CountryDetailScreen.kt`: pantalla de detalle.
- `app/src/main/java/com/mapex/ui/theme/`: tema, colores y componentes visuales.

## Cómo ejecutar el proyecto

### Opción 1: Android Studio

1. Abre el proyecto en Android Studio.
2. Espera a que Gradle sincronice.
3. Ejecuta la app en un emulador o dispositivo físico.

### Opción 2: Terminal

Desde la raíz del proyecto:

```powershell
./gradlew assembleDebug
```


## Autor
- Juan Camilo Cuenca Sepulveda
- Federico Alvarez Muños
- Brandon Montealegre
- Diego Alexander Jimenez

Proyecto Android creado para exploración de países con datos de REST Countries.


# Level UP - Aplicación de Comercio Electrónico

## Descripción

Level UP es una aplicación móvil de comercio electrónico desarrollada con Jetpack Compose para Android. La aplicación permite a los usuarios explorar productos, agregar artículos al carrito de compras y gestionar sus pedidos de manera intuitiva.

## Características

- Navegación fluida entre pantallas usando Navigation Compose
- Catálogo de productos con búsqueda y filtrado
- Carrito de compras con gestión de cantidades
- Interfaz moderna y responsive con Material Design 3
- Arquitectura MVVM con inyección de dependencias
- Gestión de estado con StateFlow

## Tecnologías Utilizadas

### Core
- Kotlin 2.0.21
- Jetpack Compose
- Material Design 3
- Android SDK 34 (mínimo SDK 24)

### Arquitectura y Patrones
- MVVM (Model-View-ViewModel)
- Clean Architecture
- Repository Pattern
- Dependency Injection con Hilt

### Bibliotecas Principales
- **Compose BOM 2024.02.00**: UI moderna y declarativa
- **Hilt 2.51.1**: Inyección de dependencias
- **Navigation Compose 2.7.6**: Navegación entre pantallas
- **Lifecycle & ViewModel 2.7.0**: Gestión de estado y ciclo de vida
- **Coroutines 1.7.3**: Programación asíncrona
- **Coil 2.5.0**: Carga de imágenes
- **Gson 2.10.1**: Serialización JSON

## Estructura del Proyecto

```
app/
├── src/
│   └── main/
│       ├── java/com/levelup/
│       │   ├── data/
│       │   │   ├── model/          # Modelos de datos
│       │   │   └── repository/     # Repositorios
│       │   ├── ui/
│       │   │   ├── navigation/     # Configuración de navegación
│       │   │   ├── screens/        # Pantallas de la app
│       │   │   └── theme/          # Tema y colores
│       │   ├── utils/              # Utilidades y helpers
│       │   ├── viewmodel/          # ViewModels
│       │   └── MainActivity.kt     # Actividad principal
│       └── res/                    # Recursos (layouts, strings, etc.)
└── build.gradle.kts                # Configuración de Gradle
```

## Pantallas Principales

### HomeScreen
Pantalla principal que muestra el catálogo de productos con:
- Grid de productos
- Búsqueda por nombre
- Filtrado por categoría
- Navegación a detalle del producto

### ProductDetailScreen
Detalles completos del producto incluyendo:
- Información del producto
- Precio y disponibilidad
- Botón para agregar al carrito
- Gestión de cantidad

### CartScreen
Carrito de compras con:
- Lista de productos agregados
- Controles de cantidad
- Cálculo de subtotales
- Total a pagar
- Botón de checkout

### ProductScreen
Listado completo de productos con opciones de filtrado.

### ProductReview
Sistema de reseñas y calificaciones de productos.

## Requisitos del Sistema

- Android Studio Hedgehog o superior
- JDK 17
- Android SDK 34
- Gradle 8.9
- Dispositivo o emulador con Android 7.0 (API 24) o superior

## Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/tu-usuario/level-up-app.git
cd level-up-app
```

### 2. Abrir en Android Studio

- Abre Android Studio
- Selecciona "Open an existing project"
- Navega a la carpeta del proyecto
- Espera a que Gradle sincronice las dependencias

### 3. Configurar el archivo local.properties

Crea o edita el archivo `local.properties` en la raíz del proyecto:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

### 4. Compilar el proyecto

```bash
# Windows
.\gradlew clean build

# Linux/Mac
./gradlew clean build
```

## Ejecución de la Aplicación

### Desde Android Studio

1. Conecta un dispositivo Android o inicia un emulador
2. Haz clic en el botón "Run" o presiona Shift + F10
3. Selecciona el dispositivo de destino

### Desde la línea de comandos

```bash
# Instalar en dispositivo conectado
.\gradlew installDebug

# Ejecutar la aplicación
adb shell am start -n com.levelup/.MainActivity
```

### Usando ADB inalámbrico

```bash
# Conectar dispositivo
adb connect DIRECCIÓN_IP:5555

# Instalar y ejecutar
.\gradlew installDebug
```

## Configuración de Gradle

El proyecto utiliza las siguientes configuraciones en `gradle.properties`:

```properties
android.useAndroidX=true
android.enableJetifier=true
kotlin.code.style=official
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
org.gradle.configureondemand=true
android.nonTransitiveRClass=true
```

## Arquitectura

### MVVM Pattern

La aplicación sigue el patrón MVVM (Model-View-ViewModel):

- **Model**: Clases de datos y repositorios
- **View**: Composables de UI
- **ViewModel**: Lógica de negocio y gestión de estado

### Inyección de Dependencias

Se utiliza Hilt para la inyección de dependencias:

```kotlin
@HiltAndroidApp
class LevelUpApplication : Application()

@AndroidEntryPoint
class MainActivity : ComponentActivity()
```

### Navegación

Navigation Compose gestiona la navegación entre pantallas:

```kotlin
NavHost(navController = navController, startDestination = "home") {
    composable("home") { HomeScreen(navController) }
    composable("cart") { CartScreen(navController) }
    // ... más rutas
}
```

## Testing

### Ejecutar tests unitarios

```bash
.\gradlew test
```

### Ejecutar tests instrumentados

```bash
.\gradlew connectedAndroidTest
```

## Compilación para Producción

### Generar APK de release

```bash
.\gradlew assembleRelease
```

El APK se generará en: `app/build/outputs/apk/release/app-release.apk`

### Generar Bundle para Play Store

```bash
.\gradlew bundleRelease
```

El bundle se generará en: `app/build/outputs/bundle/release/app-release.aab`

## Problemas Conocidos y Soluciones

### Error de compilación con Compose

Si encuentras errores relacionados con Compose Animation, asegúrate de usar Compose BOM 2024.02.00 o superior.

### Problemas con ADB

Si el dispositivo no se detecta:

```bash
adb kill-server
adb start-server
adb devices
```

## Contribuir

Las contribuciones son bienvenidas. Por favor:

1. Haz fork del proyecto
2. Crea una rama para tu feature (`git checkout -b feature/nueva-caracteristica`)
3. Commit tus cambios (`git commit -m 'Agregar nueva característica'`)
4. Push a la rama (`git push origin feature/nueva-caracteristica`)
5. Abre un Pull Request

## Licencia

Este proyecto está bajo la Licencia Apache 2.0. Ver el archivo `LICENSE` para más detalles.

## Contacto

Para preguntas o sugerencias, por favor abre un issue en el repositorio.

## Changelog

### Versión 1.0.0
- Lanzamiento inicial
- Catálogo de productos
- Carrito de compras
- Sistema de navegación
- Integración con Hilt
- UI con Material Design 3
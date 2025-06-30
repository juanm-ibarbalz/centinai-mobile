# CentinAI App

<!--

CentinAI es una plataforma de observabilidad y análisis de mensajes en tiempo real para agentes conversacionales. Procesa, visualiza y exporta métricas clave de interacción (tiempos de respuesta, uso de tokens, categorías de mensajes) mediante una interfaz web, una API REST y un módulo de análisis por batch.-->

La aplicación Android está construida con Jetpack Compose y utiliza una arquitectura de navegación basada en `NavController`. La funcionalidad principal incluye una pantalla de inicio (`SplashScreen`), una pantalla para mostrar contenido web (`WebViewScreen`) y una pantalla de error (`ErrorScreen`). Utiliza un `WebLoadViewModel` para gestionar la lógica relacionada con la carga de contenido web.

## Características Principales

<!-- Lista las características clave de tu aplicación. -->
*   **Navegación Fluida**: Utiliza Jetpack Compose Navigation para gestionar las transiciones entre pantallas.
*   **Visualización de Contenido Web**: Integra un `WebView` para mostrar páginas web dentro de la aplicación.
*   **Manejo de Carga y Errores**:
    *   Muestra una pantalla de inicio (`SplashScreen`) mientras se realizan las cargas iniciales.
    *   Presenta una pantalla de error (`ErrorScreen`) con opción de reintentar en caso de fallo.
*   **Interfaz de Usuario Moderna**: Construida con Jetpack Compose para una UI declarativa y moderna.
*   **Gestión de Estado**: Utiliza `ViewModel` (`WebLoadViewModel`) para gestionar el estado y la lógica de la UI.
*   **Edge-to-Edge Display**: Habilitado para una experiencia visual inmersiva.

## Tecnologías Utilizadas

*   **Lenguaje**: Kotlin
*   **UI Toolkit**: Jetpack Compose
    *   `androidx.compose.ui`
    *   `androidx.compose.material3`
    *   `androidx.compose.foundation`
    *   `androidx.navigation:navigation-compose`
*   **Gestión de Ciclo de Vida y Estado**:
    *   `androidx.lifecycle:lifecycle-runtime-compose`
    *   `androidx.lifecycle:lifecycle-viewmodel-compose`
*   **Componente WebView**: Integrado a través de `AndroidView`.

## Cómo Empezar

### Prerrequisitos

*   Android Studio [Iguana | 2023.2.1 o superior]
*   JDK [17 o superior]
*   Configurar un emulador de Android o un dispositivo físico con Android [ API 26 (Oreo) o superior].

### Instalación y Ejecución

1.  **Clona el repositorio:**
2.  **Abre el proyecto en Android Studio.**
3.  **Sincroniza el proyecto con los archivos Gradle.** Android Studio debería hacerlo automáticamente. Si no, haz clic en "Sync Project with Gradle Files" (el icono del elefante con una flecha).
4.  <!-- **Configuración Adicional:**
    *  Valida que en el archivo Constants consuma la siguiente URL:
    APP_WEB_URL = "https://centinai-frontend.up.railway.app/login" -->
5.  **Ejecuta la aplicación**:
    *   Selecciona un dispositivo o emulador.
    *   Haz clic en el botón "Run" (el triángulo verde) en Android Studio o usa el menú "Run" > "Run 'app'".

### Usuario con conversaciones cargadas para testear
    * User: juanmartin@example.com
    * Password: 12345678

## Arquitectura

La aplicación sigue principios de una arquitectura **MVVM (Model-View-ViewModel)**, aprovechando las ventajas de Jetpack Compose para la capa de Vista. Esta separación de responsabilidades facilita la mantenibilidad, testeabilidad y escalabilidad del código.

*   **Vista (View)**:
    *   Implementada con **Jetpack Compose**. Las funciones Composable como `SplashScreen`, `WebViewScreen`, y `ErrorScreen` son responsables de definir la interfaz de usuario y reaccionar a los cambios de estado.
    *   Observan los datos expuestos por el `ViewModel` (a través de `StateFlow`, `LiveData` o estados de Compose) y se actualizan de forma declarativa cuando estos datos cambian.
    *   Delegan todas las acciones del usuario y la lógica de presentación al `ViewModel`.

*   **ViewModel (`WebLoadViewModel`)**:
    *   Actúa como intermediario entre la Vista y la lógica de negocio/datos (Modelo).
    *   Contiene la lógica de presentación y el estado de la UI para las pantallas relacionadas con la carga de contenido web (por ejemplo, `isLoading`, `errorState`, `urlToLoad`).
    *   Expone datos a la Vista de forma que puedan ser observados y sobrevivir a cambios de configuración (gracias a `androidx.lifecycle.ViewModel`).
    *   No tiene conocimiento directo de las vistas de Android (framework), lo que facilita las pruebas unitarias.
    *   En este proyecto, `WebLoadViewModel` gestiona el estado de la carga del `WebView`, maneja los posibles errores y decide qué pantalla mostrar (carga, contenido web o error).

*   **Modelo (Model)**:
     *  Obtiene datos de una API de red  para cargar en el `WebView`  "https://centinai-frontend.up.railway.app/login" -->
     *   El `ViewModel` interactuaría con esta capa para obtener y manipular datos. En el contexto actual, el "modelo" podría ser tan simple como la lógica que determina la URL a cargar o el manejo de la lógica de reintento.

*   **Navegación**:
    *   Gestionada por **Jetpack Compose Navigation (`NavController`)**. Define los diferentes destinos (pantallas) de la aplicación y las transiciones entre ellos de una manera centralizada y type-safe.

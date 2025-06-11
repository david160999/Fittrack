# Proyecto de Rutinas de Entrenamiento

Este proyecto es una aplicación Android para la gestión de rutinas de entrenamiento semanal, permitiendo al usuario crear, modificar, copiar, eliminar y calendarizar rutinas y ejercicios de forma personalizada.

## Tecnologías y Arquitectura

- **Kotlin**: Lenguaje principal de desarrollo.
- **MVVM (Model-View-ViewModel)**: Patrón de arquitectura para separar la lógica de negocio de la interfaz de usuario.
- **Clean Architecture / Clean Code**: Principios de diseño para mantener el código modular, desacoplado y fácil de probar.
- **Room**: Librería de persistencia para la gestión local de la base de datos SQLite, utilizada para almacenar semanas, rutinas, ejercicios y fechas.
- **Firebase**: Integración para autenticación y almacenamiento remoto, así como para la funcionalidad de compartir entrenamientos mediante un código único entre usuarios.
- **Dagger Hilt**: Inyección de dependencias para facilitar la escalabilidad y pruebas.
- **Coroutines + StateFlow**: Programación reactiva y asincrónica para gestionar estados de la interfaz y operaciones de base de datos.
- **Navigation Component**: Para la navegación entre fragmentos de la app.
- **Material Components y XML layouts**: Diseño visual basado en Material Design mediante layouts en XML.
- **RecyclerView + Adapters**: Listas dinámicas y personalizables para semanas, rutinas y ejercicios.
- **BottomSheetDialogFragment & DialogFragment**: Uso de diálogos personalizados para interacción avanzada (selección de semana, calendario, confirmaciones, etc).

## Estructura General

- **data/**: Modelos de datos, entidades Room y DAOs.
- **domain/**: Casos de uso y lógica de negocio.
- **presentation/**: Fragments, Activities, Adapters y ViewModels.
- **di/**: Definiciones para la inyección de dependencias con Hilt.

## Principales Funcionalidades

- **Gestión de Semanas**: Crear, copiar, eliminar y seleccionar semanas de entrenamiento.
- **Gestión de Rutinas**: Añadir, editar, eliminar, copiar y reordenar rutinas dentro de cada semana (con soporte drag&drop).
- **Gestión de Ejercicios**: (Según implementación, permite añadir y visualizar ejercicios en cada rutina).
- **Asignación de Fechas**: Calendarización de rutinas mediante diálogo personalizado y almacenamiento de fechas.
- **Compartir Entrenamientos**: Los entrenamientos pueden ser compartidos con otras personas mediante un **código de invitación** único. Usando Firebase, otro usuario puede introducir el código y obtener una copia del entrenamiento compartido, facilitando el intercambio social y la colaboración.
- **Interfaz Reactiva**: Actualización en tiempo real de la interfaz mediante StateFlow y LiveData.
- **Feedback Visual**: Uso de animaciones, shimmer, snackbars y menús contextuales para una experiencia de usuario fluida.

## Ejemplo de Flujo de Usuario

1. El usuario selecciona un entrenamiento y accede a la vista de semanas.
2. Puede crear nuevas semanas, copiar una existente o eliminarla.
3. Dentro de una semana, puede añadir rutinas, cambiar su orden mediante arrastre, copiar rutinas o eliminarlas.
4. Puede asignar fechas a rutinas usando el calendario integrado.
5. Puede compartir un entrenamiento generando un código y enviándolo a otra persona, que podrá usarlo para importar el entrenamiento desde Firebase.
6. Toda la información se almacena localmente en Room, y puede sincronizarse y compartirse mediante Firebase si está habilitado.

## Dependencias principales (build.gradle)

```gradle
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1"
implementation "androidx.navigation:navigation-fragment-ktx:2.7.0"
implementation "androidx.recyclerview:recyclerview:1.3.1"
implementation "com.google.android.material:material:1.9.0"
implementation "androidx.room:room-runtime:2.5.2"
kapt "androidx.room:room-compiler:2.5.2"
implementation "com.google.dagger:hilt-android:2.48"
kapt "com.google.dagger:hilt-compiler:2.48"
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
implementation 'com.google.firebase:firebase-auth-ktx:22.1.0'
implementation 'com.google.firebase:firebase-firestore-ktx:24.8.1'
```

## Notas de Desarrollo

- **Requiere Android 6.0+ (API 23+)**
- El proyecto sigue buenas prácticas de desacoplamiento y testabilidad.
- Los comentarios en el código explican cada fragmento y función relevante.
- Personaliza los recursos en `res/values` para adaptar textos y estilos.

## Capturas de Pantalla

*(Agrega aquí imágenes representativas de la app si lo deseas)*

---

¿Dudas o sugerencias?  
¡Contribuciones y feedback son bienvenidos!

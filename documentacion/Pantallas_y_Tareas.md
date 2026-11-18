# Kadai · Reparto de trabajo: pantallas y tareas pendientes

> Documento para **repartir el trabajo en equipo antes de conectar Firebase**.
> Basado en `Daily_Challenges_Documentacion_Proyecto.md` (v1.2, secciones 6, 10, 13, 14 y 15) y en el estado real del código (`app/src/main/java/com/example/kadai`).
>
> Convención de estado: `- [ ]` pendiente · `- [x]` terminado. Añade el nombre del responsable entre paréntesis al aceptar una tarea, p. ej. `(Ana)`.

**Aclaración sobre "Activities":** en este proyecto **no se creará una `Activity` por pantalla**. Se mantiene una única `MainActivity` y cada pantalla será un *destino Composable* del `NavHost` (Navigation Compose). La única actividad Android pendiente es configurar `MainActivity` como host de navegación (tarea T-01). Donde el documento dice "pantalla / activity", léase "Composable destino".

---

## 1. Estado actual (inventario)

### Ya existe
- [x] `MainActivity.kt`: navegación provisional con `isLoggedIn` (Login → Home) y TODOs de Firebase.
- [x] `ui/screens/login/LoginScreen.kt`: diseño completo, callbacks sin implementar.
- [x] `ui/screens/home/HomeScreen.kt`: diseño completo con datos de ejemplo (`sampleGroups`, `sampleActivity`).
- [x] `ui/components/KadaiComponents.kt`: `KadaiTextField`, `PulsingDot`, `RadialAccentBackground`, `MetaChip`.
- [x] `ui/theme/`: `Color.kt`, `Type.kt`, `Shape.kt`, `Theme.kt` (tema oscuro KADAI).
- [x] Proyecto Gradle configurado con Kotlin + Jetpack Compose (Material 3 + icons extended).

### Falta para poder trabajar en paralelo
- [ ] Grafo de navegación (Navigation Compose) y rutas centralizadas.
- [ ] `MainActivity` como `NavHost` + barra inferior compartida (hoy la barra vive dentro de `HomeScreen`).
- [ ] ViewModels por pantalla y `UiState` (carga / error / éxito).
- [ ] Modelos de dominio y repositorios *fake* con datos mock (para no depender de Firebase).
- [ ] El resto de pantallas (ver sección 3).
- [ ] Componentes compartidos extraídos de Login/Home (ver sección 4).
- [ ] Dependencias Gradle nuevas (ver sección 5.1).

---

## 2. Convenciones para que los archivos no choquen

- [ ] Crear cada pantalla en `app/src/main/java/com/example/kadai/ui/screens/<modulo>/XxxScreen.kt`.
- [ ] Firma estándar: `@Composable fun XxxScreen(modifier: Modifier = Modifier, onBack: () -> Unit = {}, onNavigate: ... , ...)`, **sin lógica de Firebase**; solo callbacks y parámetros con valores por defecto (mock).
- [ ] Añadir siempre un `@Preview` con datos de ejemplo para poder revisar la UI sin backend.
- [ ] Usar `KadaiTheme` y los colores de `ui/theme/Color.kt` (`Background`, `Surface`, `SurfaceElevated`, `Border`, `PurplePrimary`, `PurpleSoft`, `Success`, `Warning`, `TextPrimary`, `TextSecondary`, `TextMuted`, `TextDisabled`, `TextGhost`).
- [ ] Los textos visibles irán en `res/values/strings.xml` (los actuales están hardcodeados; migrarlos es tarea T-20).
- [ ] Los componentes reutilizables van en `ui/components/`, **no dentro de una pantalla**.
- [ ] Nadie edite `MainActivity.kt`, `build.gradle.kts` ni `Routes.kt` salvo el responsable de integración (T-01/T-02).
- [ ] Antes de tocar una pantalla existente (Login/Home), avisar: son la base del tema visual.

### Plantilla de asignación rápida

| Módulo | Pantallas | Responsable | Estado |
|---|---|---|---|
| Navegación / infraestructura | T-01, T-02, T-03 |  |  |
| Autenticación | P-01 a P-04 |  |  |
| Reto y pruebas | P-05 a P-09 |  |  |
| Grupos y ranking | P-10 a P-16 |  |  |
| Perfil y social | P-17 a P-24 |  |  |
| Componentes compartidos | C-01 a C-12 |  |  |
| Backend / Firebase (fase posterior) | F-01 a F-08 |  |  |

---

## 3. Pantallas a crear (lista de tareas)

### 3.1. Autenticación

- [ ] **P-01 · Splash / pantalla de arranque** (`splash`) · responsable: ___
  - [ ] Archivo: `ui/screens/splash/SplashScreen.kt`
  - [ ] Logo KADAI animado, indicador de carga y decisión de ruta (login / home) vía callback.
  - [ ] Sin lógica real de sesión todavía: parámetro `isLoggedIn` mock.

- [ ] **P-02 · Inicio de sesión** (`login`) · *existe, hay que pulirla* · responsable: ___
  - [ ] Extraer `KadaiPrimaryButton`, `GoogleSignInButton`, `PasswordTextField` a `ui/components/`.
  - [ ] Icono de Google y ojo de contraseña reales (hoy son placeholders, ver C-11).
  - [ ] Validación de email/contraseña y estados de error (campos ya soportan `isError`).
  - [ ] Conectar los 4 callbacks con el `NavHost` (registro, recuperación, login, Google).

- [ ] **P-03 · Registro** (`register`) · responsable: ___
  - [ ] Archivo: `ui/screens/login/RegisterScreen.kt`
  - [ ] Campos: nombre de usuario, email, contraseña, confirmar contraseña.
  - [ ] Validación (longitud, coincidencia, email) y mensajes de error.
  - [ ] Enlace a "ya tengo cuenta" y callback `onRegistered()`.

- [ ] **P-04 · Recuperar contraseña** (`forgot_password`) · responsable: ___
  - [ ] Archivo: `ui/screens/login/ForgotPasswordScreen.kt`
  - [ ] Campo email, envío simulado, confirmación "revisa tu correo" y volver a login.

### 3.2. Inicio y reto diario

- [ ] **P-05 · Inicio / Home** (`home`) · *existe, hay que adaptarla* · responsable: ___
  - [ ] Extraer `ModernBottomNav` a `ui/components/KadaiBottomNav.kt` y usar un `Scaffold` compartido.
  - [ ] Quitar el estado de navegación interno (`currentNavRoute`) y usar el `NavController`.
  - [ ] Los datos (`groups`, `activity`, retos del día) pasarán por parámetros/`UiState`, no `sample*`.
  - [ ] Conectar `onGroupClick`, `onChallengeClick`, `onAddGroupClick`, `onNotifClick`, `onProfileClick`.

- [ ] **P-06 · Detalle del reto** (`challenge_detail/{dailyChallengeId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/challenge/ChallengeDetailScreen.kt`
  - [ ] Título, descripción, categoría, hora oficial de publicación y cuenta atrás hasta el cierre.
  - [ ] Estado de participación (ya entregó / no entregó) y botón "Subir mi prueba".
  - [ ] Acceso a "Ver pruebas del grupo" y a "Proponer reto".

- [ ] **P-07 · Subir / sustituir prueba** (`submit_proof`) · responsable: ___
  - [ ] Archivo: `ui/screens/challenge/SubmitProofScreen.kt`
  - [ ] Selector foto/vídeo desde galería (y cámara), vista previa, progreso de subida.
  - [ ] Límites de tamaño/duración (decisión pendiente D-08) y mensajes de error.
  - [ ] Confirmación y sustitución mientras el periodo siga abierto; botón disabled tras el cierre.

- [ ] **P-08 · Pruebas del grupo** (`group_proofs/{groupId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/proof/GroupProofsScreen.kt`
  - [ ] Lista/grid de pruebas de los miembros, filtro por estado (pendiente/válida/rechazada).
  - [ ] Acciones: votar, reaccionar, abrir detalle; ocultar la propia prueba para votar (RF-16).
  - [ ] Estados: vacío (nadie ha entregado), cargando, error.

- [ ] **P-09 · Detalle de prueba** (`proof_detail/{proofId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/proof/ProofDetailScreen.kt`
  - [ ] Visualización de foto/vídeo a pantalla completa, autor, grupo y hora de entrega.
  - [ ] Voto positivo/negativo editable (RF-17), barra de reacciones y botón de favorito (RF-25).
  - [ ] Contador de votos y estado de validación; bloqueo de voto propio y tras el cierre.

### 3.3. Grupos y ranking

- [ ] **P-10 · Grupos (listado)** (`groups`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/GroupsScreen.kt`
  - [ ] Lista de grupos con nombre, foto/emoji, número de miembros y acceso.
  - [ ] Botón "Crear grupo" y campo/entrada para unirse por enlace o código (RF-05, RF-07).
  - [ ] Estado vacío: "aún no perteneces a ningún grupo".

- [ ] **P-11 · Crear grupo** (`create_group`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/CreateGroupScreen.kt`
  - [ ] Formulario: nombre, descripción y fotografía (RF-03), validación y creación simulada.

- [ ] **P-12 · Detalle del grupo** (`group_detail/{groupId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/GroupDetailScreen.kt`
  - [ ] Cabecera con nombre/descripción/foto, lista de miembros (activos e inactivos).
  - [ ] Acciones de administrador (RF-04, RF-06): editar grupo, gestionar invitaciones, expulsar, eliminar.
  - [ ] Acciones de usuario: abandonar grupo (RF-26), ver ranking, ver historial, ver pruebas del grupo.
  - [ ] Mostrar enlace/código de invitación del grupo.

- [ ] **P-13 · Invitar / unirse a grupo** (`invite`, `join_group`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/InviteScreen.kt` y `JoinGroupScreen.kt`
  - [ ] Compartir enlace o copiar código; introducción del código con validación y confirmación.

- [ ] **P-14 · Gestionar miembros** (`manage_members/{groupId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/ManageMembersScreen.kt`
  - [ ] Lista con rol (admin/miembro) y estado, botón expulsar con diálogo de confirmación.
  - [ ] Respeta la regla: el administrador **no** puede alterar votos ni puntuaciones (doc §4.2).

- [ ] **P-15 · Ranking del grupo** (`ranking/{groupId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/groups/RankingScreen.kt`
  - [ ] Clasificación acumulativa con posición, avatar, puntos y miembros inactivos (RF-20, doc §5.7).
  - [ ] Selector por grupo y explicación rápida de cómo se calculan los puntos.

- [ ] **P-16 · Historial de retos y resultados** (`challenge_history/{groupId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/challenge/ChallengeHistoryScreen.kt`
  - [ ] Lista paginada de retos pasados con fecha, título, participación y puntos obtenidos (RF-21).
  - [ ] Detalle de resultado: desglose `completar + posición + rapidez` (doc §5.5) y estado de validez.

### 3.4. Perfil, galerías y funcionalidades sociales

- [ ] **P-17 · Perfil** (`profile`) · responsable: ___
  - [ ] Archivo: `ui/screens/profile/ProfileScreen.kt`
  - [ ] Avatar, nombre de usuario, grupos, puntos por grupo, retos completados, porcentaje y racha (doc §5.10).
  - [ ] Accesos a editar perfil, mi galería, favoritos, notificaciones y configuración.

- [ ] **P-18 · Editar perfil** (`edit_profile`) · responsable: ___
  - [ ] Archivo: `ui/screens/profile/EditProfileScreen.kt`
  - [ ] Cambiar nombre de usuario y foto de perfil (RF-02), guardar/cancelar.

- [ ] **P-19 · Galería personal** (`my_gallery`) · responsable: ___
  - [ ] Archivo: `ui/screens/profile/GalleryScreen.kt`
  - [ ] Grid paginado de las propias pruebas, filtro por grupo/reto, openings al detalle de prueba.
  - [ ] Respeta privacidad: nada se hace público automáticamente (doc §5.10).

- [ ] **P-20 · Galería de otro usuario** (`user_gallery/{userId}`) · responsable: ___
  - [ ] Archivo: `ui/screens/profile/UserGalleryScreen.kt`
  - [ ] Solo pruebas permitidas por las reglas de los grupos compartidos (decisión pendiente D-09).

- [ ] **P-21 · Favoritos** (`favorites`) · responsable: ___
  - [ ] Archivo: `ui/screens/profile/FavoritesScreen.kt`
  - [ ] Pruebas guardadas (RF-25), quitar de favoritos y abrir detalle; estado vacío.

- [ ] **P-22 · Notificaciones** (`notifications`) · responsable: ___
  - [ ] Archivo: `ui/screens/notifications/NotificationsScreen.kt`
  - [ ] Lista por tipo (nueva prueba, superado en ranking, actividad del grupo), leídas/no leídas y "marcar todo como leído" (RF-23).

- [ ] **P-23 · Proponer reto** (`propose_challenge`) · responsable: ___
  - [ ] Archivo: `ui/screens/challenge/ProposeChallengeScreen.kt`
  - [ ] Formulario: título, descripción y categoría (RF-10); aviso de que pasa revisión (doc §5.9).

- [ ] **P-24 · Administrar catálogo de retos** (`admin_challenges`) · responsable: ___
  - [ ] Archivo: `ui/screens/admin/AdminChallengesScreen.kt`
  - [ ] Rol de administrador de la app (RF-11): lista de propuestas pendientes, aprobar/rechazar, retos aprobados.
  - [ ] Acceso restringido por permisos (preparar callback/flag mock hasta Firebase).

- [ ] **P-25 · Configuración / cuenta** (`settings`) · *recomendada, no explícita en el doc* · responsable: ___
  - [ ] Archivo: `ui/screens/profile/SettingsScreen.kt`
  - [ ] Cerrar sesión, notificaciones, privacidad y borrar cuenta (gestión básica de cuenta, RF-01).

---

## 4. Componentes compartidos a crear (sección transversal)

Extraer los que ya están dentro de pantallas y crear los que faltan. Todos en `ui/components/`.

- [ ] **C-01** `KadaiBottomNav.kt` — extraer `ModernBottomNav` desde `HomeScreen` (5 ítems + badge).
- [ ] **C-02** `KadaiButtons.kt` — extraer `KadaiPrimaryButton` y `GoogleSignInButton` desde `LoginScreen`.
- [ ] **C-03** `KadaiTextField.kt` — unificar `KadaiTextField` y el `PasswordTextField` local de Login (toggle ojo real).
- [ ] **C-04** `KadaiTopBar.kt` — barra superior con título, volver y acción (la necesitan casi todas las pantallas nuevas).
- [ ] **C-05** `KadaiCard.kt` / `KadaiSectionHeader.kt` — tarjeta y cabecera de sección genéricas.
- [ ] **C-06** `KadaiAsyncImage.kt` — carga de imágenes con placeholder/error (requiere Coil, ver T-04).
- [ ] **C-07** `KadaiStates.kt` — `LoadingState`, `ErrorState` (reintentar), `EmptyState` e `SuccessSnackbar` (requerido en §7.1).
- [ ] **C-08** `GroupCard.kt` / `MemberRow.kt` — reutilizables en Grupos, Detalle grupo y Ranking.
- [ ] **C-09** `VoteButtons.kt` / `ReactionBar.kt` — extraer `ModernVoteChip` y añadir selector de emojis.
- [ ] **C-10** `ChallengeCard.kt` — extraer `ModernDailyChallengeCard` de `HomeScreen` con parámetros (`alreadySubmitted`).
- [ ] **C-11** Iconos y assets reales: vector drawable del logo KADAI, icono de Google, iconos de "ojos"/visibilidad (hoy hay placeholders; doc §7.1).
- [ ] **C-12** `ProofPicker.kt` — selector de foto/vídeo y permisos de almacenamiento/cámara (compartido por P-07 y P-18).

---

## 5. Lo que falta en el proyecto (tareas técnicas)

### 5.1. Dependencias y configuración Gradle

- [ ] **T-01** `MainActivity` como host único: `setContent { KadaiApp() }` con `NavHost`, `Scaffold` y barra inferior.
- [ ] **T-02** `ui/navigation/Routes.kt` + `KadaiNavHost.kt`: constantes de rutas, argumentos (`groupId`, `proofId`, `dailyChallengeId`) y grafo completo P-01…P-25.
- [ ] **T-03** Añadir `androidx.navigation:navigation-compose` al version catalog (`gradle/libs.versions.toml`) y a `app/build.gradle.kts`.
- [ ] **T-04** Añadir cargador de imágenes (Coil) y `lifecycle-viewmodel-compose` / `lifecycle-runtime-compose`.
- [ ] **T-05** (Fase Firebase, más adelante) Plugin de Google Services, `google-services.json`, Firebase Auth, Firestore, Storage, Cloud Messaging y coroutines.

### 5.2. Arquitectura (MVVM + Clean Architecture, doc §8.3)

- [ ] **T-06** Estructura de paquetes: `ui/` (Presentation), `domain/model`, `domain/usecase`, `domain/repository`, `data/repository`, `data/remote`.
- [ ] **T-07** Modelos de dominio: `User`, `Group`, `Membership`, `Challenge`, `DailyChallenge`, `Proof`, `GroupValidation`, `Vote`, `Score`, `Reaction`, `Favorite`, `Notification` (doc §9).
- [ ] **T-08** Interfaces de repositorio en `domain/repository` (sin mencionar Firebase).
- [ ] **T-09** Repositorios `Fake*` con datos de ejemplo para que todas las pantallas funcionen sin backend.
- [ ] **T-10** `UiState` + ViewModels por pantalla (al menos para Home, Retos, Grupos, Perfil) con `StateFlow<UiState>`.

### 5.3. Recursos, manifiesto y UX

- [ ] **T-11** Permisos en `AndroidManifest.xml`: INTERNET, `READ_MEDIA_IMAGES`, `READ_MEDIA_VIDEO` (y `READ_EXTERNAL_STORAGE` para API baja), cámara y `POST_NOTIFICATIONS`.
- [ ] **T-12** Migrar textos hardcodeados a `res/values/strings.xml` (tema, Home, Login, botones).
- [ ] **T-13** Comprobar `statusBarsPadding()` / `navigationBarsPadding()` e `imePadding()` en todas las pantallas nuevas.
- [ ] **T-14** Accesibilidad: `contentDescription` en iconos/imágenes, tamaños táctiles ≥ 48 dp, contraste del tema oscuro.
- [ ] **T-15** Diseñar y documentar el flujo de navegación completo (mapa de rutas + acciones) para que el equipo sepa qué callback lleva a cada pantalla.

### 5.4. Pruebas (Fase 8)

- [ ] **T-16** Tests unitarios de reglas de negocio: validación 50 %, `P_posicion`, `P_tiempo`, `P_total`, idempotencia del cierre (doc §5.5, §11.2).
- [ ] **T-17** Tests de composables (navegación, estados, voto propio bloqueado).
- [ ] **T-18** Tests de integración con Firebase y de reglas de seguridad (fase posterior).
- [ ] **T-19** Pruebas en distintos tamaños de pantalla y preparación de la demo del MVP (doc §14).

### 5.5. Decisiones pendientes que bloquean implementación (doc §15)

- [ ] **D-01** Confirmar el cómputo de miembros para `P_posicion` (activos que podían participar).
- [ ] **D-02** Confirmar que la posición usa la hora de la **última** versión entregada antes del cierre.
- [ ] **D-03** Confirmar umbral del 50 % sobre votos emitidos y validez automática sin votos.
- [ ] **D-04** Confirmar cálculo de posiciones definitivas al cierre sobre pruebas válidas.
- [ ] **D-05** Definir el cierre sin participantes (sin asignar puntos).
- [ ] **D-06** Fijar zona horaria (propuesta `Europe/Madrid`).
- [ ] **D-07** Definir quién administra el catálogo global y cómo se reporta contenido.
- [ ] **D-08** Fijar límites concretos de duración/tamaño de vídeos e imágenes (bloquea P-07/C-12).
- [ ] **D-09** Decir si las galerías son públicas, solo amigos o solo miembros del grupo (bloquea P-19/P-20).
- [ ] **D-10** Contrastar funcionalidades y tecnologías con el enunciado y criterios de evaluación de la asignatura.

### 5.6. Backend / Firebase (no se toca hasta terminar las pantallas)

- [ ] **F-01** Crear proyecto Firebase y conectar la app (plugin + `google-services.json`).
- [ ] **F-02** Diseñar el modelo definitivo de Firestore (colecciones, documentos e índices) a partir del modelo conceptual (doc §9).
- [ ] **F-03** Reglas de seguridad: membresía para leer pruebas, voto único por usuario/prueba/grupo, permisos admin, puntos solo desde backend.
- [ ] **F-04** Storage: reglas por grupo y usuario, límites de tamaño y rutas `storagePath`.
- [ ] **F-05** Cloud Function de publicación diaria (hora aleatoria 08:00–17:00, zona horaria, reto aprobado).
- [ ] **F-06** Cloud Function de cierre: bloquear entregas/votos, validar, ordenar, calcular puntos y actualizar ranking, **idempotente**.
- [ ] **F-07** Firebase Cloud Messaging: notificaciones de nueva prueba, superación en ranking y actividad; permisos y canales.
- [ ] **F-08** Sustituir los repositorios `Fake*` por implementaciones reales (Auth, Firestore, Storage) y conectar los ViewModels.

---

## 6. Sugerencia de reparto (equipo de 4)

| Persona | Responsabilidad principal | Tareas |
|---|---|---|
| **A · Infraestructura** | Navegación, tema, componentes base, Gradle | T-01…T-05, T-11…T-15, C-01…C-07 |
| **B · Reto y pruebas** | Flujo diario y multimedia | P-05…P-09, P-16, P-23, C-10, C-12 |
| **C · Grupos** | Gestión de grupos, miembros y ranking | P-10…P-15, C-08 |
| **D · Perfil y social** | Perfil, galerías, favoritos, avisos, admin | P-17…P-22, P-24, P-25, C-09 |
| **Equipo** | Dominio y mocks compartidos | T-06…T-10 (repartir modelos por módulo) |
| **Líder / backend** | Decisiones y Firebase | D-01…D-10, F-01…F-08, T-16…T-19 |

**Orden recomendado:** primero T-01…T-04 (desbloquea a todos) → componentes C-01…C-07 → pantallas P-01…P-25 en paralelo → revisión integrada → D-01…D-10 → F-01…F-08.

---

## 7. Definición de "terminado" para cada pantalla

Antes de marcar `- [x]` en una tarea P-XX, verificar:

- [ ] El Composable compila y tiene un `@Preview` con datos de ejemplo.
- [ ] No referencia Firebase directamente; expone callbacks/`UiState`.
- [ ] Muestra estados de **carga, error y vacío** (doc §7.1).
- [ ] Usa `KadaiTheme`, colores y componentes compartidos; sin estilos duplicados.
- [ ] Respeta insets del sistema y tiene `contentDescription` en iconos/imágenes.
- [ ] Los textos están en `strings.xml` (o anotados para migrar).
- [ ] La ruta está registrada en `KadaiNavHost.kt` y se puede navegar a ella desde la pantalla correspondiente.

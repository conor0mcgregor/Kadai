# Daily Challenges
## Documento de definición del proyecto
**Asignatura:** Programación de Aplicaciones Móviles Nativas (PAMN)  
**Titulación:** Ingeniería Informática  
**Plataforma objetivo:** Android  
**Versión del documento:** 1.0  
**Estado:** Especificación inicial del proyecto  
**Nombre:** Daily Challenges (provisional)

---

## 1. Resumen del proyecto

Daily Challenges es una aplicación móvil nativa para Android orientada a fomentar la interacción y la competición amistosa entre grupos de amigos mediante retos diarios.

Los usuarios pueden crear o pertenecer a varios grupos privados. Cada día, la aplicación publica un reto común para todos los grupos. Los miembros intentan completarlo y suben una fotografía o un vídeo como prueba. Los demás integrantes de cada grupo valoran individualmente si la prueba es válida.

Cuando termina el periodo del reto, se determina qué pruebas son válidas y se calculan los puntos. La puntuación combina una recompensa por completar el reto, una bonificación por la posición de entrega y otra por la rapidez. Cada grupo mantiene su propio ranking acumulativo.

La aplicación también incluye perfiles, galerías de pruebas, favoritos, reacciones e notificaciones.

## 2. Objetivos

### 2.1. Objetivo principal

Desarrollar una aplicación Android nativa que permita organizar competiciones informales entre amigos a través de retos diarios, pruebas multimedia, validación comunitaria y rankings.

### 2.2. Objetivos específicos

- Permitir el registro e inicio de sesión de usuarios.
- Permitir crear y administrar grupos privados.
- Publicar un reto diario común a todos los grupos.
- Permitir entregar una fotografía o vídeo como prueba.
- Permitir que los miembros de un grupo validen las pruebas de los demás.
- Calcular automáticamente la puntuación de cada participante.
- Mantener un ranking histórico por grupo.
- Ofrecer perfiles y galerías de retos.
- Permitir guardar pruebas de otros usuarios como favoritas.
- Informar de novedades mediante notificaciones.

## 3. Alcance funcional

### 3.1. Funcionalidades incluidas

1. Registro, inicio de sesión y gestión básica de cuenta.
2. Perfil de usuario.
3. Creación y gestión de grupos privados.
4. Invitación de usuarios mediante enlace o código.
5. Catálogo de retos y publicación diaria automática.
6. Propuesta de nuevos retos por parte de los usuarios.
7. Entrega y sustitución de pruebas multimedia.
8. Votación y validación comunitaria.
9. Cálculo de puntos y ranking acumulativo.
10. Historial de retos.
11. Galería personal y favoritos.
12. Reacciones y notificaciones.

### 3.2. Fuera del alcance inicial

Estas funcionalidades podrán estudiarse para futuras versiones, pero no son necesarias para la primera versión funcional:

- Aplicación para iOS.
- Mensajería privada entre usuarios.
- Videollamadas o retransmisiones en directo.
- Competiciones públicas entre todos los usuarios.
- Monetización, suscripciones o publicidad.
- Sistema avanzado de moderación automatizada mediante inteligencia artificial.

## 4. Actores y roles

### 4.1. Usuario

Un usuario registrado puede:

- Crear y pertenecer a varios grupos.
- Consultar el reto diario.
- Subir y sustituir su prueba mientras esté abierto el periodo.
- Votar las pruebas de otros miembros de sus grupos.
- Consultar rankings e historiales.
- Reaccionar a las pruebas.
- Consultar perfiles y galerías.
- Guardar pruebas de otros usuarios en favoritos.
- Proponer nuevos retos.
- Abandonar un grupo.

### 4.2. Administrador de grupo

Además de las funciones de usuario, puede:

- Modificar el nombre, descripción y fotografía del grupo.
- Gestionar invitaciones.
- Expulsar integrantes.
- Eliminar el grupo.

No se ha definido que el administrador pueda alterar votos, puntuaciones o resultados. Esas operaciones deben estar controladas por las reglas del sistema.

### 4.3. Administrador de la aplicación

Es el responsable de gestionar el catálogo global de retos y aprobar o rechazar las propuestas enviadas por los usuarios. Este rol se contempla para que el catálogo no pueda modificarse libremente sin supervisión.

## 5. Reglas de negocio

### 5.1. Publicación del reto diario

- Existe un único reto diario global, común a todos los grupos.
- El reto se selecciona aleatoriamente de un catálogo de retos aprobados.
- La publicación se produce una vez al día, en un momento aleatorio comprendido entre las 08:00 y las 17:00.
- El nuevo reto sustituye al anterior y abre un nuevo periodo de participación.
- El periodo de un reto termina cuando se publica el reto del día siguiente.
- La hora oficial de publicación debe proceder del servidor, no del reloj del dispositivo.
- Todos los usuarios deben consultar el mismo reto y la misma hora oficial de publicación.

**Ejemplo:** si el reto se publica a las 11:35 del lunes, los usuarios pueden participar hasta que se publique el reto del martes.

### 5.2. Aplicación del reto a varios grupos

Un usuario puede pertenecer a varios grupos. La prueba que suba para un reto global será la misma en todos sus grupos.

Sin embargo:

- La prueba se valida de forma independiente en cada grupo.
- Los votos de un grupo no afectan a la validación en otro.
- La puntuación se calcula independientemente para cada grupo.
- El usuario puede obtener puntos en un grupo y no obtenerlos en otro si los resultados de las votaciones son diferentes.

Esto evita que el usuario tenga que subir varias veces el mismo contenido y mantiene la competición propia de cada grupo.

### 5.3. Entrega de pruebas

- Cada usuario puede entregar una prueba por reto global.
- La prueba puede ser una fotografía o un vídeo.
- El usuario puede sustituir su prueba mientras el periodo de entrega siga abierto.
- Cada sustitución actualiza la prueba vigente y su hora de entrega.
- La hora de la última entrega válida es la que se utiliza para ordenar las posiciones.
- Una vez publicado el siguiente reto, no se pueden subir ni modificar pruebas del reto anterior.
- La fecha y hora se registran en el servidor.
- La aplicación debe limitar el tamaño y duración de los archivos para controlar el almacenamiento y el consumo de datos.

**Consideración:** si un usuario pertenece a varios grupos, la entrega es única, pero se procesa en cada grupo por separado.

### 5.4. Votación de pruebas

- Los miembros de cada grupo pueden votar las pruebas de los demás integrantes.
- El autor de una prueba no puede votar su propia prueba.
- Cada miembro dispone de un voto por prueba y grupo.
- El voto puede modificarse mientras la votación esté abierta.
- Los votos son independientes por grupo.
- La votación puede comenzar desde que se entrega la prueba.
- El resultado definitivo se calcula al finalizar el periodo del reto, cuando se publica el siguiente.
- La validación requiere que al menos el 50 % de los votos emitidos sean positivos.
- Los votos no emitidos no cuentan en el denominador.

**Fórmula de validación:**

Una prueba es válida si:

`votos_positivos / votos_emitidos >= 0,50`

Ejemplos:

| Votos positivos | Votos negativos | Resultado |
|---:|---:|---|
| 3 | 1 | Válida |
| 2 | 2 | Válida |
| 1 | 3 | No válida |
| 0 | 0 | Válida automáticamente al cierre |

Si no se alcanza una decisión porque no hay votos, la prueba se considera válida automáticamente al finalizar el periodo.

**Regla adicional:** si existen votos emitidos, se aplica la regla del 50 %. Por tanto, una prueba con votos emitidos y menos del 50 % de votos positivos no se considera válida.

### 5.5. Puntuación

La puntuación se compone de tres partes:

1. Puntos por completar correctamente el reto.
2. Bonificación por posición de entrega.
3. Bonificación por rapidez.

Solo las pruebas que resulten válidas generan puntos.

#### A. Puntos por completar

Todo usuario cuya prueba sea válida obtiene:

`P_completar = 100`

#### B. Bonificación por posición

La bonificación depende del número de miembros del grupo y de la posición de entrega.

Propuesta de fórmula:

`P_posicion = 10 * (N - R)`

Donde:

- `N` es el número de miembros activos del grupo que pueden participar en ese reto.
- `R` es la posición del usuario entre las pruebas válidas, ordenadas por hora de entrega.
- La primera posición corresponde a `R = 1`.
- La última posición obtiene 0 puntos adicionales.

Ejemplo para un grupo de 6 miembros:

| Posición | Cálculo | Puntos |
|---:|---:|---:|
| 1.ª | 10 × (6 - 1) | 50 |
| 2.ª | 10 × (6 - 2) | 40 |
| 3.ª | 10 × (6 - 3) | 30 |
| 4.ª | 10 × (6 - 4) | 20 |
| 5.ª | 10 × (6 - 5) | 10 |
| 6.ª | 10 × (6 - 6) | 0 |

La posición se determina usando la última entrega registrada antes del cierre. Las pruebas rechazadas no obtienen puntos. Para que el orden no dependa de una votación todavía no finalizada, el sistema calcula las posiciones definitivas al cerrar el reto, considerando únicamente las pruebas válidas.

Si dos entregas tienen exactamente la misma marca temporal, se utilizará el identificador de registro del servidor como criterio técnico estable de desempate. La hora del servidor tendrá precisión suficiente para que estos casos sean poco frecuentes.

#### C. Bonificación por rapidez

La bonificación por rapidez se calcula a partir de los minutos transcurridos desde la publicación del reto hasta la última entrega del usuario:

`P_tiempo = max(100 - minutos_transcurridos, 0)`

Ejemplos:

| Tiempo desde publicación | Puntos |
|---:|---:|
| 5 minutos | 95 |
| 20 minutos | 80 |
| 60 minutos | 40 |
| 100 minutos | 0 |
| 150 minutos | 0 |

Se recomienda calcular los minutos transcurridos como minutos completos, redondeando hacia abajo, para que la fórmula sea sencilla y consistente.

#### D. Puntuación total

`P_total = P_completar + P_posicion + P_tiempo`

Ejemplo: un usuario de un grupo de 6 personas entrega en segunda posición, 20 minutos después de la publicación, y su prueba es válida:

- Completar: 100 puntos.
- Posición: 40 puntos.
- Rapidez: 80 puntos.
- **Total: 220 puntos.**

La puntuación se calcula por grupo. Una misma prueba puede generar puntuaciones diferentes en distintos grupos debido a sus diferentes integrantes, votos y posiciones.

### 5.6. No participación y pruebas rechazadas

- No completar un reto no resta puntos.
- No entregar una prueba no resta puntos.
- Una prueba rechazada no genera puntos.
- No existen penalizaciones negativas en la primera versión.

### 5.7. Ranking

- Cada grupo tiene su propio ranking.
- El ranking es acumulativo y permanente.
- Los puntos obtenidos se suman al total histórico del usuario en ese grupo.
- No se reinicia semanal ni mensualmente.
- Se conserva el historial de resultados.
- Si un usuario abandona el grupo, conserva su posición histórica y aparece como inactivo.
- El abandono no elimina los puntos obtenidos anteriormente.

### 5.8. Grupos

Cada grupo contiene:

- Nombre.
- Descripción.
- Fotografía.
- Administrador.
- Lista de integrantes.
- Sistema de invitación mediante enlace o código.
- Ranking acumulativo.
- Historial de retos y resultados.

Los grupos son privados. Solo los usuarios que formen parte de ellos pueden acceder a su contenido interno.

### 5.9. Propuestas de retos

- Los usuarios pueden proponer nuevos retos.
- Una propuesta no pasa directamente al catálogo.
- Debe ser revisada y aprobada por un administrador de la aplicación.
- Solo los retos aprobados pueden seleccionarse para la publicación diaria.
- Deben rechazarse retos peligrosos, ilegales, discriminatorios, que impliquen acoso o que puedan perjudicar a terceros.

### 5.10. Perfil de usuario

El perfil incluye:

- Nombre de usuario.
- Fotografía de perfil.
- Grupos a los que pertenece.
- Puntos totales en cada grupo.
- Número de retos completados.
- Porcentaje de retos completados.
- Racha de días consecutivos participando.
- Galería de sus propias pruebas.
- Galería de pruebas de otros usuarios guardadas como favoritas.

La visibilidad de las galerías debe respetar la privacidad de los grupos y de las pruebas. Una prueba compartida en un grupo privado no debe hacerse pública automáticamente.

### 5.11. Funcionalidades sociales y notificaciones

Se contemplan:

- Reacciones y emojis en las pruebas.
- Notificación cuando un miembro sube una prueba.
- Notificación cuando un usuario es superado en el ranking.
- Notificaciones relacionadas con la actividad de los grupos.
- Historial de retos completados.

## 6. Requisitos funcionales

Los requisitos funcionales describen las operaciones que debe ofrecer la aplicación.

| ID | Requisito |
|---|---|
| RF-01 | El sistema permitirá registrar usuarios e iniciar sesión. |
| RF-02 | El usuario podrá consultar y modificar su perfil. |
| RF-03 | El usuario podrá crear grupos privados. |
| RF-04 | El administrador podrá modificar los datos del grupo. |
| RF-05 | El administrador podrá gestionar invitaciones. |
| RF-06 | El administrador podrá expulsar integrantes y eliminar el grupo. |
| RF-07 | El usuario podrá pertenecer a varios grupos. |
| RF-08 | El sistema publicará un reto global diario entre las 08:00 y las 17:00. |
| RF-09 | El sistema seleccionará aleatoriamente un reto aprobado. |
| RF-10 | El usuario podrá proponer nuevos retos. |
| RF-11 | El administrador de la aplicación podrá aprobar o rechazar propuestas. |
| RF-12 | El usuario podrá subir una fotografía o vídeo como prueba. |
| RF-13 | El usuario podrá sustituir su prueba antes del cierre. |
| RF-14 | El sistema registrará la hora de entrega en el servidor. |
| RF-15 | Los integrantes podrán votar las pruebas de su grupo. |
| RF-16 | El sistema impedirá votar la propia prueba. |
| RF-17 | El usuario podrá modificar su voto mientras la votación esté abierta. |
| RF-18 | El sistema calculará la validez de las pruebas al cerrar el reto. |
| RF-19 | El sistema calculará automáticamente los puntos. |
| RF-20 | El usuario podrá consultar el ranking de cada grupo. |
| RF-21 | El usuario podrá consultar el historial de retos. |
| RF-22 | El usuario podrá reaccionar a las pruebas. |
| RF-23 | El usuario recibirá notificaciones de actividad relevante. |
| RF-24 | El usuario podrá consultar galerías personales. |
| RF-25 | El usuario podrá guardar pruebas de otros usuarios en favoritos. |
| RF-26 | El usuario podrá abandonar un grupo conservando su historial. |

## 7. Requisitos no funcionales

### 7.1. Plataforma y usabilidad

- La aplicación será nativa para Android.
- La interfaz se desarrollará con Kotlin y Jetpack Compose.
- El diseño será moderno, minimalista y predominantemente oscuro.
- La navegación deberá ser clara y consistente.
- Se deberán mostrar estados de carga, error y confirmación.

### 7.2. Seguridad

- La autenticación se realizará mediante Firebase Authentication.
- Las operaciones sensibles se validarán en el servidor.
- Las reglas de seguridad de Firestore impedirán accesos no autorizados.
- Los archivos de Storage estarán protegidos según los permisos del usuario y del grupo.
- Un usuario no podrá modificar directamente su puntuación.
- Un usuario no podrá votar varias veces en la misma prueba y grupo.
- Se comprobará que el usuario pertenece al grupo antes de permitirle consultar su información privada.
- Las operaciones administrativas requerirán permisos específicos.

### 7.3. Rendimiento

- Las pantallas deberán cargar únicamente los datos necesarios.
- Las imágenes y vídeos se almacenarán en Firebase Storage, no dentro de documentos de Firestore.
- Se limitará el tamaño de las pruebas multimedia.
- Se evitarán lecturas innecesarias de Firestore.
- Se utilizará paginación para historiales y galerías extensas cuando sea necesario.

### 7.4. Integridad de datos

- Los puntos se calcularán de forma centralizada.
- La hora de publicación y entrega procederá del servidor.
- Se evitarán duplicados en entregas y votos.
- Las operaciones que actualicen resultados y rankings deberán ser seguras frente a ejecuciones repetidas.
- El cierre de un reto deberá poder ejecutarse de forma idempotente, sin asignar puntos dos veces.

### 7.5. Privacidad

- Los grupos serán privados.
- Las pruebas estarán disponibles únicamente para los usuarios autorizados.
- Las galerías respetarán los permisos del contenido original.
- Se permitirá eliminar o sustituir contenido conforme a las reglas de la aplicación y a los requisitos legales aplicables.

## 8. Tecnologías

### 8.1. Aplicación Android

| Tecnología | Uso |
|---|---|
| Kotlin | Lenguaje principal. |
| Jetpack Compose | Construcción de interfaces. |
| Android Studio | Entorno de desarrollo. |
| MVVM | Organización de la capa de presentación. |
| Clean Architecture | Separación de responsabilidades. |
| Kotlin Coroutines / Flow | Operaciones asíncronas y observación de datos. |

### 8.2. Backend y servicios

| Servicio | Responsabilidad |
|---|---|
| Firebase Authentication | Registro e inicio de sesión. |
| Cloud Firestore | Usuarios, grupos, retos, pruebas, votos y rankings. |
| Firebase Storage | Fotografías y vídeos. |
| Cloud Functions | Publicación de retos, cierre, validación y puntuaciones. |
| Firebase Cloud Messaging | Notificaciones push. |

### 8.3. Arquitectura general

Se propone una arquitectura MVVM combinada con principios de Clean Architecture.

Las responsabilidades se separarán en tres capas:

**Presentation**
- Pantallas de Jetpack Compose.
- Componentes reutilizables.
- ViewModels.
- Estados de interfaz y eventos.

**Domain**
- Modelos de dominio.
- Casos de uso.
- Reglas de negocio.
- Interfaces de repositorios.

**Data**
- Implementaciones de repositorios.
- Fuentes de datos de Firebase.
- Modelos DTO y mapeadores.
- Acceso a Firestore, Storage y Authentication.

La aplicación Android no será responsable de decidir unilateralmente resultados, puntos o permisos. Las operaciones críticas se verificarán en Cloud Functions y mediante reglas de seguridad.

## 9. Modelo conceptual de datos

La siguiente es una primera propuesta conceptual. La estructura definitiva de colecciones y documentos se diseñará antes de implementar Firestore.

### 9.1. Usuario

Representa una cuenta de la aplicación.

Atributos orientativos:

- `uid`
- `username`
- `email`
- `photoUrl`
- `createdAt`
- `streak`
- `favoriteProofs`

### 9.2. Grupo

Representa un grupo privado de amigos.

Atributos orientativos:

- `groupId`
- `name`
- `description`
- `photoUrl`
- `adminId`
- `createdAt`
- `status`

### 9.3. Membresía de grupo

Representa la pertenencia de un usuario a un grupo.

Atributos orientativos:

- `groupId`
- `userId`
- `role`
- `status` (activo o inactivo)
- `joinedAt`
- `leftAt`

Esta entidad permite conservar el historial de los usuarios que abandonan un grupo.

### 9.4. Reto

Representa un reto del catálogo global.

Atributos orientativos:

- `challengeId`
- `title`
- `description`
- `category`
- `createdBy`
- `status` (pendiente, aprobado o rechazado)
- `createdAt`

### 9.5. Publicación diaria

Representa la instancia del reto que se publica un día concreto.

Atributos orientativos:

- `dailyChallengeId`
- `challengeId`
- `publishedAt`
- `closesAt`
- `status`

Es importante separar el reto del catálogo de su publicación diaria. Así se puede reutilizar un mismo reto en diferentes fechas sin duplicar su definición.

### 9.6. Prueba

Representa el contenido multimedia subido por un usuario para una publicación diaria.

Atributos orientativos:

- `proofId`
- `dailyChallengeId`
- `userId`
- `mediaType`
- `storagePath`
- `submittedAt`
- `updatedAt`

La prueba global puede estar asociada a varios grupos a través de sus resultados de validación.

### 9.7. Validación por grupo

Representa el resultado de una prueba dentro de un grupo concreto.

Atributos orientativos:

- `proofId`
- `groupId`
- `status`
- `positiveVotes`
- `negativeVotes`
- `isValid`
- `closedAt`

Separar la prueba de su validación permite que la misma prueba tenga resultados diferentes en distintos grupos.

### 9.8. Voto

Representa el voto de un miembro sobre una prueba en un grupo.

Atributos orientativos:

- `voteId`
- `proofId`
- `groupId`
- `userId`
- `isPositive`
- `createdAt`
- `updatedAt`

Debe existir como máximo un voto vigente por usuario, prueba y grupo.

### 9.9. Puntuación y ranking

Representa los puntos obtenidos por un usuario en un grupo.

Atributos orientativos:

- `groupId`
- `userId`
- `dailyChallengeId`
- `completionPoints`
- `positionPoints`
- `timePoints`
- `totalPoints`
- `calculatedAt`

Se conservará un registro de puntos por reto y usuario para poder consultar el historial y evitar asignaciones duplicadas.

### 9.10. Reacciones y favoritos

Las reacciones registrarán el usuario, la prueba, el tipo de reacción y la fecha.

Los favoritos relacionarán al usuario que guarda una prueba con la prueba guardada.

### 9.11. Notificaciones

Atributos orientativos:

- `notificationId`
- `userId`
- `type`
- `title`
- `body`
- `relatedId`
- `createdAt`
- `readAt`

## 10. Pantallas principales propuestas

La navegación inicial se organizará alrededor de las siguientes pantallas:

1. **Inicio de sesión y registro:** acceso a la aplicación.
2. **Inicio:** reto diario, estado de participación y acceso a grupos.
3. **Detalle del reto:** descripción, tiempo restante y acción para subir una prueba.
4. **Subir prueba:** selección de fotografía o vídeo y confirmación.
5. **Pruebas del grupo:** contenido entregado por los miembros y opciones de votación.
6. **Detalle de prueba:** visualización multimedia, votación y reacciones.
7. **Ranking:** clasificación acumulativa del grupo.
8. **Grupos:** listado de grupos, creación y acceso.
9. **Detalle del grupo:** miembros, información, invitaciones e historial.
10. **Perfil:** estadísticas, retos completados y galerías.
11. **Favoritos:** pruebas guardadas.
12. **Notificaciones:** novedades y actividad.
13. **Proponer reto:** formulario para enviar una propuesta.

La lista es una propuesta funcional inicial. La navegación definitiva y la agrupación de pantallas se concretarán durante el diseño UI/UX.

## 11. Automatizaciones del sistema

El backend deberá encargarse de las operaciones que no pueden depender de que un usuario tenga la aplicación abierta.

### 11.1. Publicación diaria

- Programar la publicación una vez al día.
- Elegir una hora aleatoria entre las 08:00 y las 17:00.
- Seleccionar un reto aprobado.
- Crear la publicación diaria.
- Establecer el cierre de la publicación anterior.
- Notificar a los usuarios.

### 11.2. Cierre de un reto

Cuando se publica el siguiente reto:

1. Se bloquean nuevas entregas y modificaciones del reto anterior.
2. Se bloquean los cambios de voto.
3. Se calcula la validez de cada prueba en cada grupo.
4. Se consideran válidas las pruebas sin votos.
5. Se aplica el umbral del 50 % a las pruebas con votos.
6. Se ordenan las pruebas válidas por hora de entrega.
7. Se calculan las bonificaciones de posición y rapidez.
8. Se guardan los resultados.
9. Se actualizan los rankings.
10. Se envían las notificaciones correspondientes.

El proceso debe ser idempotente: si se ejecuta dos veces por un error o reintento, no debe duplicar puntos.

## 12. Riesgos y decisiones técnicas que requieren atención

| Riesgo | Medida prevista |
|---|---|
| Manipulación de puntos desde el cliente | Cálculo exclusivo en backend. |
| Votos duplicados | Identificador único por prueba, grupo y usuario. |
| Publicación diaria duplicada | Control de ejecución e idempotencia. |
| Diferencias entre relojes de móviles | Uso de marcas temporales del servidor. |
| Archivos multimedia grandes | Límites, compresión y Storage. |
| Acceso a pruebas de grupos ajenos | Reglas de seguridad y comprobación de membresía. |
| Abandono de grupos | Membresías inactivas y conservación del historial. |
| Retos peligrosos o inadecuados | Revisión y aprobación de propuestas. |
| Puntuaciones distintas entre grupos | Validación y ranking independientes. |

## 13. Plan de desarrollo propuesto

Se recomienda implementar el proyecto de forma incremental.

### Fase 1. Preparación

- Crear el proyecto Android.
- Configurar Kotlin y Jetpack Compose.
- Configurar Firebase.
- Definir estructura de paquetes.
- Preparar navegación y tema visual.

### Fase 2. Usuarios y grupos

- Registro e inicio de sesión.
- Perfil básico.
- Crear grupos.
- Invitar usuarios.
- Gestionar miembros y permisos.

### Fase 3. Retos diarios

- Crear catálogo de retos.
- Proponer y aprobar retos.
- Implementar publicación diaria.
- Mostrar reto activo y plazo restante.

### Fase 4. Pruebas multimedia

- Seleccionar fotografía o vídeo.
- Subir archivos a Firebase Storage.
- Guardar metadatos en Firestore.
- Permitir sustituir pruebas.
- Mostrar las pruebas del grupo.

### Fase 5. Validación

- Implementar votaciones.
- Impedir el voto propio.
- Permitir cambiar el voto.
- Calcular resultados al cierre.
- Gestionar pruebas sin votos.

### Fase 6. Puntuación y ranking

- Implementar fórmula de puntos.
- Calcular posiciones.
- Actualizar ranking acumulativo.
- Mostrar historial de resultados.

### Fase 7. Funcionalidades sociales

- Reacciones.
- Favoritos.
- Galerías.
- Notificaciones.
- Estadísticas y rachas.

### Fase 8. Pruebas y entrega

- Pruebas unitarias de reglas de negocio.
- Pruebas de integración con Firebase.
- Pruebas de permisos y seguridad.
- Pruebas en distintos dispositivos y tamaños de pantalla.
- Corrección de errores.
- Preparación de memoria y demostración.

## 14. Criterios de aceptación del MVP

La primera versión se considerará funcional cuando se pueda demostrar el siguiente flujo completo:

1. Un usuario crea una cuenta.
2. Crea un grupo privado e invita a otros usuarios.
3. El sistema publica un reto diario.
4. Los integrantes consultan el reto.
5. Un usuario sube una fotografía o vídeo.
6. Los demás integrantes pueden consultar y votar esa prueba.
7. El autor no puede votar su propia prueba.
8. Los usuarios pueden cambiar sus votos antes del cierre.
9. Al publicarse el siguiente reto, el sistema cierra el anterior.
10. El sistema determina si las pruebas son válidas.
11. Se calculan los puntos de los participantes.
12. Se actualiza el ranking del grupo.
13. Los usuarios pueden consultar el resultado y el historial.

## 15. Decisiones pendientes y supuestos de esta versión

Aunque el funcionamiento principal está definido, antes de cerrar el diseño técnico definitivo deben concretarse algunos detalles:

1. **Miembros utilizados para calcular la posición:** se propone contar a los miembros activos del grupo que podían participar en ese reto, aunque no hayan entregado prueba. Esto evita que la bonificación dependa de cuántos decidan participar.
2. **Cambio de prueba:** la posición se calcula con la hora de la última versión entregada antes del cierre.
3. **Votos:** se ha definido el umbral del 50 % sobre votos emitidos. La prueba sin ningún voto se considera válida.
4. **Pruebas válidas y orden:** las posiciones definitivas se calculan al cerrar el reto, considerando únicamente las pruebas válidas.
5. **Retos sin participantes:** se debe permitir cerrar el periodo sin asignar puntos.
6. **Zona horaria:** se propone utilizar una zona horaria común para la publicación global, inicialmente `Europe/Madrid`, teniendo en cuenta los cambios de horario.
7. **Moderación:** queda por concretar quién administra el catálogo global y cómo se gestionan reportes de contenido.
8. **Duración y tamaño de vídeos:** se definirán límites concretos antes de implementar la subida multimedia.
9. **Privacidad de galerías:** se deberá decidir si las galerías personales son visibles para todos, solo para amigos o únicamente para miembros de los grupos correspondientes.
10. **Condiciones académicas:** se deben contrastar las funcionalidades y tecnologías con el enunciado y los criterios de evaluación del profesor.

---

## 16. Conclusión

Daily Challenges propone una aplicación Android nativa centrada en la competición amistosa entre grupos de amigos. Su característica principal es combinar retos diarios globales con pruebas multimedia y validación independiente por cada grupo.

La aplicación utiliza Kotlin, Jetpack Compose y Firebase, con una arquitectura organizada por capas. Las operaciones críticas, como la validación de pruebas, el cálculo de puntuaciones y la actualización de rankings, se delegan al backend para mantener la integridad de los resultados.

Este documento establece la base funcional y técnica del proyecto. El siguiente paso será cerrar los puntos pendientes, diseñar el modelo definitivo de Firestore y preparar la arquitectura del proyecto Android antes de comenzar la implementación.

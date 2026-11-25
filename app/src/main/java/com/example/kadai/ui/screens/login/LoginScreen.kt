package com.example.kadai.ui.screens.login

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kadai.ui.components.KadaiPrimaryButton
import com.example.kadai.ui.components.KadaiTextField
import com.example.kadai.ui.theme.Background
import com.example.kadai.ui.theme.Border
import com.example.kadai.ui.theme.KadaiTheme
import com.example.kadai.ui.theme.PurplePrimary
import com.example.kadai.ui.theme.PurpleSoft
import com.example.kadai.ui.theme.Surface
import com.example.kadai.ui.theme.TextDisabled
import com.example.kadai.ui.theme.TextPrimary

/**
 * Pantalla de inicio de sesión de KADAI.
 *
 * @param onLoginClick     Callback al pulsar "Iniciar sesión"
 * @param onGoogleClick    Callback al pulsar "Continuar con Google"
 * @param onRegisterClick  Callback al pulsar "Regístrate"
 * @param onForgotClick    Callback al pulsar "¿Olvidaste tu contraseña?"
 */
@Composable
fun LoginScreen(
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleClick: () -> Unit = {},
    onRegisterClick: () -> Unit = {},
    onForgotClick: () -> Unit = {},
) {
    var email    by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Gradiente radial de acento en la cabecera
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            PurplePrimary.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        center = Offset(x = 900f, y = -60f),
                        radius = 700f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // ── Logo ────────────────────────────────────────────────────────
            KadaiLogoMark()

            Spacer(modifier = Modifier.height(44.dp))

            // ── Bienvenida ──────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Bienvenido de nuevo",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Inicia sesión para ver el reto de hoy",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextDisabled
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Campo de Email ───────────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CORREO ELECTRÓNICO",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9090A8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                KadaiTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "tu@email.com",
                    leadingIcon = Icons.Outlined.Email,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Campo de Contraseña ──────────────────────────────────────────
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "CONTRASEÑA",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF9090A8),
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                PasswordTextField(
                    value = password,
                    onValueChange = { password = it }
                )
            }

            // ── Olvidé contraseña ────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = "¿Olvidaste tu contraseña?",
                    style = MaterialTheme.typography.bodySmall,
                    color = PurplePrimary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onForgotClick() }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Botón principal ──────────────────────────────────────────────
            KadaiPrimaryButton(
                text = "Iniciar sesión",
                onClick = { onLoginClick(email, password) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Divisor ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border,
                    thickness = 1.dp
                )
                Text(
                    text = "o continúa con",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFF4A4A5C)
                )
                HorizontalDivider(
                    modifier = Modifier.weight(1f),
                    color = Border,
                    thickness = 1.dp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón Google ─────────────────────────────────────────────────
            GoogleSignInButton(onClick = onGoogleClick)

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // ── Enlace de registro ───────────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = TextDisabled)) {
                        append("¿Aún no tienes cuenta? ")
                    }
                    withStyle(SpanStyle(color = PurpleSoft, fontWeight = FontWeight.SemiBold)) {
                        append("Regístrate")
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.clickable { onRegisterClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ── Campo de contraseña con toggle ──────────────────────────────────────────

@Composable
private fun PasswordTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }

    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
        cursorBrush = androidx.compose.ui.graphics.SolidColor(PurpleSoft),
        visualTransformation = if (passwordVisible) VisualTransformation.None
        else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface)
                    .border(
                        width = 1.5.dp,
                        color = if (value.isNotEmpty()) Color(0xFF3A3A4A) else Border,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lock,
                        contentDescription = null,
                        tint = if (value.isNotEmpty()) PurpleSoft else Color(0xFF4A4A5C),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (value.isEmpty()) {
                            Text(
                                "••••••••",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color(0xFF3E3E50)
                            )
                        }
                        innerTextField()
                    }
                    // Toggle visibilidad
                    Icon(
                        imageVector = if (passwordVisible)
                            Icons.Outlined.Lock // placeholder — usar painterResource con ic real
                        else
                            Icons.Outlined.Lock,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña",
                        tint = Color(0xFF4A4A5C),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { passwordVisible = !passwordVisible }
                    )
                }
            }
        }
    )
}

// ── Logo mark KADAI ──────────────────────────────────────────────────────────

@Composable
fun KadaiLogoMark(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Isotipo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF7C3AED),
                            Color(0xFF9F5FF1),
                            Color(0xFFA855F7)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // "K" con estilo — texto grande como sustituto
            // En producción usar un vector drawable personalizado
            Text(
                text = "K",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-2).sp
                ),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Wordmark
        Text(
            text = "KADAI",
            style = MaterialTheme.typography.displayMedium.copy(
                letterSpacing = 8.sp,
                fontWeight = FontWeight.Bold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "ACEPTA EL RETO",
            style = MaterialTheme.typography.labelSmall.copy(
                letterSpacing = 2.sp
            ),
            color = TextDisabled
        )
    }
}

// ── Botón de Google ──────────────────────────────────────────────────────────

@Composable
fun GoogleSignInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Surface,
            contentColor = Color(0xFFCCCCDD)
        ),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, Border)
    ) {
        // Icono de Google simplificado — en producción usar painterResource
        GoogleColorIcon()
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Continuar con Google",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFFCCCCDD)
        )
    }
}

@Composable
private fun GoogleColorIcon() {
    // Placeholder de color para el icono de Google
    // En producción: Icon(painterResource(R.drawable.ic_google), ...)
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF4285F4), Color(0xFF34A853))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "G",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

// ── Preview ──────────────────────────────────────────────────────────────────

@Preview(
    name = "Login Screen",
    showBackground = true,
    backgroundColor = 0xFF0F0F12,
    device = "spec:width=390dp,height=844dp,dpi=460"
)
@Composable
private fun LoginScreenPreview() {
    KadaiTheme {
        LoginScreen()
    }
}

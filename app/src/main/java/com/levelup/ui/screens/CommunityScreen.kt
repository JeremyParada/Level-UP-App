package com.levelup.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.levelup.ui.theme.LevelUpPrimary
import com.levelup.ui.theme.LevelUpSecondary

data class Post(
    val id: String,
    val userName: String,
    val userAvatar: String,
    val content: String,
    val likes: Int,
    val comments: Int,
    val timestamp: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(navController: NavController) {
    // Posts simulados
    val posts = remember {
        listOf(
            Post(
                "1",
                "GamerPro123",
                "GP",
                "¡Acabo de completar Elden Ring! Qué experiencia increíble 🎮",
                45,
                12,
                "Hace 2 horas"
            ),
            Post(
                "2",
                "TechNinja",
                "TN",
                "¿Alguien más esperando el próximo lanzamiento de Zelda? 🗡️",
                78,
                23,
                "Hace 5 horas"
            ),
            Post(
                "3",
                "RetroGamer",
                "RG",
                "Jugando a los clásicos de Super Nintendo. ¡Nostalgia pura! 🕹️",
                92,
                18,
                "Hace 1 día"
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Comunidad",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Crear nuevo post */ },
                containerColor = LevelUpPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Crear post", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(posts) { post ->
                PostCard(post)
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header del post
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(LevelUpPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.userAvatar,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.userName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.timestamp,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { /* TODO: Más opciones */ }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "Más opciones"
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Contenido del post
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            // Acciones del post
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                PostAction(
                    icon = Icons.Default.Favorite,
                    count = post.likes,
                    text = "Me gusta"
                )

                PostAction(
                    icon = Icons.Default.Star,
                    count = post.comments,
                    text = "Comentarios"
                )

                PostAction(
                    icon = Icons.Default.Share,
                    count = null,
                    text = "Compartir"
                )
            }
        }
    }
}

@Composable
fun PostAction(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int?,
    text: String
) {
    TextButton(
        onClick = { /* TODO */ }
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = LevelUpPrimary
        )
        Spacer(modifier = Modifier.width(4.dp))
        if (count != null) {
            Text(text = count.toString(), color = LevelUpPrimary)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(text = text, color = MaterialTheme.colorScheme.onSurface)
    }
}
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
import java.text.SimpleDateFormat
import java.util.*

data class ProductReview(
    val id: String,
    val userName: String,
    val userAvatar: String,
    val rating: Int,
    val comment: String,
    val date: Date,
    val productName: String,
    val helpful: Int = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewsScreen(navController: NavController) {
    // Reseñas simuladas
    val reviews = remember {
        listOf(
            ProductReview(
                "1",
                "GamerPro123",
                "GP",
                5,
                "Excelente producto, llegó en perfectas condiciones y funciona increíble. Totalmente recomendado para cualquier gamer.",
                Date(),
                "Mouse Gamer Logitech G502 HERO",
                15
            ),
            ProductReview(
                "2",
                "TechLover",
                "TL",
                4,
                "Muy buen producto, aunque el precio es un poco elevado. La calidad es excelente y el rendimiento supera mis expectativas.",
                Date(System.currentTimeMillis() - 86400000),
                "Auriculares Gamer HyperX Cloud II",
                8
            ),
            ProductReview(
                "3",
                "RetroGamer88",
                "RG",
                5,
                "¡Increíble! Este juego de mesa es perfecto para reuniones familiares. La calidad de los componentes es excepcional.",
                Date(System.currentTimeMillis() - 172800000),
                "Catan",
                23
            ),
            ProductReview(
                "4",
                "ProPlayer",
                "PP",
                3,
                "Es bueno, pero esperaba un poco más por el precio. Cumple su función pero hay mejores opciones en el mercado.",
                Date(System.currentTimeMillis() - 259200000),
                "Silla Gamer Secretlab Titan",
                5
            )
        )
    }

    var filterRating by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Reseñas de Productos",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LevelUpPrimary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Filtros de calificación
            RatingFilterSection(
                selectedRating = filterRating,
                onRatingSelected = { filterRating = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Estadísticas generales
            ReviewStatsCard(reviews)

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de reseñas
            val filteredReviews = if (filterRating != null) {
                reviews.filter { it.rating == filterRating }
            } else {
                reviews
            }

            if (filteredReviews.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay reseñas con esta calificación",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredReviews) { review ->
                        ReviewCard(review)
                    }
                }
            }
        }
    }
}

@Composable
fun RatingFilterSection(
    selectedRating: Int?,
    onRatingSelected: (Int?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedRating == null,
            onClick = { onRatingSelected(null) },
            label = { Text("Todas") }
        )

        for (rating in 5 downTo 1) {
            FilterChip(
                selected = selectedRating == rating,
                onClick = { onRatingSelected(rating) },
                label = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("$rating")
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LevelUpPrimary,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                )
            )
        }
    }
}

@Composable
fun ReviewStatsCard(reviews: List<ProductReview>) {
    val averageRating = if (reviews.isNotEmpty()) {
        reviews.map { it.rating }.average()
    } else 0.0

    val ratingDistribution = (1..5).map { rating ->
        reviews.count { it.rating == rating }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LevelUpPrimary.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Calificación promedio
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = String.format("%.1f", averageRating),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    color = LevelUpPrimary
                )
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < averageRating.toInt()) {
                                Icons.Default.Star
                            } else {
                                Icons.Default.Star
                            },
                            contentDescription = null,
                            tint = if (index < averageRating.toInt()) {
                                LevelUpPrimary
                            } else {
                                Color.Gray.copy(alpha = 0.3f)
                            },
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    text = "${reviews.size} reseñas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Distribución de calificaciones
            Column(
                modifier = Modifier.weight(1f).padding(start = 24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ratingDistribution.reversed().forEachIndexed { index, count ->
                    val rating = 5 - index
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "$rating",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(16.dp)
                        )
                        LinearProgressIndicator(
                            progress = if (reviews.isNotEmpty()) count.toFloat() / reviews.size else 0f,
                            modifier = Modifier
                                .weight(1f)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = LevelUpPrimary
                        )
                        Text(
                            text = "$count",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.width(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReviewCard(review: ProductReview) {
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
            // Header de la reseña
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(LevelUpSecondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userAvatar,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = review.userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                .format(review.date),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Calificación con estrellas
                Row {
                    repeat(review.rating) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = LevelUpPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    repeat(5 - review.rating) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = Color.Gray.copy(alpha = 0.3f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del producto
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LevelUpPrimary.copy(alpha = 0.1f)
            ) {
                Text(
                    text = review.productName,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = LevelUpPrimary,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Comentario
            Text(
                text = review.comment,
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            Spacer(modifier = Modifier.height(8.dp))

            // Acciones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { /* TODO: Marcar como útil */ }
                ) {
                    Icon(
                        Icons.Default.ThumbUp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Útil (${review.helpful})")
                }

                TextButton(
                    onClick = { /* TODO: Reportar */ }
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reportar")
                }
            }
        }
    }
}
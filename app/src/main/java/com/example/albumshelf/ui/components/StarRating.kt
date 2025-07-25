package com.example.albumshelf.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun StarRating(
    modifier: Modifier = Modifier,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            val isSelected = i <= rating
            Icon(
                imageVector = if (isSelected) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Star $i",
                tint = if (isSelected) Color(0xFFFFD700) else Color.Gray,
                modifier = Modifier.clickable { onRatingChange(i) }
            )
        }
    }
}

@Composable
fun StarDisplay(
    modifier: Modifier = Modifier,
    rating: Int
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            Icon(
                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = "Star $i",
                tint = if (i <= rating) Color(0xFFFFD700) else Color.Gray
            )
        }
    }
}

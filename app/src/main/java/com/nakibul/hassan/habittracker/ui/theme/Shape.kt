package com.nakibul.hassan.habittracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Premium rounded shapes with larger corner radii for a modern, posh look
val Shapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

// Custom shapes for specific premium components
val PremiumCardShape = RoundedCornerShape(20.dp)
val PremiumButtonShape = RoundedCornerShape(16.dp)
val PremiumPillShape = RoundedCornerShape(24.dp)
val PremiumBottomNavShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)


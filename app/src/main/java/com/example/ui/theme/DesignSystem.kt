package com.example.ui.theme

import androidx.compose.ui.unit.dp

object DesignSystem {
    // Optimized Spacing scale for compact vertical height and clean visual separation
    object Spacing {
        val micro = 2.dp       // Extra tight accents, lines
        val tiny = 4.dp        // Small gaps between text, badge spacing
        val extraSmall = 6.dp  // Margin between sub-elements inside a card
        val small = 8.dp       // Secondary elements, inner spacing
        val medium = 10.dp     // Content group padding, standard cards
        val large = 12.dp      // Spacing between screens cards, list items
        val extraLarge = 16.dp // Large visual gaps, title groups
        val huge = 20.dp       // Screen sections separation
    }

    // Padding presets for consistent layout density and reduced height
    object Padding {
        val screenOuter = 12.dp  // Standard screen container margins
        val cardInner = 10.dp    // Default padding within standard cards
        val cardCompact = 8.dp   // Compact card internal padding
        val badgeInner = 4.dp    // Tags & status pills padding
    }
}

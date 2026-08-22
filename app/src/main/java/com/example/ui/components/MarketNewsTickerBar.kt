package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MarketNewsItem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MarketNewsTickerBar(
    activeNews: MarketNewsItem?,
    onOpenBourse: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeNews == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
            .clickable { onOpenBourse() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("market_news_ticker")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f, fill = false)
            ) {
                // Flashing Live Dot
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(CrimsonFrenzy, CircleShape)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "FLASH BOURSE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = CrimsonFrenzy,
                    letterSpacing = 0.5.sp,
                    maxLines = 1,
                    softWrap = false
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${activeNews.emoji} ${activeNews.headline}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    maxLines = 1
                )
            }

            if (activeNews.affectedTicker != null) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .background(
                            if (activeNews.priceImpactPercent >= 0) Color(0xFF064E3B)
                            else Color(0xFF7F1D1D),
                            RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${activeNews.affectedTicker} ${if (activeNews.priceImpactPercent >= 0) "+" else ""}${(activeNews.priceImpactPercent * 100).toInt()}%",
                        color = if (activeNews.priceImpactPercent >= 0) EmeraldDark else Color(0xFFFCA5A5),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }
        }
    }
}

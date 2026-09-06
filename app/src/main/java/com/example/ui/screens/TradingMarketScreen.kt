package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MoneyFormatter
import com.example.model.StockItem
import com.example.ui.theme.DesignSystem
import com.example.ui.theme.AmberDark
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.GameUiState

@Composable
fun TradingMarketScreen(
    state: GameUiState,
    onBuyStock: (ticker: String, qty: Int) -> Unit,
    onSellStock: (ticker: String, qty: Int) -> Unit,
    onStakeCrypto: (ticker: String, qty: Int) -> Unit = { _, _ -> },
    onUnstakeCrypto: (ticker: String, qty: Int) -> Unit = { _, _ -> },
    onPlaceAuctionBid: (String) -> Unit = {},
    onBuyoutAuctionLot: (String) -> Unit = {},
    onResetAuctionLot: (String) -> Unit = {},
    onAcquireTakeoverStake: (String) -> Unit = {},
    onOpenAuctionDialog: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("trading_market_root")
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurface,
            contentColor = AmberDark,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = AmberDark,
                    height = 3.dp
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bourse & Crypto", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Gavel, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Enchères & Fusions", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                selectedContentColor = AmberDark,
                unselectedContentColor = TextSecondary
            )
        }

        if (selectedTab == 1) {
            AuctionsAndTakeoverContent(
                state = state,
                onPlaceBid = onPlaceAuctionBid,
                onBuyoutLot = onBuyoutAuctionLot,
                onResetLot = onResetAuctionLot,
                onAcquireStake = onAcquireTakeoverStake,
                onOpenLiveWarModal = onOpenAuctionDialog
            )
        } else {
            var selectedCategoryFilter by remember { mutableIntStateOf(0) }

            val totalPortfolioValue = state.stocks.sumOf { (it.ownedShares + it.stakedShares) * it.price }
            val totalStocksValue = state.stocks.filter { !it.isCrypto }.sumOf { it.ownedShares * it.price }
            val totalCryptoValue = state.stocks.filter { it.isCrypto }.sumOf { (it.ownedShares + it.stakedShares) * it.price }
            val totalUnrealizedPnL = state.stocks.sumOf { it.unrealizedProfitLoss }
            val totalStakingYieldPerSec = state.stocks.filter { it.stakedShares > 0 }.sumOf { it.stakedShares * it.price * 0.00005 }

            val filteredStocks = when (selectedCategoryFilter) {
                1 -> state.stocks.filter { !it.isCrypto }
                2 -> state.stocks.filter { it.isCrypto }
                3 -> state.stocks.filter { it.stakedShares > 0 || (it.isCrypto && it.ownedShares > 0) }
                else -> state.stocks
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("trading_market_list"),
                contentPadding = PaddingValues(DesignSystem.Padding.screenOuter),
                verticalArrangement = Arrangement.spacedBy(DesignSystem.Spacing.small)
            ) {
                item {
                    // Portfolio Summary Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("portfolio_summary_card"),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(DesignSystem.Padding.cardCompact)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SALLE DES MARCHÉS & CRYPTO 24/7",
                                        color = AmberDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Portefeuille : ${MoneyFormatter.format(totalPortfolioValue)}",
                                        color = CyberCyan,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF082F49), RoundedCornerShape(8.dp))
                                        .border(1.dp, CyberCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ShowChart,
                                            contentDescription = null,
                                            tint = CyberCyan,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = "MARCHÉ EN DIRECT",
                                            color = CyberCyan,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            maxLines = 1,
                                            softWrap = false
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Portfolio Metrics Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("📈 Actions: ${MoneyFormatter.format(totalStocksValue)}", fontSize = 11.sp, color = TextSecondary)
                                    Text("🪙 Cryptos: ${MoneyFormatter.format(totalCryptoValue)}", fontSize = 11.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    val isProfitable = totalUnrealizedPnL >= 0
                                    val pnlColor = if (isProfitable) EmeraldDark else CrimsonFrenzy
                                    Text(
                                        text = "P&L Non Réalisé: ${if (isProfitable) "+" else ""}${MoneyFormatter.format(totalUnrealizedPnL)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = pnlColor
                                    )
                                    if (totalStakingYieldPerSec > 0) {
                                        Text(
                                            text = "🔒 Staking: +${MoneyFormatter.formatPerSec(totalStakingYieldPerSec)}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AmberDark
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Category Filter Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val filterLabels = listOf("Tous", "📈 Actions", "🪙 Crypto", "🔒 Staking")
                                filterLabels.forEachIndexed { index, label ->
                                    val isSelected = selectedCategoryFilter == index
                                    val chipBg = if (isSelected) AmberDark else DarkSurfaceVariant
                                    val chipText = if (isSelected) Color.Black else TextSecondary

                                    OutlinedButton(
                                        onClick = { selectedCategoryFilter = index },
                                        shape = RoundedCornerShape(20.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(containerColor = chipBg, contentColor = chipText),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) AmberDark else DarkCardBorder),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(text = label, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                items(filteredStocks, key = { it.ticker }) { stock ->
                    StockItemCard(
                        stock = stock,
                        playerCash = state.cash,
                        onBuy = { onBuyStock(stock.ticker, 1) },
                        onSell = { onSellStock(stock.ticker, 1) },
                        onBuyMax = { onBuyStock(stock.ticker, 10) },
                        onSellAll = { onSellStock(stock.ticker, stock.ownedShares) },
                        onStakeOne = { onStakeCrypto(stock.ticker, 1) },
                        onStakeAll = { onStakeCrypto(stock.ticker, stock.ownedShares) },
                        onUnstakeAll = { onUnstakeCrypto(stock.ticker, stock.stakedShares) }
                    )
                }
            }
        }
    }
}

@Composable
fun StockItemCard(
    stock: StockItem,
    playerCash: Double,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onBuyMax: () -> Unit,
    onSellAll: () -> Unit,
    onStakeOne: () -> Unit = {},
    onStakeAll: () -> Unit = {},
    onUnstakeAll: () -> Unit = {}
) {
    val isPositive = stock.changePercent >= 0
    val trendColor = if (isPositive) EmeraldDark else CrimsonFrenzy

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("stock_card_${stock.ticker}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (stock.isCrypto) AmberDark.copy(alpha = 0.5f) else DarkCardBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(DesignSystem.Padding.cardCompact)
        ) {
            // Header Row: Badge + Ticker + Price + 24h Change
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .background(if (stock.isCrypto) Color(0xFF78350F) else Color(0xFF064E3B), RoundedCornerShape(4.dp))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = if (stock.isCrypto) "🪙 CRYPTO" else "📈 ACTION",
                                color = if (stock.isCrypto) AmberDark else EmeraldDark,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stock.ticker,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = stock.name,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = MoneyFormatter.format(stock.price),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isPositive) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                            contentDescription = null,
                            tint = trendColor,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${if (isPositive) "+" else ""}${String.format("%.2f", stock.changePercent)}%",
                            color = trendColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

            // 2D Live Interactive Mini Chart (Compact Height)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(38.dp)
                    .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    if (stock.history.size > 1) {
                        val maxVal = stock.history.maxOrNull() ?: 1f
                        val minVal = stock.history.minOrNull() ?: 0f
                        val range = (maxVal - minVal).coerceAtLeast(0.01f)

                        val path = Path()
                        val stepX = size.width / (stock.history.size - 1)

                        stock.history.forEachIndexed { i, p ->
                            val normY = (p - minVal) / range
                            val y = size.height - (normY * (size.height - 6f)) - 3f
                            val x = i * stepX
                            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }

                        drawPath(
                            path = path,
                            color = trendColor,
                            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.extraSmall))

            // Holdings and P&L info
            val totalOwned = stock.ownedShares + stock.stakedShares
            if (totalOwned > 0) {
                val pnlColor = if (stock.unrealizedProfitLoss >= 0) EmeraldDark else CrimsonFrenzy
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Possédé : ${stock.ownedShares} (${MoneyFormatter.format(stock.ownedShares * stock.price)})",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "P&L : ${if (stock.unrealizedProfitLoss >= 0) "+" else ""}${MoneyFormatter.format(stock.unrealizedProfitLoss)} (${String.format("%.1f", stock.unrealizedProfitLossPercent)}%)",
                        color = pnlColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            } else {
                Text(
                    text = "Possédé : 0 titre",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(DesignSystem.Spacing.micro))

            // Fast Trading Controls with fixed compact padding
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Buy 1
                Button(
                    onClick = onBuy,
                    enabled = playerCash >= stock.price,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldPrimary,
                        contentColor = Color.Black,
                        disabledContainerColor = DarkSurfaceVariant,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .testTag("buy_stock_${stock.ticker}")
                ) {
                    Text(text = "+1", fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                }

                // Buy 5
                Button(
                    onClick = onBuyMax,
                    enabled = playerCash >= stock.price * 5,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF047857),
                        contentColor = Color.White,
                        disabledContainerColor = DarkSurfaceVariant,
                        disabledContentColor = TextMuted
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .testTag("buy_max_stock_${stock.ticker}")
                ) {
                    Text(text = "+5", fontSize = 11.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                }

                // Sell 1
                OutlinedButton(
                    onClick = onSell,
                    enabled = stock.ownedShares > 0,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CrimsonFrenzy
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (stock.ownedShares > 0) CrimsonFrenzy else DarkCardBorder),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .testTag("sell_stock_${stock.ticker}")
                ) {
                    Text(text = "VENDRE", fontSize = 9.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                }

                // Sell All
                OutlinedButton(
                    onClick = onSellAll,
                    enabled = stock.ownedShares > 0,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CrimsonFrenzy
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (stock.ownedShares > 0) CrimsonFrenzy else DarkCardBorder),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(30.dp)
                        .testTag("sell_all_stock_${stock.ticker}")
                ) {
                    Text(text = "TOUT", fontSize = 9.sp, fontWeight = FontWeight.Black, maxLines = 1, softWrap = false)
                }
            }

            if (stock.isCrypto) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "🔒 Staking: ${stock.stakedShares} token(s)",
                            color = AmberDark,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (stock.stakedShares > 0) {
                            val yieldSec = stock.stakedShares * stock.price * 0.00005
                            Text(
                                text = "+${MoneyFormatter.formatPerSec(yieldSec)}",
                                color = EmeraldDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Button(
                            onClick = onStakeAll,
                            enabled = stock.ownedShares > 0,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberDark,
                                contentColor = Color.Black,
                                disabledContainerColor = DarkSurfaceVariant,
                                disabledContentColor = TextMuted
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("STAKER TOUT", fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }

                        OutlinedButton(
                            onClick = onUnstakeAll,
                            enabled = stock.stakedShares > 0,
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (stock.stakedShares > 0) AmberDark else DarkCardBorder),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(26.dp)
                        ) {
                            Text("RETIRER", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

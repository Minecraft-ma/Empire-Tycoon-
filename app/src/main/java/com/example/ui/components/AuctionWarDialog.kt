package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AuctionLot
import com.example.model.MoneyFormatter
import com.example.ui.theme.AmberDark
import com.example.ui.theme.AmberPrimary
import com.example.ui.theme.CrimsonFrenzy
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AuctionWarDialog(
    isOpen: Boolean,
    lot: AuctionLot?,
    playerCash: Double,
    onPlaceBid: (String) -> Unit,
    onBuyoutLot: (String) -> Unit,
    onResetLot: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen || lot == null) return

    val isAffordableBid = playerCash >= lot.nextMinBid
    val isAffordableBuyout = playerCash >= lot.instantBuyoutPrice

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("auction_war_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, if (lot.isPlayerWinning) AmberDark else DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = lot.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "SALLE DES ENCHÈRES",
                                color = AmberDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = lot.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Fermer", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Timer and Current Bid Highlights
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, if (lot.timeRemainingSec <= 8) CrimsonFrenzy else DarkCardBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "TEMPS RESTANT", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.HourglassBottom,
                                    contentDescription = null,
                                    tint = if (lot.timeRemainingSec <= 8) CrimsonFrenzy else AmberDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (lot.isWonByPlayer) "ADJUGÉ !" else if (lot.isExpired) "EXPIRÉ" else "${lot.timeRemainingSec}s",
                                    color = if (lot.timeRemainingSec <= 8 && !lot.isWonByPlayer) CrimsonFrenzy else TextPrimary,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, if (lot.isPlayerWinning) EmeraldDark else AmberDark.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "OFFRE ACTUELLE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = MoneyFormatter.format(lot.currentBid),
                                color = if (lot.isPlayerWinning) EmeraldLight else AmberDark,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                // Current Leading Bidder
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Enchérisseur en tête :", color = TextSecondary, fontSize = 11.sp)
                        Text(
                            text = if (lot.isPlayerWinning) "👑 VOUS (En tête)" else lot.highestBidderName,
                            color = if (lot.isPlayerWinning) EmeraldLight else AmberDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                // Description and Perks
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = lot.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EmeraldDark.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = EmeraldLight, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lot.permanentMultiplier > 0) "+${(lot.permanentMultiplier * 100).toInt()}% Multiplicateur Global"
                                   else "+${MoneyFormatter.format(lot.bonusCashYieldPerSec)}/sec",
                            color = EmeraldLight,
                            fontWeight = FontWeight.Black,
                            fontSize = 11.sp
                        )
                    }
                }

                // Rivals in Arena
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "Concurrents connectés :", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    lot.activeRivals.forEach { rival ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = rival.avatar, fontSize = 16.sp)
                                Text(
                                    text = rival.name.split(" ").firstOrNull() ?: rival.name,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }

                // Action Buttons
                Spacer(modifier = Modifier.height(16.dp))
                if (lot.isWonByPlayer) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EmeraldDark, RoundedCornerShape(10.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🎉 Lot remporté avec succès !",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                } else if (lot.isExpired) {
                    Button(
                        onClick = { onResetLot(lot.id) },
                        colors = ButtonDefaults.buttonColors(containerColor = AmberDark),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Relancer la vente aux enchères", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onPlaceBid(lot.id) },
                            enabled = isAffordableBid,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberDark,
                                disabledContainerColor = DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Poser l'offre (+15%)",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isAffordableBid) DarkBackground else TextMuted
                                )
                                Text(
                                    text = MoneyFormatter.format(lot.nextMinBid),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isAffordableBid) DarkBackground else TextMuted
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onBuyoutLot(lot.id) },
                            enabled = isAffordableBuyout,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberCyan),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAffordableBuyout) CyberCyan else DarkCardBorder
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Achat Immédiat",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    color = if (isAffordableBuyout) CyberCyan else TextMuted
                                )
                                Text(
                                    text = MoneyFormatter.format(lot.instantBuyoutPrice),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isAffordableBuyout) CyberCyan else TextMuted
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

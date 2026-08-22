package com.example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object TycoonNotificationHelper {
    private const val CHANNEL_ID = "tycoon_notifications"
    private const val CHANNEL_NAME = "Empire Tycoon Alertes"

    // EXACTLY 30 Premium & Immersive daily notifications to avoid spam and keep the app fresh!
    val templates = listOf(
        Pair("💸 Coffres de profits pleins !", "Votre empire tourne sans vous ! Venez récolter vos millions de bénéfices passifs."),
        Pair("🚀 Opportunité de Prestige !", "Vos conseillers estiment que votre empire est prêt à être vendu pour décupler vos gains !"),
        Pair("📈 Alerte Bourse Crypto", "Le cours du DogeGold s'envole de +18% ! C'est le moment d'empocher ou d'investir."),
        Pair("💼 Nouveau Manager disponible", "Un directeur d'exception attend votre signature pour automatiser votre chaîne logistique."),
        Pair("🤝 Offre de Sponsor VIP", "Une marque internationale propose un contrat exclusif. Réclamez votre bonus de cash !"),
        Pair("⚠️ Alerte Crise Financière !", "Une rumeur fait chuter le marché. Prenez une décision rapide pour protéger vos actifs."),
        Pair("🎁 Votre Cadeau du Jour attend", "Ne perdez pas votre série de connexion quotidienne ! Un boost de x2 attend son ouverture."),
        Pair("💰 Jackpot à la Roue VIP", "Un lancer gratuit est disponible sur la Roue de la Fortune. Tentez de gagner le gros lot !"),
        Pair("⚡ Surcharge de Production !", "Vos usines tournent en surrégime. Ouvrez l'application pour booster vos gains par 10 !"),
        Pair("🎓 Conseil du Conseil d'Administration", "Vos hauts dirigeants proposent une nouvelle stratégie R&D pour accélérer vos revenus."),
        Pair("🚀 Objectif Quotidien Débloqué", "Vous avez validé une mission. Vos récompenses en cash premium sont prêtes à être récupérées !"),
        Pair("🏦 Rachat d'Actions", "Le marché boursier propose un rachat d'actifs très avantageux. Examinez les taux !"),
        Pair("🔬 Technologie R&D disponible", "Votre laboratoire a finalisé une recherche majeure. Améliorez votre productivité maintenant !"),
        Pair("👑 Roi du Leaderboard ?", "Un rival vient de dépasser votre valeur nette ! Reprenez le contrôle du classement mondial !"),
        Pair("📦 Livraison Express Reçue !", "Un colis mystère contenant des milliards de dollars a été déposé à votre quartier général !"),
        Pair("🔥 Frénésie Financière !", "Le multiplicateur global vient d'être doublé temporairement. Connectez-vous vite pour en profiter !"),
        Pair("🏬 Rénovation d'Immeubles", "Vos parcs immobiliers réclament des investissements. Améliorez-les pour tripler leur rendement."),
        Pair("💎 Monétisation Optimisée", "Vos régies de sponsors VIP rapportent 25% de bonus supplémentaire aujourd'hui."),
        Pair("🛸 Mégaprojet Spatial", "Votre projet de colonisation martienne a besoin de fonds pour passer au niveau supérieur !"),
        Pair("🎮 Mini-Jeu Actif !", "Le défi interactif du sponsor est disponible. Explosez le score pour gagner le gros lot !"),
        Pair("💵 Taxe sur la Fortune évitée", "Vos conseillers fiscaux ont optimisé votre empire. Profitez d'une exonération temporaire de 2H !"),
        Pair("🌟 Badge d'Honneur déverrouillé", "Un nouveau succès légendaire est prêt à être réclamé dans votre centre de trophées."),
        Pair("🍾 Signature de Contrat VIP", "Un consortium d'investisseurs propose de financer votre prochaine expansion."),
        Pair("⚡ Boost d'Énergie", "Vos tappers automatiques se sont rechargés au maximum. Préparez-vous à cliquer !"),
        Pair("📊 Rapport de Rentabilité", "Bilan comptable : Votre empire a généré un bénéfice record durant les dernières 24 heures !"),
        Pair("🧪 Laboratoire de Fusion", "La fusion nucléaire propre est désormais opérationnelle dans vos laboratoires de recherche."),
        Pair("🐳 Alerte Crypto-Whale !", "Une baleine vient d'acheter massivement du Bitcoin. Préparez-vous à des variations extrêmes !"),
        Pair("🛠️ Maintenance de l'Empire", "Tout est propre et optimisé. Vos managers ont nettoyé les lignes de production."),
        Pair("🏰 Le Sommet des Milliardaires", "Une invitation exclusive vous attend pour rejoindre le club privé des magnats de l'industrie !"),
        Pair("🌍 Expansion Globale", "De nouveaux bureaux internationaux s'ouvrent à Singapour et New York. Prenez la direction !")
    )

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notification de l'Empire Tycoon"
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun sendDailyNotification(context: Context, index: Int) {
        val safeIndex = index.coerceIn(0, templates.lastIndex)
        val (title, text) = templates[safeIndex]

        createNotificationChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.notify(safeIndex + 100, builder.build())
        } catch (e: SecurityException) {
            // Permission not granted yet on Android 13+
        }
    }

    fun scheduleOfflineAlarms(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? android.app.AlarmManager ?: return
        
        // Schedule several future notifications:
        // - 15 seconds (for instant testing when closing the app!)
        // - 2 hours
        // - 6 hours
        // - 24 hours
        // - 48 hours
        // - 72 hours
        val intervals = listOf(
            15L * 1000L to 0,             // 15 seconds (Instant showcase)
            2 * 3600L * 1000L to 1,       // 2 hours
            6 * 3600L * 1000L to 2,       // 6 hours
            24 * 3600L * 1000L to 3,      // 24 hours
            48 * 3600L * 1000L to 4,      // 48 hours
            72 * 3600L * 1000L to 5       // 72 hours
        )

        intervals.forEach { (delayMs, subIndex) ->
            val alarmIntent = Intent(context, TycoonNotificationReceiver::class.java).apply {
                putExtra("notification_index", (0..29).random())
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1000 + subIndex,
                alarmIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            
            val triggerAtMillis = System.currentTimeMillis() + delayMs
            
            try {
                alarmManager.set(
                    android.app.AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } catch (e: Exception) {
                // Safely handle permission constraints
            }
        }
    }
}

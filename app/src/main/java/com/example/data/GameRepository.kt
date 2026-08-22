package com.example.data

import com.example.model.AdNetworkTier
import com.example.model.AuctionLot
import com.example.model.Business
import com.example.model.CorporateTakeover
import com.example.model.CrisisEvent
import com.example.model.DailyMilestoneChest
import com.example.model.DailyMission
import com.example.model.DailyMissionCategory
import com.example.model.DailyMissionType
import com.example.model.Executive
import com.example.model.ExpandedTechNode
import com.example.model.LuxuryAsset
import com.example.model.MiniGameType
import com.example.model.MoneyFormatter
import com.example.model.PlayerAvatar
import com.example.model.RivalBidder
import com.example.model.SponsorOffer
import com.example.model.StockItem
import com.example.model.TechBranch
import kotlin.math.max

object GameRepository {

    fun getDefaultBusinesses(): List<Business> = listOf(
        // ==================== 1. MAGASINS & COMMERCE ====================
        Business(
            id = "biz_coffee",
            name = "Coffee Startup Express",
            category = "Restauration Rapide & Coworking",
            categoryGroup = "MAGASINS",
            level = 1,
            baseCost = 15.0,
            baseRevenuePerSec = 1.5,
            isUnlocked = true,
            managerHired = false,
            managerName = "Chloé Barista Pro",
            managerCost = 100.0,
            managerAvatar = "☕",
            description = "Torréfaction artisanale et cafés branchés pour startupeurs pressés.",
            iconType = "coffee",
            cycleTimeSeconds = 1.0f
        ),
        Business(
            id = "biz_bakery",
            name = "Boulangerie & Pâtisserie Bio",
            category = "Artisanat Gourmand",
            categoryGroup = "MAGASINS",
            level = 0,
            baseCost = 75.0,
            baseRevenuePerSec = 8.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Julien Maître Boulanger",
            managerCost = 600.0,
            managerAvatar = "🥖",
            description = "Pains au levain naturel et viennoiseries fraîches à forte marge.",
            iconType = "store",
            cycleTimeSeconds = 1.5f
        ),
        Business(
            id = "biz_supermarket",
            name = "Supermarché Express 24/7",
            category = "Grande Distribution",
            categoryGroup = "MAGASINS",
            level = 0,
            baseCost = 450.0,
            baseRevenuePerSec = 45.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Marc Chef de Rayon",
            managerCost = 3_500.0,
            managerAvatar = "🛒",
            description = "Magasin de proximité avec caisses automatiques et flux continu.",
            iconType = "store",
            cycleTimeSeconds = 2.0f
        ),
        Business(
            id = "biz_fashion",
            name = "Aura Maison de Luxe",
            category = "Haute Couture & Mode",
            categoryGroup = "MAGASINS",
            level = 0,
            baseCost = 2_800.0,
            baseRevenuePerSec = 240.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Valérie Directrice Artistique",
            managerCost = 22_000.0,
            managerAvatar = "💎",
            description = "Vêtements haute couture et accessoires éco-responsables prestigieux.",
            iconType = "fashion",
            cycleTimeSeconds = 3.0f
        ),
        Business(
            id = "biz_megastore",
            name = "Mégastore Électronique & Gaming",
            category = "Retail High-Tech",
            categoryGroup = "MAGASINS",
            level = 0,
            baseCost = 18_000.0,
            baseRevenuePerSec = 1_400.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Kevin Responsable Tech",
            managerCost = 140_000.0,
            managerAvatar = "📱",
            description = "Espace immense dédié aux consoles, smartphones et matériel gaming.",
            iconType = "store",
            cycleTimeSeconds = 4.0f
        ),
        Business(
            id = "biz_hypercar",
            name = "Concessionnaire Hypercars VIP",
            category = "Automobile d'Exception",
            categoryGroup = "MAGASINS",
            level = 0,
            baseCost = 120_000.0,
            baseRevenuePerSec = 8_500.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Don Enzo Concessionnaire",
            managerCost = 950_000.0,
            managerAvatar = "🏎️",
            description = "Showroom exclusif de supercars et véhicules de collection sur mesure.",
            iconType = "car",
            cycleTimeSeconds = 5.0f
        ),

        // ==================== 2. BANQUE & FINANCE ====================
        Business(
            id = "biz_exchange",
            name = "Bureau de Change & Crypto-ATM",
            category = "Monétique & Devises",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 4_500.0,
            baseRevenuePerSec = 350.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Sam Trader Devises",
            managerCost = 35_000.0,
            managerAvatar = "🪙",
            description = "Conversion instantanée de devises internationales et bornes crypto.",
            iconType = "crypto",
            cycleTimeSeconds = 2.5f
        ),
        Business(
            id = "biz_neobank",
            name = "Nova Neo-Bank & Cartes Gold",
            category = "Fintech & Services Digitaux",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 32_000.0,
            baseRevenuePerSec = 2_200.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Darius Ingénieur Fintech",
            managerCost = 240_000.0,
            managerAvatar = "💳",
            description = "Application bancaire mobile sans frais et cartes de crédit titanium.",
            iconType = "credit_card",
            cycleTimeSeconds = 4.0f
        ),
        Business(
            id = "biz_quant",
            name = "Société de Courtage & Trading Quant",
            category = "Marchés Financiers",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 250_000.0,
            baseRevenuePerSec = 16_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Sarah Algorithmic Trader",
            managerCost = 1_800_000.0,
            managerAvatar = "📈",
            description = "Algorithmes de trading haute fréquence exploitant les micro-arbitrages.",
            iconType = "trading",
            cycleTimeSeconds = 5.5f
        ),
        Business(
            id = "biz_private_equity",
            name = "Fonds de Private Equity & VC",
            category = "Capital-Investissement",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 2_000_000.0,
            baseRevenuePerSec = 115_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Guillaume Associé Gérant",
            managerCost = 15_000_000.0,
            managerAvatar = "💼",
            description = "Rachats stratégiques d'entreprises et prises de participation majoritaires.",
            iconType = "bank",
            cycleTimeSeconds = 7.0f
        ),
        Business(
            id = "biz_private_bank",
            name = "Banque Privée & Gestion de Fortune",
            category = "Gestion Privée Internationale",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 18_000_000.0,
            baseRevenuePerSec = 950_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Lord Sterling Banquier",
            managerCost = 130_000_000.0,
            managerAvatar = "🏰",
            description = "Gestion discrète de patrimoines pour oligarques et grandes familles.",
            iconType = "bank",
            cycleTimeSeconds = 10.0f
        ),
        Business(
            id = "biz_central_vault",
            name = "Chambre Forte d'Or & Réserve",
            category = "Réserve de Valeur Souveraine",
            categoryGroup = "BANQUE",
            level = 0,
            baseCost = 150_000_000.0,
            baseRevenuePerSec = 7_200_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Commandant Steel Gardien",
            managerCost = 1_100_000_000.0,
            managerAvatar = "🏛️",
            description = "Coffres-forts ultra-sécurisés abritant les lingots et obligations souveraines.",
            iconType = "bank",
            cycleTimeSeconds = 15.0f
        ),

        // ==================== 3. INDUSTRIE & LOGISTIQUE ====================
        Business(
            id = "biz_shipping",
            name = "Compagnie de Fret & Logistique",
            category = "Transport Routier & Aérien",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 15_000.0,
            baseRevenuePerSec = 1_100.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Karim Directeur Flotte",
            managerCost = 110_000.0,
            managerAvatar = "🚚",
            description = "Flotte de camions poids lourds et avions cargo pour liaisons intercités.",
            iconType = "shipping",
            cycleTimeSeconds = 3.5f
        ),
        Business(
            id = "biz_warehouse",
            name = "Méga-Entrepôt Robotisé",
            category = "Logistique Intelligente",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 140_000.0,
            baseRevenuePerSec = 9_200.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Ingénieur Lucas Robotique",
            managerCost = 1_000_000.0,
            managerAvatar = "📦",
            description = "Hub de tri automatisé par drones et convoyeurs d'intelligence artificielle.",
            iconType = "industry",
            cycleTimeSeconds = 5.0f
        ),
        Business(
            id = "biz_foundry",
            name = "Fonderie & Complexe Métallurgique",
            category = "Industrie Lourde",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 1_200_000.0,
            baseRevenuePerSec = 72_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Boris Maître de Forge",
            managerCost = 9_000_000.0,
            managerAvatar = "⚙️",
            description = "Production d'acier spécial et d'alliages de titane pour l'aéronautique.",
            iconType = "factory",
            cycleTimeSeconds = 7.0f
        ),
        Business(
            id = "biz_clean_energy",
            name = "Centrale Électrique Solaire & Éolienne",
            category = "Énergie Renouvelable",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 10_000_000.0,
            baseRevenuePerSec = 550_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Dr. Laura Ingénieure Climat",
            managerCost = 75_000_000.0,
            managerAvatar = "⚡",
            description = "Champs de panneaux solaires bifaciaux et parcs d'éoliennes offshore géants.",
            iconType = "energy",
            cycleTimeSeconds = 10.0f
        ),
        Business(
            id = "biz_gigafactory",
            name = "Gigafactory de Véhicules Électriques",
            category = "Manufacture Haute Cadence",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 85_000_000.0,
            baseRevenuePerSec = 4_200_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Hans Superviseur d'Usine",
            managerCost = 600_000_000.0,
            managerAvatar = "🔋",
            description = "Lignes d'assemblage automatisées produisant des millions de cellules de batterie.",
            iconType = "factory",
            cycleTimeSeconds = 14.0f
        ),
        Business(
            id = "biz_pharma_bio",
            name = "Complexe Pharma & Bio-Synthèse",
            category = "Biotechnologies & Santé",
            categoryGroup = "INDUSTRIE",
            level = 0,
            baseCost = 750_000_000.0,
            baseRevenuePerSec = 32_000_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Professeur Estelle Nobel",
            managerCost = 5_000_000_000.0,
            managerAvatar = "🧪",
            description = "Synthèse moléculaire automatisée de traitements de pointe et biomédicaments.",
            iconType = "industry",
            cycleTimeSeconds = 20.0f
        ),

        // ==================== 4. TECH & MÉDIAS ====================
        Business(
            id = "biz_tech",
            name = "Nexus AI & Apps Mobile Studio",
            category = "Logiciels & Algorithmes",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 120.0,
            baseRevenuePerSec = 14.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Alex Lead Dev",
            managerCost = 900.0,
            managerAvatar = "💻",
            description = "Applications mobiles virales et micro-services d'IA en SaaS récurrent.",
            iconType = "tech",
            cycleTimeSeconds = 1.5f
        ),
        Business(
            id = "biz_media",
            name = "Vortex Streaming & Médias Empire",
            category = "Divertissement & Cinéma",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 9_500.0,
            baseRevenuePerSec = 750.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Camille Productrice Exécutive",
            managerCost = 75_000.0,
            managerAvatar = "🎬",
            description = "Plateforme mondiale de séries, blockbusters et retransmissions e-sport.",
            iconType = "media",
            cycleTimeSeconds = 3.5f
        ),
        Business(
            id = "biz_cloud_datacenter",
            name = "Datacenter Cloud & Super-Serveurs",
            category = "Infrastructures Réseau",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 450_000.0,
            baseRevenuePerSec = 28_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Hugo Architecte Cloud",
            managerCost = 3_200_000.0,
            managerAvatar = "🌐",
            description = "Racks de serveurs refroidis par immersion hébergeant les modèles d'IA mondiaux.",
            iconType = "tech",
            cycleTimeSeconds = 6.0f
        ),
        Business(
            id = "biz_gaming_studio",
            name = "Studio de Jeux Vidéo AAA & VR",
            category = "Jeux Vidéo & Métavers",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 4_200_000.0,
            baseRevenuePerSec = 220_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Maxime Game Director",
            managerCost = 30_000_000.0,
            managerAvatar = "🎮",
            description = "Développement de mondes ouverts photoréalistes et franchises légendaires.",
            iconType = "tech",
            cycleTimeSeconds = 8.0f
        ),
        Business(
            id = "biz_space",
            name = "Astra Orbital & Satellites",
            category = "Aérospatial & Télécoms",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 38_000_000.0,
            baseRevenuePerSec = 1_800_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "Dr. Hélène Astro-Physicienne",
            managerCost = 280_000_000.0,
            managerAvatar = "🚀",
            description = "Constellation de microsatellites assurant l'internet mondial à haut débit.",
            iconType = "space",
            cycleTimeSeconds = 12.0f
        ),
        Business(
            id = "biz_quantum",
            name = "A.I. Supercalculateur Singularity",
            category = "Deep Tech & Conscience IA",
            categoryGroup = "TECH",
            level = 0,
            baseCost = 320_000_000.0,
            baseRevenuePerSec = 14_000_000.0,
            isUnlocked = false,
            managerHired = false,
            managerName = "C.O.R.E. Synthétique V9",
            managerCost = 2_500_000_000.0,
            managerAvatar = "🤖",
            description = "Réseau neuronal quantique calculant les flux économiques globaux.",
            iconType = "quantum",
            cycleTimeSeconds = 18.0f
        )
    )

    fun getDefaultStocks(): List<StockItem> = listOf(
        StockItem(
            ticker = "TECH-AI",
            name = "CyberCore Systems",
            category = "Tech & GPU",
            price = 145.0,
            previousPrice = 140.0,
            history = listOf(130f, 135f, 142f, 138f, 145f),
            volatility = 0.08f
        ),
        StockItem(
            ticker = "LUX-GL",
            name = "Lumière Prestige Corp",
            category = "Luxe Mondial",
            price = 320.0,
            previousPrice = 325.0,
            history = listOf(310f, 318f, 325f, 322f, 320f),
            volatility = 0.04f
        ),
        StockItem(
            ticker = "NOVA-COIN",
            name = "Nova Decentral Token",
            category = "Crypto Actif",
            price = 42.5,
            previousPrice = 38.0,
            history = listOf(28f, 35f, 44f, 36f, 42.5f),
            volatility = 0.16f
        ),
        StockItem(
            ticker = "ASTRA-SP",
            name = "AeroSpace Dynamics",
            category = "Spatial & Défense",
            price = 580.0,
            previousPrice = 570.0,
            history = listOf(540f, 555f, 560f, 570f, 580f),
            volatility = 0.05f
        ),
        StockItem(
            ticker = "GREEN-NRG",
            name = "Solaris Fusion Clean",
            category = "Énergie Verte",
            price = 85.0,
            previousPrice = 86.0,
            history = listOf(78f, 82f, 89f, 86f, 85f),
            volatility = 0.07f
        ),
        StockItem(
            ticker = "BTC-TYC",
            name = "Bitcoin Tycoon Gold",
            category = "Crypto Volatile",
            price = 62000.0,
            previousPrice = 61500.0,
            history = listOf(58000f, 59500f, 63000f, 60500f, 62000f),
            volatility = 0.22f
        ),
        StockItem(
            ticker = "ETH-TYC",
            name = "Ethereum Smart Chain",
            category = "Crypto Volatile",
            price = 3400.0,
            previousPrice = 3450.0,
            history = listOf(3100f, 3250f, 3500f, 3350f, 3400f),
            volatility = 0.28f
        ),
        StockItem(
            ticker = "DOGE-MEME",
            name = "Dogecoin Meme Frenzy",
            category = "Crypto Volatile",
            price = 0.12,
            previousPrice = 0.11,
            history = listOf(0.08f, 0.15f, 0.21f, 0.09f, 0.12f),
            volatility = 0.48f
        ),
        StockItem(
            ticker = "SOL-ELEC",
            name = "Solana Hyper Network",
            category = "Crypto Volatile",
            price = 145.0,
            previousPrice = 148.0,
            history = listOf(110f, 130f, 155f, 140f, 145f),
            volatility = 0.35f
        ),
        StockItem(
            ticker = "BIO-GEN",
            name = "Genomix Therapeutics",
            category = "Biotech Innovation",
            price = 210.0,
            previousPrice = 208.0,
            history = listOf(190f, 195f, 220f, 205f, 210f),
            volatility = 0.12f
        ),
        StockItem(
            ticker = "COAL-OUT",
            name = "Carbon Retrograde",
            category = "Industrie Lourde",
            price = 45.0,
            previousPrice = 45.2,
            history = listOf(42f, 43f, 46f, 44f, 45f),
            volatility = 0.06f
        ),
        StockItem(
            ticker = "SND-BOX",
            name = "Sandbox Real Estate",
            category = "Métavers & Gaming",
            price = 1.8,
            previousPrice = 1.9,
            history = listOf(1.2f, 1.5f, 2.1f, 1.7f, 1.8f),
            volatility = 0.32f
        ),
        StockItem(
            ticker = "FOOD-TYC",
            name = "Silo Agri-Global",
            category = "Consommation",
            price = 95.0,
            previousPrice = 94.5,
            history = listOf(90f, 92f, 96f, 94f, 95f),
            volatility = 0.03f
        ),
        StockItem(
            ticker = "CRU-OIL",
            name = "Oryx Black Gold",
            category = "Pétrole & Énergie",
            price = 75.0,
            previousPrice = 76.5,
            history = listOf(70f, 73f, 79f, 74f, 75f),
            volatility = 0.08f
        ),
        StockItem(
            ticker = "LITE-COIN",
            name = "Litecoin Silver Classic",
            category = "Crypto Volatile",
            price = 85.0,
            previousPrice = 83.0,
            history = listOf(75f, 80f, 92f, 82f, 85f),
            volatility = 0.18f
        ),
        StockItem(
            ticker = "MEME-PEPE",
            name = "Pepe Frog Premium",
            category = "Crypto Volatile",
            price = 0.005,
            previousPrice = 0.004,
            history = listOf(0.002f, 0.007f, 0.012f, 0.003f, 0.005f),
            volatility = 0.65f
        )
    )

    fun getDefaultExecutives(): List<Executive> = listOf(
        Executive(
            id = "exec_cto",
            name = "Elena Vance",
            role = "Directrice des Technologies (CTO)",
            cost = 5_000.0,
            perkTitle = "Algorithmes Prédictifs",
            perkDescription = "+40% de revenus passifs sur toutes les entreprises de l'empire.",
            passiveRevenueBoost = 0.40,
            emoji = "👩‍💻"
        ),
        Executive(
            id = "exec_cmo",
            name = "Sarah Brand",
            role = "Directrice Marketing & Ads (CMO)",
            cost = 25_000.0,
            perkTitle = "Monétisation Virale",
            perkDescription = "+100% de valeur sur les bannières publicitaires et récompenses sponsors.",
            adCpmBoost = 1.00,
            emoji = "🎯"
        ),
        Executive(
            id = "exec_cfo",
            name = "Victoria Sterling",
            role = "Directrice Financière (CFO)",
            cost = 150_000.0,
            perkTitle = "Optimisation Fiscale & Cashflow",
            perkDescription = "+60% de revenus globaux et réduction de 20% des coûts d'amélioration.",
            passiveRevenueBoost = 0.60,
            emoji = "📈"
        ),
        Executive(
            id = "exec_coo",
            name = "Maya Lin",
            role = "Directrice des Opérations (COO)",
            cost = 800_000.0,
            perkTitle = "Négociatrice Foudroyante",
            perkDescription = "+300% de puissance de frappe (Action Tap) et déclenchement du Frenzy x10 accéléré.",
            clickPowerBoost = 3.00,
            emoji = "⚡"
        )
    )

    fun getDefaultAdNetworks(): List<AdNetworkTier> = listOf(
        AdNetworkTier(
            id = "ad_banner_basic",
            name = "Sponsoring Bannières Corporatives",
            cpmRate = 4.5,
            unlockCost = 50.0,
            isUnlocked = true,
            description = "Diffuse des bannières interactives élégantes sur les applications de nos filiales.",
            autoAdIncomePerSec = 2.0
        ),
        AdNetworkTier(
            id = "ad_reward_video",
            name = "Campagnes Vidéos Institutionnelles",
            cpmRate = 28.0,
            unlockCost = 1_200.0,
            isUnlocked = false,
            description = "Intègre des spots promotionnels à haute valeur ajoutée offrant des primes d'engagement.",
            autoAdIncomePerSec = 35.0
        ),
        AdNetworkTier(
            id = "ad_interactive_3d",
            name = "Partenariats Événementiels 3D",
            cpmRate = 120.0,
            unlockCost = 30_000.0,
            isUnlocked = false,
            description = "Commandite des démonstrations et simulations interactives lors de sommets financiers.",
            autoAdIncomePerSec = 580.0
        ),
        AdNetworkTier(
            id = "ad_ai_hyper_target",
            name = "Consortium IA de Sponsoring Prédictif",
            cpmRate = 750.0,
            unlockCost = 650_000.0,
            isUnlocked = false,
            description = "Ciblage algorithmique en temps réel maximisant les retours sur investissement de nos mécènes.",
            autoAdIncomePerSec = 8_900.0
        )
    )

    fun getSponsorOffersPool(): List<SponsorOffer> = listOf(
        SponsorOffer(
            id = "sp_gold_rush",
            brandName = "Golden Venture Capital",
            title = "Pluie d'Or Interactive !",
            description = "Attrape les sacs de billets qui tombent en 5 secondes pour rafler un bonus jackpot !",
            rewardCashFactor = 50.0,
            rewardMultiplier = 3.0,
            multiplierDurationSec = 45,
            miniGameType = MiniGameType.GOLD_RUSH_CATCH,
            badge = "JACKPOT FLASH",
            tagColorHex = 0xFFFFD700
        ),
        SponsorOffer(
            id = "sp_deal_pitch",
            brandName = "Apex Private Equity",
            title = "Négociation de Contrat Express",
            description = "Verrouille le deal au bon timing sur la jauge pour doubler tes investissements !",
            rewardCashFactor = 75.0,
            rewardMultiplier = 4.0,
            multiplierDurationSec = 60,
            miniGameType = MiniGameType.DEAL_PITCH_SLIDER,
            badge = "DEAL DU SIÈCLE",
            tagColorHex = 0xFF00E5FF
        ),
        SponsorOffer(
            id = "sp_crypto_pump",
            brandName = "Nova Blockchain Network",
            title = "Pump & Mine Crypto Challenge",
            description = "Tappe à toute vitesse pour miner des blocs de NovaCoins et faire exploser la crypto !",
            rewardCashFactor = 100.0,
            rewardMultiplier = 5.0,
            multiplierDurationSec = 40,
            miniGameType = MiniGameType.CRYPTO_FAST_PUMP,
            badge = "CRYPTO SURGE",
            tagColorHex = 0xFF00E676
        ),
        SponsorOffer(
            id = "sp_lucky_wheel",
            brandName = "Fortune Lux Megacorp",
            title = "Roue de la Fortune VIP",
            description = "Tourne la roue des sponsors pour remporter du cash instantané ou un boost x10 !",
            rewardCashFactor = 120.0,
            rewardMultiplier = 6.0,
            multiplierDurationSec = 60,
            miniGameType = MiniGameType.LUCKY_VIP_SPIN,
            badge = "ROUE VIP",
            tagColorHex = 0xFFFF4081
        ),
        SponsorOffer(
            id = "sp_viral_campaign",
            brandName = "Vortex Media Buzz",
            title = "Campagne Blitz Réseaux Sociaux",
            description = "Éclate les bulles virales à l'écran pour déclencher une tempête médiatique !",
            rewardCashFactor = 60.0,
            rewardMultiplier = 3.5,
            multiplierDurationSec = 50,
            miniGameType = MiniGameType.VIRAL_AD_CAMPAIGN,
            badge = "BUZZ VIRAL",
            tagColorHex = 0xFFB388FF
        )
    )

    fun getDefaultAvatars(): List<PlayerAvatar> = listOf(
        PlayerAvatar(id = 0, emoji = "🎩", title = "Tycoon Traditionnel", unlockedByPrestige = 0),
        PlayerAvatar(id = 1, emoji = "💼", title = "Entrepreneur Moderne", unlockedByPrestige = 0),
        PlayerAvatar(id = 2, emoji = "🚀", title = "Pionnier Tech & Spatial", unlockedByPrestige = 0),
        PlayerAvatar(id = 3, emoji = "💎", title = "Magnat de la Haute Joaillerie", unlockedByPrestige = 0),
        PlayerAvatar(id = 4, emoji = "⚡", title = "Trader Haute Fréquence", unlockedByPrestige = 1),
        PlayerAvatar(id = 5, emoji = "🦁", title = "Loup de Wall Street", unlockedByPrestige = 2),
        PlayerAvatar(id = 6, emoji = "👑", title = "Monarque de la Finance", unlockedByPrestige = 3),
        PlayerAvatar(id = 7, emoji = "🪐", title = "Empereur Galactique", unlockedByPrestige = 5)
    )

    fun getDefaultAchievements(): List<com.example.model.Achievement> = listOf(
        com.example.model.Achievement(
            id = "ach_taps_10",
            title = "Premier Pas de Négociation",
            description = "Signe 10 contrats en tapant sur le bouton d'action.",
            iconEmoji = "🤝",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 250.0,
            rewardLabel = "+$250",
            targetValue = 10L
        ),
        com.example.model.Achievement(
            id = "ach_taps_100",
            title = "Signataire Compulsif",
            description = "Atteins un total de 100 taps sur le deal core.",
            iconEmoji = "⚡",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 2_500.0,
            rewardLabel = "+$2.5K",
            targetValue = 100L
        ),
        com.example.model.Achievement(
            id = "ach_combo_20",
            title = "Maître du Combo",
            description = "Atteins un enchaînement combo de 20x en tapant vite.",
            iconEmoji = "🔥",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 5_000.0,
            rewardLabel = "+$5K",
            targetValue = 20L
        ),
        com.example.model.Achievement(
            id = "ach_cash_10k",
            title = "Premiers Dix Mille",
            description = "Cumule au total 10 000 $ de cash généré.",
            iconEmoji = "💵",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 3_000.0,
            rewardLabel = "+$3K",
            targetValue = 10_000L
        ),
        com.example.model.Achievement(
            id = "ach_cash_1m",
            title = "Club des Millionnaires",
            description = "Génère un total cumulé de 1 000 000 $.",
            iconEmoji = "💰",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 150_000.0,
            rewardLabel = "+$150K",
            targetValue = 1_000_000L
        ),
        com.example.model.Achievement(
            id = "ach_cash_1b",
            title = "Club des Milliardaires",
            description = "Atteins le statut de milliardaire (1 Milliard $ cumulé).",
            iconEmoji = "🏆",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 100_000_000.0,
            rewardLabel = "+$100M",
            targetValue = 1_000_000_000L
        ),
        com.example.model.Achievement(
            id = "ach_stocks_10",
            title = "Trader Actif",
            description = "Réalise 10 transactions d'achat ou vente d'actions.",
            iconEmoji = "📊",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000.0,
            rewardLabel = "+$10K",
            targetValue = 10L
        ),
        com.example.model.Achievement(
            id = "ach_ads_unlocked",
            title = "Régie Multicanale",
            description = "Déploie au moins 2 régies publicitaires.",
            iconEmoji = "📺",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 25_000.0,
            rewardLabel = "+$25K",
            targetValue = 2L
        ),
        com.example.model.Achievement(
            id = "ach_sponsors_5",
            title = "Star des Marques",
            description = "Remporte 5 mini-jeux sponsorisés avec succès.",
            iconEmoji = "⭐",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 50_000.0,
            rewardLabel = "+$50K",
            targetValue = 5L
        ),
        com.example.model.Achievement(
            id = "ach_crisis_3",
            title = "Gestionnaire de Crise Émérite",
            description = "Prends 3 décisions stratégiques victorieuses lors de crises.",
            iconEmoji = "🛡️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 40_000.0,
            rewardLabel = "+$40K",
            targetValue = 3L
        ),
        com.example.model.Achievement(
            id = "ach_prestige_1",
            title = "Vente d'Empire Prestige",
            description = "Réalise ton premier Prestige pour vendre ton empire financier.",
            iconEmoji = "🎖️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 250_000.0,
            rewardLabel = "+$250K",
            targetValue = 1L
        ),
        com.example.model.Achievement(
            id = "ach_cash_1t",
            title = "Maître de la Galaxie",
            description = "Cumule un total colossal de 1 Trillion $ sur la durée de vie de l'empire.",
            iconEmoji = "🌌",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 500_000_000_000.0,
            rewardLabel = "+$500B",
            targetValue = 1_000_000_000_000L
        ),
        com.example.model.Achievement(
            id = "ach_prestige_5",
            title = "Magnat Multiversel",
            description = "Atteins le niveau 5 de Prestige.",
            iconEmoji = "👑",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 2_000_000_000.0,
            rewardLabel = "+$2B",
            targetValue = 5L
        ),
        com.example.model.Achievement(
            id = "ach_stocks_50",
            title = "Loup de Wall Street",
            description = "Réalise un total de 50 transactions d'achat ou de vente d'actions.",
            iconEmoji = "📈",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000_000.0,
            rewardLabel = "+$10M",
            targetValue = 50L
        ),
        com.example.model.Achievement(
            id = "ach_taps_1000",
            title = "Surchargé de Taps",
            description = "Atteins 1000 signatures de contrats par tap.",
            iconEmoji = "⚡",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 50_000_000.0,
            rewardLabel = "+$50M",
            targetValue = 1000L
        ),
        com.example.model.Achievement(
            id = "ach_taps_5000",
            title = "Légende du Clic Direct",
            description = "Signe un total de 5 000 contrats par tap.",
            iconEmoji = "🖱️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 500_000_000.0,
            rewardLabel = "+$500M",
            targetValue = 5000L
        ),
        com.example.model.Achievement(
            id = "ach_combo_50",
            title = "Rythme Effréné",
            description = "Atteins un combo multiplicateur exceptionnel de 50x.",
            iconEmoji = "☄️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 1_000_000.0,
            rewardLabel = "+$1M",
            targetValue = 50L
        ),
        com.example.model.Achievement(
            id = "ach_combo_100",
            title = "Doigts de Lumière",
            description = "Atteins un combo légendaire de 100x !",
            iconEmoji = "✨",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 100_000_000.0,
            rewardLabel = "+$100M",
            targetValue = 100L
        ),
        com.example.model.Achievement(
            id = "ach_prestige_10",
            title = "Empereur du Phénix",
            description = "Atteins le Prestige niveau 10.",
            iconEmoji = "🔥",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 100_000_000_000.0,
            rewardLabel = "+$100B",
            targetValue = 10L
        ),
        com.example.model.Achievement(
            id = "ach_stocks_100",
            title = "Loup Légendaire de Wall Street",
            description = "Effectue 100 transactions d'actions.",
            iconEmoji = "💹",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 250_000_000.0,
            rewardLabel = "+$250M",
            targetValue = 100L
        ),
        com.example.model.Achievement(
            id = "ach_sponsors_20",
            title = "Ambassadeur Global",
            description = "Remporte 20 mini-jeux de sponsors.",
            iconEmoji = "📢",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 50_000_000.0,
            rewardLabel = "+$50M",
            targetValue = 20L
        ),
        com.example.model.Achievement(
            id = "ach_crisis_10",
            title = "Négociateur de Paix",
            description = "Résous 10 crises d'entreprise avec succès.",
            iconEmoji = "🌍",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 25_000_000.0,
            rewardLabel = "+$25M",
            targetValue = 10L
        ),
        com.example.model.Achievement(
            id = "ach_managers_1",
            title = "Premier Manager Recruté",
            description = "Embauche ton tout premier manager d'entreprise.",
            iconEmoji = "👔",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 5_000.0,
            rewardLabel = "+$5K",
            targetValue = 1L
        ),
        com.example.model.Achievement(
            id = "ach_managers_6",
            title = "Ressources Humaines Pro",
            description = "Embauche 6 managers d'entreprise simultanément.",
            iconEmoji = "🧑‍🤝‍🧑",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 5_000_000.0,
            rewardLabel = "+$5M",
            targetValue = 6L
        ),
        com.example.model.Achievement(
            id = "ach_managers_12",
            title = "Directoire Planétaire",
            description = "Embauche les 12 managers pour automatiser toutes tes entreprises.",
            iconEmoji = "🌐",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 500_000_000.0,
            rewardLabel = "+$500M",
            targetValue = 12L
        ),
        com.example.model.Achievement(
            id = "ach_exec_1",
            title = "Recrutement Exécutif",
            description = "Embauche ton premier cadre dirigeant pour bénéficier de bonus passifs.",
            iconEmoji = "👩‍💼",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000_000.0,
            rewardLabel = "+$10M",
            targetValue = 1L
        ),
        com.example.model.Achievement(
            id = "ach_exec_3",
            title = "Conseil d'Administration Complet",
            description = "Embauche au moins 3 cadres dirigeants dans ton profil.",
            iconEmoji = "🏛️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 500_000_000.0,
            rewardLabel = "+$500M",
            targetValue = 3L
        ),
        com.example.model.Achievement(
            id = "ach_biz_coffee_100",
            title = "Empire de la Caféine",
            description = "Améliore le Coffee Startup Express au niveau 100.",
            iconEmoji = "☕",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 1_000_000.0,
            rewardLabel = "+$1M",
            targetValue = 100L
        ),
        com.example.model.Achievement(
            id = "ach_biz_tech_50",
            title = "Leader de la Tech",
            description = "Améliore Nexus AI Studio au niveau 50.",
            iconEmoji = "💻",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000_000.0,
            rewardLabel = "+$10M",
            targetValue = 50L
        ),
        com.example.model.Achievement(
            id = "ach_biz_quantum_10",
            title = "Maître Quantique",
            description = "Améliore le Supercalculateur Singularity au niveau 10.",
            iconEmoji = "🖲️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000_000_000.0,
            rewardLabel = "+$10B",
            targetValue = 10L
        ),
        com.example.model.Achievement(
            id = "ach_tech_5",
            title = "R&D Avancée",
            description = "Déverrouille 5 améliorations technologiques dans la boutique.",
            iconEmoji = "🧬",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 50_000_000.0,
            rewardLabel = "+$50M",
            targetValue = 5L
        ),
        com.example.model.Achievement(
            id = "ach_tech_all",
            title = "Singularité Technologique",
            description = "Déverrouille toutes les améliorations technologiques.",
            iconEmoji = "🦾",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 5_000_000_000.0,
            rewardLabel = "+$5B",
            targetValue = 9L
        ),
        com.example.model.Achievement(
            id = "ach_mega_1",
            title = "Grand Architecte",
            description = "Construis la première étape d'un mégaprojet.",
            iconEmoji = "🏗️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 250_000_000.0,
            rewardLabel = "+$250M",
            targetValue = 1L
        ),
        com.example.model.Achievement(
            id = "ach_mega_max",
            title = "Merveille du Monde",
            description = "Termine entièrement (Étape 5) n'importe quel mégaprojet.",
            iconEmoji = "🏟️",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 10_000_000_000.0,
            rewardLabel = "+$10B",
            targetValue = 5L
        ),
        com.example.model.Achievement(
            id = "ach_ads_max",
            title = "Réseau Médias Absolu",
            description = "Déverrouille toutes les régies publicitaires.",
            iconEmoji = "📣",
            rewardType = com.example.model.AchievementRewardType.CASH,
            rewardValue = 100_000_000.0,
            rewardLabel = "+$100M",
            targetValue = 5L
        )
    )

    fun getRandomCrisis(): CrisisEvent = listOf(
        CrisisEvent(
            id = "crisis_server_crash",
            title = "Panne de Serveurs Cloud Mondiale !",
            description = "Le studio tech subit une attaque DDoS massive. Comment réagis-tu ?",
            choiceA = "Déployer le pare-feu IA d'urgence (Coûte 5% de trésorerie, +Boost x3 après)",
            choiceB = "Contenir manuellement sans dépenser (Risque de baisse de revenus 15s)",
            timeLimitSec = 12,
            rewardMultiplier = 3.0,
            costRatio = 0.05
        ),
        CrisisEvent(
            id = "crisis_hostile_takeover",
            title = "Tentative d'OPA Hostile d'un Concurrent !",
            description = "Un conglomérat tente de racheter des parts de ton entreprise en sous-main.",
            choiceA = "Lancer une contre-attaque médiatique agressive (Jackpot si réussi)",
            choiceB = "Activer la pilule empoisonnée de sécurité financière",
            timeLimitSec = 10,
            rewardMultiplier = 4.0,
            costRatio = 0.08
        ),
        CrisisEvent(
            id = "crisis_celebrity_endorsement",
            title = "Une Célébrité Planétaire porte ta Marque !",
            description = "Un post Instagram devient viral avec 50 millions de vues en 10 minutes !",
            choiceA = "Signer un contrat exclusif de sponsoring immédiat (Gros boost)",
            choiceB = "Lancer une série limitée de produits collector",
            timeLimitSec = 10,
            rewardMultiplier = 3.5,
            costRatio = 0.02
        )
    ).random()

    fun getDefaultDailyRewards(): List<com.example.model.DailyLoginReward> = listOf(
        com.example.model.DailyLoginReward(1, "Prime de Bienvenue", "+$5,000", "🎁", 5_000.0, isCurrentDay = true),
        com.example.model.DailyLoginReward(2, "Investisseur en Herbe", "+$25,000", "💼", 25_000.0, 2.0, 60),
        com.example.model.DailyLoginReward(3, "Bourse en Folie", "+$100,000", "📈", 100_000.0),
        com.example.model.DailyLoginReward(4, "Frenzy Éclair", "+$500,000 + Boost x5", "🔥", 500_000.0, 5.0, 45),
        com.example.model.DailyLoginReward(5, "Coffre R&D", "+$2.5M Cash", "🔬", 2_500_000.0),
        com.example.model.DailyLoginReward(6, "Valise Diamant VIP", "+$10M Cash", "💎", 10_000_000.0),
        com.example.model.DailyLoginReward(7, "Couronne du Tycoon", "+$50M + Couronne Or", "👑", 50_000_000.0, 10.0, 120)
    )

    fun getDailyRewardsForStreak(streakDays: Int, claimedDays: Set<Int> = emptySet()): List<com.example.model.DailyLoginReward> {
        val currentDayInCycle = ((streakDays.coerceAtLeast(1) - 1) % 7) + 1
        return getDefaultDailyRewards().map { reward ->
            reward.copy(
                isCurrentDay = (reward.dayNumber == currentDayInCycle),
                isClaimed = claimedDays.contains(reward.dayNumber)
            )
        }
    }

    fun getDefaultMilestoneChests(prestigeLevel: Int = 0): List<DailyMilestoneChest> {
        val multiplier = 1.0 + (prestigeLevel * 0.5)
        return listOf(
            DailyMilestoneChest(
                milestoneTarget = 2,
                title = "Coffre Bronze de Débutant",
                iconEmoji = "🥉",
                rewardCash = 50_000.0 * multiplier,
                rewardBoostMultiplier = 1.5,
                rewardBoostDurationSec = 60,
                rewardLabel = "+${MoneyFormatter.format(50_000.0 * multiplier)} + Boost x1.5"
            ),
            DailyMilestoneChest(
                milestoneTarget = 4,
                title = "Coffre Argent de l'Investisseur",
                iconEmoji = "🥈",
                rewardCash = 250_000.0 * multiplier,
                rewardBoostMultiplier = 2.0,
                rewardBoostDurationSec = 90,
                rewardLabel = "+${MoneyFormatter.format(250_000.0 * multiplier)} + Boost x2.0"
            ),
            DailyMilestoneChest(
                milestoneTarget = 6,
                title = "Coffre Or du Grand Magnat",
                iconEmoji = "👑",
                rewardCash = 1_500_000.0 * multiplier,
                rewardBoostMultiplier = 3.0,
                rewardBoostDurationSec = 120,
                rewardLabel = "+${MoneyFormatter.format(1_500_000.0 * multiplier)} + Boost x3.0"
            )
        )
    }

    fun generateDailyMissions(netWorth: Double = 1000.0, prestigeLevel: Int = 0): List<DailyMission> {
        val mult = max(1.0, 1.0 + (prestigeLevel * 0.75))
        val baseScale = max(1.0, netWorth / 5000.0).coerceAtMost(100_000.0)

        val tapTarget = (50L..100L).random()
        val tapReward = max(10_000.0, 5_000.0 * baseScale * mult)

        val cashTarget = (max(5_000.0, netWorth * 0.3)).toLong()
        val cashReward = max(25_000.0, cashTarget * 0.5 * mult)

        val upgradeTarget = (3L..8L).random()
        val upgradeReward = max(20_000.0, 10_000.0 * baseScale * mult)

        val stockTarget = (2L..5L).random()
        val stockReward = max(35_000.0, 15_000.0 * baseScale * mult)

        val sponsorTarget = (1L..3L).random()
        val sponsorReward = max(50_000.0, 20_000.0 * baseScale * mult)

        val challengeTarget = (1L..2L).random()
        val challengeReward = max(60_000.0, 30_000.0 * baseScale * mult)

        val comboTarget = (15L..25L).random()
        val comboReward = max(35_000.0, 15_000.0 * baseScale * mult)

        val crisisTarget = 1L
        val crisisReward = max(50_000.0, 22_000.0 * baseScale * mult)

        return listOf(
            DailyMission(
                id = "mission_tap_deals",
                type = DailyMissionType.TAP_CONTRACTS,
                title = "Signataire Acharné",
                description = "Réalise $tapTarget signatures de contrats rapides sur le bouton d'action.",
                iconEmoji = "⚡",
                category = DailyMissionCategory.NEGOTIATION,
                currentProgress = 0,
                targetProgress = tapTarget,
                rewardCash = tapReward,
                rewardBoostMultiplier = 1.5,
                rewardBoostDurationSec = 45,
                rewardLabel = "+${MoneyFormatter.format(tapReward)}",
                targetTab = 0
            ),
            DailyMission(
                id = "mission_earn_cash",
                type = DailyMissionType.EARN_CASH,
                title = "Flux de Trésorerie Journalier",
                description = "Génère un total de ${MoneyFormatter.format(cashTarget.toDouble())} aujourd'hui.",
                iconEmoji = "💵",
                category = DailyMissionCategory.MANAGEMENT,
                currentProgress = 0,
                targetProgress = cashTarget,
                rewardCash = cashReward,
                rewardLabel = "+${MoneyFormatter.format(cashReward)}",
                targetTab = 1
            ),
            DailyMission(
                id = "mission_upgrade_biz",
                type = DailyMissionType.UPGRADE_BUSINESSES,
                title = "Expansion de l'Empire",
                description = "Améliore $upgradeTarget niveaux d'entreprises dans ton empire.",
                iconEmoji = "🏢",
                category = DailyMissionCategory.MANAGEMENT,
                currentProgress = 0,
                targetProgress = upgradeTarget,
                rewardCash = upgradeReward,
                rewardBoostMultiplier = 1.2,
                rewardBoostDurationSec = 30,
                rewardLabel = "+${MoneyFormatter.format(upgradeReward)}",
                targetTab = 1
            ),
            DailyMission(
                id = "mission_trade_stocks",
                type = DailyMissionType.TRADE_STOCKS,
                title = "Spéculateur de Wall Street",
                description = "Réalise $stockTarget opérations d'achat ou vente d'actions en bourse.",
                iconEmoji = "📊",
                category = DailyMissionCategory.TRADING,
                currentProgress = 0,
                targetProgress = stockTarget,
                rewardCash = stockReward,
                rewardLabel = "+${MoneyFormatter.format(stockReward)}",
                targetTab = 2
            ),
            DailyMission(
                id = "mission_sponsor_deal",
                type = DailyMissionType.PLAY_SPONSOR_MINIGAME,
                title = "Partenariats Stratégiques",
                description = "Participe avec succès à $sponsorTarget mini-jeux ou événements sponsors.",
                iconEmoji = "🎁",
                category = DailyMissionCategory.MARKETING,
                currentProgress = 0,
                targetProgress = sponsorTarget,
                rewardCash = sponsorReward,
                rewardBoostMultiplier = 2.0,
                rewardBoostDurationSec = 60,
                rewardLabel = "+${MoneyFormatter.format(sponsorReward)}",
                targetTab = 3
            ),
            DailyMission(
                id = "mission_wheel_or_ad",
                type = DailyMissionType.SPIN_WHEEL,
                title = "Coup de Poker Fortune",
                description = "Fais tourner la Roue VIP ou visionne une pub pour débloquer un bonus.",
                iconEmoji = "🎡",
                category = DailyMissionCategory.CHALLENGE,
                currentProgress = 0,
                targetProgress = challengeTarget,
                rewardCash = challengeReward,
                rewardBoostMultiplier = 2.5,
                rewardBoostDurationSec = 60,
                rewardLabel = "+${MoneyFormatter.format(challengeReward)}",
                targetTab = 0
            ),
            DailyMission(
                id = "mission_reach_combo",
                type = DailyMissionType.REACH_COMBO,
                title = "Vitesse Supersonique",
                description = "Atteins un enchaînement combo de $comboTarget x aujourd'hui.",
                iconEmoji = "🔥",
                category = DailyMissionCategory.NEGOTIATION,
                currentProgress = 0,
                targetProgress = comboTarget,
                rewardCash = comboReward,
                rewardBoostMultiplier = 1.8,
                rewardBoostDurationSec = 45,
                rewardLabel = "+${MoneyFormatter.format(comboReward)}",
                targetTab = 0
            ),
            DailyMission(
                id = "mission_resolve_crisis",
                type = DailyMissionType.RESOLVE_CRISIS,
                title = "Gestionnaire des Risques",
                description = "Prends une décision stratégique cruciale lors d'une crise aujourd'hui.",
                iconEmoji = "🛡️",
                category = DailyMissionCategory.CHALLENGE,
                currentProgress = 0,
                targetProgress = crisisTarget,
                rewardCash = crisisReward,
                rewardBoostMultiplier = 2.2,
                rewardBoostDurationSec = 60,
                rewardLabel = "+${MoneyFormatter.format(crisisReward)}",
                targetTab = 1
            )
        )
    }

    fun getDefaultDailyQuests(): List<com.example.model.DailyQuest> = listOf(
        com.example.model.DailyQuest(
            id = "quest_tap_50",
            title = "Négociateur Forcené",
            description = "Réalise 50 taps dans la salle des marchés.",
            iconEmoji = "⚡",
            currentProgress = 0,
            targetProgress = 50,
            rewardCash = 15_000.0,
            rewardLabel = "+$15K"
        ),
        com.example.model.DailyQuest(
            id = "quest_upgrade_5",
            title = "Bâtisseur d'Empire",
            description = "Améliore tes entreprises 5 fois aujourd'hui.",
            iconEmoji = "🏢",
            currentProgress = 0,
            targetProgress = 5,
            rewardCash = 30_000.0,
            rewardLabel = "+$30K"
        ),
        com.example.model.DailyQuest(
            id = "quest_stock_3",
            title = "Loup de Wall Street",
            description = "Achète ou vends 3 actions en bourse.",
            iconEmoji = "📊",
            currentProgress = 0,
            targetProgress = 3,
            rewardCash = 50_000.0,
            rewardLabel = "+$50K"
        )
    )

    fun getDefaultTechUpgrades(): List<com.example.model.TechUpgrade> = listOf(
        com.example.model.TechUpgrade(
            id = "tech_quantum_ai",
            name = "Serveurs Quantiques IA",
            description = "+30% de revenus passifs permanents sur toutes tes entreprises.",
            cost = 100_000.0,
            techCostPoints = 1,
            iconEmoji = "🧠",
            effectType = com.example.model.TechEffectType.PASSIVE_BOOST_25
        ),
        com.example.model.TechUpgrade(
            id = "tech_hft_algo",
            name = "Algorithmes Trading HFT",
            description = "Dividendes automatiques toutes les 10s sur les actions détenues.",
            cost = 500_000.0,
            techCostPoints = 2,
            iconEmoji = "📈",
            effectType = com.example.model.TechEffectType.STOCK_DIVIDEND_AUTO
        ),
        com.example.model.TechUpgrade(
            id = "tech_crit_deals",
            name = "Signature Éclair Haute Fréquence",
            description = "+25% de chance de Coup Critique lors des taps (Cash x5).",
            cost = 1_500_000.0,
            techCostPoints = 3,
            iconEmoji = "⚡",
            effectType = com.example.model.TechEffectType.CLICK_CRIT_CHANCE
        ),
        com.example.model.TechUpgrade(
            id = "tech_viral_matrix",
            name = "Matrice Marketing Virale",
            description = "Doubles les récompenses de tous les sponsors et mini-jeux !",
            cost = 10_000_000.0,
            techCostPoints = 5,
            iconEmoji = "🌐",
            effectType = com.example.model.TechEffectType.SPONSOR_REWARD_DOUBLED
        ),
        com.example.model.TechUpgrade(
            id = "tech_crypto_miner",
            name = "Ferme de Minage de Cryp-Tyc",
            description = "+500 $/sec de revenus nets générés passivement par l'aspiration d'air des serveurs.",
            cost = 2_500_000.0,
            techCostPoints = 3,
            iconEmoji = "⛏️",
            effectType = com.example.model.TechEffectType.PASSIVE_BOOST_25
        ),
        com.example.model.TechUpgrade(
            id = "tech_prestige_angel_perk",
            name = "Puce d'Amplification d'Anges",
            description = "+150% de bonus multiplicateur d'anges supplémentaires lors du prochain prestige.",
            cost = 50_000_000.0,
            techCostPoints = 5,
            iconEmoji = "👼",
            effectType = com.example.model.TechEffectType.PASSIVE_BOOST_25
        )
    )

    fun getDefaultNewsFeed(): List<com.example.model.MarketNewsItem> = listOf(
        com.example.model.MarketNewsItem(
            id = "news_1",
            headline = "L'action QNTM s'envole de +18% suite à une percée dans l'informatique quantique !",
            affectedTicker = "QNTM",
            priceImpactPercent = 18.0,
            emoji = "🚀",
            timestampFormatted = "À l'instant"
        ),
        com.example.model.MarketNewsItem(
            id = "news_2",
            headline = "Marché du Café : Pénurie mondiale de grains d'arabica, hausse des prix !",
            affectedTicker = "COFF",
            priceImpactPercent = 12.0,
            emoji = "☕",
            timestampFormatted = "Il y a 2m"
        ),
        com.example.model.MarketNewsItem(
            id = "news_3",
            headline = "Les banques centrales annoncent une injection record de liquidités !",
            affectedTicker = "NEOB",
            priceImpactPercent = 15.0,
            emoji = "🏦",
            timestampFormatted = "Il y a 5m"
        )
    )

    fun getDefaultMegaprojects(): List<com.example.model.Megaproject> = listOf(
        com.example.model.Megaproject(
            id = "mega_tower",
            name = "Gratte-Ciel Central Metropolis",
            category = "Immobilier Majeur",
            description = "Construisez la plus haute tour d'affaires de la ville. Octroie +30% de revenus passifs globaux par niveau.",
            stage = 0,
            maxStage = 5,
            baseCost = 100_000.0,
            passiveMultiplierBonus = 0.30,
            iconEmoji = "🏢"
        ),
        com.example.model.Megaproject(
            id = "mega_space",
            name = "Station Spatiale Commerciale OrbitX",
            category = "Infrastructures Spatiales",
            description = "Déployez un réseau orbital de tourisme stellaire. Octroie +50% de revenus passifs par niveau.",
            stage = 0,
            maxStage = 5,
            baseCost = 5_000_000.0,
            passiveMultiplierBonus = 0.50,
            iconEmoji = "🛰️"
        ),
        com.example.model.Megaproject(
            id = "mega_6g",
            name = "Réseau Quantique Global 6G",
            category = "Télécoms Futuristes",
            description = "Inondez la planète de connexions instantanées ultra-sécurisées. Octroie +75% de revenus par niveau.",
            stage = 0,
            maxStage = 5,
            baseCost = 50_000_000.0,
            passiveMultiplierBonus = 0.75,
            iconEmoji = "🌐"
        )
    )

    fun getDefaultProductivityUpgrades(): List<com.example.model.ProductivityUpgrade> = listOf(
        // CLICK POWER
        com.example.model.ProductivityUpgrade(
            id = "upg_stylus_gold",
            name = "Stylet en Or 24K",
            category = com.example.model.UpgradeCategory.CLICK_POWER,
            description = "Un stylet de luxe pour signer des contrats plus vite. +15% de cash par clic.",
            baseCost = 250.0,
            costMultiplier = 1.15,
            level = 0,
            multiplierPerLevel = 0.15,
            iconEmoji = "✒️",
            badgeText = "+15% Clic",
            tagHexColor = 0xFFF59E0B
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_quantum_finger",
            name = "Doigt Biométrique Quantique",
            category = com.example.model.UpgradeCategory.CLICK_POWER,
            description = "Technologie cybernétique décuplant la vélocité des transactions. +25% de cash par clic.",
            baseCost = 2_500.0,
            costMultiplier = 1.18,
            level = 0,
            multiplierPerLevel = 0.25,
            iconEmoji = "🦾",
            badgeText = "+25% Clic",
            tagHexColor = 0xFF3B82F6
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_crit_mastery",
            name = "Signature Critique de Maître",
            category = com.example.model.UpgradeCategory.CLICK_POWER,
            description = "Améliore la précision des négociations. +5% de chance de coup critique (Cash x5).",
            baseCost = 12_000.0,
            costMultiplier = 1.22,
            level = 0,
            multiplierPerLevel = 0.20,
            iconEmoji = "⚡",
            badgeText = "+20% Puissance",
            tagHexColor = 0xFFEF4444
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_autotapper_drone",
            name = "Drone Négociateur Automatique",
            category = com.example.model.UpgradeCategory.CLICK_POWER,
            description = "Un assistant robotisé qui signe des accords en continu. +15% de rendement clic.",
            baseCost = 60_000.0,
            costMultiplier = 1.20,
            level = 0,
            multiplierPerLevel = 0.15,
            iconEmoji = "🤖",
            badgeText = "+15% Clic",
            tagHexColor = 0xFF8B5CF6
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_frenzy_amplifier",
            name = "Amplificateur Frenzy Surchargé",
            category = com.example.model.UpgradeCategory.CLICK_POWER,
            description = "Fait exploser les bénéfices du mode Frenzy. +30% de boost supplémentaire.",
            baseCost = 300_000.0,
            costMultiplier = 1.25,
            level = 0,
            multiplierPerLevel = 0.30,
            iconEmoji = "🔥",
            badgeText = "+30% Frenzy",
            tagHexColor = 0xFFF97316
        ),

        // PASSIVE BUSINESS
        com.example.model.ProductivityUpgrade(
            id = "upg_logistics_ai",
            name = "IA Logistique Autonome",
            category = com.example.model.UpgradeCategory.PASSIVE_BUSINESS,
            description = "Optimise les chaînes de valeur de toutes tes filiales. +10% de revenus passifs globaux.",
            baseCost = 1_000.0,
            costMultiplier = 1.16,
            level = 0,
            multiplierPerLevel = 0.10,
            iconEmoji = "🧠",
            badgeText = "+10% Passif",
            tagHexColor = 0xFF10B981
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_supply_chain",
            name = "Approvisionnement Supra-Conducteur",
            category = com.example.model.UpgradeCategory.PASSIVE_BUSINESS,
            description = "Accélère la vitesse de cycle de toutes les entreprises de +15%.",
            baseCost = 18_000.0,
            costMultiplier = 1.18,
            level = 0,
            multiplierPerLevel = 0.15,
            iconEmoji = "⚡",
            badgeText = "+15% Vitesse",
            tagHexColor = 0xFF06B6D4
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_tax_haven",
            name = "Conseil en Optimisation Fiscale",
            category = com.example.model.UpgradeCategory.PASSIVE_BUSINESS,
            description = "Réduit les taxes et augmente les dividendes nets reversés. +12% de gains passifs.",
            baseCost = 85_000.0,
            costMultiplier = 1.20,
            level = 0,
            multiplierPerLevel = 0.12,
            iconEmoji = "🏛️",
            badgeText = "+12% Marge",
            tagHexColor = 0xFF14B8A6
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_synergy_hub",
            name = "Hub Synergique Holding",
            category = com.example.model.UpgradeCategory.PASSIVE_BUSINESS,
            description = "Crée des synergies entre toutes tes filiales débloquées. +20% de revenus combinés.",
            baseCost = 600_000.0,
            costMultiplier = 1.22,
            level = 0,
            multiplierPerLevel = 0.20,
            iconEmoji = "🌐",
            badgeText = "+20% Synergie",
            tagHexColor = 0xFF6366F1
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_fusion_generators",
            name = "Micro-Générateurs à Fusion",
            category = com.example.model.UpgradeCategory.PASSIVE_BUSINESS,
            description = "Alimentation énergétique illimitée à coût nul. +35% de revenus passifs massifs.",
            baseCost = 3_000_000.0,
            costMultiplier = 1.25,
            level = 0,
            multiplierPerLevel = 0.35,
            iconEmoji = "⚛️",
            badgeText = "+35% Énergie",
            tagHexColor = 0xFFEC4899
        ),

        // ADS AND SPONSORS
        com.example.model.ProductivityUpgrade(
            id = "upg_admob_hd_monetization",
            name = "Diffuseur Vidéo Ultra Haute Définition",
            category = com.example.model.UpgradeCategory.ADS_AND_SPONSORS,
            description = "Monétise les flux vidéo publicitaires au meilleur tarif. +25% de bonus cash sur toutes les pubs.",
            baseCost = 4_000.0,
            costMultiplier = 1.17,
            level = 0,
            multiplierPerLevel = 0.25,
            iconEmoji = "📺",
            badgeText = "+25% Gains Pub",
            tagHexColor = 0xFFEAB308
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_sponsor_frequency",
            name = "Aimant à Partenariats VIP",
            category = com.example.model.UpgradeCategory.ADS_AND_SPONSORS,
            description = "Attire de prestigieux sponsors mondiaux. +30% sur les gains des mini-jeux publicitaires.",
            baseCost = 25_000.0,
            costMultiplier = 1.19,
            level = 0,
            multiplierPerLevel = 0.30,
            iconEmoji = "🎁",
            badgeText = "+30% Sponsors",
            tagHexColor = 0xFFF59E0B
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_turbo_boost_duration",
            name = "Super-Batterie Multiplicateur Pub",
            category = com.example.model.UpgradeCategory.ADS_AND_SPONSORS,
            description = "Prolonge la durée des multiplicateurs x2 / x4 déclenchés par les annonces. +20% durée.",
            baseCost = 150_000.0,
            costMultiplier = 1.20,
            level = 0,
            multiplierPerLevel = 0.20,
            iconEmoji = "🔋",
            badgeText = "+20% Durée Boost",
            tagHexColor = 0xFF10B981
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_viral_campaign_booster",
            name = "Matrice de Viralité Numérique",
            category = com.example.model.UpgradeCategory.ADS_AND_SPONSORS,
            description = "Multiplie l'impact de chaque visionnage publicitaire. +40% de rendement sponsor.",
            baseCost = 1_200_000.0,
            costMultiplier = 1.25,
            level = 0,
            multiplierPerLevel = 0.40,
            iconEmoji = "🚀",
            badgeText = "+40% Viralité",
            tagHexColor = 0xFFD946EF
        ),

        // FINANCE AND MARKET
        com.example.model.ProductivityUpgrade(
            id = "upg_hft_dividends",
            name = "Dividendes Automatiques HFT",
            category = com.example.model.UpgradeCategory.FINANCE_AND_MARKET,
            description = "Perçois des dividendes réguliers sur toutes les actions détenues en bourse. +15% de dividendes.",
            baseCost = 15_000.0,
            costMultiplier = 1.18,
            level = 0,
            multiplierPerLevel = 0.15,
            iconEmoji = "📈",
            badgeText = "+15% Dividendes",
            tagHexColor = 0xFF059669
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_zero_broker_fee",
            name = "Terminal Trader Institutionnel",
            category = com.example.model.UpgradeCategory.FINANCE_AND_MARKET,
            description = "Réduit les frais de courtage et augmente le profit de revente d'actions. +10% de plus-values.",
            baseCost = 90_000.0,
            costMultiplier = 1.20,
            level = 0,
            multiplierPerLevel = 0.10,
            iconEmoji = "💼",
            badgeText = "+10% Trading",
            tagHexColor = 0xFF2563EB
        ),
        com.example.model.ProductivityUpgrade(
            id = "upg_venture_compound",
            name = "Intérêts Composés Trésorerie",
            category = com.example.model.UpgradeCategory.FINANCE_AND_MARKET,
            description = "Fais fructifier ton argent disponible en trésorerie. +20% d'intérêts passifs.",
            baseCost = 750_000.0,
            costMultiplier = 1.22,
            level = 0,
            multiplierPerLevel = 0.20,
            iconEmoji = "🏦",
            badgeText = "+20% Intérêts",
            tagHexColor = 0xFF7C3AED
        )
    )

    fun getDefaultAuctionLots(): List<AuctionLot> = listOf(
        AuctionLot(
            id = "auc_patent_ai",
            title = "Brevet Exclusif Micro-Processeurs Quantiques",
            category = "Deep Tech & Brevets",
            description = "Monopole mondial sur l'architecture silicium supraconductrice. +40% de multiplicateur global permanent !",
            startingBid = 250_000.0,
            currentBid = 250_000.0,
            highestBidderName = "Fonds Souverain Singapour",
            isPlayerWinning = false,
            timeRemainingSec = 30,
            permanentMultiplier = 0.40,
            bonusCashYieldPerSec = 5_000.0,
            iconEmoji = "🔬",
            activeRivals = listOf(
                RivalBidder("riv_1", "Lord Sterling", "Baron du Pétrole", "🎩", 2_000_000.0),
                RivalBidder("riv_2", "Zhang Wei", "Titan de la Silicon Valley", "💻", 4_500_000.0)
            )
        ),
        AuctionLot(
            id = "auc_downtown_tower",
            title = "Tour Skyrise 5th Avenue Manhattan",
            category = "Immobilier Trophée",
            description = "Gratte-ciel emblématique au cœur du quartier financier. Génère +$50K/s et +50% de revenus passifs.",
            startingBid = 1_500_000.0,
            currentBid = 1_500_000.0,
            highestBidderName = "Apex Capital Partners",
            isPlayerWinning = false,
            timeRemainingSec = 45,
            permanentMultiplier = 0.50,
            bonusCashYieldPerSec = 50_000.0,
            iconEmoji = "🏙️",
            activeRivals = listOf(
                RivalBidder("riv_3", "Elena Rostova", "Magnat de l'Acier", "💎", 15_000_000.0),
                RivalBidder("riv_4", "Sofia Al-Mansoor", "Héritière Immobilière", "👑", 25_000_000.0)
            )
        ),
        AuctionLot(
            id = "auc_orbital_concession",
            title = "Concession Minière de l'Astéroïde Psyché-16",
            category = "Ressources Spatiales",
            description = "Droits d'extraction de métaux précieux d'une valeur inestimable. +100% de multiplicateur et +$500K/s !",
            startingBid = 20_000_000.0,
            currentBid = 20_000_000.0,
            highestBidderName = "Consortium Spatial Européen",
            isPlayerWinning = false,
            timeRemainingSec = 60,
            permanentMultiplier = 1.00,
            bonusCashYieldPerSec = 500_000.0,
            iconEmoji = "☄️",
            activeRivals = listOf(
                RivalBidder("riv_5", "Victor Vance", "Pionnier Aérospatial", "🚀", 120_000_000.0),
                RivalBidder("riv_2", "Zhang Wei", "Titan de la Silicon Valley", "💻", 200_000_000.0)
            )
        ),
        AuctionLot(
            id = "auc_sovereign_bank",
            title = "Licence Bancaire Privée Suisse & Lichtenstein",
            category = "Secteur Bancaire Élite",
            description = "Autorisation de réserve fractionnaire et gestion de fortunes souveraines. +75% de dividendes et revenus.",
            startingBid = 100_000_000.0,
            currentBid = 100_000_000.0,
            highestBidderName = "Zurich Private Reserve",
            isPlayerWinning = false,
            timeRemainingSec = 50,
            permanentMultiplier = 0.75,
            bonusCashYieldPerSec = 2_000_000.0,
            iconEmoji = "🏦",
            activeRivals = listOf(
                RivalBidder("riv_1", "Lord Sterling", "Baron du Pétrole", "🎩", 500_000_000.0),
                RivalBidder("riv_4", "Sofia Al-Mansoor", "Héritière Immobilière", "👑", 800_000_000.0)
            )
        )
    )

    fun getDefaultCorporateTakeovers(): List<CorporateTakeover> = listOf(
        CorporateTakeover(
            id = "corp_hyperlink",
            name = "HyperLink Telecommunications",
            industry = "Télécoms & Réseau Fibre",
            rivalCeoName = "Arthur Pendelton",
            rivalCeoAvatar = "👨‍💼",
            totalEnterpriseValue = 500_000.0,
            ownedStakePercentage = 0,
            baseRevenuePerSec = 15_000.0,
            iconEmoji = "📡",
            description = "Fournisseur d'accès télécom d'envergure nationale. Racheter les actions élimine la concurrence et verse d'énormes dividendes."
        ),
        CorporateTakeover(
            id = "corp_aero_vanguard",
            name = "Vanguard Air & Defense",
            industry = "Aéronautique & Drones Furtifs",
            rivalCeoName = "Général Klaus Hoffman",
            rivalCeoAvatar = "🪖",
            totalEnterpriseValue = 5_000_000.0,
            ownedStakePercentage = 0,
            baseRevenuePerSec = 120_000.0,
            iconEmoji = "✈️",
            description = "Constructeur de jets supersoniques et drones cargo de transport d'actifs pour ultra-riches."
        ),
        CorporateTakeover(
            id = "corp_solaris_grid",
            name = "Solaris MegaGrid Global",
            industry = "Centrales Solaires & Batteries Solides",
            rivalCeoName = "Beatrice Van Der Bilt",
            rivalCeoAvatar = "👩‍🔬",
            totalEnterpriseValue = 50_000_000.0,
            ownedStakePercentage = 0,
            baseRevenuePerSec = 1_500_000.0,
            iconEmoji = "☀️",
            description = "Monopole de distribution électrique propre alimentant les centres de données et mégalopoles d'Asie et d'Europe."
        ),
        CorporateTakeover(
            id = "corp_singularity_labs",
            name = "Singularity Cybernetics & AI",
            industry = "Super-Intelligence & Robotique",
            rivalCeoName = "Dr. Xavier Sterling",
            rivalCeoAvatar = "🤖",
            totalEnterpriseValue = 500_000_000.0,
            ownedStakePercentage = 0,
            baseRevenuePerSec = 18_000_000.0,
            iconEmoji = "🧠",
            description = "Le laboratoire de recherche le plus avancé du monde en automatisation industrielle et intelligence artificielle autonome."
        )
    )

    fun getDefaultExpandedTechTree(): List<ExpandedTechNode> = listOf(
        // BRANCHE IA & ALGORITHMES
        ExpandedTechNode(
            id = "tech_ai_1",
            name = "Réseau Neuronal Auto-Apprenant",
            branch = TechBranch.AI_COMPUTING,
            tier = 1,
            description = "Améliore la vitesse d'arbitrage de trading et les profits passifs de +25%.",
            cost = 75_000.0,
            requiresTechId = null,
            bonusLabel = "+25% Passif",
            multiplierBoost = 0.25,
            iconEmoji = "🤖"
        ),
        ExpandedTechNode(
            id = "tech_ai_2",
            name = "Calculateurs Quantiques Photoniques",
            branch = TechBranch.AI_COMPUTING,
            tier = 2,
            description = "Prédit les cours de bourse et booste tous les gains de +50%.",
            cost = 1_200_000.0,
            requiresTechId = "tech_ai_1",
            bonusLabel = "+50% Passif & Stocks",
            multiplierBoost = 0.50,
            iconEmoji = "💡"
        ),
        ExpandedTechNode(
            id = "tech_ai_3",
            name = "Conscience Synthétique Omnisciente",
            branch = TechBranch.AI_COMPUTING,
            tier = 3,
            description = "L'IA pilote l'empire entier à la perfection. +100% de revenus globaux !",
            cost = 25_000_000.0,
            requiresTechId = "tech_ai_2",
            bonusLabel = "+100% Global",
            multiplierBoost = 1.00,
            iconEmoji = "🌌"
        ),

        // BRANCHE ÉNERGIE & FUSION
        ExpandedTechNode(
            id = "tech_nrg_1",
            name = "Stockage Énergétique Graphène",
            branch = TechBranch.ENERGY_FUSION,
            tier = 1,
            description = "Réduit les coûts opérationnels de l'empire et accélère la cadence. +20% revenus.",
            cost = 100_000.0,
            requiresTechId = null,
            bonusLabel = "+20% Rendement",
            multiplierBoost = 0.20,
            iconEmoji = "🔋"
        ),
        ExpandedTechNode(
            id = "tech_nrg_2",
            name = "Centrale à Confinement Magnétique",
            branch = TechBranch.ENERGY_FUSION,
            tier = 2,
            description = "Alimente toutes vos filiales en énergie propre illimitée. +60% de cashflow.",
            cost = 2_500_000.0,
            requiresTechId = "tech_nrg_1",
            bonusLabel = "+60% Cashflow",
            multiplierBoost = 0.60,
            iconEmoji = "⚛️"
        ),
        ExpandedTechNode(
            id = "tech_nrg_3",
            name = "Moissonneuse Solaire Orbitale Dyson",
            branch = TechBranch.ENERGY_FUSION,
            tier = 3,
            description = "Capte le rayonnement direct du soleil. Rendement phénoménal de +120% !",
            cost = 45_000_000.0,
            requiresTechId = "tech_nrg_2",
            bonusLabel = "+120% Production",
            multiplierBoost = 1.20,
            iconEmoji = "☀️"
        ),

        // BRANCHE SPATIAL & MINAGE
        ExpandedTechNode(
            id = "tech_spc_1",
            name = "Lanceurs Réutilisables Lourds",
            branch = TechBranch.SPACE_MINING,
            tier = 1,
            description = "Démocratise le fret vers l'orbite basse. +30% de revenus passifs.",
            cost = 250_000.0,
            requiresTechId = null,
            bonusLabel = "+30% Spatial",
            multiplierBoost = 0.30,
            iconEmoji = "🚀"
        ),
        ExpandedTechNode(
            id = "tech_spc_2",
            name = "Base de Forage Lunaire Hélium-3",
            branch = TechBranch.SPACE_MINING,
            tier = 2,
            description = "Extrait des isotopes précieux de la surface lunaire. +75% de revenus.",
            cost = 5_000_000.0,
            requiresTechId = "tech_spc_1",
            bonusLabel = "+75% Ressources",
            multiplierBoost = 0.75,
            iconEmoji = "🌕"
        ),
        ExpandedTechNode(
            id = "tech_spc_3",
            name = "Complexe de Raffinage Interstellaire",
            branch = TechBranch.SPACE_MINING,
            tier = 3,
            description = "Raffine des tonnes de platine et d'or depuis la ceinture de Kuiper. +150% global !",
            cost = 80_000_000.0,
            requiresTechId = "tech_spc_2",
            bonusLabel = "+150% Minage Stellaire",
            multiplierBoost = 1.50,
            iconEmoji = "🪐"
        ),

        // BRANCHE BIO-NANOTECH
        ExpandedTechNode(
            id = "tech_bio_1",
            name = "Nano-Assemblage Moléculaire",
            branch = TechBranch.BIO_NANOTECH,
            tier = 1,
            description = "Fabrique des produits instantanément avec zéro gaspillage. +25% de clic & passif.",
            cost = 150_000.0,
            requiresTechId = null,
            bonusLabel = "+25% Clic & Passif",
            multiplierBoost = 0.25,
            iconEmoji = "🧪"
        ),
        ExpandedTechNode(
            id = "tech_bio_2",
            name = "Thérapie Génique & Longévité C-Suite",
            branch = TechBranch.BIO_NANOTECH,
            tier = 2,
            description = "Vos dirigeants travaillent à un niveau d'énergie surhumain. +65% de rentabilité.",
            cost = 3_500_000.0,
            requiresTechId = "tech_bio_1",
            bonusLabel = "+65% Dirigeants",
            multiplierBoost = 0.65,
            iconEmoji = "🧬"
        ),
        ExpandedTechNode(
            id = "tech_bio_3",
            name = "Téléportation Logistique Quantique",
            branch = TechBranch.BIO_NANOTECH,
            tier = 3,
            description = "Livraison instantanée à travers la galaxie. +135% de profits !",
            cost = 60_000_000.0,
            requiresTechId = "tech_bio_2",
            bonusLabel = "+135% Logistique",
            multiplierBoost = 1.35,
            iconEmoji = "🌀"
        )
    )

    fun getDefaultLuxuryAssets(): List<LuxuryAsset> = listOf(
        LuxuryAsset(
            id = "lux_penthouse_nyc",
            name = "Triplex Penthouse Billionaires' Row",
            category = "Immobilier de Prestige",
            location = "New York, USA",
            description = "Vue panoramique à 360° sur Central Park, piscine suspendue et héliport privé.",
            cost = 500_000.0,
            isPurchased = false,
            prestigeScore = 150,
            passiveIncomeMultiplier = 0.15,
            clickPowerBoostPercent = 0.20,
            iconEmoji = "🏙️"
        ),
        LuxuryAsset(
            id = "lux_superyacht_monaco",
            name = "Superyacht Fjord Horizon 120m",
            category = "Maritime d'Exception",
            location = "Port Hercule, Monaco",
            description = "2 sous-marins de poche, piste d'hélicoptère, salle de cinéma et suite propriétaire de 300m².",
            cost = 2_500_000.0,
            isPurchased = false,
            prestigeScore = 400,
            passiveIncomeMultiplier = 0.25,
            clickPowerBoostPercent = 0.35,
            iconEmoji = "🛥️"
        ),
        LuxuryAsset(
            id = "lux_supersonic_jet",
            name = "Gulfstream HyperSonic Mach 2.4",
            category = "Aviation d'Affaires",
            location = "Aéroport de Genève / Le Bourget",
            description = "Relie Paris à Tokyo en 3 heures. Cabine pressurisée anti-jetlag avec spa et bureau sécurisé.",
            cost = 10_000_000.0,
            isPurchased = false,
            prestigeScore = 900,
            passiveIncomeMultiplier = 0.40,
            clickPowerBoostPercent = 0.50,
            iconEmoji = "✈️"
        ),
        LuxuryAsset(
            id = "lux_private_island",
            name = "Atoll Privé Tetiaroa Paradise",
            category = "Territoire Souverain",
            location = "Polynésie Française",
            description = "Île sanctuaire autonome en énergie solaire avec pistes d'atterrissage, lagon privé et villas d'invités.",
            cost = 45_000_000.0,
            isPurchased = false,
            prestigeScore = 2000,
            passiveIncomeMultiplier = 0.60,
            clickPowerBoostPercent = 0.75,
            iconEmoji = "🏝️"
        ),
        LuxuryAsset(
            id = "lux_orbital_villa",
            name = "Villa Orbitale Elysium Zero-G",
            category = "Résidence Spatiale",
            location = "Orbite Géostationnaire Terrestre",
            description = "La première résidence spatiale privée de l'Histoire humaine. Vue imprenable sur la Terre et prestige absolu.",
            cost = 200_000_000.0,
            isPurchased = false,
            prestigeScore = 5000,
            passiveIncomeMultiplier = 1.00,
            clickPowerBoostPercent = 1.25,
            iconEmoji = "🌌"
        )
    )
}


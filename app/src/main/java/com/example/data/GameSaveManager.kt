package com.example.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import com.example.model.Achievement
import com.example.model.AdNetworkTier
import com.example.model.Business
import com.example.model.CareerStats
import com.example.model.DailyMilestoneChest
import com.example.model.DailyMission
import com.example.model.Executive
import com.example.model.OfflineEarningsReport
import com.example.model.SponsorshipContract
import com.example.model.StockItem
import org.json.JSONArray
import org.json.JSONObject
import java.nio.charset.StandardCharsets

object GameSaveManager {
    private const val PREFS_NAME = "empire_tycoon_player_save"
    private const val KEY_SAVE_DATA = "saved_game_json"

    fun saveGame(
        context: Context,
        playerName: String,
        selectedAvatarId: Int,
        soundEnabled: Boolean,
        hapticsEnabled: Boolean,
        cash: Double,
        totalCashEarned: Double,
        prestigeLevel: Int,
        prestigeBonusMultiplier: Double,
        businesses: List<Business>,
        stocks: List<StockItem>,
        executives: List<Executive>,
        adNetworks: List<AdNetworkTier>,
        achievements: List<Achievement>,
        careerStats: CareerStats,
        dailyMissions: List<DailyMission> = emptyList(),
        milestoneChests: List<DailyMilestoneChest> = emptyList(),
        dailyRewards: List<com.example.model.DailyLoginReward> = emptyList(),
        dailyStreakDays: Int = 1,
        lastMissionDayEpoch: Long = 0L,
        dailyCashEarned: Double = 0.0,
        productivityUpgrades: List<com.example.model.ProductivityUpgrade> = emptyList(),
        lastWheelSpinTimestampEpoch: Long = 0L,
        wonAuctionLotIds: Set<String> = emptySet(),
        takeoverStakes: Map<String, Int> = emptyMap(),
        unlockedTechIds: Set<String> = emptySet(),
        purchasedLuxuryIds: Set<String> = emptySet(),
        companyName: String = "Mon Entreprise",
        sponsorshipContracts: List<SponsorshipContract> = emptyList()
    ): String {
        val root = JSONObject()
        root.put("version", 5)
        root.put("playerName", playerName)
        root.put("companyName", companyName)
        root.put("selectedAvatarId", selectedAvatarId)
        root.put("soundEnabled", soundEnabled)
        root.put("hapticsEnabled", hapticsEnabled)
        root.put("cash", cash)
        root.put("totalCashEarned", totalCashEarned)
        root.put("prestigeLevel", prestigeLevel)
        root.put("prestigeBonusMultiplier", prestigeBonusMultiplier)
        root.put("lastWheelSpinTimestampEpoch", lastWheelSpinTimestampEpoch)

        // Expansion: Sponsorship Contracts
        val sponsorArray = JSONArray()
        sponsorshipContracts.forEach { s ->
            val obj = JSONObject()
            obj.put("id", s.id)
            obj.put("isSigned", s.isSigned)
            obj.put("timeRemaining", s.timeRemainingSeconds)
            obj.put("totalEarned", s.totalEarningsAccumulated)
            sponsorArray.put(obj)
        }
        root.put("sponsorshipContracts", sponsorArray)

        // Expansion: Auctions won
        val aucArray = JSONArray()
        wonAuctionLotIds.forEach { aucArray.put(it) }
        root.put("wonAuctionLotIds", aucArray)

        // Expansion: Takeover stakes
        val takeoverArray = JSONArray()
        takeoverStakes.forEach { (id, stake) ->
            val obj = JSONObject()
            obj.put("id", id)
            obj.put("stake", stake)
            takeoverArray.put(obj)
        }
        root.put("takeoverStakes", takeoverArray)

        // Expansion: Tech nodes unlocked
        val techArray = JSONArray()
        unlockedTechIds.forEach { techArray.put(it) }
        root.put("unlockedTechIds", techArray)

        // Expansion: Luxury assets purchased
        val luxArray = JSONArray()
        purchasedLuxuryIds.forEach { luxArray.put(it) }
        root.put("purchasedLuxuryIds", luxArray)

        // Productivity Upgrades
        val upgArray = JSONArray()
        productivityUpgrades.forEach { u ->
            val obj = JSONObject()
            obj.put("id", u.id)
            obj.put("level", u.level)
            upgArray.put(obj)
        }
        root.put("productivityUpgrades", upgArray)

        // Businesses
        val bizArray = JSONArray()
        businesses.forEach { b ->
            val obj = JSONObject()
            obj.put("id", b.id)
            obj.put("level", b.level)
            obj.put("isUnlocked", b.isUnlocked)
            obj.put("managerHired", b.managerHired)
            obj.put("customName", b.name) // Save custom business name!
            bizArray.put(obj)
        }
        root.put("businesses", bizArray)

        // Stocks
        val stockArray = JSONArray()
        stocks.forEach { s ->
            val obj = JSONObject()
            obj.put("ticker", s.ticker)
            obj.put("ownedShares", s.ownedShares)
            obj.put("totalInvested", s.totalInvested)
            obj.put("price", s.price)
            stockArray.put(obj)
        }
        root.put("stocks", stockArray)

        // Executives
        val execArray = JSONArray()
        executives.forEach { e ->
            val obj = JSONObject()
            obj.put("id", e.id)
            obj.put("hired", e.hired)
            execArray.put(obj)
        }
        root.put("executives", execArray)

        // Ad Networks
        val adArray = JSONArray()
        adNetworks.forEach { a ->
            val obj = JSONObject()
            obj.put("id", a.id)
            obj.put("isUnlocked", a.isUnlocked)
            obj.put("level", a.level)
            adArray.put(obj)
        }
        root.put("adNetworks", adArray)

        // Achievements
        val achArray = JSONArray()
        achievements.forEach { ac ->
            val obj = JSONObject()
            obj.put("id", ac.id)
            obj.put("currentValue", ac.currentValue)
            obj.put("isUnlocked", ac.isUnlocked)
            obj.put("isClaimed", ac.isClaimed)
            achArray.put(obj)
        }
        root.put("achievements", achArray)

        // Daily Missions
        val missionArray = JSONArray()
        dailyMissions.forEach { m ->
            val obj = JSONObject()
            obj.put("id", m.id)
            obj.put("currentProgress", m.currentProgress)
            obj.put("targetProgress", m.targetProgress)
            obj.put("isCompleted", m.isCompleted)
            obj.put("isClaimed", m.isClaimed)
            missionArray.put(obj)
        }
        root.put("dailyMissions", missionArray)

        // Milestone Chests
        val chestArray = JSONArray()
        milestoneChests.forEach { c ->
            val obj = JSONObject()
            obj.put("target", c.milestoneTarget)
            obj.put("isUnlocked", c.isUnlocked)
            obj.put("isClaimed", c.isClaimed)
            chestArray.put(obj)
        }
        root.put("milestoneChests", chestArray)

        // Daily Login Rewards (7-Days)
        val rewardArray = JSONArray()
        dailyRewards.forEach { r ->
            val obj = JSONObject()
            obj.put("dayNumber", r.dayNumber)
            obj.put("isClaimed", r.isClaimed)
            rewardArray.put(obj)
        }
        root.put("dailyRewards", rewardArray)

        root.put("dailyStreakDays", dailyStreakDays)
        root.put("lastMissionDayEpoch", lastMissionDayEpoch)
        root.put("dailyCashEarned", dailyCashEarned)

        // Career Stats
        val statsObj = JSONObject()
        statsObj.put("totalTaps", careerStats.totalTaps)
        statsObj.put("highestCombo", careerStats.highestCombo)
        statsObj.put("totalCrisesResolved", careerStats.totalCrisesResolved)
        statsObj.put("totalMiniGamesWon", careerStats.totalMiniGamesWon)
        statsObj.put("totalStockTrades", careerStats.totalStockTrades)
        statsObj.put("totalPrestigeResets", careerStats.totalPrestigeResets)
        statsObj.put("totalPlayTimeSeconds", careerStats.totalPlayTimeSeconds)
        statsObj.put("lastSavedTimestamp", System.currentTimeMillis())
        root.put("careerStats", statsObj)

        val jsonString = root.toString()
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SAVE_DATA, jsonString).apply()
        return jsonString
    }

    fun loadGame(context: Context): GameLoadResult? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_SAVE_DATA, null) ?: return null
        return parseJsonSave(jsonString)
    }

    fun loadGameFromJson(jsonString: String): GameLoadResult? {
        return parseJsonSave(jsonString)
    }

    fun exportSaveString(context: Context): String? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_SAVE_DATA, null) ?: return null
        return Base64.encodeToString(jsonString.toByteArray(StandardCharsets.UTF_8), Base64.NO_WRAP)
    }

    fun importSaveString(context: Context, encodedString: String): Boolean {
        return try {
            val decodedBytes = Base64.decode(encodedString.trim(), Base64.NO_WRAP)
            val jsonString = String(decodedBytes, StandardCharsets.UTF_8)
            val result = parseJsonSave(jsonString)
            if (result != null) {
                val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(KEY_SAVE_DATA, jsonString).apply()
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    fun clearSave(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    private fun parseJsonSave(jsonString: String): GameLoadResult? {
        return try {
            val root = JSONObject(jsonString)
            val playerName = root.optString("playerName", "Player234")
            val companyName = root.optString("companyName", "Mon Entreprise")
            val selectedAvatarId = root.optInt("selectedAvatarId", 0)
            val soundEnabled = root.optBoolean("soundEnabled", true)
            val hapticsEnabled = root.optBoolean("hapticsEnabled", true)
            val cash = root.optDouble("cash", 50.0)
            val totalCashEarned = root.optDouble("totalCashEarned", 50.0)
            val prestigeLevel = root.optInt("prestigeLevel", 0)
            val prestigeBonusMultiplier = root.optDouble("prestigeBonusMultiplier", 1.0)

            // Business map
            val bizMap = mutableMapOf<String, Triple<Int, Boolean, Boolean>>()
            val bizNamesMap = mutableMapOf<String, String>()
            val bizArray = root.optJSONArray("businesses")
            if (bizArray != null) {
                for (i in 0 until bizArray.length()) {
                    val obj = bizArray.getJSONObject(i)
                    val id = obj.getString("id")
                    bizMap[id] = Triple(
                        obj.optInt("level", 0),
                        obj.optBoolean("isUnlocked", false),
                        obj.optBoolean("managerHired", false)
                    )
                    val customName = obj.optString("customName", "")
                    if (customName.isNotEmpty()) {
                        bizNamesMap[id] = customName
                    }
                }
            }

            // Stocks map
            val stockMap = mutableMapOf<String, Pair<Int, Double>>()
            val stockArray = root.optJSONArray("stocks")
            if (stockArray != null) {
                for (i in 0 until stockArray.length()) {
                    val obj = stockArray.getJSONObject(i)
                    stockMap[obj.getString("ticker")] = Pair(
                        obj.optInt("ownedShares", 0),
                        obj.optDouble("totalInvested", 0.0)
                    )
                }
            }

            // Exec map
            val execHiredSet = mutableSetOf<String>()
            val execArray = root.optJSONArray("executives")
            if (execArray != null) {
                for (i in 0 until execArray.length()) {
                    val obj = execArray.getJSONObject(i)
                    if (obj.optBoolean("hired", false)) {
                        execHiredSet.add(obj.getString("id"))
                    }
                }
            }

            // Ad map
            val adUnlockedSet = mutableSetOf<String>()
            val adLevelsMap = mutableMapOf<String, Int>()
            val adArray = root.optJSONArray("adNetworks")
            if (adArray != null) {
                for (i in 0 until adArray.length()) {
                    val obj = adArray.getJSONObject(i)
                    val id = obj.getString("id")
                    if (obj.optBoolean("isUnlocked", false)) {
                        adUnlockedSet.add(id)
                    }
                    val lvl = obj.optInt("level", 1)
                    adLevelsMap[id] = lvl
                }
            }

            // Achievements map
            val achMap = mutableMapOf<String, Triple<Long, Boolean, Boolean>>()
            val achArray = root.optJSONArray("achievements")
            if (achArray != null) {
                for (i in 0 until achArray.length()) {
                    val obj = achArray.getJSONObject(i)
                    achMap[obj.getString("id")] = Triple(
                        obj.optLong("currentValue", 0L),
                        obj.optBoolean("isUnlocked", false),
                        obj.optBoolean("isClaimed", false)
                    )
                }
            }

            // Daily Missions map
            val missionMap = mutableMapOf<String, Triple<Long, Boolean, Boolean>>()
            val missionArray = root.optJSONArray("dailyMissions")
            if (missionArray != null) {
                for (i in 0 until missionArray.length()) {
                    val obj = missionArray.getJSONObject(i)
                    missionMap[obj.getString("id")] = Triple(
                        obj.optLong("currentProgress", 0L),
                        obj.optBoolean("isCompleted", false),
                        obj.optBoolean("isClaimed", false)
                    )
                }
            }

            // Milestone Chests map
            val chestMap = mutableMapOf<Int, Pair<Boolean, Boolean>>()
            val chestArray = root.optJSONArray("milestoneChests")
            if (chestArray != null) {
                for (i in 0 until chestArray.length()) {
                    val obj = chestArray.getJSONObject(i)
                    chestMap[obj.optInt("target", 0)] = Pair(
                        obj.optBoolean("isUnlocked", false),
                        obj.optBoolean("isClaimed", false)
                    )
                }
            }

            // Daily Login Rewards claimed set
            val claimedRewardDays = mutableSetOf<Int>()
            val rewardArray = root.optJSONArray("dailyRewards")
            if (rewardArray != null) {
                for (i in 0 until rewardArray.length()) {
                    val obj = rewardArray.getJSONObject(i)
                    if (obj.optBoolean("isClaimed", false)) {
                        claimedRewardDays.add(obj.optInt("dayNumber", 0))
                    }
                }
            }

            val dailyStreakDays = root.optInt("dailyStreakDays", 1)
            val lastMissionDayEpoch = root.optLong("lastMissionDayEpoch", 0L)
            val dailyCashEarned = root.optDouble("dailyCashEarned", 0.0)
            val lastWheelSpinTimestampEpoch = root.optLong("lastWheelSpinTimestampEpoch", 0L)

            // Productivity Upgrades map
            val upgMap = mutableMapOf<String, Int>()
            val upgArray = root.optJSONArray("productivityUpgrades")
            if (upgArray != null) {
                for (i in 0 until upgArray.length()) {
                    val obj = upgArray.getJSONObject(i)
                    upgMap[obj.getString("id")] = obj.optInt("level", 0)
                }
            }

            // Expansion maps
            val wonAuctionLotIds = mutableSetOf<String>()
            val aucArray = root.optJSONArray("wonAuctionLotIds")
            if (aucArray != null) {
                for (i in 0 until aucArray.length()) {
                    wonAuctionLotIds.add(aucArray.getString(i))
                }
            }

            val takeoverStakes = mutableMapOf<String, Int>()
            val takeoverArray = root.optJSONArray("takeoverStakes")
            if (takeoverArray != null) {
                for (i in 0 until takeoverArray.length()) {
                    val obj = takeoverArray.getJSONObject(i)
                    takeoverStakes[obj.getString("id")] = obj.optInt("stake", 0)
                }
            }

            val unlockedTechIds = mutableSetOf<String>()
            val techArray = root.optJSONArray("unlockedTechIds")
            if (techArray != null) {
                for (i in 0 until techArray.length()) {
                    unlockedTechIds.add(techArray.getString(i))
                }
            }

            val purchasedLuxuryIds = mutableSetOf<String>()
            val luxArray = root.optJSONArray("purchasedLuxuryIds")
            if (luxArray != null) {
                for (i in 0 until luxArray.length()) {
                    purchasedLuxuryIds.add(luxArray.getString(i))
                }
            }

            // Sponsorship Contracts map: id -> Triple(isSigned, timeRemaining, totalEarned)
            val sponsorshipSavedData = mutableMapOf<String, Triple<Boolean, Int, Double>>()
            val sponsorArray = root.optJSONArray("sponsorshipContracts")
            if (sponsorArray != null) {
                for (i in 0 until sponsorArray.length()) {
                    val obj = sponsorArray.getJSONObject(i)
                    sponsorshipSavedData[obj.getString("id")] = Triple(
                        obj.optBoolean("isSigned", false),
                        obj.optInt("timeRemaining", 0),
                        obj.optDouble("totalEarned", 0.0)
                    )
                }
            }

            // Career Stats
            val statsObj = root.optJSONObject("careerStats")
            val careerStats = if (statsObj != null) {
                CareerStats(
                    totalTaps = statsObj.optLong("totalTaps", 0L),
                    highestCombo = statsObj.optInt("highestCombo", 0),
                    totalCrisesResolved = statsObj.optInt("totalCrisesResolved", 0),
                    totalMiniGamesWon = statsObj.optInt("totalMiniGamesWon", 0),
                    totalStockTrades = statsObj.optInt("totalStockTrades", 0),
                    totalPrestigeResets = statsObj.optInt("totalPrestigeResets", 0),
                    totalPlayTimeSeconds = statsObj.optLong("totalPlayTimeSeconds", 0L),
                    lastSavedTimestamp = statsObj.optLong("lastSavedTimestamp", System.currentTimeMillis())
                )
            } else {
                CareerStats()
            }

            GameLoadResult(
                playerName = playerName,
                selectedAvatarId = selectedAvatarId,
                soundEnabled = soundEnabled,
                hapticsEnabled = hapticsEnabled,
                cash = cash,
                totalCashEarned = totalCashEarned,
                prestigeLevel = prestigeLevel,
                prestigeBonusMultiplier = prestigeBonusMultiplier,
                businessSavedData = bizMap,
                stockSavedData = stockMap,
                hiredExecIds = execHiredSet,
                unlockedAdIds = adUnlockedSet,
                achSavedData = achMap,
                careerStats = careerStats,
                missionSavedData = missionMap,
                chestSavedData = chestMap,
                claimedDailyRewardDays = claimedRewardDays,
                dailyStreakDays = dailyStreakDays,
                lastMissionDayEpoch = lastMissionDayEpoch,
                dailyCashEarned = dailyCashEarned,
                upgradeLevels = upgMap,
                customBusinessNames = bizNamesMap,
                lastWheelSpinTimestampEpoch = lastWheelSpinTimestampEpoch,
                wonAuctionLotIds = wonAuctionLotIds,
                takeoverStakes = takeoverStakes,
                unlockedTechIds = unlockedTechIds,
                purchasedLuxuryIds = purchasedLuxuryIds,
                companyName = companyName,
                adLevels = adLevelsMap,
                sponsorshipSavedData = sponsorshipSavedData
            )
        } catch (e: Exception) {
            null
        }
    }
}

data class GameLoadResult(
    val playerName: String,
    val selectedAvatarId: Int,
    val soundEnabled: Boolean,
    val hapticsEnabled: Boolean,
    val cash: Double,
    val totalCashEarned: Double,
    val prestigeLevel: Int,
    val prestigeBonusMultiplier: Double,
    val businessSavedData: Map<String, Triple<Int, Boolean, Boolean>>,
    val stockSavedData: Map<String, Pair<Int, Double>>,
    val hiredExecIds: Set<String>,
    val unlockedAdIds: Set<String>,
    val achSavedData: Map<String, Triple<Long, Boolean, Boolean>>,
    val careerStats: CareerStats,
    val missionSavedData: Map<String, Triple<Long, Boolean, Boolean>> = emptyMap(),
    val chestSavedData: Map<Int, Pair<Boolean, Boolean>> = emptyMap(),
    val claimedDailyRewardDays: Set<Int> = emptySet(),
    val dailyStreakDays: Int = 1,
    val lastMissionDayEpoch: Long = 0L,
    val dailyCashEarned: Double = 0.0,
    val upgradeLevels: Map<String, Int> = emptyMap(),
    val customBusinessNames: Map<String, String> = emptyMap(),
    val lastWheelSpinTimestampEpoch: Long = 0L,
    val wonAuctionLotIds: Set<String> = emptySet(),
    val takeoverStakes: Map<String, Int> = emptyMap(),
    val unlockedTechIds: Set<String> = emptySet(),
    val purchasedLuxuryIds: Set<String> = emptySet(),
    val companyName: String = "Mon Entreprise",
    val adLevels: Map<String, Int> = emptyMap(),
    val sponsorshipSavedData: Map<String, Triple<Boolean, Int, Double>> = emptyMap()
)


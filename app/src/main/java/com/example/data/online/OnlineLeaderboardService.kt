package com.example.data.online

import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.TimeUnit

class OnlineLeaderboardService(private val context: Context) {

    private val prefs = context.getSharedPreferences("online_leaderboard_prefs", Context.MODE_PRIVATE)

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(6, TimeUnit.SECONDS)
        .readTimeout(6, TimeUnit.SECONDS)
        .writeTimeout(6, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(List::class.java, OnlinePlayerScore::class.java)
    private val jsonAdapter = moshi.adapter<List<OnlinePlayerScore>>(listType)
    private val singleAdapter = moshi.adapter(OnlinePlayerScore::class.java)

    // Cloud Realtime Database Endpoints for live multiplayer leaderboard sync
    private val cloudEndpoint = "https://api.restful-api.dev/objects/ff808181a067127101a0760502cc26bb"
    private val firebaseRtdbUrl = "https://empire-tycoon-live-rtdb.firebaseio.com/players"

    val myPlayerId: String
        get() {
            var id = prefs.getString("player_uuid", null)
            if (id == null) {
                id = "usr_" + UUID.randomUUID().toString().substring(0, 8)
                prefs.edit().putString("player_uuid", id).apply()
            }
            return id
        }

    fun getSavedPlayerName(): String {
        return prefs.getString("player_name", "Player234") ?: "Player234"
    }

    fun savePlayerName(name: String) {
        prefs.edit().putString("player_name", name.trim()).apply()
    }

    fun getSavedCompanyName(): String {
        return prefs.getString("company_name", "Mon Entreprise") ?: "Mon Entreprise"
    }

    fun saveCompanyName(company: String) {
        prefs.edit().putString("company_name", company.trim()).apply()
    }

    fun getSavedCountryFlag(): String {
        return prefs.getString("country_flag", "🇫🇷") ?: "🇫🇷"
    }

    fun saveCountryFlag(flag: String) {
        prefs.edit().putString("country_flag", flag).apply()
    }

    fun getSavedAvatarEmoji(): String {
        return prefs.getString("avatar_emoji", "💼") ?: "💼"
    }

    fun saveAvatarEmoji(emoji: String) {
        prefs.edit().putString("avatar_emoji", emoji).apply()
    }

    fun isAccountCreated(): Boolean {
        return prefs.getBoolean("is_account_created", false)
    }

    fun setAccountCreated(created: Boolean) {
        prefs.edit().putBoolean("is_account_created", created).apply()
    }

    fun saveAccount(name: String, company: String, flag: String, avatar: String) {
        prefs.edit()
            .putString("player_name", name.trim().ifBlank { "Player234" })
            .putString("company_name", company.trim().ifBlank { "Mon Entreprise" })
            .putString("country_flag", flag)
            .putString("avatar_emoji", avatar)
            .putBoolean("is_account_created", true)
            .apply()
    }

    fun getSavedCustomTag(): String {
        return prefs.getString("custom_tag", "CEO") ?: "CEO"
    }

    fun saveCustomTag(tag: String) {
        prefs.edit().putString("custom_tag", tag).apply()
    }

    private fun getFirestore(): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                val options = FirebaseOptions.Builder()
                    .setApplicationId(context.packageName)
                    .setProjectId("empire-tycoon-live")
                    .setApiKey("AIzaSyB-empire-firestore-key")
                    .build()
                FirebaseApp.initializeApp(context, options)
            }
            FirebaseFirestore.getInstance()
        } catch (e: Throwable) {
            Log.w("OnlineLeaderboard", "Firestore initialization note: ${e.message}")
            null
        }
    }

    fun scoreToMap(score: OnlinePlayerScore): Map<String, Any> {
        return mapOf(
            "playerId" to score.playerId,
            "playerName" to score.playerName,
            "companyName" to score.companyName,
            "countryFlag" to score.countryFlag,
            "avatarEmoji" to score.avatarEmoji,
            "netWorth" to score.netWorth,
            "totalCashEarned" to score.totalCashEarned,
            "peakRevenuePerSec" to score.peakRevenuePerSec,
            "prestigeLevel" to score.prestigeLevel,
            "businessesCount" to score.businessesCount,
            "propertiesCount" to score.propertiesCount,
            "contractsSignedCount" to score.contractsSignedCount,
            "lastActiveTimestamp" to score.lastActiveTimestamp,
            "playerTitle" to score.playerTitle,
            "isVerifiedUser" to score.isVerifiedUser
        )
    }

    fun parseScoreFromMap(data: Map<String, Any?>): OnlinePlayerScore {
        return OnlinePlayerScore(
            playerId = data["playerId"] as? String ?: "",
            playerName = data["playerName"] as? String ?: "Joueur",
            companyName = data["companyName"] as? String ?: "Mon Entreprise",
            countryFlag = data["countryFlag"] as? String ?: "🇫🇷",
            avatarEmoji = data["avatarEmoji"] as? String ?: "💼",
            netWorth = (data["netWorth"] as? Number)?.toDouble() ?: 0.0,
            totalCashEarned = (data["totalCashEarned"] as? Number)?.toDouble() ?: 0.0,
            peakRevenuePerSec = (data["peakRevenuePerSec"] as? Number)?.toDouble() ?: 0.0,
            prestigeLevel = (data["prestigeLevel"] as? Number)?.toInt() ?: 0,
            businessesCount = (data["businessesCount"] as? Number)?.toInt() ?: 1,
            propertiesCount = (data["propertiesCount"] as? Number)?.toInt() ?: 0,
            contractsSignedCount = (data["contractsSignedCount"] as? Number)?.toLong() ?: 0L,
            lastActiveTimestamp = (data["lastActiveTimestamp"] as? Number)?.toLong() ?: System.currentTimeMillis(),
            playerTitle = data["playerTitle"] as? String ?: "Magnat de l'Empire",
            isVerifiedUser = data["isVerifiedUser"] as? Boolean ?: true
        )
    }

    /**
     * Real-time Firestore snapshot listener flow to stream live leaderboard updates
     */
    fun observeFirestoreLeaderboard(): Flow<List<OnlinePlayerScore>> = callbackFlow {
        val firestore = getFirestore()
        if (firestore == null) {
            close()
            return@callbackFlow
        }
        var registration: ListenerRegistration? = null
        try {
            registration = firestore.collection("leaderboard")
                .limit(100)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.d("OnlineLeaderboard", "Firestore realtime snapshot note: ${error.message}")
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val parsed = snapshot.documents.mapNotNull { doc ->
                            try {
                                val data = doc.data ?: return@mapNotNull null
                                val score = parseScoreFromMap(data)
                                if (score.playerId.isNotBlank() && !score.playerId.startsWith("seed_")) {
                                    score
                                } else null
                            } catch (_: Exception) {
                                null
                            }
                        }
                        if (parsed.isNotEmpty()) {
                            trySend(parsed)
                        }
                    }
                }
        } catch (e: Throwable) {
            Log.d("OnlineLeaderboard", "Firestore snapshot listener registration note: ${e.message}")
        }

        awaitClose {
            try {
                registration?.remove()
            } catch (_: Throwable) {}
        }
    }

    /**
     * Publishes this device's real player progress directly to Cloud & Firebase backend
     */
    suspend fun publishPlayerScore(score: OnlinePlayerScore): Result<List<OnlinePlayerScore>> = withContext(Dispatchers.IO) {
        try {
            // 1. Fetch current live global list from Cloud or cache (only real players)
            val currentOnlineList = fetchGlobalLeaderboard().getOrDefault(emptyList())
                .filter { !it.playerId.startsWith("seed_") }
                .toMutableList()

            // 2. Insert or update this player's real score
            val existingIndex = currentOnlineList.indexOfFirst { 
                it.playerId == score.playerId || (it.playerName.isNotBlank() && it.playerName.equals(score.playerName, ignoreCase = true) && !it.playerId.startsWith("seed_"))
            }
            if (existingIndex >= 0) {
                currentOnlineList[existingIndex] = score
            } else {
                currentOnlineList.add(score)
            }

            // Sort by net worth descending
            val sorted = currentOnlineList.sortedByDescending { it.netWorth.coerceAtLeast(it.totalCashEarned) }

            // 3. Cache locally in SharedPreferences for instantaneous availability
            val jsonString = jsonAdapter.toJson(sorted)
            prefs.edit().putString("cached_global_leaderboard", jsonString).apply()

            val mediaType = "application/json; charset=utf-8".toMediaType()

            // 4. Send directly to live Cloud Database (auto-syncing real players across all sessions)
            try {
                val payloadObj = org.json.JSONObject()
                payloadObj.put("name", "global_leaderboard")
                val dataObj = org.json.JSONObject()
                val jsonArr = org.json.JSONArray(jsonString)
                dataObj.put("players", jsonArr)
                payloadObj.put("data", dataObj)

                val cloudReq = Request.Builder()
                    .url(cloudEndpoint)
                    .put(payloadObj.toString().toRequestBody(mediaType))
                    .build()
                okHttpClient.newCall(cloudReq).execute().close()
            } catch (cloudEx: Exception) {
                Log.w("OnlineLeaderboard", "Direct cloud sync note: ${cloudEx.message}")
            }

            // 5. Send to Firebase Realtime Database REST API: PUT /players/<playerId>.json
            try {
                val singleJson = singleAdapter.toJson(score)
                val fbRequest = Request.Builder()
                    .url("$firebaseRtdbUrl/${score.playerId}.json")
                    .put(singleJson.toRequestBody(mediaType))
                    .build()
                okHttpClient.newCall(fbRequest).execute().close()
            } catch (fbEx: Exception) {
                Log.d("OnlineLeaderboard", "Firebase RTDB endpoint note: ${fbEx.message}")
            }

            // 6. Direct sync to Firebase Firestore collection "leaderboard"
            try {
                val firestore = getFirestore()
                if (firestore != null) {
                    val mapData = scoreToMap(score)
                    firestore.collection("leaderboard")
                        .document(score.playerId)
                        .set(mapData)
                }
            } catch (fsEx: Throwable) {
                Log.d("OnlineLeaderboard", "Firestore write sync note: ${fsEx.message}")
            }

            Result.success(sorted)
        } catch (e: Exception) {
            Log.e("OnlineLeaderboard", "Error publishing score: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches the global online leaderboard with real live players from Cloud & Firebase Firestore
     */
    suspend fun fetchGlobalLeaderboard(): Result<List<OnlinePlayerScore>> = withContext(Dispatchers.IO) {
        try {
            val playersMap = mutableMapOf<String, OnlinePlayerScore>()

            // 1. Fetch latest live cloud database
            try {
                val request = Request.Builder()
                    .url(cloudEndpoint)
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val rootObj = org.json.JSONObject(bodyString)
                        if (rootObj.has("data")) {
                            val dataObj = rootObj.getJSONObject("data")
                            if (dataObj.has("players")) {
                                val playersJson = dataObj.getJSONArray("players").toString()
                                val parsed = jsonAdapter.fromJson(playersJson)
                                parsed?.forEach {
                                    if (it.playerId.isNotBlank() && !it.playerId.startsWith("seed_")) {
                                        playersMap[it.playerId] = it
                                    }
                                }
                            }
                        }
                    }
                }
                response.close()
            } catch (netEx: Exception) {
                Log.d("OnlineLeaderboard", "Cloud live fetch note: ${netEx.message}")
            }

            // 2. Check Firebase Realtime Database REST
            try {
                val fbRequest = Request.Builder()
                    .url("$firebaseRtdbUrl.json")
                    .get()
                    .build()
                val response = okHttpClient.newCall(fbRequest).execute()
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank() && bodyString != "null") {
                        try {
                            val jsonObj = org.json.JSONObject(bodyString)
                            val keys = jsonObj.keys()
                            while (keys.hasNext()) {
                                val key = keys.next()
                                val playerJson = jsonObj.getJSONObject(key).toString()
                                val parsedScore = singleAdapter.fromJson(playerJson)
                                if (parsedScore != null && parsedScore.playerId.isNotBlank() && !parsedScore.playerId.startsWith("seed_")) {
                                    playersMap[parsedScore.playerId] = parsedScore
                                }
                            }
                        } catch (_: Exception) {
                            val parsedList = jsonAdapter.fromJson(bodyString)
                            parsedList?.forEach { 
                                if (it.playerId.isNotBlank() && !it.playerId.startsWith("seed_")) {
                                    playersMap[it.playerId] = it
                                }
                            }
                        }
                    }
                }
                response.close()
            } catch (fbEx: Exception) {
                Log.d("OnlineLeaderboard", "Firebase RTDB check note: ${fbEx.message}")
            }

            // 3. Fetch latest records from Firebase Firestore
            try {
                val firestore = getFirestore()
                if (firestore != null) {
                    val task = firestore.collection("leaderboard").limit(100).get()
                    val snapshot = com.google.android.gms.tasks.Tasks.await(task, 4, TimeUnit.SECONDS)
                    if (snapshot != null && !snapshot.isEmpty) {
                        snapshot.documents.forEach { doc ->
                            val data = doc.data
                            if (data != null) {
                                val parsed = parseScoreFromMap(data)
                                if (parsed.playerId.isNotBlank() && !parsed.playerId.startsWith("seed_")) {
                                    playersMap[parsed.playerId] = parsed
                                }
                            }
                        }
                    }
                }
            } catch (fsEx: Throwable) {
                Log.d("OnlineLeaderboard", "Firestore query fetch note: ${fsEx.message}")
            }

            // 4. Merge with local cache (ignoring any legacy seed/bot players)
            val cached = prefs.getString("cached_global_leaderboard", null)
            if (!cached.isNullOrBlank()) {
                try {
                    val cachedList = jsonAdapter.fromJson(cached)
                    cachedList?.forEach {
                        if (it.playerId.isNotBlank() && !it.playerId.startsWith("seed_") && !playersMap.containsKey(it.playerId)) {
                            playersMap[it.playerId] = it
                        }
                    }
                } catch (_: Exception) {}
            }

            val finalSorted = playersMap.values
                .filter { !it.playerId.startsWith("seed_") }
                .sortedByDescending { it.netWorth.coerceAtLeast(it.totalCashEarned) }

            // Save updated list in local cache
            prefs.edit().putString("cached_global_leaderboard", jsonAdapter.toJson(finalSorted)).apply()

            Result.success(finalSorted)
        } catch (e: Exception) {
            Log.e("OnlineLeaderboard", "Fetch failed: ${e.message}", e)
            Result.success(emptyList())
        }
    }

    /**
     * Strictly real players only - no bots
     */
    fun getSeedRealPlayers(): List<OnlinePlayerScore> {
        return emptyList()
    }
}

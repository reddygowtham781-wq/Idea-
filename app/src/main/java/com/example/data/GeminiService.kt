package com.example.data

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Gemini Request / Response Models using Moshi ---

@JsonClass(generateAdapter = true)
data class GeminiPart(
    @Json(name = "text") val text: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiContent(
    @Json(name = "parts") val parts: List<GeminiPart>
)

@JsonClass(generateAdapter = true)
data class GeminiRequest(
    @Json(name = "contents") val contents: List<GeminiContent>,
    @Json(name = "systemInstruction") val systemInstruction: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiCandidate(
    @Json(name = "content") val content: GeminiContent? = null
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
    @Json(name = "candidates") val candidates: List<GeminiCandidate>? = null
)

// --- Retrofit Service Interface ---

interface GeminiApi {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

// --- Service Implementation ---

object GeminiService {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    private val api: GeminiApi = retrofit.create(GeminiApi::class.java)

    /**
     * Obtains feedback for a new or existing business/technical idea.
     */
    suspend fun getIdeaFeedback(
        title: String,
        description: String,
        category: String,
        audience: String,
        valueProp: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the AI Studio Secrets panel."
        }

        val prompt = """
            Validate and provide professional advice/constructive critique for this idea:
            Title: $title
            Category: $category
            Description: $description
            Target Audience: $audience
            Value Proposition: $valueProp

            Please structure your response with these clear sections using Markdown:
            1. **Overall Potential**: High-level feasibility, strengths, and market appeal.
            2. **Key Challenges & Risks**: What are the 2-3 biggest risks or mistakes common to this kind of idea?
            3. **Target Audience Refining**: Suggestions to better reach or define the market.
            4. **Actionable Roadmap**: Give 3-4 clear, concrete next milestones to validate or build this idea.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are an expert product strategist, startup mentor, and technical advisor. Provide encouraging but highly realistic, analytical, and professional advice."))
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No advice returned. Please try modifying your details."
        } catch (e: Exception) {
            "Failed to reach Gemini: ${e.localizedMessage ?: e.message}"
        }
    }

    /**
     * Resolves a mistake, challenge, or roadblock.
     */
    suspend fun getMistakeSolution(
        ideaTitle: String,
        ideaDescription: String,
        mistakeDescription: String,
        impact: String
    ): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return "Please configure your GEMINI_API_KEY in the AI Studio Secrets panel."
        }

        val prompt = """
            I am working on the idea "$ideaTitle" ($ideaDescription) and have encountered a challenge/mistake.
            
            Challenge / Roadblock:
            $mistakeDescription
            
            Estimated Impact Level: $impact

            Please analyze this obstacle and provide professional troubleshooting advice. Structure your response with:
            1. **Root Cause Analysis**: Why does this mistake or obstacle typically happen?
            2. **Immediate Workarounds**: Short-term fixes or actions to unblock immediately.
            3. **Strategic Solution**: Long-term solutions, processes, or code/architecture recommendations to prevent it in the future.
            4. **AI Coach Tip**: A piece of encouragement or mental shift to overcome this obstacle.
        """.trimIndent()

        val request = GeminiRequest(
            contents = listOf(
                GeminiContent(parts = listOf(GeminiPart(text = prompt)))
            ),
            systemInstruction = GeminiContent(
                parts = listOf(GeminiPart(text = "You are an expert troubleshooter, engineering mentor, and startup coach. Provide deep, tactical, actionable advice with positive reinforcement."))
            )
        )

        return try {
            val response = api.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: "No solution suggestions returned. Try elaborating on your mistake description."
        } catch (e: Exception) {
            "Failed to reach Gemini: ${e.localizedMessage ?: e.message}"
        }
    }
}

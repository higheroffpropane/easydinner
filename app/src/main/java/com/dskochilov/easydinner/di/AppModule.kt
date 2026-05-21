package com.dskochilov.easydinner.di

import com.dskochilov.easydinner.BuildConfig
import com.dskochilov.easydinner.data.local.FavoriteRecipesRepository
import com.dskochilov.easydinner.data.remote.EasyDinnerApi
import com.dskochilov.easydinner.data.remote.KtorEasyDinnerApi
import com.dskochilov.easydinner.data.repository.RecipeRepositoryImpl
import com.dskochilov.easydinner.domain.repository.RecipeRepository
import com.dskochilov.easydinner.domain.usecase.SearchRecipesUseCase
import com.dskochilov.easydinner.presentation.favorites.FavoriteRecipesViewModel
import com.dskochilov.easydinner.presentation.search.RecipeSearchViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    single {
        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }
    }
    single<EasyDinnerApi> { KtorEasyDinnerApi(get(), BuildConfig.API_BASE_URL) }
    single { FavoriteRecipesRepository(get()) }
    single<RecipeRepository> { RecipeRepositoryImpl(get()) }
    single { SearchRecipesUseCase(get()) }
    viewModelOf(::FavoriteRecipesViewModel)
    viewModelOf(::RecipeSearchViewModel)
}

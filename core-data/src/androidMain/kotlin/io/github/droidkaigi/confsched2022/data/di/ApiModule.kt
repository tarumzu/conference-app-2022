package io.github.droidkaigi.confsched2022.data.di

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.datastore.DataStoreSettings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.droidkaigi.confsched2022.data.NetworkService
import io.github.droidkaigi.confsched2022.data.UserDatastore
import io.github.droidkaigi.confsched2022.data.auth.AuthApi
import io.github.droidkaigi.confsched2022.data.auth.Authenticator
import io.github.droidkaigi.confsched2022.data.auth.AuthenticatorImpl
import io.github.droidkaigi.confsched2022.data.sessions.defaultKtorConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import javax.inject.Singleton
import okhttp3.OkHttpClient

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {
    @Provides
    @Singleton
    fun provideNetworkService(
        httpClient: HttpClient,
        authApi: AuthApi
    ): NetworkService {
        return NetworkService(httpClient, authApi)
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        httpClient: HttpClient,
        userDatastore: UserDatastore,
        authenticator: Authenticator
    ): AuthApi {
        return AuthApi(httpClient, userDatastore, authenticator)
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        okHttpClient: OkHttpClient
    ): HttpClient {
        val httpClient = HttpClient(OkHttp) {
            engine {
                config {
                    preconfigured = okHttpClient
                }
            }
            defaultKtorConfig()
        }
        return httpClient
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient()
    }

    @Provides
    @Singleton
    fun provideAuthenticator(): Authenticator {
        return AuthenticatorImpl()
    }

    private val Context.dataStore by preferencesDataStore(
        name = UserDatastore.NAME,
    )

    @Provides
    @Singleton
    fun provideFlowSettings(application: Application): FlowSettings {
        return DataStoreSettings(datastore = application.dataStore)
    }
    @Provides
    @Singleton
    fun providePreferenceDatastore(flowSettings: FlowSettings): UserDatastore {
        return UserDatastore(flowSettings)
    }
}
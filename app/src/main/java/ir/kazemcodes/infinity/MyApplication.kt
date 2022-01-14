package ir.kazemcodes.infinity

import android.app.Application
import android.webkit.WebView
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.firebase.analytics.FirebaseAnalytics
import com.zhuinden.simplestack.GlobalServices
import com.zhuinden.simplestackextensions.servicesktx.add
import dagger.hilt.android.HiltAndroidApp
import ir.kazemcodes.infinity.sources.Extensions
import ir.kazemcodes.infinity.domain.use_cases.local.LocalUseCase
import ir.kazemcodes.infinity.domain.use_cases.preferences.PreferencesUseCase
import ir.kazemcodes.infinity.domain.use_cases.remote.RemoteUseCase
import ir.kazemcodes.infinity.notification.Notifications
import ir.kazemcodes.infinity.sources.utils.NetworkHelper
import ir.kazemcodes.infinity.util.SourceMapper
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.bindSingleton
import timber.log.Timber
import javax.inject.Inject


@HiltAndroidApp
class MyApplication : Application(), DIAware, Configuration.Provider {

    lateinit var globalServices: GlobalServices
        private set

    @Inject
    lateinit var localUseCase: LocalUseCase

    @Inject
    lateinit var remoteUseCase: RemoteUseCase

    @Inject
    lateinit var preferencesUseCase: PreferencesUseCase

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val di: DI = DI.lazy {
        import(KodeinModule)
    }
    private val KodeinModule = DI.Module("AppModule") {
        bindSingleton<NetworkHelper> { NetworkHelper(this@MyApplication) }
        bindSingleton<Extensions> { Extensions(this@MyApplication) }
        bindSingleton<PreferencesUseCase> { preferencesUseCase }
        bindSingleton<WebView> { WebView(this@MyApplication) }
    }


    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        globalServices = GlobalServices.builder()
            .add(localUseCase)
            .add(remoteUseCase)
            .add(preferencesUseCase)
            .add(SourceMapper(this))
            .build()
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        setupNotificationChannels()


    }

    private fun setupNotificationChannels() {
        try {
            Notifications.createChannels(this)
        } catch (e: Exception) {
            Timber.e("Failed to modify notification channels")
        }
    }

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()



}




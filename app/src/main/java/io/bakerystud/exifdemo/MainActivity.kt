package io.bakerystud.exifdemo

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber
import ru.terrakok.cicerone.*
import ru.terrakok.cicerone.android.support.*

class MainActivity : AppCompatActivity() {

    lateinit var deviceId: String

    private lateinit var navigatorHolder: NavigatorHolder
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        initNavigation()
        router.replaceScreen(Screens.enterCode)

        val subscribe = Single.create<String> {
            it.onSuccess(FirebaseInstanceId.getInstance().id)
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { deviceId ->
                this.deviceId = deviceId
                if (Build.VERSION.SDK_INT < 23 || PermissionChecker.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PermissionChecker.PERMISSION_GRANTED
                ) {
                    onPermitted()
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        322
                    )
                }
            }
    }

    private fun initNavigation() {
        val cicerone = Cicerone.create()
        navigatorHolder = cicerone.navigatorHolder
        navigatorHolder.setNavigator(SupportAppNavigator(this, R.id.container))
        router = cicerone.router
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            322 -> {
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    onPermitted()
                } else {
                    finish()
                }
                return
            }
        }
    }

    private fun onPermitted() {
        val subscribe = Completable.create {
            DataPublisher(this, deviceId).publish()
            it.onComplete()
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onUploadFinished)
    }

    private fun onUploadFinished() {
        showSnackbar("Upload complete")
    }
}
fun Activity.showSnackbar(message: String?, short: Boolean = true) {
    message ?: return
    val duration = if (short) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG
    Snackbar.make(this.findViewById(android.R.id.content), message, duration).show()
}

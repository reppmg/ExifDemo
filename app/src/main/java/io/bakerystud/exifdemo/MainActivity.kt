package io.bakerystud.exifdemo

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.core.view.forEach
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import timber.log.Timber

class MainActivity : AppCompatActivity(), EnterCodeFragment.EnterCodeController {

    lateinit var deviceId: String

    private lateinit var navigatorHolder: NavigatorHolder
    private lateinit var router: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        Timber.plant(Timber.DebugTree())
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        FirebaseApp.initializeApp(this)

        initNavigation()

        val subscribe = Single.create<String> {
            it.onSuccess(FirebaseInstanceId.getInstance().id)
        }.subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { deviceId ->
                this.deviceId = deviceId
                val fragmentManager = this.supportFragmentManager
                val fragments = fragmentManager.fragments
                for (fragment in fragments) {
                    (fragment as? EnterCodeFragment)?.showCode(deviceId)
                }
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

        initPagerAndNavigation()
    }

    private fun initPagerAndNavigation() {
        pager.adapter = object : FragmentStatePagerAdapter(
            supportFragmentManager,
            BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        ) {
            private val fragments = listOf(
                EnterCodeFragment(),
                EventsListFragment(),
                EnterCodeFragment()
            )

            override fun getItem(position: Int): Fragment = fragments[position]

            override fun getCount(): Int = 3
        }

        pager.addOnPageChangeListener(OnPageSelectedListener { position ->
            bottomBar.menu.forEach {
                it.isChecked = false
            }
            bottomBar.menu[position].isChecked = true
        })

        bottomBar.inflateMenu(R.menu.navigation)
        bottomBar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_qr -> pager.currentItem = 0
                R.id.item_events -> pager.currentItem = 1
                R.id.item_friends_list -> pager.currentItem = 2
            }
            true
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
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onUploadFinished, this::onError)
    }

    private fun onError(error: Throwable?) {
        Timber.e(error)
        showSnackbar("Something went wrong: ${error?.message}")
        FirebaseAnalytics.getInstance(this).logEvent("errorrrr", Bundle().apply {
            putString("message", error?.message.toString())
        })
    }

    private fun onDownloadFinished(photos: List<PhotoRecord>) {
        (currentFragment() as EnterCodeFragment).progressVisible(false)
        if (photos.isNullOrEmpty()) {
            showSnackbar("No data from this user yet")
            return
        }
        DataStorage.frenPhotos.onNext(photos)
        Timber.d("onDownloadFinished")
    }

    private fun onUploadFinished() {
        showSnackbar("Upload complete")
    }

    override fun onNextClicked(code: String) {
        val download = DataFetcher().fetch(code)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(this::onDownloadFinished, this::onFetchError)
    }

    private fun onFetchError(error: Throwable?) {
        Timber.e(error)
        showSnackbar("Error fetching data: ${error?.message}")
        (currentFragment() as EnterCodeFragment).progressVisible(false)
    }
}

fun Activity.showSnackbar(message: String?, short: Boolean = true) {
    message ?: return
    val duration = if (short) Snackbar.LENGTH_SHORT else Snackbar.LENGTH_LONG
    Snackbar.make(this.findViewById(android.R.id.content), message, duration).show()
}


fun FragmentActivity.currentFragment(): Fragment? {
    val fragmentManager = this.supportFragmentManager
    val fragments = fragmentManager.fragments
    for (fragment in fragments) {
        if (fragment != null && fragment.isVisible)
            return fragment
    }
    return null
}
package com.nazmusa.launcher

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var pager: ViewPager2
    private lateinit var gestureDetector: GestureDetector
    private var allApps: List<AppInfoModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ThemeManager.styleFor(PrefsManager.theme))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        allApps = AppRepository.loadAllApps(this)
        setupClock()
        setupPages()
        setupDock()
        setupDrawerGesture()
    }

    override fun onResume() {
        super.onResume()
        // Reload in case the theme/font/shape/columns changed in Settings.
        setupPages()
        setupDock()
    }

    private fun setupClock() {
        val tvClock = findViewById<android.widget.TextView>(R.id.tvClock)
        val tvDate = findViewById<android.widget.TextView>(R.id.tvDate)
        tvClock.typeface = FontManager.typefaceFor(PrefsManager.font)
        tvDate.typeface = FontManager.typefaceFor(PrefsManager.font)
        tvClock.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        tvDate.text = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
    }

    private fun setupPages() {
        pager = findViewById(R.id.homePager)
        val perPage = PrefsManager.gridColumns * 4
        val pages = allApps.chunked(perPage).ifEmpty { listOf(emptyList()) }
        pager.adapter = HomePagerAdapter(
            pages,
            onClick = { AppRepository.launch(this, it) },
            onLongClick = { addToDock(it); true }
        )
    }

    private fun setupDock() {
        val dock = findViewById<android.widget.LinearLayout>(R.id.dock)
        dock.removeAllViews()
        val favorites = allApps.filter { PrefsManager.dockPackages.contains(it.packageName) }.take(5)
        favorites.forEach { app ->
            val iv = layoutInflater.inflate(R.layout.item_dock, dock, false) as android.widget.ImageView
            val sizePx = (52 * resources.displayMetrics.density).toInt()
            iv.setImageDrawable(IconShapeUtils.apply(app.icon, PrefsManager.iconShape, sizePx))
            iv.setOnClickListener { AppRepository.launch(this, app) }
            dock.addView(iv)
        }
    }

    private fun addToDock(app: AppInfoModel) {
        val current = PrefsManager.dockPackages.toMutableSet()
        if (current.size >= 5) return
        current.add(app.packageName)
        PrefsManager.dockPackages = current
        setupDock()
    }

    private fun setupDrawerGesture() {
        val root = findViewById<android.view.View>(R.id.rootHome)
        val drawerContainer = findViewById<android.widget.FrameLayout>(R.id.drawerContainer)

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent, velocityX: Float, velocityY: Float
            ): Boolean {
                if (e1 != null && e1.y - e2.y > 150 && Math.abs(velocityY) > 400) {
                    openDrawer(drawerContainer)
                    return true
                }
                return false
            }

            override fun onLongPress(e: MotionEvent) {
                openSettings()
            }
        })

        root.setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }
    }

    private fun openDrawer(container: android.widget.FrameLayout) {
        container.visibility = android.view.View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.drawerContainer, AppDrawerFragment(), AppDrawerFragment.TAG)
            .commit()
    }

    private fun openSettings() {
        SettingsDialog { recreate() }.show(supportFragmentManager, SettingsDialog.TAG)
    }

    override fun onBackPressed() {
        val container = findViewById<android.widget.FrameLayout>(R.id.drawerContainer)
        if (container.visibility == android.view.View.VISIBLE) {
            container.visibility = android.view.View.GONE
            supportFragmentManager.findFragmentByTag(AppDrawerFragment.TAG)?.let {
                supportFragmentManager.beginTransaction().remove(it).commit()
            }
        } else {
            super.onBackPressed()
        }
    }
}

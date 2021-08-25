package com.example.scoreup.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.scoreup.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_news_display.*

class NewsDisplayActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration2: AppBarConfiguration
    private lateinit var mNavController: NavController
    private lateinit var mDialog: AlertDialog
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_display)
        setUpActionBar()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        mNavController = findNavController(R.id.nav_host_fragment)

        drawerLayout = findViewById(R.id.drawer_layout)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_news_ongoing, R.id.navigation_news_upcoming
            )
        )
        appBarConfiguration2 = AppBarConfiguration(
            setOf(
                R.id.action_settings, R.id.action_log_out
            ), drawerLayout
        )


        nav_view_2.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.action_log_out -> {
                    showAlertDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }

        setupActionBarWithNavController(mNavController, appBarConfiguration)
        navView.setupWithNavController(mNavController)
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle(R.string.log_out_alert_dialog_title_text)
        builder.setMessage(R.string.log_out_alert_dialog_message_text)

        builder.setPositiveButton(R.string.yes_text) { dialogInterface, _ ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LogInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(R.string.cancel_text) { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        mDialog = builder.create()
        mDialog.setCancelable(false)
        mDialog.show()
    }
    private fun setUpActionBar() {
        setSupportActionBar(tb_nd_back)

        val actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setIcon(R.drawable.ic_vector_hamburger_icon)
        actionBar.setHomeButtonEnabled(true)

        tb_nd_back.setOnClickListener { drawerLayout.open() }
    }

    override fun onBackPressed() {

        if (drawerLayout.isOpen) {
            drawerLayout.close()
        } else {
            super.onBackPressed()
        }
    }
}
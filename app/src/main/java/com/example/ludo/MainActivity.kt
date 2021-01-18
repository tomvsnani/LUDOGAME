package com.example.ludo

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.ludo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadFragment(CoinsFragment(), true)


        setUpToolBar()


    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean) {
        if (addToBackStack)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, fragment).addToBackStack(null).commit()


            }
        else
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, fragment).commit()


            }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setUpToolBar() {

        drawerLayout = binding.drawerlayout

        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, binding.drawerlayout, R.string.openDrawer,
            R.string.closeDrawer
        )

        setSupportActionBar(binding.maintoolbar)


        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        actionBarDrawerToggle.apply {
            isDrawerIndicatorEnabled = false
            setHomeAsUpIndicator(
                resources.getDrawable(
                    R.drawable.ic_hamburger, null
                )
            )
        }


    }


    fun setUpFragmentsToolbarProperties(
        toolbarText: String,
        isDrawerLocked: Boolean = false,
        @SuppressLint("UseCompatLoadingForDrawables") drawerIcon: Drawable = resources.getDrawable(
            R.drawable.ic_hamburger,
            null
        )
    ) {
        this.apply {
            if (isDrawerLocked)
                binding.drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            else
                binding.drawerlayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            setToolBarText(toolbarText)
            actionBarDrawerToggle.isDrawerIndicatorEnabled = false
            actionBarDrawerToggle.setHomeAsUpIndicator(drawerIcon)

        }
    }

    private fun setToolBarText(toolbarText: String) {
        binding.titleEditText.setText(toolbarText)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START)
            else
                drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
}
package com.example.ludo

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.ludo.databinding.ActivityMainBinding
import com.example.ludo.databinding.AlertDialogLayoutBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var gameType: String

    @JvmField
    var retrofit: RetrofitInterface? = null

    var isHost = false


    lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        retrofit = getRetrofit()
        setContentView(binding.root)


        if (getPreferences(Activity.MODE_PRIVATE).getString(Constants.USERIDCONSTANT, "")
                .isNullOrEmpty()
        )

            loadFragment(LoginFragment(), true)
        else
            loadFragment(SelectAGameFragment(), true,"home")

        binding.navigationView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    supportFragmentManager.popBackStackImmediate("home", 0)
                }
                R.id.profile -> {
                    loadFragment(ProfileFragment(), true)

                }
                R.id.logout -> {
                    getPreferences(Activity.MODE_PRIVATE).edit().clear().apply()

                }
            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return@setNavigationItemSelectedListener true
        }



        setUpToolBar()


        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> loadFragment(ProfileFragment(), true)
                R.id.rules -> loadFragment(RulesFragment(), true)
            }
            return@setOnNavigationItemSelectedListener true
        }


    }

    fun getUserId(): String {
        return getPreferences(Activity.MODE_PRIVATE)
            .getString(Constants.USERIDCONSTANT, "")!!
    }

    fun sessionManageMent(userModelClass: UserModelClass) {
        getPreferences(Activity.MODE_PRIVATE).apply {
            edit().putString(Constants.USERNAMECONSTANT, userModelClass.username)
                .putString(Constants.USERPHONECONSTANT, userModelClass.phone_number)
                .putString(Constants.EMAILCONSTANT, userModelClass.email)
                .putString(Constants.USERIDCONSTANT, userModelClass.id)
                .apply()

        }
    }

    fun loadFragment(fragment: Fragment, addToBackStack: Boolean = true,backstackname:String?=null) {

        if (supportFragmentManager.fragments.size > 0)

            for (i in supportFragmentManager.fragments) {
                if (i.javaClass.simpleName == fragment.javaClass.simpleName) {
                    beginFrgmentTransaction(false, i, backstackname)
                    break

                } else {
                    beginFrgmentTransaction(true, fragment,backstackname)
                    break
                }
            }
        else
            beginFrgmentTransaction(addToBackStack, fragment, backstackname)


    }

    private fun beginFrgmentTransaction(
        addToBackStack: Boolean,
        i: Fragment,
        backstackname: String?
    ) {
        if (addToBackStack)
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, i).addToBackStack(backstackname).commit()


            }
        else
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.container, i).commit()


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
                ResourcesCompat.getDrawable(resources, R.drawable.ic_hamburger, null)
            )
        }


    }

    fun showToast(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun setUpFragmentsToolbarProperties(
        toolbarText: String,
        isDrawerLocked: Boolean = false,
      drawerIcon: Drawable? = ResourcesCompat.getDrawable(
            resources,
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

            if(drawerIcon==null)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                actionBarDrawerToggle.isDrawerIndicatorEnabled = false
                actionBarDrawerToggle.setHomeAsUpIndicator(drawerIcon)
            }

        }
    }

    private fun setToolBarText(toolbarText: String) {
        binding.titleEditText.text = toolbarText
        binding.titleEditText.setTextColor(Color.WHITE)
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

    fun getUserData(): UserModelClass {
        return UserModelClass()
    }

    fun getOpponentUserData(): UserModelClass {
        return UserModelClass("2", "Raghu", "500")
    }

    fun displayGeneralAlertDialog(text: String = "", src: Int = 0, type: String = "") {
        var alert = AlertDialog.Builder(this).create()
        var view = layoutInflater.inflate(R.layout.alert_dialog_layout, null, false)
        alert.setView(view)
        var binding = AlertDialogLayoutBinding.bind(view)
        binding.alertDialogTextView.text = text
        Glide.with(this).load(src).into(binding.alertDialogImageView)
        alert.window?.decorView?.rootView?.setBackgroundColor(Color.TRANSPARENT)

//        alert?.window?.setLayout(
//            resources.displayMetrics.widthPixels / 2,
//            ViewGroup.LayoutParams.WRAP_CONTENT
//        )

        alert.show()


    }

    private fun getRetrofit(): RetrofitInterface {
        return Retrofit.Builder().let {
            it.addConverterFactory(GsonConverterFactory.create())
            it.baseUrl(Constants.BASEURL)
            it.build().create(RetrofitInterface::class.java)
        }
    }





}
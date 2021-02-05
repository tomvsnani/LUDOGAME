package com.example.ludo.ui.activities

import com.example.ludo.data.GameMatchedPlayerDetailsModelClass
import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.example.ludo.*
import com.example.ludo.data.Data
import com.example.ludo.data.GameResultModelClass
import com.example.ludo.data.UserModelClass
import com.example.ludo.databinding.ActivityMainBinding
import com.example.ludo.databinding.AlertDialogLayoutBinding
import com.example.ludo.ui.fragments.*
import com.example.ludo.ui.fragments.login.LoginFragment
import com.example.ludo.utils.Constants
import com.example.ludo.utils.RetrofitInterface
import com.freshchat.consumer.sdk.Freshchat
import com.freshchat.consumer.sdk.FreshchatConfig
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var gameType: String
    var gameResultModel: GameResultModelClass? = null
    var isGamesultSubmitted = false
    var gameFeeMutableList: MutableLiveData<String> = MutableLiveData()
    var profileDetailsLiveData: MutableLiveData<String> = MutableLiveData()


    var gameDetailsLiveData: MutableLiveData<List<Data>> = MutableLiveData()

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
            loadFragment(SelectAGameFragment(), true, "home")

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
                    loadFragment(LoginFragment())
                }

            }
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            return@setNavigationItemSelectedListener true
        }

        val config = FreshchatConfig(
            "5abd7a5e-e73d-4087-8f61-e4b8c55011d3",
            "0f5d7ac8-467a-4475-9403-043f7f092c5a"
        )
        config.setDomain("msdk.in.freshchat.com")
        Freshchat.getInstance(applicationContext).init(config)

        setUpToolBar()


        binding.bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.profile -> loadFragment(ProfileFragment(), true)
                R.id.rules -> loadFragment(RulesFragment(), true)
                R.id.chatwithadmin -> Freshchat.showConversations(getApplicationContext());
                R.id.stories -> loadFragment(StoriesFragment(), true)

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


    override fun onStart() {
        super.onStart()
        getUserCoins()
    }

    fun loadFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        backstackname: String? = null
    ) {

        if (supportFragmentManager.fragments.size > 0)

            for (i in supportFragmentManager.fragments) {
                if (i.javaClass.simpleName == fragment.javaClass.simpleName) {
                    beginFrgmentTransaction(false, i, backstackname)
                    break

                } else {
                    beginFrgmentTransaction(true, fragment, backstackname)
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

            if (drawerIcon == null)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
            else {
                supportActionBar?.setDisplayHomeAsUpEnabled(true)
                actionBarDrawerToggle.isDrawerIndicatorEnabled = false
                actionBarDrawerToggle.setHomeAsUpIndicator(drawerIcon)
            }

        }
    }

    fun setToolBarText(toolbarText: String) {
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
            it.client(
                OkHttpClient.Builder().addInterceptor(object : Interceptor {
                    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
                        val request = chain.request()

                        val response: okhttp3.Response = chain.proceed(request)
                        val rawJson: String = response.body()?.string()!!
                        Log.d("interceptor", rawJson)


                        // Re-create the response before returning it because body can be read only once

                        // Re-create the response before returning it because body can be read only once
                        return response.newBuilder()
                            .body(ResponseBody.create(response.body()?.contentType(), rawJson))
                            .build()
                    }
                }).build()

            )
            it.addConverterFactory(GsonConverterFactory.create())
            it.baseUrl(Constants.BASEURL)
            it.build().create(RetrofitInterface::class.java)
        }
    }


    fun getPlayersDetails(gameId: String?) {
        if (gameType == Constants.LUDOGAMETYPE)
            getLudoPlayerDetails(gameId)
        else
            getSnakePlayerDetails(gameId)
    }

    private fun getLudoPlayerDetails(gameId: String?) {
        binding.progressbar.visibility = View.VISIBLE
        retrofit?.playersGameMatchDetails(gameId!!)?.enqueue(
            object : Callback<GameMatchedPlayerDetailsModelClass> {
                override fun onFailure(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    t: Throwable
                ) {
                    showToast(t.toString())
                    binding.progressbar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    response: retrofit2.Response<GameMatchedPlayerDetailsModelClass>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            //  binding.roomcodeedittext.setText("helloomg")
                            gameDetailsLiveData.value = response.body()?.data
                            gameFeeMutableList.value = response.body()?.entryfee
                        } else {
                            showToast(response.body()?.message!!)
                        }
                    } else
                        showToast(response.toString())
                    binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    fun getUserCoins() {
        Log.d("inloginfrag", "inlogin")
        binding.progressbar.visibility = View.VISIBLE
        retrofit?.getProfileData(getUserId())?.enqueue(
            object : Callback<GameMatchedPlayerDetailsModelClass> {
                override fun onFailure(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    t: Throwable
                ) {
                    showToast(t.toString())
                    binding.progressbar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    response: Response<GameMatchedPlayerDetailsModelClass>
                ) {
                    if (response.isSuccessful) {
                        if (response?.body()?.status == "1") {
                            binding.apply {
                                if (response.body()?.data != null && response.body()?.data?.isNotEmpty()!!) {


                                    profileDetailsLiveData.value = response.body()?.data!![0].wallet
                                }
                            }
                        } else {
                            showToast(response.body()?.message!!)
                        }
                    } else {
                        showToast(response.toString())
                    }
                    binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    private fun getSnakePlayerDetails(gameId: String?) {
        binding.progressbar.visibility = View.VISIBLE
        retrofit?.playersGameMatchDetails_snake(gameId!!)?.enqueue(
            object : Callback<GameMatchedPlayerDetailsModelClass> {
                override fun onFailure(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    t: Throwable
                ) {
                    showToast(t.toString())
                    binding.progressbar.visibility = View.GONE
                }

                override fun onResponse(
                    call: Call<GameMatchedPlayerDetailsModelClass>,
                    response: retrofit2.Response<GameMatchedPlayerDetailsModelClass>
                ) {
                    if (response.isSuccessful) {
                        if (response.body()?.status == "1") {
                            //  binding.roomcodeedittext.setText("helloomg")
                            gameFeeMutableList.value = response.body()?.entryfee
                            gameDetailsLiveData.value = response.body()?.data
                        } else {
                            showToast(response.body()?.message!!)
                        }
                    } else
                        showToast(response.toString())
                    binding.progressbar.visibility = View.GONE
                }
            }
        )
    }


    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(R.id.container) is SelectAGameFragment)
            finish()
        else
            super.onBackPressed()
    }

}
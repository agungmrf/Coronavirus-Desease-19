package com.example.coronavirus.features.onboarding

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.coronavirus.ProjectApplication
import com.example.coronavirus.R
import com.example.coronavirus.adapter.BaseActivity
import com.example.coronavirus.adapter.ViewPagerAdapter
import com.example.coronavirus.databinding.ActivityOnboardingBinding
import com.example.coronavirus.di.ViewModelFactory
import com.example.coronavirus.features.main.view.MainActivity
import com.example.coronavirus.features.onboarding.viewmodel.OnboardingViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class OnboardingActivity : BaseActivity<OnboardingViewModel>() {
    private val binding by viewBinding(ActivityOnboardingBinding::inflate)
    private lateinit var auth: FirebaseAuth

    override fun nightMode(): Int = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM

    override var viewModel: OnboardingViewModel
        get() = ViewModelProvider(this, ViewModelFactory{OnboardingViewModel()})[OnboardingViewModel::class.java]
        set(value) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        (application as ProjectApplication).appComponent.inject(viewModel)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        window.statusBarColor = Color.WHITE

        FirebaseApp.initializeApp(this)
        auth = Firebase.auth

        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        init()
    }

    private fun init(){
        binding.btnNext.setOnClickListener {
            next()
        }

        binding.btnLogin.setOnClickListener {
            signIn()
        }

        setupObserver()
        setupTab()

        viewModel.loginLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                val account = task.getResult(ApiException::class.java)!!
                showLoadingDialog(getString(R.string.login_msg))
                viewModel.firebaseAuthWithGoogle(account.idToken!!, auth, this)
            }
        }
    }

    private fun setupObserver(){
        viewModel.apply {
            loginStatus.observe(this@OnboardingActivity, { status ->
                if (status) {
                    openMain()
                } else {
                    Toast.makeText(this@OnboardingActivity, getString(R.string.gagallogin), Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    private fun signIn() {
        viewModel.loginLauncher.launch(viewModel.prepareSignIn(getString(R.string.token)).signInIntent)
    }

    private fun openMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun next(){
        val currPos: Int = binding.pagerOnboarding.currentItem
        if ((currPos + 1) != binding.pagerOnboarding.adapter?.itemCount) {
            binding.pagerOnboarding.currentItem = currPos + 1
        }
    }

    private fun setupTab(){
        val fragments = ArrayList<Fragment>()
        fragments.add(OnboardingFragment.newInstance(1))
        fragments.add(OnboardingFragment.newInstance(2))
        fragments.add(OnboardingFragment.newInstance(3))

        val fragmentAdapter = ViewPagerAdapter(fragments = fragments, lifecycle = lifecycle, manager = supportFragmentManager)
        binding.pagerOnboarding.apply {
            adapter = fragmentAdapter
            registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    if (position == 2) {
                        binding.btnNext.visibility = GONE
                        binding.lytLogin.visibility = VISIBLE
                    } else {
                        binding.btnNext.visibility = VISIBLE
                        binding.lytLogin.visibility = GONE
                    }
                    super.onPageSelected(position)
                }
            })
        }

        binding.dotsIndicator.apply {
            setViewPager2(binding.pagerOnboarding)
        }
    }
}
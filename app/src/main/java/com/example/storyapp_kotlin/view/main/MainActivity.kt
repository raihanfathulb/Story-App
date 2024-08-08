package com.example.storyapp_kotlin.view.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.storyapp_kotlin.R
import com.example.storyapp_kotlin.adapter.LoadingStateAdapter
import com.example.storyapp_kotlin.adapter.StoryListAdapter
import com.example.storyapp_kotlin.data.api.Story
import com.example.storyapp_kotlin.databinding.ActivityMainBinding
import com.example.storyapp_kotlin.utils.animateVisibility
import com.example.storyapp_kotlin.view.create.CreateActivity
import com.example.storyapp_kotlin.view.map.MapActivity
import com.example.storyapp_kotlin.view.welcome.WelcomeActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryListAdapter
    private lateinit var loadingStateAdapter: LoadingStateAdapter

    private var token: String = ""
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        token = intent.getStringExtra(EXTRA_TOKEN)!!

        setSwipeRefreshLayout()
        setRecyclerView()
        observeStories()

        binding.fabCreateStory.setOnClickListener {
            Intent(this, CreateActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                viewModel.saveUserToken("")
                Intent(this, WelcomeActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
                true
            }
            R.id.menu_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            R.id.menu_map -> {
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setSwipeRefreshLayout() {
        binding.swipeRefresh.setOnRefreshListener {
            listAdapter.refresh()
            binding.viewLoading.animateVisibility(false)
        }
    }

    private fun setRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        listAdapter = StoryListAdapter()
        loadingStateAdapter = LoadingStateAdapter { listAdapter.retry() }

        binding.rvStories.apply {
            layoutManager = linearLayoutManager
            adapter = listAdapter.withLoadStateFooter(
                footer = loadingStateAdapter
            )
        }
    }

    private fun observeStories() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getStories(token).collectLatest { pagingData ->
                    listAdapter.submitData(pagingData)
                }
            }
        }

        listAdapter.addLoadStateListener { loadState ->
            binding.swipeRefresh.isRefreshing = loadState.refresh is LoadState.Loading
            binding.viewLoading.animateVisibility(loadState.refresh is LoadState.Loading)
            binding.tvNotFoundError.animateVisibility(listAdapter.itemCount == 0)
            binding.ivNotFoundError.animateVisibility(listAdapter.itemCount == 0)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
    }
}
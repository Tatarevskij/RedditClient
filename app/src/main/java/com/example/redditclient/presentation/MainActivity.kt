package com.example.redditclient.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.redditclient.R
import com.example.redditclient.databinding.ActivityMainBinding
import com.example.redditclient.presentation.authorization.AuthorizationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()
    val binding by viewBinding(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_main)

        lifecycleScope.launch {
            viewModel.statusFlow.collect {
                if (it.appStatus != null)
                    Toast.makeText(this@MainActivity, it.appStatus, Toast.LENGTH_SHORT).show()
                if (it.repoStatus != null)
                    Toast.makeText(this@MainActivity, it.repoStatus, Toast.LENGTH_SHORT).show()
                viewModel.statusReset()
            }
        }

        binding.subredditsBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_linksFragment)
        }
        binding.favoritesBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_favoritesFragment)
        }
        binding.profileBtn.setOnClickListener {
            findNavController(R.id.nav_host_fragment).navigate(R.id.action_global_profileFragment)
        }
    }
}
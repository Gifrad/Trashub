package com.capstone.project.trashhub.view.search

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.capstone.project.trashhub.R
import com.capstone.project.trashhub.databinding.ActivitySearchBinding
import com.capstone.project.trashhub.network.model.ListBankSampah
import com.capstone.project.trashhub.view.adapter.ListBankSampahAdapter
import com.capstone.project.trashhub.view.home.HomeActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private lateinit var listBankSampahAdapter: ListBankSampahAdapter
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAction()
        viewModel()
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java)).apply {
                finishAffinity()
            }
        }

    }

    private fun viewModel() {
        searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

        searchViewModel.listBankSamapah.observe(this, {
            getAdapter()
            listBankSampahAdapter.setList(it)
            binding.rvSearch1.adapter = listBankSampahAdapter

            binding.searchView.imeOptions = EditorInfo.IME_ACTION_SEARCH
            binding.searchView.setOnQueryTextListener(object :
                SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(action: String?): Boolean {
                    if (action != null) {

                        if (action.isEmpty()) {
                            binding.tvNotFound.visibility = View.INVISIBLE
                            binding.rvSearch1.visibility = View.VISIBLE
                            binding.rvSearch2.visibility = View.GONE
                        } else if (action.isNotEmpty()) {
                            val listData = it
                            val filter =
                                listData?.filter { it.name.contains("$action", true) }
                            listBankSampahAdapter.setList(filter as ArrayList<ListBankSampah>)
                            if (action.isNotEmpty()) {
                                binding.rvSearch2.visibility = View.VISIBLE
                                binding.rvSearch2.adapter = listBankSampahAdapter
                                binding.rvSearch1.visibility = View.INVISIBLE
                                if (filter.isEmpty()) {
                                    binding.tvNotFound.visibility = View.VISIBLE
                                } else {
                                    binding.tvNotFound.visibility = View.INVISIBLE
                                }
                            } else {
                                binding.rvSearch1.visibility = View.VISIBLE
                                binding.rvSearch2.visibility = View.GONE

                            }
                        }
                    }
                    return false
                }

            })
        })

        searchViewModel.showDataSearch()


    }

    private fun getAdapter() {
        listBankSampahAdapter = ListBankSampahAdapter()
        binding.apply {
            rvSearch1.layoutManager = GridLayoutManager(this@SearchActivity, 2)
            rvSearch2.layoutManager = GridLayoutManager(this@SearchActivity, 2)
        }

    }
}
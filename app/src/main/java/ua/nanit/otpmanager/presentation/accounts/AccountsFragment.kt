package ua.nanit.otpmanager.presentation.accounts

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import ua.nanit.otpmanager.R
import ua.nanit.otpmanager.appComponent
import ua.nanit.otpmanager.databinding.FragAccountsBinding
import ua.nanit.otpmanager.domain.account.Account
import ua.nanit.otpmanager.presentation.addnew.ScanCodeActivity
import javax.inject.Inject

class AccountsFragment : AccountListener, Fragment() {

    private val viewModel: AccountsViewModel by viewModels { viewModelFactory }

    private lateinit var binding: FragAccountsBinding
    private lateinit var navController: NavController
    private lateinit var clipboardManager: ClipboardManager

    private var fabEnabled = false

    @Inject
    lateinit var viewModelFactory: AccountsViewModelFactory

    override fun onAttach(context: Context) {
        super.onAttach(context)
        appComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = findNavController()
        clipboardManager = requireContext().getSystemService(Context.CLIPBOARD_SERVICE)
                as ClipboardManager

        activateMainMenu()
        enableFab(false)

        val adapter = AccountsAdapter(this, emptyList())

        binding.accountsList.layoutManager = LinearLayoutManager(requireContext())
        binding.accountsList.adapter = adapter
        binding.addAccountBtn.setOnClickListener {
            enableFab(!fabEnabled)
        }

        binding.addManualBtn.setOnClickListener {
            enableFab(false)
            navController.navigate(R.id.actionNavAddManual)
        }

        binding.addScanBtn.setOnClickListener {
            enableFab(false)
            startActivity(Intent(requireContext(), ScanCodeActivity::class.java))
        }

        viewModel.accounts.observe(viewLifecycleOwner) {
            adapter.updateAll(it)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updates.collect {
                adapter.update(it)
            }
        }
    }

    override fun onUpdate(account: AccountWrapper) {
        viewModel.updateAccount(account)
    }

    override fun onCopy(password: String) {
        clipboardManager.setPrimaryClip(ClipData.newPlainText("otp", password))
        Snackbar.make(requireView(), R.string.accounts_copied, Snackbar.LENGTH_SHORT).show()
    }

    override fun onSelect(account: Account) {
        //activateEditorMenu()
    }

    private fun enableFab(enabled: Boolean) {
        fabEnabled = enabled

        if (enabled) {
            binding.addManualBtn.show()
            binding.addManualText.visibility = View.VISIBLE
            binding.addScanBtn.show()
            binding.addScanText.visibility = View.VISIBLE
        } else {
            binding.addManualBtn.hide()
            binding.addManualText.visibility = View.GONE
            binding.addScanBtn.hide()
            binding.addScanText.visibility = View.GONE
        }
    }

    private fun activateMainMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.main, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun activateEditorMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.editor, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

}
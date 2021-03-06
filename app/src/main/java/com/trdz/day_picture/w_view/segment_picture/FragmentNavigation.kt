package com.trdz.day_picture.w_view.segment_picture

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.*
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.trdz.day_picture.R
import com.trdz.day_picture.x_view_model.MainViewModel
import androidx.core.content.ContextCompat
import com.google.android.material.bottomappbar.BottomAppBar
import com.trdz.day_picture.databinding.FragmentNavigationBinding
import com.trdz.day_picture.x_view_model.StatusMessage
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.chip.Chip
import com.trdz.day_picture.w_view.Leader
import com.trdz.day_picture.w_view.MainActivity
import com.trdz.day_picture.w_view.segment_book.WindowKnowledge
import com.trdz.day_picture.z_utility.*
import kotlinx.android.synthetic.main.preset_chips.*
import java.io.File
import java.lang.Thread.sleep

class FragmentNavigation: Fragment() {

	//region Elements
	private var _executors: Leader? = null
	private val executors get() = _executors!!
	private var _binding: FragmentNavigationBinding? = null
	private val binding get() = _binding!!
	private var _viewModel: MainViewModel? = null
	private val viewModel get() = _viewModel!!
	private var mood = 1
	private var lastChose = 1
	var isMain = true
	var isFirst = false
	//endregion

	//region Base realization
	override fun onDestroyView() {
		super.onDestroyView()
		_binding = null
		_executors = null
		_viewModel = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		arguments?.let {
			isFirst = it.getBoolean(KEY_FINSTANCE)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		_binding = FragmentNavigationBinding.inflate(inflater, container, false)
		_executors = (requireActivity() as MainActivity)
		_viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		setPager()
		setViewModel()
		setMenu()
		binding.motionPicture.addTransitionListener(object: MotionLayout.TransitionListener {
			override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {}
			override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
			override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
				binding.presetAppbar.pageIndicator.refreshDots()
			}
			override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
			}
		})
		if (isFirst) {
			binding.motionPicture.setTransitionDuration(2000)
			binding.motionPicture.transitionToEnd()
		}
		buttonBinds()
	}

	private fun setPager() {
		with(binding) {
			viewPager.adapter = FragmentNavigationPager(childFragmentManager, requireContext()).apply {
				setWindows()
				add(WIN_CODE_POD, WindowPictureOf.newInstance(PREFIX_POD))
				add(WIN_CODE_POE, WindowPictureOf.newInstance(PREFIX_EPC))
				add(WIN_CODE_POM, WindowPictureOf.newInstance(PREFIX_MRP))
			}
			binding.viewPager.currentItem = 1
			viewPager.setPageTransformer(true, FragmentNavigationTransformer())
			presetAppbar.pageIndicator.attachTo(binding.viewPager)
			viewPager.addOnPageChangeListener(object: OnPageChangeListener {
				override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
				override fun onPageSelected(position: Int) {
					presetAppbar.toolbar.title = when (position) {
						0 -> getString(R.string.POE_title)
						1 -> getString(R.string.POD_title)
						2 -> getString(R.string.POM_title)
						else -> getString(R.string.MISSING)
					}
				}

				override fun onPageScrollStateChanged(state: Int) {}
			})
		}
	}

	private fun setViewModel() {
		val observer = Observer<StatusMessage> { renderData(it) }
		viewModel.getMessage().observe(viewLifecycleOwner, observer)
	}
	//endregion

	//region Menu realization
	override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
		super.onCreateOptionsMenu(menu, inflater)
		inflater.inflate(R.menu.menu_bottom_bar, menu)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (!inPassiveMode()) {
			when (item.itemId) {
				R.id.app_bar_note -> {
					binding.motionPicture.setTransitionDuration(750)
					binding.motionPicture.addTransitionListener(object: MotionLayout.TransitionListener {
						override fun onTransitionStarted(motionLayout: MotionLayout?, startId: Int, endId: Int) {
							mood = 3
						}

						override fun onTransitionChange(motionLayout: MotionLayout?, startId: Int, endId: Int, progress: Float) {}
						override fun onTransitionCompleted(motionLayout: MotionLayout?, currentId: Int) {
							executors.getNavigation().replace(requireActivity().supportFragmentManager, com.trdz.day_picture.w_view.segment_book.FragmentNavigation(), false, R.id.container_fragment_navigation)
						}
						override fun onTransitionTrigger(motionLayout: MotionLayout?, triggerId: Int, positive: Boolean, progress: Float) {
						}
					})
					binding.motionPicture.transitionToStart()
				}
				R.id.app_bar_res -> {
					refreshAll()
				}
				R.id.app_bar_settings -> {
					toSetting()
					executors.getNavigation().replace(requireActivity().supportFragmentManager, WindowSettings(), true)
				}
			}
		}
		return super.onOptionsItemSelected(item)
	}

	private fun toSetting() {
		binding.presetAppbar.pageIndicator.visibility = View.GONE
		binding.viewPager.visibility = View.GONE
		Log.d("@@@", "App - Settings/help subWindow ")
		binding.presetAppbar.toolbar.navigationIcon = null
		mood = 2
		viewModel.changePage(2)
	}


	private fun setMenu() {
		(requireActivity() as MainActivity).setSupportActionBar(binding.bottomAppBar)
		setHasOptionsMenu(true)
		setHelpMenu()
	}

	private fun setHelpMenu() {
		binding.presetAppbar.toolbar.setNavigationIcon(R.drawable.ic_help)
		binding.presetAppbar.toolbar.setOnClickListener { openHelp() }
	}

	private fun openHelp() {
		toSetting()
		executors.getNavigation().add(requireActivity().supportFragmentManager, WindowHelp(), true, R.id.container_fragment_navigation)
	}
	//endregion


	//region Main functional


	private fun buttonBinds() {
		with(binding) {
			favorite.setOnClickListener { viewModel.needSave(getPrefix()) }
			floatButton.setOnLongClickListener { changeMode() }
			floatButton.setOnClickListener { executors.getNavigation().add(requireActivity().supportFragmentManager, WindowSearch(), true, R.id.container_fragment_navigation) }
			presetChip.chipGroup.setOnCheckedChangeListener { _, position -> chipRealization(position) }
		}
	}

	private fun changeMode(): Boolean {
		if (isMain) {
			binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
			binding.floatButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_search))
		}
		else {
			binding.bottomAppBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
			binding.floatButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_wikipedia))
		}
		isMain = !isMain
		return true
	}

	private fun chipRealization(position: Int) {
		val chip = chipGroup.findViewById<Chip>(position) ?: return
		when (chip.tag) {
			"chip_l" -> {
				lastChose = 0
				viewModel.setChange = -1
				refreshAll()
			}
			"chip_c" -> {
				lastChose = 1
				viewModel.setChange = 0
				refreshAll()
			}
			"chip_r" -> executors.getExecutor().showToast(requireContext(), getString(R.string.egs))
		}
	}

	private fun refreshAll() {
		viewModel.analyze(PREFIX_POD)
		viewModel.analyze(PREFIX_MRP)
		viewModel.analyze(PREFIX_EPC)
	}

	private fun getPrefix(): String {
		return when (binding.viewPager.currentItem) {
			1 -> PREFIX_POD
			2 -> PREFIX_MRP
			else -> PREFIX_MRP
		}

	}

	private fun renderData(material: StatusMessage) {
		binding.motionPicture.transitionToEnd()
		when (material) {
			is StatusMessage.Success -> {
			}
			is StatusMessage.VideoError -> {
				Log.d("@@@", "Nav - error")
				binding.floatButton.showSnackBar(getString(R.string.error_this_is_video), Snackbar.LENGTH_INDEFINITE) { action(R.string.error_this_is_video_end) {} }
			}
			else -> {
				Log.d("@@@", "Nav - setting applied")
				mood = 1
				binding.presetAppbar.pageIndicator.visibility = View.VISIBLE
				binding.viewPager.visibility = View.VISIBLE
				setHelpMenu()

			}
		}
	}

	private fun inPassiveMode() = (mood > 1)

	//endregion

	companion object {
		@JvmStatic
		fun newInstance(first_instance: Boolean) =
			FragmentNavigation().apply {
				arguments = Bundle().apply {
					putBoolean(KEY_FINSTANCE, first_instance)
				}
			}
	}

}
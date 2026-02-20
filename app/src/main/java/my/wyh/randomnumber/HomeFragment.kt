package my.wyh.randomnumber

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import my.wyh.randomnumber.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var vibrator: Vibrator
    private lateinit var prefs: SharedPreferences
    private lateinit var historyAdapter: HistoryAdapter

    private var minValue: Long = 1
    private var maxValue: Long = 100
    private var isGenerating: Boolean = false
    private var currentAnimator: ValueAnimator? = null
    private var generatedNumbers = mutableSetOf<Long>()
    private val historyList = mutableListOf<HistoryItem>()

    private val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        prefs = requireContext().getSharedPreferences("random_number_prefs", Context.MODE_PRIVATE)

        setupUI()
        setupListeners()
        setupHistory()
        animateEntry()
        loadQuickRanges()
    }

    private fun setupUI() {
        binding.minValueInput.setText(minValue.toString())
        binding.maxValueInput.setText(maxValue.toString())
        binding.resultText.text = "?"
        updateGenerateButtonState()
    }

    private fun setupListeners() {
        binding.generateButton.setOnClickListener {
            if (isGenerating) {
                stopGeneration()
            } else {
                startGeneration()
            }
        }

        binding.copyButton.setOnClickListener {
            copyResultToClipboard()
        }

        binding.clearHistoryButton.setOnClickListener {
            clearHistory()
        }
    }

    private fun setupQuickButtons() {
        val quickRanges = loadQuickRanges()
        binding.quickButtonsContainer.removeAllViews()

        quickRanges.forEach { range ->
            val chip = Chip(requireContext(), null, com.google.android.material.R.style.Widget_Material3_Chip_Filter).apply {
                text = "${range.first}-${range.second}"
                tag = "quick${range.first}${range.second}"
                setOnClickListener {
                    setRange(range.first, range.second)
                    animateButtonPress(this)
                }
            }
            binding.quickButtonsContainer.addView(chip)
        }
    }

    private fun loadQuickRanges(): List<Pair<Long, Long>> {
        val defaultRanges = listOf(
            1L to 10L,
            1L to 100L,
            1L to 1000L,
            1L to 6L
        )

        val saved = prefs.getString("quick_ranges", null)
        return if (saved != null) {
            saved.split(";").mapNotNull { pair ->
                val parts = pair.split(",")
                if (parts.size == 2) {
                    try {
                        parts[0].toLong() to parts[1].toLong()
                    } catch (e: NumberFormatException) {
                        null
                    }
                } else null
            }
        } else defaultRanges
    }

    private fun setupHistory() {
        historyAdapter = HistoryAdapter(historyList)
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = historyAdapter
        }
        updateHistoryVisibility()
    }

    private fun setRange(min: Long, max: Long) {
        minValue = min
        maxValue = max
        binding.minValueInput.setText(min.toString())
        binding.maxValueInput.setText(max.toString())
        generatedNumbers.clear()
    }

    private fun startGeneration() {
        val minInput = binding.minValueInput.text.toString()
        val maxInput = binding.maxValueInput.text.toString()

        if (minInput.isEmpty() || maxInput.isEmpty()) {
            showToast(getString(R.string.error_empty_input))
            return
        }

        try {
            minValue = minInput.toLong()
            maxValue = maxInput.toLong()
        } catch (e: NumberFormatException) {
            showToast(getString(R.string.error_invalid_number))
            return
        }

        if (minValue > maxValue) {
            showToast(getString(R.string.error_min_greater_than_max))
            return
        }

        if (maxValue > Long.MAX_VALUE / 2 || minValue < Long.MIN_VALUE / 2) {
            showToast(getString(R.string.error_data_overflow))
            return
        }

        val range = maxValue - minValue + 1
        if (range <= 0) {
            showToast(getString(R.string.error_data_overflow))
            return
        }

        val allowRepeat = prefs.getBoolean("allow_repeat", true)
        if (!allowRepeat && generatedNumbers.size >= range) {
            showToast(getString(R.string.error_all_numbers_generated))
            return
        }

        isGenerating = true
        updateGenerateButtonState()
        binding.copyButton.isEnabled = false

        animateGeneration(allowRepeat)
    }

    private fun stopGeneration() {
        currentAnimator?.cancel()
        finalizeGeneration()
    }

    private fun animateGeneration(allowRepeat: Boolean) {
        val random = Random(System.currentTimeMillis())

        currentAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = Long.MAX_VALUE
            interpolator = LinearInterpolator()

            var lastUpdate = 0L
            val updateInterval = 50L

            addUpdateListener { animation ->
                val now = System.currentTimeMillis()
                if (now - lastUpdate >= updateInterval) {
                    lastUpdate = now

                    var tempValue: Long
                    var attempts = 0
                    do {
                        tempValue = random.nextLong(minValue, maxValue + 1)
                        attempts++
                    } while (!allowRepeat && generatedNumbers.contains(tempValue) && attempts < 100)

                    binding.resultText.text = tempValue.toString()
                    vibrateLight()
                }
            }

            start()
        }
    }

    private fun finalizeGeneration() {
        val allowRepeat = prefs.getBoolean("allow_repeat", true)
        val random = Random(System.currentTimeMillis())

        var finalValue: Long
        var attempts = 0
        do {
            finalValue = random.nextLong(minValue, maxValue + 1)
            attempts++
        } while (!allowRepeat && generatedNumbers.contains(finalValue) && attempts < 1000)

        if (!allowRepeat) {
            generatedNumbers.add(finalValue)
        }

        binding.resultText.text = finalValue.toString()
        addToHistory(minValue, maxValue, finalValue)
        animateResult()
        vibrateSuccess()

        isGenerating = false
        updateGenerateButtonState()
        binding.copyButton.isEnabled = true
    }

    private fun addToHistory(min: Long, max: Long, value: Long) {
        val time = dateFormat.format(Date())
        val item = HistoryItem(time, min, max, value)
        historyList.add(0, item)
        if (historyList.size > 50) {
            historyList.removeAt(historyList.size - 1)
        }
        historyAdapter.notifyItemInserted(0)
        binding.historyRecyclerView.scrollToPosition(0)
        updateHistoryVisibility()
    }

    private fun clearHistory() {
        historyList.clear()
        historyAdapter.notifyDataSetChanged()
        updateHistoryVisibility()
        generatedNumbers.clear()
        showToast(getString(R.string.history_cleared))
    }

    private fun updateHistoryVisibility() {
        val isEmpty = historyList.isEmpty()
        binding.historyRecyclerView.visibility = if (isEmpty) View.GONE else View.VISIBLE
        binding.emptyHistoryText.visibility = if (isEmpty) View.VISIBLE else View.GONE
    }

    private fun updateGenerateButtonState() {
        if (isGenerating) {
            binding.generateButton.text = getString(R.string.stop_button)
            binding.generateButton.setIconResource(R.drawable.ic_stop)
        } else {
            binding.generateButton.text = getString(R.string.start_button)
            binding.generateButton.setIconResource(R.drawable.ic_play)
        }
    }

    private fun animateResult() {
        val scaleX = ObjectAnimator.ofFloat(binding.resultText, "scaleX", 1f, 1.3f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.resultText, "scaleY", 1f, 1.3f, 1f)

        AnimatorSet().apply {
            playTogether(scaleX, scaleY)
            duration = 400
            interpolator = createNonLinearInterpolator()
            start()
        }
    }

    private fun animateEntry() {
        val views = listOf(
            binding.titleText,
            binding.resultCard,
            binding.rangeCard,
            binding.quickButtonsCard,
            binding.generateButton,
            binding.copyButton,
            binding.historyCard
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 80f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(500)
                .setStartDelay(index * 120L)
                .setInterpolator(createNonLinearInterpolator())
                .start()
        }
    }

    private fun animateButtonPress(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(100)
            .setInterpolator(createNonLinearInterpolator())
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(200)
                    .setInterpolator(createNonLinearInterpolator())
                    .start()
            }
            .start()
    }

    private fun createNonLinearInterpolator(): TimeInterpolator {
        return PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f)
    }

    private fun copyResultToClipboard() {
        val result = binding.resultText.text.toString()
        if (result == "?" || isGenerating) {
            showToast(getString(R.string.error_no_result))
            return
        }

        val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(getString(R.string.app_name), result)
        clipboard.setPrimaryClip(clip)

        showToast(getString(R.string.copied_to_clipboard))
        vibrateLight()
    }

    private fun vibrateLight() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(15)
        }
    }

    private fun vibrateSuccess() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(60)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onPause() {
        super.onPause()
        if (isGenerating) {
            finalizeGeneration()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

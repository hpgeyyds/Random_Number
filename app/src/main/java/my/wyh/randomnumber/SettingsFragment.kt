package my.wyh.randomnumber

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import my.wyh.randomnumber.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var prefs: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        prefs = requireContext().getSharedPreferences("random_number_prefs", Context.MODE_PRIVATE)

        setupUI()
        setupListeners()
        animateEntry()
    }

    private fun setupUI() {
        // 加载保存的设置
        val allowRepeat = prefs.getBoolean("allow_repeat", true)
        binding.allowRepeatSwitch.isChecked = allowRepeat

        val themeMode = prefs.getString("theme_mode", "system")
        when (themeMode) {
            "light" -> binding.themeRadioGroup.check(R.id.radioLight)
            "dark" -> binding.themeRadioGroup.check(R.id.radioDark)
            else -> binding.themeRadioGroup.check(R.id.radioSystem)
        }

        // 加载快速范围设置
        val quickRanges = loadQuickRanges()
        binding.quickRange1Input.setText("${quickRanges.getOrNull(0)?.first ?: 1},${quickRanges.getOrNull(0)?.second ?: 10}")
        binding.quickRange2Input.setText("${quickRanges.getOrNull(1)?.first ?: 1},${quickRanges.getOrNull(1)?.second ?: 100}")
        binding.quickRange3Input.setText("${quickRanges.getOrNull(2)?.first ?: 1},${quickRanges.getOrNull(2)?.second ?: 1000}")
        binding.quickRange4Input.setText("${quickRanges.getOrNull(3)?.first ?: 1},${quickRanges.getOrNull(3)?.second ?: 6}")
    }

    private fun setupListeners() {
        // 允许重复开关
        binding.allowRepeatSwitch.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("allow_repeat", isChecked).apply()
            showToast(if (isChecked) "已允许重复抽取" else "已禁止重复抽取")
        }

        // 主题模式选择
        binding.themeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val mode = when (checkedId) {
                R.id.radioLight -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    "light"
                }
                R.id.radioDark -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    "dark"
                }
                else -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    "system"
                }
            }
            prefs.edit().putString("theme_mode", mode).apply()
        }

        // 保存快速范围
        binding.saveQuickRangesButton.setOnClickListener {
            saveQuickRanges()
        }

        // 重置快速范围
        binding.resetQuickRangesButton.setOnClickListener {
            resetQuickRanges()
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

    private fun saveQuickRanges() {
        try {
            val ranges = listOf(
                parseRange(binding.quickRange1Input.text.toString()),
                parseRange(binding.quickRange2Input.text.toString()),
                parseRange(binding.quickRange3Input.text.toString()),
                parseRange(binding.quickRange4Input.text.toString())
            )

            val validRanges = ranges.filterNotNull()
            if (validRanges.size < 4) {
                showToast("请输入有效的范围格式：最小值,最大值")
                return
            }

            // 检查数据溢出
            validRanges.forEach { (min, max) ->
                if (max > Long.MAX_VALUE / 2 || min < Long.MIN_VALUE / 2 || min > max) {
                    showToast("范围 ${min}-${max} 无效，请检查输入")
                    return
                }
            }

            val savedString = validRanges.joinToString(";") { "${it.first},${it.second}" }
            prefs.edit().putString("quick_ranges", savedString).apply()

            showToast("快速取值范围已保存")
        } catch (e: Exception) {
            showToast("保存失败，请检查输入格式")
        }
    }

    private fun parseRange(input: String): Pair<Long, Long>? {
        val parts = input.split(",")
        if (parts.size != 2) return null
        return try {
            val min = parts[0].trim().toLong()
            val max = parts[1].trim().toLong()
            if (min <= max) min to max else null
        } catch (e: NumberFormatException) {
            null
        }
    }

    private fun resetQuickRanges() {
        prefs.edit().remove("quick_ranges").apply()
        binding.quickRange1Input.setText("1,10")
        binding.quickRange2Input.setText("1,100")
        binding.quickRange3Input.setText("1,1000")
        binding.quickRange4Input.setText("1,6")
        showToast("已恢复默认快速取值范围")
    }

    private fun animateEntry() {
        val views = listOf(
            binding.settingsTitle,
            binding.appearanceCard,
            binding.generationCard,
            binding.quickRangesCard
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 60f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(index * 100L)
                .setInterpolator(PathInterpolator(0.4f, 0.0f, 0.2f, 1.0f))
                .start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

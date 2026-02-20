package my.wyh.randomnumber

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.PathInterpolator
import androidx.fragment.app.Fragment
import my.wyh.randomnumber.databinding.FragmentAboutBinding

class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        animateEntry()
    }

    private fun setupListeners() {
        binding.githubButton.setOnClickListener {
            openUrl("https://github.com/hpgeyyds/Random_Number")
        }

        binding.licenseButton.setOnClickListener {
            openUrl("https://www.gnu.org/licenses/gpl-3.0.html")
        }

        binding.traeButton.setOnClickListener {
            openUrl("https://www.trae.ai")
        }

        binding.kimiButton.setOnClickListener {
            openUrl("https://www.moonshot.cn")
        }

        binding.feedbackButton.setOnClickListener {
            sendFeedback()
        }
    }

    private fun openUrl(url: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun sendFeedback() {
        try {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_SUBJECT, "随机数生成器反馈")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Handle error
        }
    }

    private fun animateEntry() {
        val views = listOf(
            binding.aboutTitle,
            binding.authorText,
            binding.versionCard,
            binding.descriptionCard,
            binding.licenseCard,
            binding.actionCard,
            binding.poweredByCard
        )

        views.forEachIndexed { index, view ->
            view.alpha = 0f
            view.translationY = 60f
            view.scaleX = 0.95f
            view.scaleY = 0.95f

            view.animate()
                .alpha(1f)
                .translationY(0f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(500)
                .setStartDelay(index * 120L)
                .setInterpolator(PathInterpolator(0.34f, 1.56f, 0.64f, 1f))
                .start()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

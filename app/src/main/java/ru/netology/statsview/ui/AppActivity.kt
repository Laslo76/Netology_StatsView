package ru.netology.statsview.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import ru.netology.statsview.R
import ru.netology.statsview.databinding.ActivityAppBinding
import android.view.animation.AnimationUtils
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val view = binding.stats
        view.data = listOf(
            2000F,
            500F,
            500F,
            500F,
            500F,
        )
        //val textView = findViewById<TextView>(R.id.label)

/*      //Пример 1 android.view.animation
        view.startAnimation(
            AnimationUtils.loadAnimation(this, R.anim.animation).apply {
                setAnimationListener(object: Animation.AnimationListener {
                    override fun onAnimationEnd(p0: Animation?) {
                        textView.text = "onAnimationEnd"
                    }

                    override fun onAnimationRepeat(p0: Animation?) {
                        textView.text = "onAnimationRepeat"
                    }

                    override fun onAnimationStart(p0: Animation?) {
                        textView.text = "onAnimationStart"
                    }
                })
            }
        )*/

        /*//Пример 3 ObjectAnimator через готовые property
        ObjectAnimator.ofFloat(view, View.ALPHA, 0.05F, 1F).apply {
            startDelay = 0
            duration = 3500
            interpolator = BounceInterpolator()
        }.start()
        */

        /*
        // Пример 4 Анимация нескольких свойств через PropertyValuesHolder
        val rotation = PropertyValuesHolder.ofFloat(View.ROTATION, 0F, 360F)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 0F, 1F)
        ObjectAnimator.ofPropertyValuesHolder(view, rotation, alpha)
            .apply {
                startDelay = 500
                duration = 3500
                interpolator = LinearInterpolator()
            }.start()
         */
/*        //Пример 5 Использование ViewPropertyAnimator
        view.animate()
            .rotation(360F)
            .setInterpolator(LinearInterpolator())
            .setDuration(3000)
            .start()
*/
        /*
//        Пример 6 Комбинация нескольких анимаций через AnimatorSet
        val alpha = ObjectAnimator.ofFloat(view, View.ALPHA, 0.25F, 1F).apply {
            duration = 300
            interpolator = LinearInterpolator()
        }
        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 0F, 1F)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 0F, 1F)
        val scale = ObjectAnimator.ofPropertyValuesHolder(view, scaleX, scaleY).apply {
            duration = 300
            interpolator = BounceInterpolator()
        }
        AnimatorSet().apply {
            startDelay = 500
            playSequentially(scale, alpha)
        }.start()*/
}
    }

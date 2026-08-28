package ru.netology.statsview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random
import androidx.core.graphics.withRotation

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {

    private var progress = 0F // От 0 до 360 (для заполнения)
    private var rotationAngle = 0F // Для вращения

    private var radius = 0F
    private val center = PointF(0F, 0F)
    private lateinit var oval: RectF

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    var data = emptyList<Float>()


    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }

        // АНИМАЦИЯ ЗАПОЛНЕНИЯ И ВРАЩЕНИЯ
        ValueAnimator.ofFloat(0f, 360f).apply {
            duration = 2_000L
            repeatCount = ValueAnimator.INFINITE
            interpolator = LinearInterpolator()
            addUpdateListener { animation ->
                progress = animation.animatedValue as Float
                rotationAngle = animation.animatedValue as Float
                invalidate()
            }
        }.start()
   }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND // Скругление стыков
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center.set(w / 2F, h / 2F)
        oval = RectF(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
    }

     override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return

        val total = data.sum()
        if (total <= 0f) return

        val GAP_DEGREES = 0.5f // Зазор между сегментами
        val numGaps = fractions.size - 1
        val totalAngle = 360f - (numGaps * GAP_DEGREES)

        canvas.withRotation(
            rotationAngle,
            center.x,
            center.y
        ) { // Сохраняем исходное состояние холста
            var startFrom = -90f
            for ((index, fraction) in fractions.withIndex()) {
                val angle = totalAngle * fraction // Полный размер сектора

                // Синхронное заполнение ВСЕХ секторов:
                // Мы берём пропорцию текущего прогресса ОТ ПОЛНОГО УГЛА СЕКТОРА.
                val sweepAngle = min(angle, progress / 360f * angle)

                paint.color = colors.getOrNull(index % colors.size) ?: randomColor()
                drawArc(oval, startFrom, sweepAngle, false, paint)

                startFrom += angle + if (index < fractions.lastIndex) GAP_DEGREES else 0f
            }

        }

        canvas.drawText(
            "%.2f%%".format(fractions.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private val fractions: List<Float>
        get() = data.map { it / data.sum() } // Нормализуем данные

    private fun randomColor(): Int =
        Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}
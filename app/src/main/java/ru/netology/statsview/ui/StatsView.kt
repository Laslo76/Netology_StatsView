package ru.netology.statsview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import ru.netology.statsview.R
import ru.netology.statsview.utils.AndroidUtils
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.core.graphics.withRotation

class StatsView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleAttr, defStyleRes) {
    private var radius = 0F
    private var center = PointF(0F, 0F)
    private var oval = RectF(0F, 0F, 0F, 0F)

    private var lineWidth = AndroidUtils.dp(context, 5F).toFloat()
    private var fontSize = AndroidUtils.dp(context, 40F).toFloat()
    private var colors = emptyList<Int>()

    private var progress = 0F // От 0 до 360
    private var rotationAngle = 0F // Угол вращения


    init {
        context.withStyledAttributes(attrs, R.styleable.StatsView) {
            lineWidth = getDimension(R.styleable.StatsView_lineWidth, lineWidth)
            fontSize = getDimension(R.styleable.StatsView_fontSize, fontSize)
            val resId = getResourceId(R.styleable.StatsView_colors, 0)
            colors = resources.getIntArray(resId).toList()
        }
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = fontSize
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val animator = object : Runnable {
            override fun run() {
                // Логика из блока выше
                progress += 7.5f; if (progress >= 360f) progress -= 360f
                rotationAngle += 3f; if (rotationAngle >= 360f) rotationAngle -= 360f

                invalidate()
                postDelayed(this, 16L) // Запускаем себя снова через 16 мс
            }
        }
        // Начинаем анимацию при создании view
        post(animator)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return
        val total = data[0]
        val fractions = data.drop(1).map { it / total }
        val fractionsSum = fractions.sum()

        if (total == 0f || fractionsSum > 1 || colors.isEmpty()) return // Защита от пустых данных или цветов

        val GAP_DEGREES = 0.5f // Зазор между сегментами
        val totalAngle = 360f
        var currentProgress = 0f

        canvas.withRotation(rotationAngle, center.x, center.y) { // Запоминаем исходное положение
            var startFrom = -90f // Начинаем сверху

            // Параллельный вывод дуг
            /*
            for ((index, fraction) in fractions.withIndex()) {
                val angle = totalAngle * fraction
                val sweepAngle = progress / 360f * angle

                // Устанавливаем цвет КАЖДОЙ итерации цикла
                paint.color = colors.getOrNull(index % colors.size) ?: randomColor()
                drawArc(oval, startFrom, sweepAngle, false, paint)

                startFrom += angle + if (index < fractions.lastIndex) GAP_DEGREES else 0f
                currentProgress += angle
            }

             */
            // Последовательный вывод дуг
            for ((index, fraction) in fractions.withIndex()) {
                val angle = totalAngle * fraction
                 val sweepAngle = when {
                    progress <= currentProgress -> 0f // Сектор ещё не начался
                    progress >= currentProgress + angle -> angle// Сектор полностью виден
                    else -> progress - currentProgress // Частично видим
                }
                //назначаем цвет КАЖДОЙ итерации цикла
                paint.color = colors.getOrNull(index % colors.size) ?: randomColor()
                canvas.drawArc(oval, startFrom, sweepAngle, false, paint)

                startFrom += angle + if (index < fractions.lastIndex) GAP_DEGREES else 0f
                currentProgress += angle
            }

        }
        canvas.drawText(
            "%.2f%%".format(fractionsSum * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint,
        )
    }

    private fun randomColor() = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}